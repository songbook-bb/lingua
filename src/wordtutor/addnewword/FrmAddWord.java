package wordtutor.addnewword;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

import wordtutor.addnewword.audio.AudioException;
import wordtutor.addnewword.recorder.AudioPanel;
import wordtutor.exception.NoAudioLineException;
import wordtutor.settings.Settings;
import wordtutor.util.keybuttons.KeyboardBtnListener;
import wordtutor.util.keybuttons.KeyboardCtrlPanel;
import wordtutor.utils.Util;

/**
 * JDialog represents the window which allow user to 
 * add new word or change existing
 * 
 */
public class FrmAddWord extends JDialog implements ActionListener, KeyListener{
	Logger logger = Logger.getLogger(FrmAddWord.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 8319724776916147796L;
	private WordData wordData;
	private JPanel panelWords;
	private JLabel labForeignWord;
	private JTextField textForeignWord;
	private JLabel labNativeWord;
	private JTextField textNativeWord;
	private JCheckBox cbAddSound;
	private AudioPanel mAudioPanel; 
	private JPanel panelManage;
	private JButton buttonOk;
	private JButton buttonCancel;
	private Settings settings = new Settings();

	public void assignWordData(WordData wordData)
	{
		this.wordData = wordData;
	}
	/**
	 * saves the data to WordData class
	 */
	private void Save()
	{
		wordData.foreignWord = Util.formatNewlyAddedWords(textForeignWord.getText());
		wordData.nativeWord  = Util.formatNewlyAddedWords(textNativeWord.getText());		
		wordData.isSaved = wordData.foreignWord.length() > 0 && wordData.nativeWord.length() > 0 ? true : false;
		if (Util.TRUE.equalsIgnoreCase(Util.getAppProperty("RECORD.SOUND"))) { 			
			wordData.addSound = mAudioPanel.getClipRecordedSize() > 0  ? cbAddSound.isSelected() : false;		
		}	
	}
	
	 public void actionPerformed(ActionEvent event) {
		if(event.getActionCommand().equals("sound"))
		{			
			if (Util.TRUE.equalsIgnoreCase(Util.getAppProperty("RECORD.SOUND"))) {  	
				if (cbAddSound.isSelected()) {
					mAudioPanel.enableRecordPCP();
				} else {
					mAudioPanel.disableAllPCP();
				}
			}	
		
		} else {
			
			if(event.getActionCommand().equals("ok"))
			{
				if (Util.TRUE.equalsIgnoreCase(Util.getAppProperty("RECORD.SOUND"))) { 				
					try {
			            if (cbAddSound.isSelected()) {
			              mAudioPanel.stop();
			            } 
					} catch (AudioException ae) {
						logger.error(ae.getMessage(), ae);
						logger.error(ae.getStackTrace());						
					}
				}	
				Save();				
			};
            if(event.getActionCommand().equals("cancel")) {
              logger.debug("HERE!");
  			  if (Util.TRUE.equalsIgnoreCase(Util.getAppProperty("RECORD.SOUND"))) { 			
  				  mAudioPanel.deleteWavFile();
  				  mAudioPanel = null;
  			  }	  
              wordData.isSaved = false;
              wordData.addSound = false;              
            };			
			this.removeAll();
			this.dispose();			
		}
 }
 
	public void keyPressed(KeyEvent e){}
	/**
	 * handles keboard pressing
	 */
	public void keyReleased(KeyEvent e)
	{
      switch (e.getKeyCode())
      {
       case KeyEvent.VK_ENTER  :  Save(); this.dispose(); break;
       case KeyEvent.VK_ESCAPE : this.dispose(); break;
      }	  
	}
	public void keyTyped(KeyEvent e){}
	 
	 
 public FrmAddWord(JFrame parent,String title,WordData wordData, String fileWave) throws AudioException, IOException, NoAudioLineException
 {
	 super(parent,title,true);
	 settings.loadFromXML();
	 //set the main panel
	 setLocation(Util.xAddWordPosition,Util.yAddWordPosition);
	 setSize(Util.xAddWordSize,Util.yAddWordSize);	 
	 assignWordData(wordData);
	 panelWords = new JPanel(new GridLayout(3,2,5,5));
	 this.add(panelWords,BorderLayout.CENTER);
	 labForeignWord = new JLabel(Util.getLocalizedString("ADD.LABEL.FOREIGN"));
	 panelWords.add(labForeignWord);
	 textForeignWord = new JTextField(this.wordData.foreignWord);
	 Font font = new Font(textForeignWord.getFont().getName(), Font.PLAIN, 15);
	 textForeignWord.setFont(font);
	 textForeignWord.setForeground(new Color(20,100,120));

	 panelWords.add(textForeignWord);
	 labNativeWord = new JLabel(Util.getLocalizedString("ADD.LABEL.NATIVE"));
	 panelWords.add(labNativeWord);
	 textNativeWord = new JTextField(this.wordData.nativeWord);
	 textNativeWord.addKeyListener(this);	 
	 panelWords.add(textNativeWord);	 
	   // Pomocnicza klawiatura na buttonach 
     KeyboardCtrlPanel kb = new KeyboardCtrlPanel(settings.getKeyboardType());
     KeyboardBtnListener buttonListener = new KeyboardBtnListener(this.textForeignWord); 
     kb.addListenerToAll(buttonListener);
     this.add(kb.getPanel(), BorderLayout.NORTH);    	 
	 if (Util.TRUE.equalsIgnoreCase(Util.getAppProperty("RECORD.SOUND"))) {
       // sound checkbox and label
       cbAddSound = new JCheckBox(Util.getLocalizedString("ADD.CHECKBOX.SOUND"));
       cbAddSound.setSelected(true);
       cbAddSound.setActionCommand("sound");
       cbAddSound.addActionListener(this);            
       panelWords.add(cbAddSound);     
	   // sound tools panel
       mAudioPanel = new AudioPanel(AudioPanel.SHOW_CONTROLS, fileWave);
       mAudioPanel.disableAllPCP();
       mAudioPanel.enableRecordPCP();     
       panelWords.add(mAudioPanel.getPanel());          
       //set the manage panel
     }     
     JPanel panelSouth = new JPanel(); 
     panelSouth.setLayout(new BoxLayout(panelSouth, BoxLayout.Y_AXIS));
     panelManage = new JPanel(new FlowLayout());
     
	 buttonOk = new JButton(Util.getLocalizedString("BUTTON.OK"));
	 buttonOk.setActionCommand("ok");
	 buttonOk.addActionListener(this);
	 panelManage.add(buttonOk);
	 buttonCancel = new JButton(Util.getLocalizedString("BUTTON.CANCEL"));
	 buttonCancel.setActionCommand("cancel");
	 buttonCancel.addActionListener(this);
	 panelManage.add(buttonCancel);	 	 
	 panelSouth.add(panelManage);
	 this.add(panelSouth,BorderLayout.SOUTH);
	 this.setVisible(true);	 	 
 }
}
