package wordtutor.addnewword.recorder;

import wordtutor.addnewword.audio.AudioException;

/**
 * Interface for communicating audio state changed events.
 *  
 * @author stefie10
 *
 */
public interface AudioCtrlListener {
	public void play() throws AudioException;
	public void stop() throws AudioException;
	public void record() throws AudioException;
}