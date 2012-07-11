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

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import wordtutor.addnewword.audio.AudioException;
import wordtutor.addnewword.utils.SwingUtils;
import wordtutor.utils.Util;

/**
 * A control panel.  It communicates with an AudioCtrlListener when buttons are pushed.
 * @author stefie10
 *
 */
public class AudioCtrlPanel {
	
	private final JPanel mPanel;
	//private final JButton mPlayButton;
	private final JButton mStopButton;
	private final JButton mRecordButton;
	public AudioCtrlPanel() {
		mPanel = SwingUtils.boxPanel(BoxLayout.LINE_AXIS);
//		mPlayButton = new JButton(Util.getLocalizedString("ACP.BUTTON.PLAY"));
//		mPanel.add(mPlayButton);
		mStopButton = new JButton(Util.getLocalizedString("ACP.BUTTON.STOP"));
		mPanel.add(mStopButton);
		mRecordButton = new JButton(Util.getLocalizedString("ACP.BUTTON.RECORD"));
		mPanel.add(mRecordButton);
		
		
//		mPlayButton.addActionListener(new ExceptionActionListener() {
//			public void doActionPerformed(ActionEvent ae) throws AudioException {
//				for (AudioCtrlListener l : mListeners) {
//					l.play();
//				}
//			}
//		});
				
		
		mStopButton.addActionListener(new ExceptionActionListener() {
			public void doActionPerformed(ActionEvent ae) throws AudioException {
				for (AudioCtrlListener l : mListeners) {
					l.stop();					
					 mRecordButton.setEnabled(true);
					 /** @TODO Poprawić PLAY bo się rozsypał - nie widzi CODEC-a - DZIWNE */
					 //mPlayButton.setEnabled(true);
				}
			}
		});
		
		mRecordButton.addActionListener(new ExceptionActionListener() {
			public void doActionPerformed(ActionEvent ae) throws AudioException {
				for (AudioCtrlListener l : mListeners) {
					l.record();
					 mRecordButton.setEnabled(false);
					 //mPlayButton.setEnabled(false);
				}				
			}
		});
	}
	public JPanel getPanel() {
		return mPanel;
	}
		
	public void distableAll() {
		mRecordButton.setEnabled(false);
		//mPlayButton.setEnabled(false);		
		mStopButton.setEnabled(false);		
	}

	public void enableRecord() {
		mRecordButton.setEnabled(true);
		mStopButton.setEnabled(true);		
	}
		
	private final List<AudioCtrlListener> mListeners = new ArrayList<AudioCtrlListener>();
	public void addListener(AudioCtrlListener l) {
		mListeners.add(l);
	}
}
