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
package wordtutor.addnewword.audio;

import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;

import org.apache.log4j.Logger;

public class AudioUtils {
  Logger logger = Logger.getLogger(AudioUtils.class);
  public static AudioFileFormat.Type RAW_TYPE = new AudioFileFormat.Type("raw", "raw");
  public static AudioFileFormat.Type WAVE_TYPE = AudioFileFormat.Type.WAVE;
  public static TargetDataLine readyLine = null;
  public static boolean isConvertedTargetLine = false;
  
  public static class LineAndStream {
    private final TargetDataLine mLine;
    private final AudioInputStream mStream;

    public TargetDataLine getLine() {
      return mLine;
    }

    public AudioInputStream getStream() {
      return mStream;
    }

    public LineAndStream(TargetDataLine line, AudioInputStream stream) {
      mLine = line;
      mStream = stream;
    }
  }

  public static void main(String args[]) throws IOException {
    printInfoMixers();
  }

  public static void printInfoMixers() {
    Logger logger = Logger.getLogger(AudioUtils.class);
    Mixer.Info[] infoMixers = AudioSystem.getMixerInfo();
    if (infoMixers != null && infoMixers.length > 0) {
      for (int i = 0; i < infoMixers.length; i++) {
        Mixer.Info info = infoMixers[i];
        Mixer objMixer = AudioSystem.getMixer(info);
        logger.info("i=" + i);
        try {
          objMixer.open();
          Line.Info[] lineasCompatibles = objMixer.getSourceLineInfo();
          Line.Info[] targetsCompatibles = objMixer.getTargetLineInfo();
          Line[] lineas = objMixer.getTargetLines();
          logger.debug("Mixer: " + info.getName());
          
          // // Collect the input formats
          // for (Line.Info lineInfo : objMixer.getSourceLineInfo()) {
          // for (AudioFormat newFormat :
          // ((DataLine.Info)lineInfo).getFormats()) {
          // logger.info("INPUT: "+newFormat);
          // }
          // //inputFormats.add(newFormat);
          // }
          // Collect the output formats
          for (Line.Info lineInfo : objMixer.getTargetLineInfo()) {
            for (AudioFormat newFormat : ((DataLine.Info) lineInfo).getFormats()) {
              logger.info("OUTPUT: " + newFormat);
            }
            // outputFormats.add(newFormat);
          }
          logger
              .debug("===================================================================================================");
          for (int j = 0; j < lineasCompatibles.length; j++) {
            logger.debug("Source " + j + " - " + lineasCompatibles[j]);
          }
          for (int j = 0; j < targetsCompatibles.length; j++) {
            logger.debug("Target " + j + " - " + targetsCompatibles[j]);
          }
          for (int j = 0; j < lineas.length; j++) {
            logger.debug("Line " + j + " - " + lineas[j].getLineInfo());
          }
          logger
              .debug("===================================================================================================");
        } catch (LineUnavailableException e) {
          // TODO Auto-generated catch block
	        logger.error(e.getMessage(), e);
	        logger.error(e.getStackTrace());
        }

      }
    }
  }

  /**
   * Gets an AudioInputStream that reads from a microphone and outputs the
   * specified format. Throws an exception if it can't.
   */
  public static LineAndStream getRecordingStream(AudioFormat format) throws AudioException {
    Logger logger = Logger.getLogger(AudioUtils.class);
    logger.debug("ENTER "+format); 	
    try {
      if (readyLine == null) {
    	logger.debug("readyLn is "+readyLine);  
      	readyLine = AudioSystem.getTargetDataLine(format);
      	logger.debug("readyLine NOW is "+readyLine);
      } else if (isConvertedTargetLine) {
    	logger.debug("isConvertedTargetLine is "+isConvertedTargetLine);  
      	throw new Exception("This was previously converted target line...");
      	// GOTO CONVERSION TARGET LINE
      }            
      return new LineAndStream(readyLine, new AudioInputStream(readyLine)); 
    } catch (Exception iae) {
      logger.debug(iae.getMessage(), iae);	
      int mNum = -1;
      logger.debug(" "+mNum);
      for (Mixer.Info mInfo : AudioSystem.getMixerInfo()) {
        mNum++;
        Mixer m = AudioSystem.getMixer(mInfo);
        logger.debug(" "+mNum +"  "+m+" "+m.getTargetLineInfo());
        for (Line.Info lInfo : m.getTargetLineInfo()) {
          try {
        	logger.debug(" "+lInfo.getLineClass() +"  "+TargetDataLine.class);
            if (lInfo.getLineClass().equals(TargetDataLine.class)) {
              DataLine.Info dInfo = (DataLine.Info) lInfo;
              for (AudioFormat f : dInfo.getFormats()) {
                if (AudioSystem.isConversionSupported(format, f)) {
                  try {
                	logger.debug("readyLine "+readyLine);  
                  	if (readyLine == null) {
                      readyLine = (TargetDataLine) m.getLine(dInfo);
                      logger.debug("TargetDataLine readyLine "+readyLine); 
                  	} else {
                  		logger.debug("closing readyLine "+readyLine);
                  		readyLine.close();
                  		logger.debug("readyLine CLOSED "+readyLine);	
                  	}
                  	logger.debug("getting specifiedFormat ");
                    AudioFormat specifiedFormat = new AudioFormat(f.getEncoding(), format.getSampleRate(),
                        f.getSampleSizeInBits(), f.getChannels(), f.getFrameSize(), format.getSampleRate(),
                        f.isBigEndian());
                    logger.debug("GOT specifiedFormat "+specifiedFormat); 
                    readyLine.open(specifiedFormat);
                    logger.debug("opened specifiedFormat "+specifiedFormat); 
                    AudioInputStream baseStream = new AudioInputStream(readyLine);
                    logger.debug("baseStream is "+baseStream); 
                    isConvertedTargetLine = true;
                    return new LineAndStream(readyLine, AudioSystem.getAudioInputStream(format, baseStream));                      
                  } catch (IllegalArgumentException iae1) {                
                    logger.error(iae1.getMessage(), iae1);
                  } catch (LineUnavailableException lue1) {
                	  throw lue1;
                  } catch (Exception exc1) {
                	logger.error(exc1.getMessage(), exc1);   
                  }
                }
              }
            }
          } catch (LineUnavailableException lue) {
            logger.trace(lue.getMessage(), lue);
            logger.warn(lue.getMessage());
          }
        }
      }
    }
    
   // throw new AudioException();
    
    
    return new LineAndStream(null, null);
    //throw new IllegalArgumentException("No free line for format " + format);
  }
}
