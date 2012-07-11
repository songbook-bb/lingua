package wordtutor.imp;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.MessageFormat;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

import wordtutor.FrmMain;
import wordtutor.WordTutor;
import wordtutor.utils.Util;

public class FrmImport extends JDialog implements ActionListener {

	private static final long serialVersionUID = -2479995298621194872L;
	Logger logger = Logger.getLogger(FrmImport.class);
	private JButton buttonChooseFile;
	private JButton buttonOk;
	private JButton buttonCancel;
	private JPanel panelImport;
	private JPanel panelManage;
	private JLabel labelImportFile;
	private JLabel labelImportPath;
	private JCheckBox cbAppend;	
	private JTextField lessonFileName;
	private JLabel labelOrder;
	private JLabel labelPhraseSeparator;
	private JLabel labelAlternativeSeparator;
	private JLabel labelEncoding;	
	private String csvFullPath = "";
	private static WordTutor importTutor;
	private static JTable tableWords;	

	public void actionPerformed(ActionEvent event) {
		// if user pressed OK, we must save the result
		boolean success = false;
		if (cbAppend.isSelected()) {
			lessonFileName.setEditable(false);			
		} else {
			lessonFileName.setEditable(true);			
		}					
		JFileChooser fileChooser = new JFileChooser();
		if (event.getActionCommand().equals("import")) {
			int userResponse = fileChooser.showOpenDialog(this);
			if (userResponse == JFileChooser.CANCEL_OPTION) {
				labelImportPath.setText("");
			} else {
				
				File selectedFile =  fileChooser.getSelectedFile();
				try {
					if (Util.isFileUTF8(selectedFile)) {
						labelImportPath.setText(".../"+fileChooser.getSelectedFile().getName());
						csvFullPath = fileChooser.getSelectedFile().getAbsolutePath();															
					} else {
						// pokaz okno ze bledny plik
						JOptionPane.showMessageDialog(this, MessageFormat.format(Util.getLocalizedString("IMPORT.ERROR.NOT.UTF8"), selectedFile.getName() ) ,Util.getLocalizedString("ERROR.DIALOG.TITLE"),JOptionPane.ERROR_MESSAGE);
					}
				} catch (Exception ex) {
					ex.getStackTrace();
				}				
			}			
		}		
		if (event.getActionCommand().equals("cancel")) {
			success = true;
		}
		if (event.getActionCommand().equals("ok")) {
			try {
				if (!Util.isEmpty(csvFullPath)) {
					// perform import action
					importFunction(importTutor, csvFullPath);
					//importTutor.addWordInImport("der Cośtam", "cosik");
					importTutor.fillTableModel();
					importTutor.saveToXML();					
					importTutor.loadFromXML();		
			   	tableWords.repaint();   	
					success = true;					
				}
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, e.getMessage(), Util.getLocalizedString("ERROR.DIALOG.TITLE"), JOptionPane.ERROR_MESSAGE);
			}
		}
		if (!Util.isEmpty(csvFullPath)) {
			buttonOk.setEnabled(true);	
		}				
		if (success) {
			// schowaj okno
			this.dispose();
		}			
	}

	public void importFunction(WordTutor importTutor, String fileName) {		
    try {
      FileInputStream fstream = new FileInputStream(fileName);
      // Get the object of DataInputStream
      DataInputStream in = new DataInputStream(fstream);
      BufferedReader br = new BufferedReader(new InputStreamReader(in));
      String strLine;
      long counter = 0;
      String question = "", answer = "";
      while ((strLine = br.readLine()) != null) {
        counter++;        
        // Print the content on the console
        if (counter % 2 == 1) {
          question = Util.removeWhiteSpaces(Util.removeUnwantedDelimiters(Util.removeDoubledSpaces(strLine), Util.SHOW_COMMENT));
        }
        if (counter % 2 == 0) {
          answer = Util.removeWhiteSpaces(Util.removeUnwantedDelimiters(Util.removeDoubledSpaces(strLine), Util.SHOW_COMMENT));
          importTutor.addWordInImport(question, answer);          
        }        
      }
      in.close();
      fstream.close();
    } catch (Exception e) {// Catch exception if any
      System.err.println("Error: " + e.getMessage());
    }

		
	}
	
	public FrmImport(FrmMain parent, String title, WordTutor tutor, JTable tableWords) {
		super(parent, title);
		setImportTutor(tutor);		
		setTableWords(tableWords);
		setLocation(Util.xConfigPosition, Util.yConfigPosition);
		setSize(Util.xConfigSize, Util.yConfigSize);				
		
		panelImport = new JPanel();

		add(panelImport, BorderLayout.NORTH);
		panelImport.setLayout(new GridLayout(7, 2, 2, 2));
								
		cbAppend = new JCheckBox(Util.getLocalizedString("IMPORT.CHECKBOX.APPEND"));
		cbAppend.setSelected(true);
		cbAppend.setToolTipText(Util.getLocalizedString("IMPORT.CHECKBOX.APPEND.TIP"));
		cbAppend.addActionListener(this);
		// TODO - bo chwilowo TYLKO APPEND działa
		cbAppend.setEnabled(false);
		panelImport.add(cbAppend);		
		
		lessonFileName = new JTextField(tutor.getCurrentDictFile());
		lessonFileName.setEditable(false);
		panelImport.add(lessonFileName);		
				
		labelOrder = new JLabel(Util.getLocalizedString("IMPORT.LABEL.ORDER"));
		panelImport.add(labelOrder);
		panelImport.add(new JLabel(Util.getLocalizedString("IMPORT.LABEL.DEFAULT.ORDER")));
		labelPhraseSeparator = new JLabel(Util.getLocalizedString("IMPORT.LABEL.PHRASE.SEPARATOR"));
		panelImport.add(labelPhraseSeparator);
		panelImport.add(new JLabel(Util.getLocalizedString("IMPORT.LABEL.DEFAULT.PHRASE.SEPARATOR")));		
		labelAlternativeSeparator = new JLabel(Util.getLocalizedString("IMPORT.LABEL.ALTERNATIVE.SEPARATOR"));
		panelImport.add(labelAlternativeSeparator);
		panelImport.add(new JLabel(Util.getLocalizedString("IMPORT.LABEL.DEFAULT.ALTERNATIVE.SEPARATOR")));
		labelEncoding = new JLabel(Util.getLocalizedString("IMPORT.LABEL.ENCODING"));
		panelImport.add(labelEncoding);
		panelImport.add(new JLabel(Util.getLocalizedString("IMPORT.LABEL.DEFAULT.ENCODING")));
		
		// show import file
		labelImportFile = new JLabel(Util.getLocalizedString("IMPORT.LABEL.FILE"));
		panelImport.add(labelImportFile);
		labelImportPath = new JLabel("");
		panelImport.add(labelImportPath);
		
		// CHOOSER on the end
		buttonChooseFile = new JButton(Util.getLocalizedString("IMPORT.LABEL.BUTTON"));
		buttonChooseFile.setActionCommand("import");
		buttonChooseFile.addActionListener(this);
		panelImport.add(buttonChooseFile);		
			
		panelManage = new JPanel(new FlowLayout());
		add(panelManage, BorderLayout.SOUTH);
		buttonOk = new JButton(Util.getLocalizedString("BUTTON.OK"));
		buttonOk.setActionCommand("ok");
		buttonOk.addActionListener(this);
		buttonOk.setEnabled(false);		
		panelManage.add(buttonOk);

		buttonCancel = new JButton(Util.getLocalizedString("BUTTON.CANCEL"));
		buttonCancel.setActionCommand("cancel");
		buttonCancel.addActionListener(this);
		panelManage.add(buttonCancel);
		setVisible(true);

	}

	public static void setImportTutor(WordTutor importTutor) {
		FrmImport.importTutor = importTutor;
	}

	public static void setTableWords(JTable tableWords) {
		FrmImport.tableWords = tableWords;
	}

}
