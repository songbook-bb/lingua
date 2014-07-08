package wordtutor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.media.Player;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import wordtutor.container.IWord;
import wordtutor.imp.FrmImport;
import wordtutor.imp.FrmImportXls;
import wordtutor.utils.BatchConvert;
import wordtutor.utils.Util;

public class FrmMain extends JFrame implements ActionListener, KeyListener,
		WindowListener {
	public String searchString = "";
	Logger logger = Logger.getLogger(FrmMain.class);
	private static final long serialVersionUID = -1277370862368265825L;
	private JLabel searchPhrase;
	private JButton startLesson;
	private JButton openSettings;
	private JButton clearLearning;
	private JButton switchLesson;
	private JButton aboutLingmem;
	private JPanel panelWords;
	private JPanel panelManage;
	private JPanel panelTools;
	private JButton buttonSearch;
	private JButton buttonAdd;
	private JButton buttonEdit;
	private JButton buttonDel;
	private JButton buttonMerge;
	private JButton buttonNewDict;
	private JButton buttonXlsImport;
	private JButton buttonXlsExport;
	private JButton buttonDupl;

	public static JTable tableWords;

	public static WordsTableModel tableModel;
	public static WordTutor tutor;

	public void createTable() {
		tableModel = new WordsTableModel();
		tableWords = new JTable(tableModel);
		tableWords.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent me) {
				Player player = null;
				// Util util = new Util();
				Point click = new Point(me.getX(), me.getY());
				int column = tableWords.columnAtPoint(click);
				int row = tableWords.rowAtPoint(click);
				// kolumna 5 - przechowuje SoundId
				if (column == 5
						&& (Integer) tableWords.getValueAt(row, column) > 0) {
					String complexFileName = tutor.getCurrentDictFile()
							.substring(0,
									tutor.getCurrentDictFile().indexOf("."))
							+ File.separator
							+ (Integer) tableWords.getValueAt(row, column)
							+ Util.getAppProperty("MP3.EXTENSION");
					Util.playSound(player, complexFileName);
				}
			}
		});
		tableWords.setAutoCreateRowSorter(true);
		TableColumnModel tcm = tableWords.getColumnModel();
		tcm.getColumn(0).setPreferredWidth(30);
		tcm.getColumn(1).setPreferredWidth(250);
		tcm.getColumn(2).setPreferredWidth(250);
		tcm.getColumn(3).setPreferredWidth(30);
		tcm.getColumn(4).setPreferredWidth(30);
		tcm.getColumn(5).setPreferredWidth(30);
		tableWords.setColumnModel(tcm);
		panelWords.add(new JScrollPane(tableWords));
		tutor.assignTableModel(tableModel);
		tutor.fillTableModel();
		tableWords.addKeyListener(this);
	}

	public void createNewDictFile(String fileName) throws IOException {
		FileWriter outFile = new FileWriter(fileName);
		PrintWriter out = new PrintWriter(outFile);
		out.print("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><dictionary/>");
		out.close();
		outFile.close();
	}

	private void startLesson() {
		if (tutor.getSettings().isExamMode()) {
			tutor.clearLearning(true);
			tutor.doWeirdCustom();
			tutor.saveToXML();
			tutor.loadFromXML();
		}

		if (tutor.isWordListNotEmpty() == Util.T) {
			tableWords.setVisible(false);
			try {
				new FrmLesson(
						this,
						tutor.getSettings().isExamMode() ? MessageFormat
								.format(Util
										.getLocalizedString("MAIN.EXAM.TITLE"),
										tutor.getCurrentDictFile())
								: MessageFormat.format(
										Util.getLocalizedString("MAIN.LESSON.TITLE"),
										tutor.getCurrentDictFile()), tutor);
				tutor.fillTableModel();
				tableWords.setVisible(true);
				tableWords.repaint();
				tutor.saveToXML();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				logger.error(e.getStackTrace());
				JOptionPane
						.showMessageDialog(
								this,
								e.getMessage(),
								Util.getLocalizedString("MAIN.LESSON.BAD.CONFIG.TITLE"),
								JOptionPane.WARNING_MESSAGE);
			}
		} else if (tutor.isWordListNotEmpty() == Util.F) {
			JOptionPane.showMessageDialog(this,
					Util.getLocalizedString("MAIN.LESSON.WARNING.MESSAGE"),
					Util.getLocalizedString("MAIN.LESSON.WARNING.TITLE"),
					JOptionPane.WARNING_MESSAGE);
		} else if (tutor.isWordListNotEmpty() == Util.E) {
			JOptionPane.showMessageDialog(this,
					Util.getLocalizedString("MAIN.LESSON.BAD.CONFIG.MESSAGE"),
					Util.getLocalizedString("MAIN.LESSON.BAD.CONFIG.TITLE"),
					JOptionPane.WARNING_MESSAGE);
		}
	}

	private void clearLearning() {
		tutor.clearLearning(false);
		tutor.saveToXML();
		tableWords.repaint();
	}

	private void searchWord() {
		TableRowSorter<WordsTableModel> sorter = new TableRowSorter<WordsTableModel>(
				tableModel);
		searchString = (String) JOptionPane.showInputDialog(this,
				Util.getLocalizedString("MAIN.SEARCH.ACTION") + "\n",
				Util.getLocalizedString("MAIN.SEARCH.TITLE"),
				JOptionPane.PLAIN_MESSAGE, null, null, "");

		RowFilter<Object, Object> bothRowsContainExpressionFilter = new RowFilter<Object, Object>() {
			public boolean include(
					Entry<? extends Object, ? extends Object> entry) {
				for (int i = entry.getValueCount() - 1; i >= 0; i--) {
					if (entry.getStringValue(i).toUpperCase()
							.indexOf(searchString.toUpperCase()) > -1) {
						return true;
					}
				}
				return false;
			}
		};
		// pokazanie filtrowanej frazy
		searchPhrase.setText(searchString);
		sorter.setRowFilter(bothRowsContainExpressionFilter);
		tableWords.setRowSorter(sorter);
		// tabela wyfiltrowanych pozycji
		tableWords.repaint();
	}

	private void infoAbout() {
		JOptionPane.showMessageDialog(
				this,
				Util.getLocalizedString("PROGRAM.VERSION") + " "
						+ Util.programVersion + Util.NEW_LINE_DELIMITER
						+ Util.getLocalizedString("PROGRAM.AUTHOR") + " "
						+ Util.authorInfo + Util.NEW_LINE_DELIMITER
						+ Util.getLocalizedString("PROGRAM.CONTACT") + " "
						+ Util.contactEmail + Util.NEW_LINE_DELIMITER
						+ Util.getLocalizedString("PROGRAM.HELPED.A.LOT") + " "
						+ Util.thanksList + Util.NEW_LINE_DELIMITER
						+ Util.NEW_LINE_DELIMITER
						+ Util.getLocalizedString("PROGRAM.MY.THANKS"),
				Util.getLocalizedString("MAIN.ABOUT.TITLE"),
				JOptionPane.INFORMATION_MESSAGE);
	}

	private void switchLesson(String lessonFileName) {
		tutor.saveToXML();
		tableWords.setVisible(false);
		String switchToLessonFile = "";
		if (StringUtils.isBlank(lessonFileName)) {
			switchToLessonFile = chooseLessonNaqFile();
		} else {
			switchToLessonFile = lessonFileName;
		}
		if (!Util.isEmpty(switchToLessonFile)) {
			tutor.setCurrentDictFile(switchToLessonFile);
			// gdy zmiana lekcji - przeladuj indekser sampli
			tutor.buildCurrentSoundSampleCoverMapIndex();
			tutor = new WordTutor(switchToLessonFile);
			tutor.loadFromXML();
			tutor.assignTableModel(tableModel);
			tutor.fillTableModel();
			setTitle(MessageFormat.format(
					Util.getLocalizedString("MAIN.TITLE"), switchToLessonFile));
		}
		tableWords.setVisible(true);
		tableWords.repaint();
	}

	public void openSettings() {
		tutor.openSettings(this);
	}

	public void importCSV() {
		new FrmImport(this, Util.getLocalizedString("FRAME.IMPORT.LABEL"),
				tutor, tableWords);
	}

	public void importXLS() {
		new FrmImportXls(this, Util.getLocalizedString("FRAME.XLS.LABEL"),
				tutor, tableWords);
	}

	public void exportXLS() {
		exportToXLS();
	}

	public void newDictionary() {
		String s = (String) JOptionPane.showInputDialog(this,
				Util.getLocalizedString("MAIN.DIALOG.ACTION") + "\n",
				Util.getLocalizedString("MAIN.DIALOG.TITLE"),
				JOptionPane.PLAIN_MESSAGE, null, null, "");
		if (Util.isEmpty(s))
			return;
		s = s + ".naq";
		/**
		 * @TODO Czy takiego pliku już nie ma? I sprawdzenie czy nazwa s jest
		 *       poprawnym plikiem
		 */
		try {
			File fl = new File(s);
			if (fl.exists()) {
				throw new Exception(MessageFormat.format(
						Util.getLocalizedString("MAIN.ERROR.FILE.EXISTS"), s));
			}
			createNewDictFile(s);
		} catch (Exception eio) {
			JOptionPane.showMessageDialog(this, eio.getMessage(),
					Util.getLocalizedString("ERROR.DIALOG.TITLE"),
					JOptionPane.ERROR_MESSAGE);
			eio.printStackTrace();
			return;
		}
		switchLesson(s);
	}

	public void addWord() {
		tutor.addNewWord();
		tutor.fillTableModel();
		tutor.saveToXML();
		tableWords.repaint();
		// uaktualnij indekser - bo moze nowy sampel doszedl
		tutor.buildCurrentSoundSampleCoverMapIndex();
	}

	public void duplicateSound() {
		try {
			tutor.duplicateAllSoundFiles();
			tutor.buildCurrentSoundSampleCoverMapIndex();			
		} catch (IOException ioe) {
			logger.error(ioe.getCause());
			JOptionPane.showMessageDialog(
					this,
					ioe.getMessage(),
					"Error duplicating sound files",
					JOptionPane.ERROR_MESSAGE, null);			
		}
	}
	
	public void editWord() {
		int index = tableWords.getSelectedRow();
		if (index > -1) {
			Integer rowId = (Integer) tableWords.getValueAt(index, 0);
			rowId--;
			tutor.editWord(rowId);
			tutor.fillTableModel();
			tutor.saveToXML();
			tableWords.repaint();
			// uaktualnij indekser - bo moze nowy sampel doszedl
			tutor.buildCurrentSoundSampleCoverMapIndex();
		} else {
			// show message
			JOptionPane
					.showMessageDialog(
							this,
							Util.getLocalizedString("MAIN.EDIT.NONE.SELECTED.WINDOW.MESSAGE")
									+ Util.NEW_LINE_DELIMITER,
							Util.getLocalizedString("MAIN.EDIT.NONE.SELECTED.WINDOW.TITLE"),
							JOptionPane.INFORMATION_MESSAGE, null);

		}
	}

	public void delWord() {
		int indexes[] = tableWords.getSelectedRows();
		ArrayList<Integer> rowIdList = new ArrayList<Integer>();
		for (int i : indexes) {
			Integer rowId = (Integer) tableWords.getValueAt(i, 0);
			rowId--;
			rowIdList.add(rowId);
		}
		Collections.sort(rowIdList);
		if (indexes.length != 0) {
			tutor.delWord(toIntArray(rowIdList));
			tutor.fillTableModel();
			tutor.saveToXML();
			tableWords.repaint();
		}
	}

	public void mergeWords() {
		int indexes[] = tableWords.getSelectedRows();
		ArrayList<Integer> rowIdList = new ArrayList<Integer>();
		for (int i : indexes) {
			Integer rowId = (Integer) tableWords.getValueAt(i, 0);
			rowId--;
			rowIdList.add(rowId);
		}
		Collections.sort(rowIdList);
		if (indexes.length == 2) {
			tutor.mergeWords(toIntArray(rowIdList));
			tutor.fillTableModel();
			tutor.saveToXML();
			tableWords.repaint();
		} else {
			// hidden function INVERT
			if (Util.TRUE.equalsIgnoreCase(Util.getAppProperty("NAQN.MODE"))) {
				tutor.exchangeWords();
				tutor.fillTableModel();
				tutor.saveToXML();
				tableWords.repaint();
			}
		}
	}

	public void actionPerformed(ActionEvent event) {
		String command = event.getActionCommand();
		if (command.equals("clearLearning")) {
			clearLearning();
		} else if (command.equals("import")) {
			importCSV();
		} else if (command.equals("xlsImport")) {
			importXLS();
		} else if (command.equals("xlsExport")) {
			exportXLS();
		} else if (command.equals("switchLesson")) {
			switchLesson(null);
		} else if (command.equals("startLesson")) {
			startLesson();
		} else if (command.equals("openSettings")) {
			openSettings();
		} else if (command.equals("search")) {
			searchWord();
		} else if (command.equals("add")) {
			addWord();
		} else if (command.equals("edit")) {
			editWord();
		} else if (command.equals("del")) {
			delWord();
		} else if (command.equals("merge")) {
			mergeWords();
		} else if (command.equals("new")) {
			newDictionary();
		} else if (command.equals("dupl")) {
			duplicateSound();
		} else if (command.equals("about")) {
			infoAbout();
		}

	}

	public void keyPressed(KeyEvent e) {
	}

	public void keyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_ENTER:
			startLesson();
		case KeyEvent.VK_F1:
			// TODO - Edit help info (localize)
			JOptionPane.showMessageDialog(this,
					"Help me to write this HELP content..." + "\n",
					"H E L P   - is just TODO... :) ",
					JOptionPane.PLAIN_MESSAGE, null);
			break;
		}
	}

	public void keyTyped(KeyEvent e) {
	}

	public String chooseLessonNaqFile() {
		String[] extensions = { "naq" };
		@SuppressWarnings("unchecked")
		Collection<File> fList = (Collection<File>) FileUtils.listFiles(
				new File(WordTutor.LESSONS_DIR), extensions, false);
		ArrayList<String> stringList = new ArrayList<String>();
		if (fList.size() == 0) {
			JOptionPane.showMessageDialog(this,
					Util.getLocalizedString("MAIN.ERROR.MESSAGE"),
					Util.getLocalizedString("ERROR.DIALOG.TITLE"),
					JOptionPane.ERROR_MESSAGE);
			this.dispose();
			System.exit(0);
		}
		for (File f : fList) {
			stringList.add(f.getName());
		}
		Collections.sort(stringList);
		String[] possibilities = stringList.toArray(new String[0]);
		String chooseLessonNaqFile = (String) JOptionPane.showInputDialog(this,
				Util.getLocalizedString("MAIN.SELECTION.ACTION") + " \n",
				Util.getLocalizedString("MAIN.SELECTION.TITLE"),
				JOptionPane.PLAIN_MESSAGE, null, possibilities,
				possibilities[0]);
		return chooseLessonNaqFile;
	}

	FrmMain(String title) throws Exception {
		super(title);
		String chooseLessonNaqFile;
		if (!StringUtils.isBlank(Util.getAppProperty("CURRENT.TEST.FILE"))) {
			chooseLessonNaqFile = Util.getAppProperty("CURRENT.TEST.FILE");
		} else {
			chooseLessonNaqFile = chooseLessonNaqFile();
		}
		if (Util.isEmpty(chooseLessonNaqFile)) {
			this.dispose();
			System.exit(0);
		}

		tutor = new WordTutor(chooseLessonNaqFile);
		tutor.loadFromXML();
		// UWAGA : moze nie najlepsze miejsce - ale na razie tu buduje indeks
		// sampli
		tutor.buildCurrentSoundSampleCoverMapIndex();
		setTitle(MessageFormat.format(Util.getLocalizedString("MAIN.TITLE"),
				chooseLessonNaqFile));
		setLocation(Util.xMainPosition, Util.yMainPosition);
		setSize(Util.xMainSize, Util.yMainSize);
		panelTools = new JPanel(new FlowLayout());
		add(panelTools, BorderLayout.NORTH);

		searchPhrase = new JLabel(searchString);
		searchPhrase.setForeground(Color.BLUE);
		panelTools.add(searchPhrase);

		buttonSearch = new JButton(Util.getLocalizedString("MAIN.SEARCH"));
		buttonSearch.setActionCommand("search");
		buttonSearch.addActionListener(this);
		buttonSearch.setToolTipText(Util.getLocalizedString("MAIN.SEARCH.TIP"));
		panelTools.add(buttonSearch);

		buttonAdd = new JButton(Util.getLocalizedString("MAIN.ADD"));
		buttonAdd.setActionCommand("add");
		buttonAdd.addActionListener(this);
		buttonAdd.setToolTipText(Util.getLocalizedString("MAIN.ADD.TIP"));
		panelTools.add(buttonAdd);

		buttonEdit = new JButton(Util.getLocalizedString("MAIN.EDIT"));
		buttonEdit.setActionCommand("edit");
		buttonEdit.addActionListener(this);
		buttonEdit.setToolTipText(Util.getLocalizedString("MAIN.EDIT.TIP"));
		panelTools.add(buttonEdit);

		buttonMerge = new JButton(Util.getLocalizedString("MAIN.MERGE"));
		buttonMerge.setActionCommand("merge");
		buttonMerge.addActionListener(this);
		buttonMerge.setToolTipText(Util.getLocalizedString("MAIN.MERGE.TIP"));
		panelTools.add(buttonMerge);

		buttonDel = new JButton(Util.getLocalizedString("MAIN.DELETE"));
		buttonDel.setActionCommand("del");
		buttonDel.addActionListener(this);
		buttonDel.setToolTipText(Util.getLocalizedString("MAIN.DELETE.TIP"));
		panelTools.add(buttonDel);

		buttonNewDict = new JButton(Util.getLocalizedString("MAIN.DICT"));
		buttonNewDict.setActionCommand("new");
		buttonNewDict.addActionListener(this);
		buttonNewDict.setToolTipText(Util.getLocalizedString("MAIN.DICT.TIP"));
		panelTools.add(buttonNewDict);

		panelWords = new JPanel();
		panelWords.setLayout(new BorderLayout());
		add(panelWords, BorderLayout.CENTER);

		panelManage = new JPanel();
		panelManage.setLayout(new FlowLayout());
		add(panelManage, BorderLayout.SOUTH);
		startLesson = new JButton(
				tutor.getSettings().isExamMode() ? Util
						.getLocalizedString("MAIN.EXAM") : Util
						.getLocalizedString("MAIN.LESSON"));
		startLesson.setActionCommand("startLesson");
		startLesson.setToolTipText(tutor.getSettings().isExamMode() ? Util
				.getLocalizedString("MAIN.EXAM.TIP") : Util
				.getLocalizedString("MAIN.LESSON.TIP"));
		panelManage.add(startLesson);
		startLesson.addActionListener(this);

		openSettings = new JButton(Util.getLocalizedString("MAIN.CONFIG"));
		openSettings.setActionCommand("openSettings");
		openSettings.setToolTipText(Util.getLocalizedString("MAIN.CONFIG.TIP"));
		panelManage.add(openSettings);
		openSettings.addActionListener(this);

		clearLearning = new JButton(Util.getLocalizedString("MAIN.CLEAR"));
		clearLearning.setActionCommand("clearLearning");
		clearLearning.setToolTipText(Util.getLocalizedString("MAIN.CLEAR.TIP"));
		panelManage.add(clearLearning);
		clearLearning.addActionListener(this);

		switchLesson = new JButton(
				Util.getLocalizedString("MAIN.SWITCH.LESSON"));
		switchLesson.setActionCommand("switchLesson");
		switchLesson.setToolTipText(Util
				.getLocalizedString("MAIN.SWITCH.LESSON.TIP"));
		panelManage.add(switchLesson);
		switchLesson.addActionListener(this);

		buttonXlsImport = new JButton(
				Util.getLocalizedString("MAIN.XLS.IMPORT"));
		buttonXlsImport.setActionCommand("xlsImport");
		buttonXlsImport.addActionListener(this);
		buttonXlsImport.setToolTipText(Util
				.getLocalizedString("MAIN.XLS.IMPORT.TIP"));
		panelManage.add(buttonXlsImport);

		buttonXlsExport = new JButton(
				Util.getLocalizedString("MAIN.XLS.EXPORT"));
		buttonXlsExport.setActionCommand("xlsExport");
		buttonXlsExport.addActionListener(this);
		buttonXlsExport.setToolTipText(Util
				.getLocalizedString("MAIN.XLS.EXPORT.TIP"));
		panelManage.add(buttonXlsExport);

		if (Util.TRUE.equalsIgnoreCase(Util.getAppProperty("NAQN.MODE"))) {
			buttonDupl = new JButton("Dupl");
			buttonDupl.setActionCommand("dupl");
			buttonDupl.addActionListener(this);
			buttonDupl.setToolTipText("Duplic");
			panelManage.add(buttonDupl);
		}

		// TODO usun jak bedzie juz kompletnie niepotrzebny import z pliku TXT
		// buttonImport = new JButton(Util.getLocalizedString("MAIN.DUP"));
		// buttonImport.setActionCommand("import");
		// buttonImport.addActionListener(this);
		// buttonImport.setToolTipText(Util.getLocalizedString("MAIN.DUP.TIP"));
		// panelManage.add(buttonImport);

		aboutLingmem = new JButton(Util.getLocalizedString("MAIN.ABOUT"));
		aboutLingmem.setActionCommand("about");
		aboutLingmem.setToolTipText(Util.getLocalizedString("MAIN.ABOUT.TIP"));
		panelManage.add(aboutLingmem);
		aboutLingmem.addActionListener(this);
		addWindowListener(this);
		createTable();
		setVisible(true);
	}

	public static void main(String args[]) {
		Logger logger = Logger.getLogger(FrmMain.class);
		// Util u = new Util();
		Util.loadAppProperties();
		Player player = null;
		int wave2mp3Result = new BatchConvert().runWave2mp3(null);
		logger.debug("Mp3 conversion is "
				+ ((wave2mp3Result == 0) ? "OK" : "NOT OK"));
		if (Util.TRUE.equalsIgnoreCase(Util.getAppProperty("PLAY.INTRO"))) {
			Util.playSound(player, Util.getAppProperty("INTRO.MP3"));
		}
		try {
			JFrame mainWindow = new FrmMain(
					Util.getLocalizedString("MAIN.FRAME.TITLE"));
			mainWindow.setDefaultCloseOperation(EXIT_ON_CLOSE);
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			ex.printStackTrace();
		}
	}

	public JButton getStartLesson() {
		return startLesson;
	}

	public void setStartLesson(JButton startLesson) {
		this.startLesson = startLesson;
	}

	public static int[] toIntArray(List<Integer> integerList) {
		int[] intArray = new int[integerList.size()];
		for (int i = 0; i < integerList.size(); i++) {
			intArray[i] = integerList.get(i);
		}
		return intArray;
	}

	public void exportToXLS() {
		try {
			Workbook wb = new HSSFWorkbook();
			Sheet sheet = wb.createSheet(tutor.getCurrentDictFile());
			// Create a row and put some cells in it. Rows are 0 based.
			Row row = sheet.createRow((short) 0);
			// Create a cell and put a value in it.
			row.createCell(0).setCellValue("");
			row.createCell(1).setCellValue("");
			row.createCell(2).setCellValue("");
			// row.createCell(0).setCellValue(Util.getLocalizedString("WTM.FOREIGN"));
			// row.createCell(1).setCellValue(Util.getLocalizedString("WTM.NATIVE"));
			int maxSplit = 1;
			for (int i = 0; i < tutor.getMaxWordCount(); i++) {
				IWord word = tutor.getWordById(i);
				row = sheet.createRow((short) (i + 1));
				row.createCell(0).setCellValue(word.getIdSound());				
				row.createCell(1).setCellValue(word.getForeignWord());
				String[] splitted = Util.improvedSplitter(word.getNativeWord(),
						Util.PHRASE_DELIMITER);
				if (splitted.length > maxSplit) {
					maxSplit = splitted.length;
				}
				for (int j = 0; j < splitted.length; j++) {
					row.createCell(j + 2).setCellValue(splitted[j]);
				}
			}
			for (int x = 0; x <= maxSplit; x++) {
				sheet.autoSizeColumn(x); // auto adjust width of the x-th column
			}
			FileOutputStream fileOut = new FileOutputStream(
					tutor.getCurrentDictFile() + Util.XLS_EXTENTION);
			wb.write(fileOut);
			fileOut.close();
			JOptionPane.showMessageDialog(this, MessageFormat.format(
					Util.getLocalizedString("MAIN.XLS.EXPORT.FRAME.MESSAGE"),
					tutor.getCurrentDictFile(), System.getProperty("user.dir")
							+ File.separator + tutor.getCurrentDictFile()
							+ Util.XLS_EXTENTION), Util
					.getLocalizedString("MAIN.XLS.EXPORT.FRAME.NAME"),
					JOptionPane.PLAIN_MESSAGE, null);

		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, e.getMessage(),
					Util.getLocalizedString("ERROR.DIALOG.TITLE"),
					JOptionPane.ERROR_MESSAGE);
			logger.error(e.getMessage(), e);
			return;
		}
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowClosed(WindowEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		logger.debug("Closing.");
		Util.storeAppProperties();
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

}