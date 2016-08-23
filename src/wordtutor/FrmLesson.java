package wordtutor;

import static org.junit.Assert.assertEquals;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.MessageFormat;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.media.Player;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.Caret;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import wordtutor.settings.Settings;
import wordtutor.util.TranslateDirection;
import wordtutor.util.keybuttons.KeyboardBtnListener;
import wordtutor.util.keybuttons.KeyboardCtrlPanel;
import wordtutor.utils.BatchConvert;
import wordtutor.utils.Util;

/**
 * represents the mode of the action button - show the next question or answer
 * the current one
 * 
 */
enum ActMode {
	ANSWER, NEXT
}

/**
 * represents the lesson
 * 
 */

public class FrmLesson extends JDialog implements ActionListener, KeyListener {
	private static final long serialVersionUID = 1278330304433235395L;
	public static String expandedAllRightAnswers = "";
	public static int defaultTextareaRows = 4;
	private static boolean CTRL_IS_PRESSED = false;
	public static final Color DEFAULT_SCROLL_COLOR = new Color(238, 238, 238);
	public static final Color BROWSE_QUESTION_SCROLL_COLOR = Color.BLUE;
	public static final Color BROWSE_RESPONSE_SCROLL_COLOR = Color.RED;
	public static final Color BUTTON_YET_ANOTHER_ANSWER_COLOR = Color.GREEN;
	private static ArrayList<ArrayList<String>> wholeAnswerList = new ArrayList<ArrayList<String>>();
	private static Logger LOG = Logger.getLogger(FrmLesson.class);

	/**
	 * mode of the action button
	 */
	private ActMode mode;
	/**
	 * text field with the answer, given by user
	 */
	public JTextField answerWord;
	/**
	 * text area with the question
	 */
	private JTextArea questionWord;
	private JTextArea responseWord;
	private JScrollPane responseScroll;
	private JScrollPane questionScroll;

	/**
	 * mp3 player tool
	 */
	Player player;
	JPanel panel;
	JButton answer;
	JButton spelling;	
	JButton addYetAnotherAnswer;
	JPanel centralPanel;
	private static Settings settings = new Settings();
	/**
	 * refers to WordTutor instance
	 */
	WordTutor tutor;

	/**
	 * 
	 * changes the mode of the action button
	 */
	private void SetMode(ActMode mode) {
		this.mode = mode;
		if (mode == ActMode.ANSWER) {
			answer.setText(Util.getLocalizedString("LESSON.BUTTON.ANSWER"));
		} else {
			answer.setText(Util.getLocalizedString("LESSON.BUTTON.NEXT"));
		}
	}

	public void spelling() {
		Player player = null;
		String complexFileName = tutor.chooseSoundSampleFileName();
		LOG.debug("complexFileName:" + complexFileName);
		tutor.setDirection(TranslateDirection.REVERSE);		
		if (tutor.getIdSound() > 0
				&& !Util.NO_SOUND_SAMPLE.equals(complexFileName)) {
			Util.playSound(player, complexFileName);
		}
	}
	
	/**
	 * it is called when the button was pressed
	 */
	public void button() {
		Player player = null;
		answerWord.setEditable(true);
		addYetAnotherAnswer.setVisible(false);
		questionScroll.getVerticalScrollBar().setBackground(
				DEFAULT_SCROLL_COLOR);
		responseScroll.getVerticalScrollBar().setBackground(
				DEFAULT_SCROLL_COLOR);
		if (mode == ActMode.ANSWER)// if the mode is answer
		{
			if (tutor.getSettings().isSpelling()) {
				spelling.setEnabled(false);
			}	
			String complexFileName = tutor.chooseSoundSampleFileName();
			LOG.debug("complexFileName:" + complexFileName);
			// String complexFileName = tutor.getCurrentDictFile().substring(0,
			// tutor.getCurrentDictFile().indexOf(".")) + File.separator +
			// tutor.getIdSound() + Util.getAppProperty("MP3.EXTENSION");
			answerWord.setEditable(false);
			if (isGoodAnswer(tutor.getRightAnswer(), answerWord.getText())) {
				// JUST SETTING LEARNED FLAG ONLY
				tutor.checkAnswer(answerWord.getText());
				// w trybie lekcji (a nie egzaminu) informuj, że było OK i puść
				// wymowę
				if (!settings.isExamMode()) {
					if (tutor.getIdSound() > 0
							&& !Util.NO_SOUND_SAMPLE.equals(complexFileName)) {
						if (!tutor.getSettings().isSpelling()) {
							Util.playSound(player, complexFileName);
						}
					} else {
						if (!tutor.getSettings().isSpelling()) {
							Util.playSound(player,Util.getAppProperty("CORRECT.MP3"));
						}
					}
					responseWord.setForeground(Color.BLUE);
					responseWord.setText(Util
							.getLocalizedString("LESSON.ALL.RIGHT"));
					String fullAnswerText = expandAll(tutor.getRightAnswer(),
							Util.REMOVE_COMMENT);
					answerWord.setText(fullAnswerText.replaceAll(
							Util.PHRASE_DELIMITER, Util.PHRASE_DELIMITER
									+ Util.SPACE));
					Caret answerWordCaret = answerWord.getCaret();
					answerWordCaret.setDot(0);
					answerWord.setCaret(answerWordCaret);
				}
				tutor.proceedWord(); // proceeding the word in the tutor
			} else {
				// increase the counter of committed mistakes
				tutor.incScore();
				//
				if (!settings.isExamMode()) {
					if (tutor.getIdSound() > 0
							&& !Util.NO_SOUND_SAMPLE.equals(complexFileName)) {
						
						if (!tutor.getSettings().isSpelling()) {
							Util.playSound(player, complexFileName);
						}
					}
					// shows all good answers
					responseWord.setForeground(Color.RED);
					// responseWord.setText(expandedAllRightAnswers);
					fillResponseAreaAndMarkScrollBrowse(expandedAllRightAnswers);
					responseWord.setCaretPosition(0);
					// IF CORRECTOR MODE
					if (Util.TRUE.equalsIgnoreCase(Util
							.getAppProperty("CORRECTION.MODE"))) {
						// only if the answer string is not empty
						if (!StringUtils.isBlank(answerWord.getText())) {
							addYetAnotherAnswer.setVisible(true);
						}
					}

				} else {
					// gdy tryb egzaminu - remove this word not to ask again
					tutor.proceedWord();
				}
			}
			// SelectAnswer();
			SetMode(ActMode.NEXT); // setting the mode

			if (tutor.isLessonEmpty()) {
				/**
				 * Zrobic okno statystyk dla lekcji i dla egzaminu
				 * 
				 */
				if (!settings.isExamMode()) {
					JOptionPane
							.showMessageDialog(
									this,
									MessageFormat.format(
											Util.getLocalizedString("LESSON.STAT.LESSON.FRAME.MESSAGE1"),
											settings.getWordsInLesson())
											+ "\n"
											+ MessageFormat.format(
													Util.getLocalizedString("LESSON.STAT.LESSON.FRAME.MESSAGE2"),
													tutor.calcLessonStatistics(),
													tutor.getCurrentDictFile()),
									Util.getLocalizedString("LESSON.STAT.LESSON.FRAME.NAME"),
									JOptionPane.PLAIN_MESSAGE, null);
				} else {
					JOptionPane
							.showMessageDialog(
									this,
									MessageFormat.format(
											Util.getLocalizedString("LESSON.STAT.EXAM.FRAME.MESSAGE"),
											tutor.calcExamStatistics()),
									Util.getLocalizedString("LESSON.STAT.EXAM.FRAME.NAME"),
									JOptionPane.PLAIN_MESSAGE, null);
				}
				/** TODO poprawić nakładanie się odtwarzania */
				// utl.playSound(player, new
				// Util().getAppProperty("CONGRATULATION.MP3"));
				// if the lesson is over, just close the window
				this.dispose();
			}
		} else if (mode == ActMode.NEXT) // if the mode is Next
		{
			// choose the word
			tutor.chooseWord(); 
			// play for spelling
			playForSpelling();
			// fills question TextArea and marks scroll
			fillQuestionAreaAndMarkScrollBrowse(tutor.getQuestion());
			questionWord.setCaretPosition(0);
			answerWord.setText("");
			responseWord.setText("");
			SetMode(ActMode.ANSWER); // set another mode
		}
		answerWord.requestFocusInWindow();
	}

	public void playForSpelling() {
		if (tutor.getSettings().isSpelling()) {
			spelling.setEnabled(true);
			String complexFileName = tutor.chooseSoundSampleFileName();
			Util.playSound(player, complexFileName);				
		}			
	}
	
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_CONTROL:
			CTRL_IS_PRESSED = true;
			break;
		default:
			break;
		}
	}

	/**
	 * handles keyboard pressing
	 */
	public void keyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_ENTER:
			button();
			break;
		case KeyEvent.VK_ESCAPE:
			this.dispose();
			break;
		case KeyEvent.VK_CONTROL:
			CTRL_IS_PRESSED = false;
			break;
		case KeyEvent.VK_SPACE:			
			if (tutor.getSettings().isSpelling()) {
				if (mode == ActMode.ANSWER) {
					spelling();				
				}
			} 
			break;			
		default:
			// nie podpowiadamy na egzaminie :)
			// warunek > 0 bo ma WCALE nie podpowiadac gdy klepie pusty string
			if (!settings.isExamMode()
					&& settings.getAutoSuggest() > 0
					&& answerWord.getText().length() >= settings
							.getAutoSuggest()) {
				// logger.debug(" "+answerWord.getText()+" "+answerWord.getText().length()+" auto : "+canAutoSuggest(tutor.getRightAnswer(),
				// answerWord.getText()));
				// Cursor cursor = answerWord.getCursor();
				canAutoSuggest(tutor.getRightAnswer(), answerWord);
				// answerWord.setText(canAutoSuggest(tutor.getRightAnswer(),
				// answerWord.getText()));
				// answerWord.setCursor(cursor);
			}
			break;
		}
	}

	public void keyTyped(KeyEvent e) {
	}

	/**
	 * action listener
	 */
	public void actionPerformed(ActionEvent event) {
		String commandString = event.getActionCommand();
		if (commandString.equals("yetAnotherAnswer")) {
			addYetAnotherAnswer();
		}
		if (commandString.equals("spelling")) {
			spelling();
		} else {
			button();			
		}
	}

	public void addYetAnotherAnswer() {
		tutor.addYetAnotherAnswer(answerWord.getText(), CTRL_IS_PRESSED);
	}

	/**
	 * default constructor. places the components to the form and starts the
	 * lesson
	 * 
	 */
	FrmLesson(JFrame parent, String title, WordTutor tutor) throws Exception {
		super(parent, title, true);
		settings.loadFromXML();
		int wave2mp3Result = new BatchConvert().runWave2mp3(null);
		LOG.debug("wave2mp3Result= " + wave2mp3Result);
		this.tutor = tutor;
		mode = ActMode.ANSWER;
		setLocation(Util.xLessonPosition, Util.yLessonPosition);
		setSize(Util.xLessonSize, Util.yLessonSize);
		answerWord = new JTextField("");
		Font font = new Font(answerWord.getFont().getName(), Font.PLAIN, 16);
		answerWord.setFont(font);
		// SelectAnswer();
		centralPanel = new JPanel();
		centralPanel.setLayout(new BorderLayout());
		centralPanel.setVisible(true);
		centralPanel.add(answerWord, BorderLayout.CENTER);
		addYetAnotherAnswer = new JButton(
				Util.getLocalizedString("LESSON.BUTTON.ADD.YET.ANOTHER.ANSWER"));
		addYetAnotherAnswer.setActionCommand("yetAnotherAnswer");
		addYetAnotherAnswer
				.setToolTipText(Util
						.getLocalizedString("LESSON.BUTTON.ADD.YET.ANOTHER.ANSWER.TIP"));
		addYetAnotherAnswer.addActionListener(this);
		addYetAnotherAnswer.setBackground(BUTTON_YET_ANOTHER_ANSWER_COLOR);
		centralPanel.add(addYetAnotherAnswer, BorderLayout.EAST);
		addYetAnotherAnswer.setVisible(false);
		add(centralPanel, BorderLayout.CENTER);
		// panel z dodatkową klawiaturą na buttonach
		KeyboardCtrlPanel kb = new KeyboardCtrlPanel(settings.getKeyboardType());
		KeyboardBtnListener buttonListener = new KeyboardBtnListener(
				this.answerWord);
		kb.addListenerToAll(buttonListener);
		add(kb.getPanel(), BorderLayout.NORTH);
		panel = new JPanel();
		panel.setLayout(new BorderLayout());
		add(panel, BorderLayout.SOUTH);
		questionWord = new JTextArea(
				Util.getLocalizedString("LESSON.BUTTON.QUESTION"));
		font = new Font(questionWord.getFont().getName(), Font.PLAIN, 16);
		questionWord.setFont(font);
		questionWord.setEditable(false);
		questionWord.setFocusable(false);
		questionWord.setRows(defaultTextareaRows);
		questionScroll = new JScrollPane(questionWord,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		// in case of spelling - do not show question at all
		if (!tutor.getSettings().isSpelling()) {
			panel.add(questionScroll, BorderLayout.NORTH);
		} else {
			spelling = new JButton(Util.getLocalizedString("LESSON.BUTTON.SPELLING"));
			spelling.setToolTipText(Util.getLocalizedString("LESSON.BUTTON.SPELLING.TIP"));
			spelling.addActionListener(this);
			spelling.setActionCommand("spelling");
			spelling.setFocusable(false);
			panel.add(spelling, BorderLayout.NORTH);			
		}
		answer = new JButton(Util.getLocalizedString("LESSON.BUTTON.ANSWER"));
		panel.add(answer, BorderLayout.SOUTH);
		answer.addActionListener(this);
		answer.setFocusable(false);
		responseWord = new JTextArea("");
		responseWord.setRows(defaultTextareaRows);
		responseWord.setEditable(false);
		responseWord.setFocusable(false);
		// JScrollPane
		responseScroll = new JScrollPane(responseWord,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		font = new Font(responseWord.getFont().getName(), Font.PLAIN, 16);
		responseWord.setFont(font);
		panel.add(responseScroll, BorderLayout.CENTER);
		this.tutor.startLesson();
		this.tutor.chooseWord();
		playForSpelling();
		// questionWord.setText(produceExpandedInput(tutor.getQuestion()));
		questionScroll.getVerticalScrollBar().setBackground(
				DEFAULT_SCROLL_COLOR);
		fillQuestionAreaAndMarkScrollBrowse(tutor.getQuestion());
		questionWord.setCaretPosition(0);
		answerWord.addKeyListener(this);
		answerWord.setCaretPosition(0);
		setVisible(true);
		answerWord.setFocusable(true);
		answerWord.requestFocus();
	}

	/**
	 * Check if the userAnswer is in rightAnswers (also those expandable)
	 * 
	 * @param rightAnswer
	 * @param userAnswer
	 * @return
	 */
	public static boolean isGoodAnswer(String rightAnswer, String userAnswer) {
		Logger logger = Logger.getLogger(FrmLesson.class);
		// RIGHT ANSWER TRIMMING
		rightAnswer = expandAll(rightAnswer, Util.REMOVE_COMMENT);
		expandedAllRightAnswers = rightAnswer.replace(Util.PHRASE_DELIMITER,
				Util.NEW_LINE_DELIMITER);
		logger.debug("rightAnswer : " + rightAnswer);
		// split and put into an array
		if (settings.isNormalized()) {
			rightAnswer = normalize(rightAnswer);
			userAnswer = normalize(userAnswer);
		}
		rightAnswer = rightAnswer.toUpperCase();
		List<String> rightAnswerList = new ArrayList<String>(
				Arrays.asList(Util.improvedSplitter(rightAnswer,
						Util.PHRASE_DELIMITER)));
		// remove punctuation
		rightAnswerList = Util.removePunctuation(rightAnswerList);
		// then sort
		Collections.sort(rightAnswerList);
		// USER ANSWER TRIMMING (comments are remove before matching)
		userAnswer = Util.removeUnwantedDelimiters(userAnswer,
				Util.REMOVE_COMMENT);
		userAnswer = Util.removeWhiteSpaces(userAnswer);
		userAnswer = userAnswer.toUpperCase();
		List<String> userAnswerList = new ArrayList<String>(
				Arrays.asList(Util.improvedSplitter(userAnswer,
						Util.PHRASE_DELIMITER)));
		// remove punctuation
		userAnswerList = Util.removePunctuation(userAnswerList);
		// then sort
		Collections.sort(userAnswerList);
		if (settings.isStrictMode()) {
			// ideally this userAnswerList and rightAnswerList should be the
			// same
			return rightAnswerList.equals(userAnswerList);
		} else {
			// there should be at least one answer
			if (userAnswerList.size() == 0) {
				return false;
			}
			// all of userAnswers should be in included in rightAnswerList
			// however rightAnswerList may be longer
			for (String oneOfAnswers : userAnswerList) {
				if (!rightAnswerList.contains(oneOfAnswers)) {
					return false;
				}
			}
			return true;
		}
	}

	/**
	 * Produce autocomplete = right matched answer string
	 * 
	 * sets component Text on end if matched
	 */
	public void canAutoSuggest(String rightAnswer, JTextField shortAnswer) {
		rightAnswer = expandAll(rightAnswer, Util.REMOVE_COMMENT);
		ArrayList<String> rightAnswerList = new ArrayList<String>(
				Arrays.asList(Util.improvedSplitter(rightAnswer,
						Util.PHRASE_DELIMITER)));
		for (String matchingAnswer : rightAnswerList) {
			if (matchingAnswer.toUpperCase().startsWith(
					shortAnswer.getText().toUpperCase())) {
				shortAnswer.setText(matchingAnswer);
				shortAnswer.setEditable(false);
			}
		}
		// return shortAnswer;
	}

	/**
	 * Calculates cartesianProduct
	 * 
	 * WARN: outside the method static arrayList wholeAnswerList is needed
	 * 
	 * @param objectList
	 * @param level
	 * @return
	 */
	public static ArrayList<ArrayList<String>> cartesianProduct(
			ArrayList<ArrayList<String>> objectList, Integer level) {
		if (level == 0) {
			ArrayList<String> firstList = objectList.get(level);
			for (int i = 0; i < firstList.size(); i++) {
				ArrayList<String> arrayItem = new ArrayList<String>();
				arrayItem.add(firstList.get(i));
				wholeAnswerList.add(arrayItem);
			}
			level = level + 1;
			cartesianProduct(objectList, level);
		} else if (level == objectList.size()) {
			return wholeAnswerList;
		} else if (level > 0) {
			ArrayList<ArrayList<String>> newResultList = new ArrayList<ArrayList<String>>();
			ArrayList<String> levelList = objectList.get(level);
			for (int i = 0; i < wholeAnswerList.size(); i++) {
				for (int j = 0; j < levelList.size(); j++) {
					ArrayList<String> arrayItem = duplicateArray(wholeAnswerList
							.get(i));
					arrayItem.add(levelList.get(j));
					newResultList.add(arrayItem);
				}
			}
			wholeAnswerList = newResultList;
			level = level + 1;
			cartesianProduct(objectList, level);
		}
		return wholeAnswerList;
	}

	/**
	 * Duplicates an array in another memory area
	 * 
	 * @param dArrayList
	 * @return
	 */
	public static ArrayList<String> duplicateArray(ArrayList<String> dArrayList) {
		ArrayList<String> newMemLocationArray = new ArrayList<String>();
		for (int i = 0; i < dArrayList.size(); i++) {
			newMemLocationArray.add(new String(dArrayList.get(i)));
		}
		return newMemLocationArray;
	}

	/**
	 * Expand all wildcard expressions
	 * 
	 * @param all
	 *            Expanded
	 * @return
	 */
	public static String expandAll(String rightAnswerText, boolean removeComment) {
		String allExpanded = "";
		for (String oneToExpand : Util.improvedSplitter(rightAnswerText,
				Util.PHRASE_DELIMITER)) {
			allExpanded += expandOnlyOneExpression(oneToExpand);
		}
		allExpanded = Util.removeUnwantedDelimiters(allExpanded, removeComment);
		return Util.removeWhiteSpaces(allExpanded);
	}

	/**
	 * normalizes string - with some exceptions (like ł is not normalized to l
	 * automatically)
	 * 
	 * @param toNormalize
	 * @return
	 */
	public static String normalize(String toNormalize) {
		toNormalize = Normalizer.normalize(toNormalize, Normalizer.Form.NFD);
		// phrase = phrase.replaceAll("[^\\p{ASCII}]", "");
		toNormalize = toNormalize.replaceAll(
				"\\p{InCombiningDiacriticalMarks}+", "");
		toNormalize = toNormalize.replaceAll("Ł", "L");
		toNormalize = toNormalize.replaceAll("ł", "l");
		return toNormalize;
	}

	/**
	 * Expand all alternatives of a SINGLE wildcard expression
	 * 
	 * 
	 * @param expression
	 *            - SINGLE wildcard expression
	 * @return all alternatives separated by Util.PHRASE_DELIMITER
	 */
	public static String expandOnlyOneExpression(String expressionString) {
		expressionString = expressionString.replaceAll(Util.QUOTE,
				Util.QUOTE_REPLACE);
		wholeAnswerList = new ArrayList<ArrayList<String>>();
		Pattern MY_PATTERN = Pattern
				.compile(Util.PATTERN_REGEXP_ALTERNATIVE_TEXT);
		Matcher m = MY_PATTERN.matcher(expressionString);
		StringBuffer sbuf = new StringBuffer();
		int i = 0;
		ArrayList<ArrayList<String>> objectList = new ArrayList<ArrayList<String>>();
		while (m.find()) {
			String s = m.group(1);
			m.appendReplacement(sbuf, Util.ALTERNATIVE_TEXT_OPEN_BRACKET + i
					+ Util.ALTERNATIVE_TEXT_CLOSE_BRACKET);
			String[] sArray = s.split(Util.ALTERNATIVE_TEXT_SEPARATOR);
			ArrayList<String> commonMeaning = new ArrayList<String>();
			for (String stringMeaning : sArray) {
				commonMeaning.add(stringMeaning);
			}
			objectList.add(commonMeaning);
			i++;
		}
		m.appendTail(sbuf);
		// formuła odpowiedzi gdy wcale nie ma alternatyw
		if (!sbuf.toString().contains(Util.ALTERNATIVE_TEXT_OPEN_BRACKET)) {
			return (sbuf.toString() + Util.PHRASE_DELIMITER).replaceAll(
					Util.QUOTE_REPLACE, Util.QUOTE);
		}
		cartesianProduct(objectList, 0);
		String regex = Util.PATTERN_REGEXP_ONE_SPACE;
		String wholeAnswerString = "";

		for (ArrayList<String> oneResult : wholeAnswerList) {
			ArrayList<String> trimmedResult = new ArrayList<String>();
			for (String trimmedItem : oneResult) {
				trimmedResult.add(trimmedItem.trim());
			}
			Object[] arguments = trimmedResult.toArray(new Object[0]);
			String message = MessageFormat.format(sbuf.toString(), arguments)
					.replaceAll(regex, " ");
			wholeAnswerString += message + Util.PHRASE_DELIMITER;
		}
		wholeAnswerString = wholeAnswerString.replaceAll(Util.QUOTE_REPLACE,
				Util.QUOTE);
		return wholeAnswerString;
	}

	/**
	 * Show all expanded questions with comments in brackets
	 * 
	 * @param text
	 * @return
	 */
	public String produceExpandedInput(String text) {
		String rightText = expandAll(
				Util.newLineToDelim(text, Util.PHRASE_DELIMITER),
				Util.SHOW_COMMENT);
		return Util.delimToNewLine(rightText, Util.PHRASE_DELIMITER);
		// return rightText.replace(Util.PHRASE_DELIMITER,
		// Util.NEW_LINE_DELIMITER);
	}

	/**
	 * fills question TextArea sets scroll and marks if scroll is has something
	 * to browse
	 * 
	 * @param text
	 */
	public void fillQuestionAreaAndMarkScrollBrowse(String text) {
		// fill the text into fields and areas
		// show all expanded questions
		String allExpandedQuestions = produceExpandedInput(text);
		int numOfLines = StringUtils.countMatches(allExpandedQuestions,
				Util.NEW_LINE_DELIMITER);
		questionWord.setText(allExpandedQuestions);
		if (numOfLines >= defaultTextareaRows) {
			questionScroll.getVerticalScrollBar().setBackground(
					BROWSE_QUESTION_SCROLL_COLOR);
		}
	}

	/**
	 * fills response TextArea sets scroll and marks if scroll has something to
	 * browse
	 * 
	 * @param text
	 */
	public void fillResponseAreaAndMarkScrollBrowse(String text) {
		// fill the text into fields and areas
		// show all expanded questions
		LOG.debug(" text '" + text + "'");
		String allExpandedQuestions = produceExpandedInput(text);
		// logger.debug(" allExpandedQuestions '"+allExpandedQuestions + "'");
		int numOfLines = StringUtils.countMatches(allExpandedQuestions,
				Util.NEW_LINE_DELIMITER);
		// logger.debug("numOfLines "+numOfLines);
		responseWord.setText(allExpandedQuestions);
		if (numOfLines >= defaultTextareaRows) {
			responseScroll.getVerticalScrollBar().setBackground(
					BROWSE_RESPONSE_SCROLL_COLOR);
		}
	}

	public static class JUnitTest {				
		@Before
		public void setUp() throws Exception {
			System.out.println("TIME :"+System.currentTimeMillis());
		}

		@Test
		public void testNormalize_true() {
			assertEquals("zolw", normalize("zółw"));
		}

		@Test
		public void testNormalize_false() {
			Map<String, String> testMap = new HashMap<String, String>();			
			testMap.put("zolw", "żółw");
			testMap.put("Zolw", "Żółw");
			testMap.put("To sa aAcCeElLnNoOsSzZzZ i juz ?!", "To są ąĄćĆęĘłŁńŃóÓśŚźŹżŻ i już ?!");			
			for (String key : testMap.keySet()) {
				assertEquals(key, normalize(testMap.get(key)));
			}
		}

		@Test 
		public void testIsGoodAnswer_for_missing_punctuation() {
			settings.setNormalized(false);
			//settings.setStrictMode(strictMode);(false);
			Map<String, String> testMap = new HashMap<String, String>();
			testMap.put("ałć.", "ałć");
			testMap.put("ałć?", "ałć");		
			testMap.put("¿ałć?", "ałć");
			testMap.put("ałć!", "ałć");
			testMap.put("¡ałć!", "ałć");			
			for (String key : testMap.keySet()) {			
				assertEquals(true, isGoodAnswer(key, testMap.get(key)));
			}
		}		
		
		@Test 
		public void testIsGoodAnswer_Normalized_true() {
			settings.setNormalized(true);
			Map<String, String> testMap = new HashMap<String, String>();
			testMap.put("ałć; au; ale boli", "ale        boli; au; alc");
			testMap.put("{/złość} to nicość", "to nicosc; złość to nicosc");			
			testMap.put("ąĄćĆęĘłŁńŃóÓśŚźŹżŻ", "aAcCeElLnNoOsSzZzZ");			
			for (String key : testMap.keySet()) {			
				assertEquals(true, isGoodAnswer(key, testMap.get(key)));
			}
		}

		@Test 
		public void testIsGoodAnswer_Normalized_false_fail() {
			settings.setNormalized(false);
			Map<String, String> testMap = new HashMap<String, String>();			
			testMap.put("ałć; au; ale boli", "ale boli; au; alc");
			testMap.put("{/złość} to nicość", "to nicosc; złość to nicosc");			
			testMap.put("ąĄćĆęĘłŁńŃóÓśŚźŹżŻ", "aAcCeElLnNoOsSzZzZ");
			for (String key : testMap.keySet()) {			
				assertEquals(false, isGoodAnswer(key, testMap.get(key)));
			}			
		}

		@Test 
		public void testIsGoodAnswer_StrictMode_true() {
			settings.setStrictMode(true);
			Map<String, String> testMap = new HashMap<String, String>();			
			testMap.put("a;b;c;d;e", "c;d;e;a;b");
			testMap.put("{/a/b/c} d e f", "c d e f;d e f;b d e f;a d e f");			
			for (String key : testMap.keySet()) {			
				assertEquals(true, isGoodAnswer(key, testMap.get(key)));
			}			
		}
		
		@Test 
		public void testIsGoodAnswer_StrictMode_true_fail() {
			settings.setStrictMode(true);
			Map<String, String> testMap = new HashMap<String, String>();		
			testMap.put("a;b;c;d;e", "c;e;a;b");
			testMap.put("{/a/b} d", "d;b d;");
			for (String key : testMap.keySet()) {			
				assertEquals(false, isGoodAnswer(key, testMap.get(key)));
			}			
		}			

		@Test 
		public void testIsGoodAnswer_Expendable_true() {			
			Map<String, String> testMap = new HashMap<String, String>();		
			testMap.put("{aa/bb} cc {dd/ee} ", "aa    cc dd;aa cc ee;bb cc dd; bb cc ee");
			testMap.put("oo {/aa/bb} xx", "oo xx;oo aa xx;oo bb xx");			
			testMap.put("oo {aa/bb/} xx", "oo aa xx;oo bb xx");			
			for (String key : testMap.keySet()) {			
				assertEquals(true, isGoodAnswer(key, testMap.get(key)));
			}			
		}			

		@Test 
		public void testExpandOnlyOneExpression_empty() {
			assertEquals(";", expandOnlyOneExpression(""));
		}					
		
		@Test(expected=Exception.class) 
		public void testExpandOnlyOneExpression_null() {
			assertEquals(null, expandOnlyOneExpression(null));			
		}			

		@Test 
		public void testExpandOnlyOneExpression_single() {
			assertEquals("tutaj;", expandOnlyOneExpression("tutaj"));
		}							
		
		@Test 
		public void testExpandOnlyOneExpression_simple() {
			assertEquals("a b f;a c f;a e f;", expandOnlyOneExpression("a {b/c/e} f"));
		}					

		@Test 
		public void testExpandOnlyOneExpression_doubled() {
			assertEquals("a c x;a d x;b c x;b d x;", expandOnlyOneExpression("{a/b} {c/d} x"));
		}							
		
	}
}
