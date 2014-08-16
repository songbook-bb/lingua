/**
 *
 * Copyright 2007 Stefanie Tellex
 *
 * This file is part of Fefie.com Sound ger.
 *
 * Fefie.com Sound Recorder is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * Fefie.com Sound Recorder is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package wordtutor.addnewword.audio;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;

import org.apache.log4j.Logger;

import wordtutor.addnewword.audio.AudioUtils.LineAndStream;
import wordtutor.addnewword.audio.MyByteArrayOutputStream.NewDataListener;
import wordtutor.addnewword.utils.IOUtils;
import wordtutor.addnewword.utils.ThreadUtils;
import wordtutor.exception.NoAudioLineException;
import wordtutor.utils.BatchConvert;

public class RecordableClip  {
	static Logger logger = Logger.getLogger(RecordableClip.class);
	private final File mFile;
	private final AudioFormat mFormat;
	public static final int mLengthInBytes = 4000000;
	public static final int bytesInSecond = 200000;	
	public LineAndStream las = null;	
	
	private int mStreamLength;
	private byte[] mStreamData = new byte[mLengthInBytes];
	private List<ClipListener> mListeners = new ArrayList<ClipListener>();
	
	/**
	 * Used for playing audio back.
	 */
	private Clip mClip;
	
	
	/**
	 * Used for recording audio.
	 */
	private RecordThread mRecordThread;
	
	private static int bytesToInt(byte[] intBytes, int offset, int length){
		ByteBuffer bb = ByteBuffer.wrap(intBytes, offset, length);
		
		bb.order(ByteOrder.LITTLE_ENDIAN);
		return (int) bb.getShort(); // & 0x0000ffff;
		//return (int) bb.getChar();
	}
	
		
	public RecordableClip(File file, AudioFormat format) throws IOException, NoAudioLineException, AudioException {
		mFile = file;
		if (! mFile.exists()) {
			IOUtils.create(mFile);
		}
		mStreamLength = IOUtils.load(mFile, mStreamData);
		
		mFormat = format;

		try {
			mClip = AudioSystem.getClip();

		} catch (LineUnavailableException lue) {
			throw new AudioException(lue);
		}
		las = AudioUtils.getRecordingStream(mFormat);
		
		if (las.getLine() == null || las.getStream() == null) {
			throw new NoAudioLineException("No line of format : "+mFormat+" is available.");
		}
		mRecordThread = new RecordThread(las);	
		//new AudioUtils.LineAndStream().getStream();		
		new Thread(new Runnable() {
			public void run() {
				int lastFramePos = -1;
				while (true) {
					if (mClip != null) {
						int newFramePos = mClip.getFramePosition();
						if (lastFramePos != newFramePos) {
							lastFramePos = newFramePos;
							for (ClipListener l : mListeners) {
								l.newPlayHead(newFramePos);
							}
						}
						ThreadUtils.sleep(100);
					}
				}
			}
		}).start();
		
		mClip.addLineListener(new LineListener() {
			
			public void update(LineEvent event) {
				
			}			
		});		
	}

	public void addClipListener(ClipListener cl) {
		mListeners.add(cl);
	}
	
	public int getSample(int idx) {
		int byteStartIdx = idx * mFormat.getFrameSize();
		return bytesToInt(mStreamData, byteStartIdx, mFormat.getFrameSize());		
	}
	
	private int getLengthInBytes(){ 
		return mStreamLength;
	}
	private ByteArrayInputStream makeInputStream() {
		return new ByteArrayInputStream(mStreamData, 0, mStreamLength);
	}
	private AudioInputStream makeStream() {
		return new AudioInputStream(makeInputStream(), mFormat, getLengthInBytes());
	}	
	
	public void start() throws AudioException  {		
		if (!mClip.isOpen()) {
			try {
				mClip.open(makeStream());
			} catch (Exception lue) {
				throw new AudioException(lue);
			}
		}
		mClip.start();
		mClip.setFramePosition(0);
	}
	
	/**
	 * Return the index (in samples) of the play head.
	 */
	public int getPlayHead() {
		return mClip.getFramePosition();
	}
	
	/**
	 * Stops recording if we're recording.
	 * Stops playing if we're playing. 
	 * @throws AudioException
	 * @throws IOException 
	 */
	public void stop() throws AudioException, IOException  {
		mClip.stop();
		mRecordThread.stop();
		mClip.flush();
		if (las != null) {
			las.getStream().close();
			las.getLine().close();			
		}
		mClip.close();
	}
	
	/**
	 * Move the playhead to the beginning of the clip.
	 * @throws AudioException
	 */
	public void reset() throws AudioException  {
		mClip.setFramePosition(0);
	}
	
	public int getLengthInSamples() {
		return getLengthInBytes() / mFormat.getFrameSize();
	}
	public int getLengthInSeconds() {
		return Math.round(getLengthInSamples() / mFormat.getSampleRate()); 
	}
	
	public int getMaxSampleHeight() {
		return (int) Math.round(Math.pow(2, mFormat.getSampleSizeInBits()));
	}
	
	private OutputStream getOutputStream() throws IOException {
		MyByteArrayOutputStream os = new MyByteArrayOutputStream(mStreamData);
		os.addListener(new NewDataListener() {
			public void newData(int offset, int length) {
				for (ClipListener l : mListeners) {
					l.newData(offset, length);
					mStreamLength = offset + length;
				}
			}
		});
		return os;
	}
	
	/**
	 * Start recording data.  Sends Newevents to NewDataListeners when new data is read. 
	 * Recording happens in a separate thread, and new data listeners are activated in the record thread. 
	 * @throws AudioException
	 */
	public void record() throws AudioException {
		try {
			mClip.close();
			mStreamLength = 0;
			mRecordThread.start(getOutputStream());
		} catch (Exception e) {
			throw new AudioException(e);
		}
	}
	
	/**
	 * Clears data from the clip.
	 */
	public void clear() {
		mStreamLength = 0;
	}
	
	private static class RecordThread {
		private final LineAndStream mSource;
		private OutputStream mDest;
		private boolean mStop = false;
		private Thread mThread;
		public RecordThread(LineAndStream source) {
			
			mSource = source;
		
		}
		public void stop() {
			mStop = true;
		}
		public boolean isRecording() {
			return mThread != null && mThread.isAlive();
		}
		
		public void start(OutputStream dest) {
			if (isRecording()) {
				throw new IllegalStateException("Already recording.  Thread is " + mThread);
			}
			mStop = false;
			mDest = dest;
			mThread = new Thread(new Runnable() {
				public void run() {
					RecordThread.this.run();
				}
			});
			mThread.start();
		}
		public void run() {
			try {
				byte[] buffer = new byte[1024];
				mSource.getLine().start();
				while(!mStop) {
					int read = mSource.getStream().read(buffer, 0, buffer.length);
					mDest.write(buffer, 0, read);
				}				
				mSource.getLine().stop();				
				mDest.close();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				logger.error(e.getStackTrace());
			}
		}
	}

	public interface ClipListener {
		public void newData(int offset, int length);
		public void newPlayHead(int playhead);
	}
	
	/**
	 * Saves the file in the given format. 
	 * If the type extension is "raw" it saves it without a header - just writes the bytes
	 * to the file in the current format.
	 * @throws IOException
	 */
	public void save(File f, AudioFileFormat.Type t) throws IOException {
		logger.debug("JUST BEFORE SAVING");
		AudioSystem.write(makeStream(), t, f);
		/**@TODO - poprawić wyliczenie współczynnika skalowania bo chyba nie działa */
		//new BatchConvert().runWave2mp3(reRead(f.getCanonicalPath()));
		new BatchConvert().runWave2mp3(null);
	}

	public Double reRead(String fileName) {
		int WAVE_HEADER_IN_BYTES = 44;
		short AMP_SAFETY_BELT = 10; 
		
		logger.debug("fileName="+fileName);
		try {
	        FileInputStream fis     = new FileInputStream(fileName);
	        BufferedInputStream bis = new BufferedInputStream(fis);
	        
            byte[] b = new byte[2];
            int counter = 0;
            Short maxValue = 0; 
            Short minValue = 0;            
	        while (bis.available() > 0) {	        	
	        	counter++;
	        	bis.read(b);
				Byte loIn = b[1];
				Byte hiIn = b[0];		
				if (counter > WAVE_HEADER_IN_BYTES) {
					Short i = (short)(( hiIn << 8 & 0xff00) |  loIn & 0x00FF) ;
					if (i < minValue) {
						minValue = i;
					}
					if (i > maxValue) {
						maxValue = i;
					}									
				} else {
					// this is preambule - WAVE AIFF header
				}
	        }
	        bis.close();
	        fis.close();
	        
	        double factorMin = minValue < 0 ? (double)((double)(Short.MIN_VALUE+AMP_SAFETY_BELT)/(double)minValue) : (Short.MAX_VALUE-AMP_SAFETY_BELT);
	        double factorMax = maxValue > 0 ? (double)((double)(Short.MAX_VALUE-AMP_SAFETY_BELT)/(double)maxValue) : (Short.MAX_VALUE-AMP_SAFETY_BELT);
	        	        
	        double amplifyFactor =  Math.min(factorMax, factorMin);
	        if (amplifyFactor < 1) {
	        	throw new Exception("Amplification should be greater or equal to 1");
	        }
	        // Amplifier factor is calculated
	        logger.debug("AMPLIFY ====  "+amplifyFactor);
	        return amplifyFactor;	        
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
	}
}
