package wordtutor.util.keybuttons;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

/**
 * Action listener for communicating additional keyboard.
 *  
 * @author Tomek
 *
 */

public class KeyboardBtnListener implements ActionListener {
	public JTextField inputField = null; 
	public KeyboardBtnListener(JTextField field) {
		this.inputField = field;
	}
	Logger logger = Logger.getLogger(KeyboardBtnListener.class);	
    public void actionPerformed(ActionEvent e) {
      String name = ((JButton)e.getSource()).getText();
      String nowText = inputField.getText();      
      inputField.setText(nowText.substring(0, inputField.getCaretPosition())+name.trim()+nowText.substring(inputField.getCaretPosition(), inputField.getText().length()));      
      inputField.requestFocus();
    }
}


