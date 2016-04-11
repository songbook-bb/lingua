package wordtutor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import wordtutor.addnewword.FrmAddWord;
import wordtutor.addnewword.WordData;
import wordtutor.container.IWord;
import wordtutor.container.IWordContainer;
import wordtutor.container.WordVectorContainer;
import wordtutor.exception.NoAudioLineException;
import wordtutor.indexer.IWordIndexer;
import wordtutor.indexer.WordVectorIndexer;
import wordtutor.settings.FrmSettings;
import wordtutor.settings.Settings;
import wordtutor.util.IWTSerializable;
import wordtutor.util.TranslateDirection;
import wordtutor.util.keybuttons.KeyboardButtonType;
import wordtutor.utils.Util;
import wordtutor.xml.dict.DictionaryType;
import wordtutor.xml.dict.ObjectFactory;
import wordtutor.xml.dict.WordType;

/**
 * The main class which manages the storages of the words and user operations
 * with them.
 * 
 */
public class WordTutor implements IWTSerializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -475588278557404052L;
	private static Logger LOG = Logger.getLogger(WordTutor.class);
	/**
	 * the index of current word
	 */
	public static final String LESSONS_DIR = ".";
	public static final String RESOURCE_DIR = "resource";

	private int wordIndex = -1;
	private String currentDictFile = "words.naq";
	private HashMap<Integer, Integer> currentSoundSampleCoverMap = new HashMap<Integer, Integer>();

	/**
	 * storage of the words. implements with WordVectorContainer
	 */
	private IWordContainer words = new WordVectorContainer();
	/**
	 * indexer of the words which are in the current lesson
	 */
	private IWordIndexer lessonIndexes = new WordVectorIndexer();
	/**
	 * user settings
	 */
	private Settings settings = new Settings();

	/**
	 * randomize generator
	 */
	private Random r = new Random();
	/**
	 * current direction of the translation
	 */
	private TranslateDirection direction;
	/**
	 * table model for showing the words in the JTable
	 */
	private WordsTableModel model;
	/**
	 * Search String that greps the lesson
	 * 
	 */
	private String searchString;
	/**
	 * Search filter result count
	 * 
	 */
	private int searchFilterResultCount;

	/**
	 * All phrases in the lesson are sounded -> enableSpelling
	 * 
	 */
	private boolean enableSpelling;
	
	/**
	 * default constructor loads the settings from XML and applies it for the
	 * word storage
	 */
	public WordTutor(String currentFile) {
		preparation(currentFile);
	}

	/**
	 * Main preparation settings for constructor
	 * 
	 * @param file
	 */
	public void preparation(String file) {
		setCurrentDictFile(file);
		direction = TranslateDirection.STRAIGHT;
		settings.loadFromXML();
		// buildCurrentSoundSampleCoverMapIndex();
	}

	/**
	 * loads the words from XML-file. Uses the classes generated by JAXB
	 */

	@SuppressWarnings("unchecked")
	public void loadFromXML() {
		// ObjectFactory factory = new ObjectFactory();
		JAXBElement<DictionaryType> jaxbDictionary = null;
		try {
			JAXBContext jc = JAXBContext.newInstance("wordtutor.xml.dict");
			Unmarshaller m = jc.createUnmarshaller();
			File f = new File(currentDictFile);
			jaxbDictionary = (JAXBElement<DictionaryType>) m.unmarshal(f);
		} catch (JAXBException je) {
			String message = je.getMessage();
			LOG.error(je.getMessage(), je);
			LOG.error(je.getStackTrace());
			JOptionPane.showMessageDialog(null, message,
					Util.getLocalizedString("ERROR.DIALOG.TITLE"),
					JOptionPane.ERROR_MESSAGE);
		}
		DictionaryType dType = jaxbDictionary.getValue();
		List<WordType> dWords = dType.getWord();
		words.clear();
		int cnt = dWords.size();
		enableSpelling = true;
		for (int i = 0; i < cnt; i++) {
			IWord newWord = words.createWord(dWords.get(i).getForeignWord(),
					dWords.get(i).getNativeWord(), dWords.get(i).getIdSound());
			newWord.setLearned(dWords.get(i).getLearned() == 1);
			newWord.setScore(dWords.get(i).getScore());
			words.add(newWord);
			if (dWords.get(i).getIdSound() == 0) {
				enableSpelling = false;
			}
		}
		//LOG.info("LOADING from FILE ["+currentDictFile+"] isSpelling = "+dType.isSpelling());
		settings.setSpelling(dType.isSpelling());
		// persist keyboard settings (taken from test file)
		settings.setKeyboardType(settings.keyboardIntToEnumeration(dType.getKeyboard()));
		settings.saveToXML();
		// rebuild current sample map each time
		buildCurrentSoundSampleCoverMapIndex();
	}

	/**
	 * saves the words to XML-file. Uses the classes generated by JAXB
	 */
	public void saveToXML() {
		ObjectFactory factory = new ObjectFactory();
		DictionaryType dType = factory.createDictionaryType();
		List<WordType> dWords = dType.getWord();
		// List<WordType> dWords = dType.getWord();
		int cnt = words.size();
		for (int i = 0; i < cnt; i++) {
			WordType newWord = new WordType();
			newWord.setForeignWord(words.get(i).getForeignWord());
			newWord.setNativeWord(words.get(i).getNativeWord());
			newWord.setScore(words.get(i).getScore());
			newWord.setLearned(words.get(i).isLearned() ? 1 : 0);
			newWord.setIdSound(words.get(i).getIdSound());
			dWords.add(newWord);
		}
		dType.setKeyboard(settings.keyboardEnumerationToInt(settings
				.getKeyboardType()));
		dType.setSpelling(settings.isSpelling());
		//LOG.info("SAVING into FILE ["+currentDictFile+"] isSpelling = "+dType.isSpelling());	
		JAXBElement<DictionaryType> jaxbDict = factory.createDictionary(dType);
		try {
			JAXBContext jc = JAXBContext.newInstance("wordtutor.xml.dict");
			Marshaller m = jc.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			File f = new File(currentDictFile);
			m.marshal(jaxbDict, f);
		} catch (JAXBException je) {
			String message = je.getMessage();
			JOptionPane.showMessageDialog(null, message,
					Util.getLocalizedString("ERROR.DIALOG.TITLE"),
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * opens the window for changing the settings apply word setting for a
	 * storage
	 */
	public void openSettings(FrmMain parentFrame) {
		new FrmSettings(parentFrame,
				Util.getLocalizedString("WT.SETTINGS.TITLE"), settings, enableSpelling);
	}

	/**
	 * add new word by while importing from csv file
	 */
	public void addWordInImport(String foreignWord, String nativeWord) {
		words.add(foreignWord, nativeWord, 0);
	}

	/**
	 * add new word by while importing from csv file
	 */
	public void addWordInImport(String foreignWord, String nativeWord,
			String soundWord) {
		Integer soundId = 0;
		try {
			soundId = Integer.parseInt(soundWord);
		} catch (NumberFormatException nfe) {
			LOG.warn(nfe.getMessage(), nfe);
		}
		words.add(foreignWord, nativeWord, soundId);
	}

	public String chooseSoundSampleFileName() {
		// logger.debug("fnMAP:"+getCurrentSoundSampleCoverMap());
		// logger.debug("sndID:"+this.getIdSound());
		if (this.getIdSound() > 0
				&& getCurrentSoundSampleCoverMap().get(this.getIdSound()) != null
				&& getCurrentSoundSampleCoverMap().get(this.getIdSound()) >= 0) {
			int chooseNameInt = (getCurrentSoundSampleCoverMap().get(
					this.getIdSound()) > 0) ? (Math.abs(r.nextInt()) % getCurrentSoundSampleCoverMap()
					.get(this.getIdSound())) : 0;
			String nameLetter = "";
			if (chooseNameInt > 0) {
				nameLetter = Character.toString((char) (96 + chooseNameInt));
			}
			String complexFileName = this.getCurrentDictFile().substring(0,
					this.getCurrentDictFile().indexOf("."))
					+ File.separator
					+ this.getIdSound()
					+ nameLetter
					+ Util.getAppProperty("MP3.EXTENSION");
			return complexFileName;
		} else {
			return Util.NO_SOUND_SAMPLE;
		}
	}

	public void duplicateAllSoundFiles() throws IOException {
		buildCurrentSoundSampleCoverMapIndex();
		char startCharacterOffsetValue = 95;
		File lessonSampleDirectory = new File(Util.getAppProperties()
				.getProperty("RESOURCE.PATH")
				+ File.separator
				+ Util.getAppProperties().getProperty("MP3.PATH")
				+ File.separator
				+ this.getCurrentDictFile().substring(0,
						this.getCurrentDictFile().indexOf("."))
				+ File.separator);
		if (lessonSampleDirectory.isDirectory()) {
			for (int i = 1; i <= words.size(); i++) {
				int j = currentSoundSampleCoverMap.get(i);
				// copying only when there is at least one sample
				if (j > 0) {
					int letterValue = (++j) + startCharacterOffsetValue;
					String currentDir = System.getProperty("user.dir");
					String srcFilePath = currentDir + File.separator
							+ lessonSampleDirectory + File.separator + "" + i
							+ ".mp3";
					String destFilePath = currentDir + File.separator
							+ lessonSampleDirectory + File.separator + "" + i
							+ Character.toString((char) letterValue) + ".mp3";
					File srcFile = new File(srcFilePath);
					File destFile = new File(destFilePath);
					LOG.info(srcFilePath);
					LOG.info(destFilePath);
					FileUtils.copyFile(srcFile, destFile);
				}
			}
		}

	}

	/**
	 * Seek mp3 in certain resource directory
	 * 
	 * This method alters hashMap - currentSoundSampleMap - key (is word phrase
	 * index) value (is number of sound mp3 samples associated with the phrase)
	 * 
	 * mind: maxSamplesCounter is set to 7
	 * @return
	 */
	public void buildCurrentSoundSampleCoverMapIndex() {
		int maxWordsInLesson = words.size();
		// maximum allowed number of sound samples
		int maxSamplesCounter = 7;
		char startCharacterOffsetValue = 95;
		String[] extensions = { "mp3" };
		File lessonSampleDirectory = new File(Util.getAppProperties()
				.getProperty("RESOURCE.PATH")
				+ File.separator
				+ Util.getAppProperties().getProperty("MP3.PATH")
				+ File.separator
				+ this.getCurrentDictFile().substring(0,
						this.getCurrentDictFile().indexOf("."))
				+ File.separator);
		// Collection<File> fList = null;
		LinkedList<File> strangeCastNotWorkedList = new LinkedList<File>();
		if (lessonSampleDirectory.isDirectory()) {
			@SuppressWarnings("unchecked")
			LinkedList<File> fList = (LinkedList<File>) FileUtils.listFiles(
					lessonSampleDirectory, extensions, false);
			// UWAGA: STRANGE CAST NOT WORKED
			strangeCastNotWorkedList = fList;
		}
		ArrayList<String> namesWithoutExtensionList = new ArrayList<String>();
		HashMap<Integer, Integer> sampleMap = new HashMap<Integer, Integer>();
		for (File mp3File : strangeCastNotWorkedList) {
			namesWithoutExtensionList.add(mp3File.getName().substring(0,
					mp3File.getName().indexOf(Util.FULL_STOP)));
		}
		for (int i = 1; i <= maxWordsInLesson; i++) {
			for (int j = 1; j < maxSamplesCounter; j++) {
				if (j == 1) {
					if (namesWithoutExtensionList.contains("" + i)) {
						sampleMap.put(i, j);
					} else {
						sampleMap.put(i, 0);
						break;
					}
				} else {
					// to produce 'a'..'b'..'c'...ect. etc.
					int letterValue = j + startCharacterOffsetValue;
					if (namesWithoutExtensionList.contains("" + i + ""
							+ Character.toString((char) letterValue))) {
						sampleMap.put(i, j);
					} else {
						break;
					}
				}
			}
		}
		setCurrentSoundSampleCoverMap(sampleMap);
		// logger.debug("MAP:"+getCurrentSoundSampleCoverMap());
		// return sampleMap;
	}

	public void doWeirdCustom() {
		// setAllWordsCapitalLetter();
		// zeroSoundIds();
		// setSoundIdsByOrder();
	}

	public void zeroSoundIds() {
		for (int i = 0; i < words.size(); i++) {
			words.get(i).setIdSound(0);
		}
	}

	public void setSoundIdsByOrder() {
		for (int i = 0; i < words.size(); i++) {
			words.get(i).setIdSound(i + 1);
		}
	}

	public void setAllWordsCapitalLetter() {
		for (int i = 0; i < words.size(); i++) {
			String nativeWord = words.get(i).getNativeWord();
			nativeWord = nativeWord.substring(0, 1).toUpperCase()
					+ nativeWord.substring(1, nativeWord.length());
			String foreignWord = words.get(i).getForeignWord();
			foreignWord = foreignWord.substring(0, 1).toUpperCase()
					+ foreignWord.substring(1, foreignWord.length());
			words.get(i).setNativeWord(nativeWord);
			words.get(i).setForeignWord(foreignWord);
		}
	}

	/**
	 * lets the user to add new word by opening a special window
	 */
	public void addNewWord() {
		WordData wordData = new WordData();
		try {
			String fileWave = getMaxIdSound()
					+ Util.getAppProperty("WAVE.EXTENSION");
			String waveFullPath = Util.getAppProperty("RESOURCE.PATH")
					+ File.separator
					+ Util.getAppProperty("MP3.PATH")
					+ File.separator
					+ getCurrentDictFile().substring(0,
							getCurrentDictFile().indexOf("."));
			;
			File f = new File(waveFullPath);
			boolean success = (!f.exists()) ? (f.mkdirs()) : true;
			if (!success) {
				LOG.error("Tried to create dir " + waveFullPath);
			}
			FrmAddWord fAddWord = null;
			// ZMIENIASZ TU - ZMIEN TEZ W editWord
			try {
				fAddWord = new FrmAddWord(null,
						Util.getLocalizedString("WT.ADDNEW.TITLE"), wordData,
						waveFullPath + File.separator + fileWave, getSearchFilterResultCount() == 0 ? getSearchString() : null);
				fAddWord.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			} catch (NoAudioLineException naex) {
				// wylacz nagrywanie i jeszcze raz pokaz okno
				Util.getAppProperties().setProperty("RECORD.SOUND", "false");
				fAddWord = new FrmAddWord(null,
						Util.getLocalizedString("WT.ADDNEW.TITLE"), wordData,
						waveFullPath + File.separator + fileWave, getSearchFilterResultCount() == 0 ? getSearchString() : null);
				fAddWord.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			LOG.error(e.getStackTrace());
		}
		if (wordData.isSaved) {
			words.add(wordData.foreignWord, wordData.nativeWord,
					wordData.addSound ? getMaxIdSound() : 0);
		}

	}

	public int getMaxIdSound() {
		int tmpId = 0;
		for (int i = 0; i < words.size(); i++) {
			tmpId = (words.get(i).getIdSound() > tmpId) ? words.get(i)
					.getIdSound() : tmpId;
		}
		tmpId++;
		return tmpId;

	}

	/**
	 * Adds just typed answer to list of right answers
	 * 
	 * @param yetAnotherAnswer
	 *            - right phrase
	 * @param addToEnd
	 *            - true (adds onto the end, false onto the beginning
	 */
	public void addYetAnotherAnswer(String yetAnotherAnswer, boolean addToEnd) {
		// trim it first
		yetAnotherAnswer = yetAnotherAnswer.trim();
		if (direction == TranslateDirection.STRAIGHT) {
			String previousAnswer = getIndexedWord(wordIndex).getNativeWord()
					.trim();
			if (addToEnd) {
				getIndexedWord(wordIndex).setNativeWord(
						previousAnswer + Util.PHRASE_DELIMITER + Util.SPACE
								+ yetAnotherAnswer);
			} else {
				getIndexedWord(wordIndex).setNativeWord(
						yetAnotherAnswer + Util.PHRASE_DELIMITER + Util.SPACE
								+ previousAnswer);
			}
		} else {
			String previousAnswer = getIndexedWord(wordIndex).getForeignWord();
			if (addToEnd) {
				getIndexedWord(wordIndex).setForeignWord(
						previousAnswer + Util.PHRASE_DELIMITER + Util.SPACE
								+ yetAnotherAnswer);
			} else {
				getIndexedWord(wordIndex).setForeignWord(
						yetAnotherAnswer + Util.PHRASE_DELIMITER + Util.SPACE
								+ previousAnswer);
			}
		}
		// do not count last mistake (because the answer was OK)
		Integer lastScore = getIndexedWord(wordIndex).getScore();
		getIndexedWord(wordIndex).setScore(--lastScore);
		saveToXML();
		loadFromXML();
	}

	/**
	 * lets the user to edit existing word by opening a special window
	 */
	public void editWord(int index) {
		boolean lastAddedSound = true;
		while (words.size() > index && lastAddedSound) {
			WordData wordData = new WordData();
			wordData.foreignWord = words.get(index).getForeignWord();
			wordData.nativeWord = words.get(index).getNativeWord();
			/**
			 * @TODO Edycja do sprawdzenie czy nie usuwa podłączenia plików
			 *       Sound
			 * 
			 */
			String fileWave;
			if (words.get(index).getIdSound() > 0) {
				/** @TODO UWAGA Chyba będziemy nadpisywać... */
				fileWave = "" + words.get(index).getIdSound();
			} else {
				fileWave = "" + getMaxIdSound();
			}
			fileWave += Util.getAppProperty("WAVE.EXTENSION");

			String waveFullPath = Util.getAppProperty("RESOURCE.PATH")
					+ File.separator
					+ Util.getAppProperty("MP3.PATH")
					+ File.separator
					+ getCurrentDictFile().substring(0,
							getCurrentDictFile().indexOf("."));

			File f = new File(waveFullPath);
			boolean success = (!f.exists()) ? (f.mkdirs()) : true;
			if (!success) {
				LOG.error("Tried to create dir " + waveFullPath);
			}
			// ZMIENIASZ TU - ZMIEN TEZ W addNewWord
			try {
				try {
					FrmAddWord fEditWord = new FrmAddWord(null,
							Util.getLocalizedString("WT.EDIT.TITLE"), wordData,
							waveFullPath + File.separator + fileWave, null);
					fEditWord.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				} catch (NoAudioLineException nale) {
					// wylacz nagrywanie i jeszcze raz pokaz okno
					Util.getAppProperties()
							.setProperty("RECORD.SOUND", "false");
					FrmAddWord fEditWord = new FrmAddWord(null,
							Util.getLocalizedString("WT.EDIT.TITLE"), wordData,
							waveFullPath + File.separator + fileWave, null);
					fEditWord.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				}
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}

			if (wordData.isSaved) {
				words.get(index).setForeignWord(wordData.foreignWord);
				words.get(index).setNativeWord(wordData.nativeWord);
				/**
				 * @TODO Może trzeba pokazać okno - gdy ustawiono jakiś w
				 *       warningów
				 * 
				 */
				int saveIdSound = words.get(index).getIdSound();
				if (wordData.addSound) {
					if (getMaxIdSound() > saveIdSound && saveIdSound > 0) {
						LOG.warn("CAUTION: Trying to duplicate idSound = "
								+ saveIdSound);
					} else {
						words.get(index).setIdSound(getMaxIdSound());
					}
				} else {
					if (saveIdSound > 0) {
						LOG.warn("CAUTION: Unsuccessful attempt to override idSound = "
								+ saveIdSound);
					} else {
						words.get(index).setIdSound(0);
					}
				}
			}
			saveToXML();
			loadFromXML();
			lastAddedSound = wordData.addSound;
			index++;
		}

	}

	/**
	 * ile mamy fraz w tej lekcji
	 * 
	 * @return
	 */

	public int getMaxWordCount() {
		return words.size();
	}

	/**
	 * lets the user to merge selected two phrases
	 */
	public void mergeWords(int indexes[]) {
		int cnt = indexes.length;
		// two lines case (just for now)
		if (cnt == 2) {
			String fullForeign = words.get(indexes[0]).getForeignWord()
					+ Util.PHRASE_DELIMITER
					+ words.get(indexes[1]).getForeignWord();
			String fullNative = words.get(indexes[0]).getNativeWord()
					+ Util.PHRASE_DELIMITER
					+ words.get(indexes[1]).getNativeWord();
			words.get(indexes[0]).setForeignWord(
					removeDuplicatedPhrasesWithinString(fullForeign));
			words.get(indexes[1]).setForeignWord(
					removeDuplicatedPhrasesWithinString(fullForeign));
			words.get(indexes[0]).setNativeWord(
					removeDuplicatedPhrasesWithinString(fullNative));
			words.get(indexes[1]).setNativeWord(
					removeDuplicatedPhrasesWithinString(fullNative));
		}
	}

	/**
	 * lets the user to exchange foreign-native into native-foreign
	 */
	public void exchangeWords() {
		int counter = words.size();
		for (int i = 0; i < counter; i++) {
			String oldForeign = words.get(i).getForeignWord();
			String oldNative = words.get(i).getNativeWord();
			words.get(i).setForeignWord(oldNative);
			words.get(i).setNativeWord(oldForeign);
		}
	}

	public String removeDuplicatedPhrasesWithinString(String multiString) {
		String[] ffArray = Util.improvedSplitter(multiString,
				Util.PHRASE_DELIMITER);
		HashMap<String, String> ffMap = new HashMap<String, String>();
		for (String oneMeaning : ffArray) {
			ffMap.put(oneMeaning.trim(), oneMeaning.trim());
		}
		String mergedFF = "";
		for (String oneItem : ffMap.keySet()) {
			mergedFF += oneItem + Util.PHRASE_DELIMITER;
		}
		// if (mergedFF.length() < 1) {
		// // nic nie robi i po cichu wychodzi
		// return;
		// }
		return mergedFF.substring(0, mergedFF.length() - 1);
	}

	/**
	 * lets the user to delete all selected words
	 */
	public void delWord(int indexes[]) {
		int cnt = indexes.length;
		for (int i = 0; i < cnt; i++) {
			words.remove(indexes[i] - i);
		}
		// find & remove duplicates (whole same example = question & answer are
		// duplicated)
		int i = 0;
		int j = 0;
		String nativeString = "";
		String foreignString = "";
		while (j < words.size()) {
			nativeString = words.get(j).getNativeWord();
			foreignString = words.get(j).getForeignWord();
			i = j;
			while (i < words.size()) {
				i++;
				if (i < words.size()
						&& words.get(i).getNativeWord().equals(nativeString)
						&& words.get(i).getForeignWord().equals(foreignString)) {
					words.remove(i);
					i--;
					LOG.info(" @@@ R E M O V E D @@@ : [" + nativeString
							+ " | " + foreignString + "]");
				}
			}
			j++;
		}

	}

	/**
	 * gets the word which is pointed by lesson index
	 */
	public IWord getIndexedWord(int i) {
		return words.get(lessonIndexes.get(i));
	}

	/**
	 * gets the i-th word
	 */
	public IWord getWordById(int i) {
		return words.get(i);
	}

	/**
	 * sets the current direction of the translating
	 */
	public void setDirection(TranslateDirection newDirection) {
		direction = newDirection;
	}

	/**
	 * chooses the next word to be indexed
	 */
	public void chooseWord() {
		direction = settings.getNextDirection();
		int lastWordIndex = wordIndex;
		// petla w celu unikniecia powtórnego losowania pod rzad tej samej frazy
		do {
			wordIndex = r.nextInt(lessonIndexes.size());
		} while ((lastWordIndex == wordIndex) && (lessonIndexes.size() > 1));
	}

	/**
	 * Podaje ustawienie językowe dodatkowej klawiatury
	 * 
	 * @return
	 */
	public KeyboardButtonType getKeyboardType() {
		return settings.getKeyboardType();
	}

	/**
	 * returns foreign spelling of the indexed word
	 */
	private String getForeignWord() {
		return getIndexedWord(wordIndex).getForeignWord();
	}

	/**
	 * returns native spelling of the indexed word
	 */
	private String getNativeWord() {
		return getIndexedWord(wordIndex).getNativeWord();
	}

	/**
	 * checks the answer given by user
	 */
	public boolean checkAnswer(String answer) {
		return getIndexedWord(wordIndex).checkAnswer(answer, direction);
	}

	/**
	 * 
	 * gets the right answer
	 */
	public String getRightAnswer() {
		String result = (direction == TranslateDirection.STRAIGHT) ? getIndexedWord(
				wordIndex).getNativeWord()
				: getIndexedWord(wordIndex).getForeignWord();
		return result;
	}

	/**
	 * increments score for error statistics
	 */
	public int incScore() {
		int currentScore = getIndexedWord(wordIndex).getScore();
		currentScore++;
		words.get(lessonIndexes.get(wordIndex)).setScore(currentScore);
		// getIndexedWord(wordIndex).setScore(currentScore);
		// logger.debug("WORD currentScore="+currentScore+" wordIndex= "+wordIndex+" word.indexed="+words.get(lessonIndexes.get(wordIndex)).getForeignWord());
		//
		// int cnt = words.size();
		// for(int i = 0;i<cnt;i++)
		// {
		// if (words.get(i).getScore() > 0 || words.get(i).isLearned() == true)
		// {
		// logger.debug("INC SCORE L="+words.get(i).isLearned()+" "+words.get(i).getScore()+" "+words.get(i).getForeignWord());
		// }
		// }
		// //getIndexedWord(wordIndex).setScore(currentScore);
		return currentScore;
	}

	/**
	 * returns idSound index of a word
	 */
	public int getIdSound() {
		return getIndexedWord(wordIndex).getIdSound();
	}

	/**
	 * returns true id translation direction is straight
	 */
	public boolean isStraightTranslation() {
		return (direction == TranslateDirection.STRAIGHT) ? true : false;
	}

	/**
	 * starts a new lesson. clears and fills the indexer of the words used in
	 * the lesson
	 */
	public void startLesson() throws Exception {
		int phraseCounter = settings.getWordsInLesson();
		int startIndex = settings.getIncScore();
		startIndex--;
		int maxPhraseIndex = words.size();
		int allLearnedWords = 0;
		for (int l = 0; l < maxPhraseIndex; l++) {
			if (words.get(l).isLearned()) {
				allLearnedWords++;
			}
		}
		int i = 0;
		int j = 0;
		if ((startIndex + phraseCounter) <= maxPhraseIndex) {
			i = startIndex;
		}
		lessonIndexes.clear();

		if (settings.isHardest() && settings.isRandom()) {
			throw new Exception(
					"Cannot configure hardest and random one at a time.");
		}

		if (settings.isRandom()) {
			HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
			if (settings.isUnlearned()) {
				// LOSOWO bierze nie zdane
				if (phraseCounter > (maxPhraseIndex - allLearnedWords)) {
					phraseCounter = maxPhraseIndex - allLearnedWords;
				}
				while (map.keySet().size() < phraseCounter) {
					int randIndex = r.nextInt(maxPhraseIndex);
					if (!words.get(randIndex).isLearned()) {
						map.put(randIndex, words.get(randIndex).getScore());
					}
				}
				for (Integer key : map.keySet()) {
					lessonIndexes.add(key);
				}
			} else {
				// LOSOWO bierze zdane i nie zdane
				while (map.keySet().size() < phraseCounter) {
					int randIndex = r.nextInt(maxPhraseIndex);
					map.put(randIndex, words.get(randIndex).getScore());
				}
				for (Integer key : map.keySet()) {
					lessonIndexes.add(key);
				}
			}
		} else if (settings.isHardest()) {
			// gdy ma brać te wyrazy dla których było najwięcej pomyłek
			HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
			ValueComparator bvc = new ValueComparator(map);
			TreeMap<Integer, Integer> sortedMap = new TreeMap<Integer, Integer>(
					bvc);
			for (i = 0; i < maxPhraseIndex; i++) {
				if (settings.isUnlearned()) {
					if (!words.get(i).isLearned()) {
						map.put(i, words.get(i).getScore());
					}
				} else {
					map.put(i, words.get(i).getScore());
				}
			}
			sortedMap.putAll(map);
			int counter = 0;
			for (Integer key : sortedMap.keySet()) {
				lessonIndexes.add(key);
				counter++;
				if (counter == phraseCounter) {
					break;
				}
			}
		} else {
			// gdy nie ma flagi hardest, ani random bierze jak leci po kolei
			while ((j < phraseCounter) && (i < maxPhraseIndex)) {
				if (!(settings.isUnlearned() && words.get(i).isLearned())) {
					lessonIndexes.add(i);
				}
				i++;
				j++;
			}
		}

	}

	/**
	 * checks if the lesson is empty
	 */
	public boolean isLessonEmpty() {
		return (lessonIndexes.size() == 0);
	}

	/**
	 * proceeds the word, that was already answered by user anyway it will just
	 * remove the word from the indexer
	 */
	public void proceedWord() {
		lessonIndexes.remove(wordIndex);
	}

	/**
	 * assigns the table model. also it assigns word storage of the tutor to
	 * model
	 */
	public void assignTableModel(WordsTableModel model) {
		this.model = model;
		model.assignWordContainer(words);
	}

	/**
	 * fills table model. actually, it just sets row count of the model
	 */
	public void fillTableModel() {
		model.setRowCount(words.size());
	}

	/**
	 * gets the question. it depends on the current translate direction if it is
	 * straight, that it will return foreign spelling of the world, in another
	 * case - native spelling of the word
	 * 
	 */
	public String getQuestion() {

		if (direction == TranslateDirection.STRAIGHT)
			return getForeignWord();
		else
			return getNativeWord();
	}

	/**
	 * clears all learned flags
	 * 
	 */
	void clearLearning(boolean cleanStatistics) {
		boolean result = (words.size() > 0);
		// if the dictionary contains words, just check if there are active ones
		if (result) {
			int cnt = words.size();
			for (int i = 0; i < cnt; i++) {
				words.get(i).setLearned(false);
				if (cleanStatistics) {
					words.get(i).setScore(0);
				}
				//
				// words.get(i).setIdSound(0);
			}
		}
	}

	/**
	 * Util.T - if there are some words check if the dictionary contains any
	 * words which are not learned
	 */
	/**
   * 
   */
	int isWordListNotEmpty() {
		int result = (words.size() > 0) ? Util.T : Util.F;
		// if the dictionary contains words, just check if there are active ones
		boolean isRH = settings.isHardest() || settings.isRandom();
		if (result == Util.T) {
			result = Util.F;
			int cnt = words.size();
			int start = 0;
			// zmien automatycznie ustawienia - bo za maly jest plik testowy
			if (settings.getWordsInLesson() > words.size()) {
				settings.setWordsInLesson(words.size());
				settings.saveToXML();
			}
			int helpCount = isRH ? (settings.getWordsInLesson()) : settings
					.getWordsInLesson() + settings.getIncScore() - 1;
			if (helpCount > words.size()) {
				// błąd konfiguracji
				return Util.E;
			}
			if (settings.isUnlearned() && !(isRH)) {
				cnt = helpCount;
				start = settings.getIncScore() - 1;
			}
			for (int i = start; i < cnt; i++) {
				result = ((!words.get(i).isLearned() ? Util.T : Util.F));
				if (result == Util.T) {
					return Util.T;
				}
			}
			// zdane i niezdane (losowo lub najtrudniejsze)
			if (!settings.isUnlearned() && isRH) {
				return Util.T;
			}
		}
		return Util.F;
	}

	public void setCurrentDictFile(String currentDictFile) {
		Util.setAppProperty("CURRENT.TEST.FILE", currentDictFile);
		this.currentDictFile = currentDictFile;
	}

	public String getCurrentDictFile() {
		return currentDictFile;
	}

	public Settings getSettings() {
		return settings;
	}

	public void setSettings(Settings settings) {
		this.settings = settings;
	}

	public String calcLessonStatistics() {
		int countGood = 0;
		for (int i = 0; i < words.size(); i++) {
			if (words.get(i).isLearned()) {
				countGood++;
			}
		}
		return "[" + countGood + "/" + words.size() + "] = "
				+ (100 * countGood / words.size());
	}

	public String calcExamStatistics() {
		int countGood = 0;
		int countBad = 0;
		for (int i = 0; i < words.size(); i++) {
			if (words.get(i).isLearned()) {
				countGood++;
			}
			if (words.get(i).getScore() > 0) {
				countBad++;
			}
		}
		return "[" + countGood + "/" + (countGood + countBad) + "] = "
				+ (100 * countGood / (countGood + countBad));
	}

	public HashMap<Integer, Integer> getCurrentSoundSampleCoverMap() {
		return currentSoundSampleCoverMap;
	}

	public void setCurrentSoundSampleCoverMap(
			HashMap<Integer, Integer> currentSoundSampleCoverMap) {
		this.currentSoundSampleCoverMap = currentSoundSampleCoverMap;
	}

	public int getSearchFilterResultCount() {
		return searchFilterResultCount;
	}

	public void setSearchFilterResultCount(int searchFilterResultCount) {
		this.searchFilterResultCount = searchFilterResultCount;
	}

	public String getSearchString() {
		return searchString;
	}

	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}
	
}

class ValueComparator implements Comparator<Integer> {

	Map<Integer, Integer> base;

	public ValueComparator(Map<Integer, Integer> base) {
		this.base = base;
	}

	public int compare(Integer a, Integer b) {
		if (base.get(a) <= base.get(b)) {
			return 1;
		} else {
			return -1;
		}
	}	
	
}
