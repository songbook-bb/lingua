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

import java.io.IOException;
import java.text.MessageFormat;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

import wordtutor.FrmMain;
import wordtutor.addnewword.audio.AudioException;
import wordtutor.addnewword.audio.RecordableClip;
import wordtutor.addnewword.audio.RecordableClip.ClipListener;
import wordtutor.addnewword.utils.SwingUtils;
import wordtutor.utils.Util;

/**
 * A panel that displays the waveform of an audio file.
 * 
 * @author stefie10
 * 
 */
public class ClipViewerPanel {
  private final JPanel mPanel;
  private final JLabel mClipInfo;
	Logger logger = Logger.getLogger(ClipViewerPanel.class);

  public ClipViewerPanel() {
    mPanel = SwingUtils.boxPanel(BoxLayout.PAGE_AXIS);
    mClipInfo = new JLabel(Util.getLocalizedString("CVP.LABEL"));
    mPanel.add(mClipInfo);
  }

  public JPanel getPanel() {
    return mPanel;
  }

  public void showClip(final RecordableClip c) {
    mClipInfo.setText(MessageFormat.format(Util.getLocalizedString("CVP.CLIP.INFO"), c.getLengthInSeconds()));
    c.addClipListener(new ClipListener() {
      public void newData(int offset, int length) {
        mClipInfo.setText(MessageFormat.format(Util.getLocalizedString("CVP.CLIP.INFO"), c.getLengthInSeconds()));
        if (c.getLengthInSeconds() > (RecordableClip.mLengthInBytes / RecordableClip.bytesInSecond)) {
          try {
            c.stop();
          } catch (AudioException ae) {
        	  logger.error(ae.getMessage(), ae);
        	  logger.error(ae.getStackTrace());
          } catch (IOException ioe) {
        	  logger.error(ioe.getMessage(), ioe);
        	  logger.error(ioe.getStackTrace());
          }
        }
      }

      public void newPlayHead(int playhead) {
      }
    });

  }

}
