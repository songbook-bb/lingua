package wordtutor.settings;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

import wordtutor.FrmMain;
import wordtutor.util.DirectionMode;
import wordtutor.util.keybuttons.KeyboardButtonType;
import wordtutor.utils.Util;

public class FrmSettings extends JDialog implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2479995298621194872L;
	Logger logger = Logger.getLogger(FrmSettings.class);
	private FrmMain parentFrame;
	private Settings settings;
	private JButton buttonOk;
	private JButton buttonCancel;
	private JPanel panelSettings;
	private JPanel panelManage;
	private JPanel panelDirection;
	private JPanel panelKeyboards;
	private JPanel panelRadioGroups;
	private JLabel labAutoSuggest;
	private JTextField textAutoSuggest;
	private JLabel labWordsInLesson;
	private JTextField textWordsInLesson;
	private JLabel labIncScore;
	private JTextField textIncScore;
	private JCheckBox cbExamMode;
	private JCheckBox cbStrictMode;
	private JCheckBox cbUnlearned;
	private JCheckBox cbHardest;
	private JCheckBox cbRandom;
	private JCheckBox cbNormalized;
	private JCheckBox cbSpelling;	
	private JLabel labDirection;
	private JRadioButton rbStraight;
	private JRadioButton rbReverse;
	private JRadioButton rbBoth;
	private JLabel labKeyboardButton;
	private JRadioButton rbPolish;
	private JRadioButton rbSpanish;
	private JRadioButton rbGerman;
	private JRadioButton rbKashubian;
	private JRadioButton rbItalian;
	private JRadioButton rbFrench;
	private JRadioButton rbCroatian;
	private JRadioButton rbLithuanian;
	private JRadioButton rbNorwegian;
	// ADD NEW KEYBOARD button HERE
	private boolean isEnabled = true;

	private void Save() throws WrongNumberException {
		int incScore = 0;
		int wordsInLesson = 0;
		int autoSuggest = 0;
		try {
			wordsInLesson = new Integer(textWordsInLesson.getText());
			incScore = new Integer(textIncScore.getText());
			autoSuggest = new Integer(textAutoSuggest.getText());
		} catch (Exception e) {
			logger.debug(e.getMessage(), e);
			throw new WrongNumberException(
					Util.getLocalizedString("SETTINGS.EXCEPTION.INVALID"));
		}
		if (wordsInLesson < 1) {
			throw new WrongNumberException(
					Util.getLocalizedString("SETTINGS.EXCEPTION.NUMBER"));
		}
		if (incScore < 1) {
			throw new WrongNumberException(
					Util.getLocalizedString("SETTINGS.EXCEPTION.FIRST"));
		}

		settings.setIncScore(incScore);
		settings.setWordsInLesson(wordsInLesson);
		settings.setAutoSuggest(autoSuggest);

		if (cbExamMode.isSelected()) {
			settings.setExamMode(true);
		} else {
			settings.setExamMode(false);
		}
		if (cbStrictMode.isSelected()) {
			settings.setStrictMode(true);
		} else {
			settings.setStrictMode(false);
		}
		if (cbUnlearned.isSelected()) {
			settings.setUnlearned(true);
		} else {
			settings.setUnlearned(false);
		}
		if (cbHardest.isSelected()) {
			settings.setHardest(true);
		} else {
			settings.setHardest(false);
		}
		if (cbRandom.isSelected()) {
			settings.setRandom(true);
		} else {
			settings.setRandom(false);
		}
		if (cbNormalized.isSelected()) {
			settings.setNormalized(true);
		} else {
			settings.setNormalized(false);
		}		
		if (cbSpelling.isSelected()) {
			settings.setSpelling(true);
		} else {
			settings.setSpelling(false);
		}		
		parentFrame.getStartLesson().setText(
				settings.isExamMode() ? Util.getLocalizedString("MAIN.EXAM")
						: Util.getLocalizedString("MAIN.LESSON"));
		parentFrame.getStartLesson().setToolTipText(
				settings.isExamMode() ? Util
						.getLocalizedString("MAIN.EXAM.TIP") : Util
						.getLocalizedString("MAIN.LESSON.TIP"));

		if (rbStraight.isSelected()) {
			settings.setDirectionMode(DirectionMode.STRAIGHT);
		} else if (rbReverse.isSelected()) {
			settings.setDirectionMode(DirectionMode.REVERSE);
		} else if (rbBoth.isSelected()) {
			settings.setDirectionMode(DirectionMode.BOTH);
		}

		if (rbSpanish.isSelected()) {
			settings.setKeyboardType(KeyboardButtonType.SPANISH);
		} else if (rbGerman.isSelected()) {
			settings.setKeyboardType(KeyboardButtonType.GERMAN);
		} else if (rbKashubian.isSelected()) {
			settings.setKeyboardType(KeyboardButtonType.KASHUBIAN);
		} else if (rbPolish.isSelected()) {
			settings.setKeyboardType(KeyboardButtonType.POLISH);
		} else if (rbItalian.isSelected()) {
			settings.setKeyboardType(KeyboardButtonType.ITALIAN);
		} else if (rbFrench.isSelected()) {
			settings.setKeyboardType(KeyboardButtonType.FRENCH);
		} else if (rbCroatian.isSelected()) {
			settings.setKeyboardType(KeyboardButtonType.CROATIAN);
		} else if (rbLithuanian.isSelected()) {
			settings.setKeyboardType(KeyboardButtonType.LITHUANIAN);
		} else if (rbNorwegian.isSelected()) {
			settings.setKeyboardType(KeyboardButtonType.NORWEGIAN);
		}
		// ADD NEW KEYBOARD selection HERE
		settings.saveToXML();

	}

	public void actionPerformed(ActionEvent event) {
		// if user pressed OK, we must save the result
		boolean success = true;
		if (event.getActionCommand().equals("ok")) {
			try {
				Save();
			} catch (WrongNumberException e) {
				JOptionPane.showMessageDialog(null, e.getMessage(),
						Util.getLocalizedString("ERROR.DIALOG.TITLE"),
						JOptionPane.ERROR_MESSAGE);
				success = false;
			}
		}
		if (success) {
			this.dispose();
		}
	}

	public FrmSettings(FrmMain parent, String title, Settings settings, boolean enableSpelling) {
		super(parent, title);
		setLocation(Util.xConfigPosition, Util.yConfigPosition);
		setSize(Util.xConfigSize, Util.yConfigSize);
		this.parentFrame = parent;
		this.settings = settings;
		panelSettings = new JPanel();
		add(panelSettings, BorderLayout.NORTH);
		panelSettings.setLayout(new GridLayout(8, 2));

		labWordsInLesson = new JLabel(Util.getLocalizedString("SETTINGS.LABEL.WORDS"));
		panelSettings.add(labWordsInLesson);

		textWordsInLesson = new JTextField(new Integer(settings.getWordsInLesson()).toString());
		panelSettings.add(textWordsInLesson);

		labIncScore = new JLabel(Util.getLocalizedString("SETTINGS.LABEL.START"));
		panelSettings.add(labIncScore);
		textIncScore = new JTextField(new Integer(settings.getIncScore()).toString());
		panelSettings.add(textIncScore);

		labAutoSuggest = new JLabel(Util.getLocalizedString("SETTINGS.LABEL.AUTO.SUGGEST"));
		panelSettings.add(labAutoSuggest);
		textAutoSuggest = new JTextField(new Integer(settings.getAutoSuggest()).toString());
		panelSettings.add(textAutoSuggest);

		// na dzień dobry ma pokazać czy ukryć
		isEnabled = !(settings.isHardest() || settings.isRandom());
		textIncScore.setEnabled(isEnabled);

		cbExamMode = new JCheckBox(Util.getLocalizedString("SETTINGS.CHECKBOX.EXAM"));
		cbExamMode.setSelected(settings.isExamMode());
		cbExamMode.setToolTipText(Util.getLocalizedString("MAIN.EXAM.TIP"));
		panelSettings.add(cbExamMode);
		// verify whole phrase or just one out of separated by delimiter
		cbStrictMode = new JCheckBox(
				Util.getLocalizedString("SETTINGS.CHECKBOX.STRICT"));
		cbStrictMode.setSelected(settings.isStrictMode());
		cbStrictMode.setToolTipText(Util
				.getLocalizedString("SETTINGS.CHECKBOX.STRICT.TIP"));
		panelSettings.add(cbStrictMode);

		cbHardest = new JCheckBox(
				Util.getLocalizedString("SETTINGS.CHECKBOX.HARDEST"));
		cbHardest.setSelected(settings.isHardest());
		cbHardest.setToolTipText(Util
				.getLocalizedString("SETTINGS.CHECKBOX.HARDEST.TIP"));
		panelSettings.add(cbHardest);

		CheckBoxListener checkBoxListener = new CheckBoxListener();
		cbHardest.addItemListener(checkBoxListener);

		cbRandom = new JCheckBox(
				Util.getLocalizedString("SETTINGS.CHECKBOX.RANDOM"));
		cbRandom.setSelected(settings.isRandom());
		cbRandom.setToolTipText(Util
				.getLocalizedString("SETTINGS.CHECKBOX.RANDOM.TIP"));
		panelSettings.add(cbRandom);
		cbRandom.addItemListener(checkBoxListener);

		cbUnlearned = new JCheckBox(
				Util.getLocalizedString("SETTINGS.CHECKBOX.TAKE.UNFLAGGED"));
		cbUnlearned.setSelected(settings.isUnlearned());
		cbUnlearned.setToolTipText(Util
				.getLocalizedString("SETTINGS.CHECKBOX.TAKE.UNFLAGGED.TIP"));
		panelSettings.add(cbUnlearned);

		cbNormalized =  new JCheckBox(
				Util.getLocalizedString("SETTINGS.CHECKBOX.NORMALIZED"));
		cbNormalized.setSelected(settings.isNormalized());
		cbNormalized.setToolTipText(Util
				.getLocalizedString("SETTINGS.CHECKBOX.NORMALIZED.TIP"));
		panelSettings.add(cbNormalized);
		
		cbSpelling =  new JCheckBox(
				Util.getLocalizedString("SETTINGS.CHECKBOX.SPELLING"));
		cbSpelling.setSelected(settings.isSpelling());
		cbSpelling.setToolTipText(Util
				.getLocalizedString("SETTINGS.CHECKBOX.SPELLING.TIP"));
		cbSpelling.setEnabled(enableSpelling);
		panelSettings.add(cbSpelling);		

		// Translation Direction Group
		panelDirection = new JPanel();
		panelDirection
				.setLayout(new BoxLayout(panelDirection, BoxLayout.Y_AXIS));
		labDirection = new JLabel(
				Util.getLocalizedString("SETTINGS.LABEL.DIRECTION"));
		panelDirection.add(labDirection);
		rbStraight = new JRadioButton(
				Util.getLocalizedString("SETTINGS.RADIO.STRAIGHT"));
		panelDirection.add(rbStraight);
		rbReverse = new JRadioButton(
				Util.getLocalizedString("SETTINGS.RADIO.REVERSE"));
		panelDirection.add(rbReverse);
		rbBoth = new JRadioButton(
				Util.getLocalizedString("SETTINGS.RADIO.BOTH"));
		panelDirection.add(rbBoth);

		ButtonGroup bgDirection = new ButtonGroup();
		bgDirection.add(rbStraight);
		bgDirection.add(rbReverse);
		bgDirection.add(rbBoth);
		switch (settings.getDirectionMode()) {
		case STRAIGHT:
			rbStraight.setSelected(true);
			break;
		case REVERSE:
			rbReverse.setSelected(true);
			break;
		case BOTH:
			rbBoth.setSelected(true);
			break;
		}

		// Additional keyboard Group
		panelKeyboards = new JPanel();
		panelKeyboards.setLayout(new BoxLayout(panelKeyboards, BoxLayout.Y_AXIS));
		labKeyboardButton = new JLabel(
				Util.getLocalizedString("SETTINGS.LABEL.KEYBOARDS"));
		panelKeyboards.add(labKeyboardButton);
		rbSpanish = new JRadioButton(
				Util.getLocalizedString("SETTINGS.RADIO.SPANISH"));
		panelKeyboards.add(rbSpanish);
		rbGerman = new JRadioButton(
				Util.getLocalizedString("SETTINGS.RADIO.GERMAN"));
		panelKeyboards.add(rbGerman);
		rbKashubian = new JRadioButton(
				Util.getLocalizedString("SETTINGS.RADIO.KASHUBIAN"));
		panelKeyboards.add(rbKashubian);
		rbPolish = new JRadioButton(
				Util.getLocalizedString("SETTINGS.RADIO.POLISH"));
		panelKeyboards.add(rbPolish);
		rbItalian = new JRadioButton(
				Util.getLocalizedString("SETTINGS.RADIO.ITALIAN"));
		panelKeyboards.add(rbItalian);
		rbFrench = new JRadioButton(
				Util.getLocalizedString("SETTINGS.RADIO.FRENCH"));
		panelKeyboards.add(rbFrench);
		rbCroatian = new JRadioButton(
				Util.getLocalizedString("SETTINGS.RADIO.CROATIAN"));
		panelKeyboards.add(rbCroatian);
		rbLithuanian = new JRadioButton(
				Util.getLocalizedString("SETTINGS.RADIO.LITHUANIAN"));
		panelKeyboards.add(rbLithuanian);
		rbNorwegian = new JRadioButton(
				Util.getLocalizedString("SETTINGS.RADIO.NORWEGIAN"));
		panelKeyboards.add(rbNorwegian);
		// ADD NEW KEYBOARD init radio button HERE
		ButtonGroup bgKeyboards = new ButtonGroup();
		bgKeyboards.add(rbSpanish);
		bgKeyboards.add(rbGerman);
		bgKeyboards.add(rbPolish);
		bgKeyboards.add(rbKashubian);
		bgKeyboards.add(rbItalian);
		bgKeyboards.add(rbFrench);
		bgKeyboards.add(rbCroatian);
		bgKeyboards.add(rbLithuanian);
		bgKeyboards.add(rbNorwegian);
		// ADD NEW KEYBOARD radio button HERE
		switch (settings.getKeyboardType()) {
		case SPANISH:
			rbSpanish.setSelected(true);
			break;
		case GERMAN:
			rbGerman.setSelected(true);
			break;
		case KASHUBIAN:
			rbKashubian.setSelected(true);
			break;
		case POLISH:
			rbPolish.setSelected(true);
			break;
		case ITALIAN:
			rbItalian.setSelected(true);
			break;
		case FRENCH:
			rbFrench.setSelected(true);
			break;
		case CROATIAN:
			rbCroatian.setSelected(true);
			break;
		case LITHUANIAN:
			rbLithuanian.setSelected(true);
			break;
		case NORWEGIAN:
			rbNorwegian.setSelected(true);
			break;
		// ADD NEW KEYBOARD setSelect HERE
		}

		panelRadioGroups = new JPanel(new GridLayout(1, 2, 5, 5));
		add(panelRadioGroups, BorderLayout.CENTER);

		panelRadioGroups.add(panelDirection);
		panelRadioGroups.add(panelKeyboards);

		panelManage = new JPanel(new FlowLayout());
		add(panelManage, BorderLayout.SOUTH);
		buttonOk = new JButton(Util.getLocalizedString("BUTTON.OK"));
		buttonOk.setActionCommand("ok");
		buttonOk.addActionListener(this);
		panelManage.add(buttonOk);

		buttonCancel = new JButton(Util.getLocalizedString("BUTTON.CANCEL"));
		buttonCancel.setActionCommand("cancel");
		buttonCancel.addActionListener(this);
		panelManage.add(buttonCancel);

		setVisible(true);

		// new JComboBox(Mood.values());
		// JComboBox<Mood> comboBox = new JComboBox<>();
		// comboBox.setModel(new DefaultComboBoxModel<>(Mood.values()));
	}

	class CheckBoxListener implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			// logger.debug("Item "+e.getID()+
			// " "+e.getItem()+" "+e.getSource());
			JCheckBox c = (JCheckBox) (e.getSource());
			// logger.debug("Item "+c.getText());
			if (c.isSelected()
					&& Util.getLocalizedString("SETTINGS.CHECKBOX.RANDOM")
							.equals(c.getText())) {
				cbHardest.setSelected(false);
			} else if (c.isSelected()
					&& Util.getLocalizedString("SETTINGS.CHECKBOX.HARDEST")
							.equals(c.getText())) {
				cbRandom.setSelected(false);
			}
			isEnabled = !(cbHardest.isSelected() || cbRandom.isSelected());
			textIncScore.setEnabled(isEnabled);
		}
	}

}
