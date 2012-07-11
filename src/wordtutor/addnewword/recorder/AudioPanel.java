/**
 *
 * Copyright 2007 Stefanie Tellex
 *
 * This file is part of Fefie.com Sound Recorder.
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
package wordtutor.addnewword.recorder;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

import wordtutor.addnewword.audio.AudioException;
import wordtutor.addnewword.audio.AudioUtils;
import wordtutor.addnewword.audio.RecordableClip;
import wordtutor.addnewword.utils.SwingUtils;
import wordtutor.exception.NoAudioLineException;

public class AudioPanel implements AudioCtrlListener {
	Logger logger = Logger.getLogger(AudioPanel.class);
	private final JPanel mPanel;
	private boolean saved = false;
	private final ClipViewerPanel mClipPanel;
	private RecordableClip mClip;
	public static File fileWave;
	public AudioCtrlPanel pcp;
	public static final boolean SHOW_CONTROLS = true;
	public static final boolean HIDE_CONTROLS = false;
	public AudioPanel(boolean showControls, String stringFileWave) throws AudioException, IOException, NoAudioLineException {
		mPanel = SwingUtils.boxPanel(BoxLayout.PAGE_AXIS);
		if (showControls) {
			pcp = new AudioCtrlPanel();
			mPanel.add(pcp.getPanel());
			pcp.addListener(this);
		}
		mClipPanel = new ClipViewerPanel();
		mPanel.add(mClipPanel.getPanel());		
		fileWave = new File(stringFileWave);
		open(fileWave);		
	}
	
	public void disableAllPCP() {
		if (pcp != null) {
			pcp.distableAll();
			deleteWavFile();
		}		
	}

	public void enableRecordPCP() {
		if (pcp != null) {
			pcp.enableRecord();	
		}		
	}
	
	
	public RecordableClip getClip() {
		return mClip;
	}
	
	public void showClip(RecordableClip c) {
		mClipPanel.showClip(c);
		mClip = c;
	}
	
	public JPanel getPanel() {
		return mPanel;
	}
	
	public void play() throws AudioException {
		if (mClip != null) {
			logger.debug("Play..."+mClip.getLengthInSeconds());
			mClip.start();
		}
	}
	public void stop() throws AudioException {
		if (mClip != null && mClip.getLengthInSamples() > 0) {
			try {
	            mClip.stop();			  
				if (!isSaved()) {
					save(fileWave);	
					setSaved(true);					
				}
			} catch (IOException ioe) {
				logger.error("Error saving file "+fileWave.getName());
				ioe.getStackTrace();
			}
			mClip.reset();			
		}
	}
	public void record() throws AudioException {
		mClip.record();		
	}

	public void save(File f) throws IOException {
		logger.debug("Nr of samples:"+this.getClip().getLengthInSamples());
		this.getClip().save(f, AudioUtils.WAVE_TYPE);
	}
	
	public void deleteWavFile() {
	  fileWave.delete();
	}

	public void open(File f) throws IOException, AudioException, NoAudioLineException {
		AudioFormat format  = new AudioFormat(44100, 16, 1, false, false);
//		logger.debug("Frame rate in second = "+format.getFrameRate());
//		logger.debug("Frame size = "+format.getFrameSize());		
//		logger.debug("Sample rate = "+format.getSampleRate());
//		logger.debug("Sample size in bits = "+format.getSampleSizeInBits());		
		RecordableClip c = new RecordableClip(f, format);
		this.showClip(c);		
	}	
	
	public int getClipRecordedSize() {
		return mClip.getLengthInSamples();
	}

	public void setSaved(boolean saved) {
		this.saved = saved;
	}

	public boolean isSaved() {
		return saved;
	}
	
}
