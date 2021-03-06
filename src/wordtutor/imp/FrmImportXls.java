package wordtutor.imp;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import wordtutor.FrmMain;
import wordtutor.WordTutor;
import wordtutor.utils.Util;

public class FrmImportXls extends JDialog implements ActionListener, WindowListener {

	private static final long serialVersionUID = -2479995298621194871L;
	Logger logger = Logger.getLogger(FrmImportXls.class);
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
	private JLabel labelAlternativeSeparator;
	private String xlsFullPath = "";
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
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
		        "Excel XLS", "xls");
		fileChooser.setFileFilter(filter);

		if (event.getActionCommand().equals("import")) {
			int userResponse = fileChooser.showOpenDialog(this);
			if (userResponse == JFileChooser.CANCEL_OPTION) {
				labelImportPath.setText("");
			} else {				
				try {
					labelImportPath.setText(".../"+ fileChooser.getSelectedFile().getName());
					xlsFullPath = fileChooser.getSelectedFile().getAbsolutePath();
						// pokaz okno ze bledny plik
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(this,
							ex.getMessage(),
							Util.getLocalizedString("ERROR.DIALOG.TITLE"),
							JOptionPane.ERROR_MESSAGE);
					logger.error(ex.getMessage(), ex);
				}
			}
		}
		if (event.getActionCommand().equals("cancel")) {
			success = true;
		}
		if (event.getActionCommand().equals("ok")) {
			try {
				if (!Util.isEmpty(xlsFullPath)) {
					// perform import action
					importFunction(importTutor, xlsFullPath);
					// importTutor.addWordInImport("der Cośtam", "cosik");
					importTutor.fillTableModel();
					importTutor.saveToXML();
					importTutor.loadFromXML();
					tableWords.repaint();
					success = true;
				}
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, e.getMessage(),
						Util.getLocalizedString("ERROR.DIALOG.TITLE"),
						JOptionPane.ERROR_MESSAGE);
				logger.error(e.getMessage(), e);
			}
		}
		if (!Util.isEmpty(xlsFullPath)) {
			buttonOk.setEnabled(true);
		}
		if (success) {
			// schowaj okno
			this.dispose();
		}
	}

	public void importFunction(WordTutor importTutor, String fileName) {

		try {
			InputStream inp = new FileInputStream(fileName);
			try {
				Workbook wb = WorkbookFactory.create(inp);
				Sheet sheet = wb.getSheetAt(0);
				Integer maxRows = sheet.getLastRowNum();
				ArrayList<ArrayList<String>> xlsList = new ArrayList<ArrayList<String>>();
				for (int i = 0; i <= maxRows; i++) {
					ArrayList<String> oneXlsRow = new ArrayList<String>();
					Row row = sheet.getRow(i);
					if (row == null) {
						continue;
						// throw new Exception ("Row for i = "+i+" is null. And maxRows is = "+maxRows);
					}
					Cell soundCell = row.getCell(0);
					String soundString = "";
					if (soundCell != null && soundCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
						soundString = ""+new Double(soundCell.getNumericCellValue()).intValue();						
					}
					Cell questionCell = row.getCell(1);
					String questionString = "";
					if (questionCell != null && questionCell.getCellType() == Cell.CELL_TYPE_STRING) {
						questionString = questionCell.getStringCellValue();
					}
					Cell answerCell = row.getCell(2);
					String answerString = "";
					if (answerCell != null && answerCell.getCellType() == Cell.CELL_TYPE_STRING) {
						answerString = answerCell.getStringCellValue();
					}
					int countCell = 3;
					Cell answerNextCell = null;
					while ((answerNextCell = row.getCell(countCell)) != null) {
						if (answerNextCell != null && answerNextCell.getCellType() == Cell.CELL_TYPE_STRING) {
							// dodaje niepuste kolejne znaczenie oddzielone
							// separatorem
							if (!StringUtils.isBlank(answerNextCell
									.getStringCellValue())) {
								answerString += Util.PHRASE_DELIMITER
										+ answerNextCell.getStringCellValue();
							}
						}
						countCell++;
					}
					if (!StringUtils.isBlank(answerString) || 
							!StringUtils.isBlank(questionString) || 
							!StringUtils.isBlank(soundString)) {
						if (StringUtils.isBlank(soundString) || !StringUtils.isNumeric(soundString)) throw new Exception("Expected soundId (column A) should have a valid numeric value at row "+(++i)+".");
						if (StringUtils.isBlank(answerString)) throw new Exception("Expected answerString (column C) should have a string value at row "+(++i)+".");
						if (StringUtils.isBlank(questionString)) throw new Exception("Expected questionString (column B) should have a string value at row "+(++i)+".");												
					}
					logger.debug(" sound="+soundString +" question="+questionString+" answer="+answerString);
					if (!StringUtils.isBlank(questionString)
							&& !StringUtils.isBlank(answerString)) {
						oneXlsRow.add(soundString);	
						oneXlsRow.add(questionString);
						oneXlsRow.add(answerString);
						xlsList.add(oneXlsRow);
					}
				}
				// add xls import to active lesson
				for (ArrayList<String> oneRowItem : xlsList) {
					String sound = oneRowItem.get(0);
					String question = oneRowItem.get(1);
					String answer = oneRowItem.get(2);
					question = Util.removeWhiteSpaces(Util.removeUnwantedDelimiters(Util.removeDoubledSpaces(question), Util.SHOW_COMMENT));
					answer = Util.removeWhiteSpaces(Util.removeUnwantedDelimiters(Util.removeDoubledSpaces(answer), Util.SHOW_COMMENT));
					// remove toLowerCase if it is wrong
					if (Util.TRUE.equalsIgnoreCase(Util.getAppProperty("IMPORT.XLS.TO.LOWER.CASE"))) {					
						importTutor.addWordInImport(question.toLowerCase(), answer.toLowerCase(), sound);						
					} else {
						importTutor.addWordInImport(question, answer, sound);
					}
		
				}

			} catch (Exception e) {
				// show error frame
				JOptionPane.showMessageDialog(null, e.getMessage(),
						Util.getLocalizedString("ERROR.DIALOG.TITLE"),
						JOptionPane.ERROR_MESSAGE);
				logger.error(e.getMessage(), e);
			} finally {
				if (inp != null) {
					inp.close();
				}
			}

		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
			JOptionPane.showMessageDialog(null, e.getMessage(),
					Util.getLocalizedString("ERROR.DIALOG.TITLE"),
					JOptionPane.ERROR_MESSAGE);
			logger.error(e.getMessage(), e);
		}
	}

	public FrmImportXls(FrmMain parent, String title, WordTutor tutor,
			JTable tableWords) {
		super(parent, title);
		new Util();
		setLocation(Util.xImportPosition, Util.yImportPosition);
		setSize(Util.xImportSize, Util.yImportSize);
		setImportTutor(tutor);
		setTableWords(tableWords);
		panelImport = new JPanel();

		add(panelImport, BorderLayout.NORTH);
		panelImport.setLayout(new GridLayout(6, 2, 2, 2));

		cbAppend = new JCheckBox(
				Util.getLocalizedString("IMPORT.XLS.CHECKBOX.APPEND"));
		cbAppend.setSelected(true);
		cbAppend.setToolTipText(Util
				.getLocalizedString("IMPORT.XLS.CHECKBOX.APPEND.TIP"));
		cbAppend.addActionListener(this);
		// TODO - bo chwilowo TYLKO APPEND działa
		cbAppend.setEnabled(false);
		panelImport.add(cbAppend);

		lessonFileName = new JTextField(tutor.getCurrentDictFile());
		lessonFileName.setEditable(false);
		panelImport.add(lessonFileName);

		labelOrder = new JLabel(Util.getLocalizedString("IMPORT.XLS.LABEL.ORDER"));
		panelImport.add(labelOrder);
		panelImport.add(new JLabel(Util
				.getLocalizedString("IMPORT.XLS.LABEL.DEFAULT.ORDER")));
		
		labelAlternativeSeparator = new JLabel(
				Util.getLocalizedString("IMPORT.XLS.LABEL.ALTERNATIVE.SEPARATOR"));
		panelImport.add(labelAlternativeSeparator);
		panelImport
				.add(new JLabel(
						Util.getLocalizedString("IMPORT.XLS.LABEL.DEFAULT.ALTERNATIVE.SEPARATOR")));
		// show import file
		labelImportFile = new JLabel(
				Util.getLocalizedString("IMPORT.XLS.LABEL.FILE"));
		panelImport.add(labelImportFile);
		labelImportPath = new JLabel("");
		panelImport.add(labelImportPath);

		// CHOOSER on the end
		buttonChooseFile = new JButton(
				Util.getLocalizedString("IMPORT.XLS.LABEL.BUTTON"));
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
		addWindowListener(this);
		setVisible(true);
	}

	public static void setImportTutor(WordTutor importTutor) {
		FrmImportXls.importTutor = importTutor;
	}

	public static void setTableWords(JTable tableWords) {
		FrmImportXls.tableWords = tableWords;
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void windowClosing(WindowEvent e) {
		storeConfigCoordinates();		
	}
	private void storeConfigCoordinates() {
		Util.setAppProperty("X.IMPORT.POSITION", ""+this.getX());
		Util.setAppProperty("Y.IMPORT.POSITION", ""+this.getY());
		Util.setAppProperty("X.IMPORT.SIZE", ""+this.getWidth());
		Util.setAppProperty("Y.IMPORT.SIZE", ""+this.getHeight());
		Util.storeAppProperties();		
		logger.debug("STORED: "+this.getX()+" "+this.getY()+" "+this.getWidth()+" "+this.getHeight());		
	}
	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub	
	}
	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub		
	}
	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
	}
	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
	}
	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
	}
}
