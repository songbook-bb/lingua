package wordtutor.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.media.Controller;
import javax.media.ControllerAdapter;
import javax.media.EndOfMediaEvent;
import javax.media.Format;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.Player;
import javax.media.PlugInManager;
import javax.media.Time;
import javax.media.format.AudioFormat;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class Util {
	static Logger logger = Logger.getLogger(Util.class);
	public static final String programVersion = "1.27";
	public static final String appconfigproperties = "appconfig.properties";
	public static final String authorInfo = "Tomek Nakonieczny";
	public static final String contactEmail = "tomek.nakonieczny@gmail.com";
	public static final String thanksList = "Paqui Garces, Zygmunt Hallman, Ludmiła Gołąbek, Mateusz Sondej, Ania Kierbedź, \n                         Paweł Studziński, Kieran Diels, Jill Lewis, Fr Brian McGinley, Madhubi Rita Gomes,\n                         Josipa Hudić, Sabine Zuch-Haischmann, Richard Baldock, Paulius Medziukevičius, \n                         Eustace Ugo, Basia Błasiak, Paul Noordveld, Eric Thierrij, Gerrit Mulder, Chantal Duijvelaar, \n                         Dennis Kasius, Alice Muhoza, Katharyn Watson, padre Cayetano Soto Diaz, \n                         Maria Garcia Diaz, Nayeli Quintero Sausedo, Karla Nuñez, Susana Ivethe González Vidales,\n                         Jesus Alonso Garcia, Cruz Antonio Lerma Patiño, Gloria Esmeralda Avila Botello, \n                         Carolina Romero Aguilar, Cristian Joel Vargas Gomes "; 
	public static final int xMainPosition = 10; // 50
	public static final int yMainPosition = 10; // 220
	public static final int xMainSize = 780; // 880
	public static final int yMainSize = 400; // 400
	public static final int xLessonPosition = 10; // 50
	public static final int yLessonPosition = 10; // 350
	public static final int xLessonSize = 780; // 880
	public static final int yLessonSize = 300; // 300
	public static final int xAddWordPosition = 10; // 50
	public static final int yAddWordPosition = 10; // 370
	public static final int xAddWordSize = 780; // 880
	public static final int yAddWordSize = 250; // 250
	public static final int xConfigPosition = 240; // 280
	public static final int yConfigPosition = 10; // 250
	public static final int xConfigSize = 360; // 360
	public static final int yConfigSize = 400; // 350
	public static final int xImportXlsPosition = 230; // 280
	public static final int yImportXlsPosition = 70; // 250
	public static final int xImportXlsSize = 390; // 360
	public static final int yImportXlsSize = 250; // 350
	public static String PHRASE_DELIMITER = ";";
	public static String SPACE = " ";
	public static String FULL_STOP = ".";
	public static String EXCLAMATION_MARK = "!";
	public static String INVERTED_EXCLAMATION_MARK = "¡";	
	public static String QUOTATION_MARK = "?";
	public static String INVERTED_QUOTATION_MARK = "¿";
	public static String COMMA = ",";	
	public static String NEW_LINE_DELIMITER = "\n";
	public static Boolean SHOW_COMMENT = false; // do not remove
	public static Boolean REMOVE_COMMENT = true; // remove from the text
	public static String PATTERN_REGEXP_NEW_LINE = "\\n";
	public static String PATTERN_REGEXP_ONE_SPACE = "\\s{2,}";
	public static String PATTERN_REGEXP_COMMENTING_TEXT = "\\((.*?)\\)";
	public static String PATTERN_REGEXP_ALTERNATIVE_TEXT = "\\{(.*?)\\}";
	public static String ALTERNATIVE_TEXT_OPEN_BRACKET = "{";
	public static String ALTERNATIVE_TEXT_CLOSE_BRACKET = "}";
	public static String ALTERNATIVE_TEXT_SEPARATOR = "/";
	public static String COMMENT_TEXT_OPEN_BRACKET = "(";
	public static String COMMENT_TEXT_CLOSE_BRACKET = ")";
	public static String XLS_EXTENTION = ".xls";
	public static String NO_SOUND_SAMPLE = "NO_SOUND_SAMPLE";	
	public static final int T = 1;
	public static final int F = 0;
	public static final int E = -1;
	public static final String TRUE = "true";	
	public static boolean isPlaying = false;
	public static ResourceBundle rb = null;
	public static Properties appProperties = null;
	
	public static Properties getAppProperties() {
		return appProperties;
	}

	public static void setAppProperties(Properties appProperties) {
		Util.appProperties = appProperties;

	}
	public static final String QUOTE = "'";
	public static final String QUOTE_REPLACE = "~";
	
	public Util() {
	}

	public static void loadAppProperties() {
		Logger logger = Logger.getLogger(Util.class);
		if (appProperties == null) {
			appProperties = new Properties();
			try {
				FileInputStream in = new FileInputStream(appconfigproperties);
				appProperties.load(in);
				in.close();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				logger.error(e.getStackTrace());
				exitVM(-1);				
			}
		}

		if (rb == null) {
			rb = localizationBundle(appProperties.getProperty("APP.LANG"));
		}
	}


	/**
	 *  Store appconfig.properties values in file
	 */
	public static void storeAppProperties() {
		Logger logger = Logger.getLogger(Util.class);
		if (appProperties != null) {
			try {
				FileOutputStream out = new FileOutputStream(appconfigproperties);
				appProperties.store(out, "Saved on program exit.");	
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				logger.error(e.getStackTrace());
				exitVM(-1);
			}
		}
	}

	/**
	 * Gives an application property value on return
	 * 
	 * @param appProperty
	 *          - String with property name
	 * @return
	 */
	public static String getAppProperty(String appProperty) {
		String returnProperty = appProperties.getProperty(appProperty);
		if ("CURRENT.TEST.FILE".equals(appProperty)) {
			if (returnProperty != null) return returnProperty;
		}
		if (StringUtils.isBlank(returnProperty)) {
			System.err.println("Missing prop " + appProperty + " in file appconfig.properties");
			exitVM(-1);			
		}
		return returnProperty;
	}

	/**
	 * Gives an application property value on return
	 * 
	 * @param appProperty
	 *          - String with property name
	 *          value - its value
	 * @return
	 */
	public static void setAppProperty(String appProperty, String value) {
		if (appProperties != null) {
			appProperties.setProperty(appProperty, value);			
		} else {
			// TODO write more clear description error info
			System.err.println("File appconfig.properties is null.");
			exitVM(-1);			
		}
	}
	
	
	/**
	 * Plays mp3 file from the given location
	 * 
	 * @param player
	 *          - Player
	 * @param mp3FilePlace
	 *          - directory where mp3 file is located
	 */
	public static void playSound(Player player, String mp3FilePlace) {
		Format input1 = new AudioFormat(AudioFormat.MPEGLAYER3);
		Format input2 = new AudioFormat(AudioFormat.MPEG);
		Format output = new AudioFormat(AudioFormat.LINEAR);
		PlugInManager.addPlugIn("com.sun.media.codec.audio.mp3.JavaDecoder", new Format[] { input1, input2 }, new Format[] { output }, PlugInManager.CODEC);
		String mp3FullPath = appProperties.getProperty("RESOURCE.PATH") + File.separator + appProperties.getProperty("MP3.PATH") + File.separator + mp3FilePlace;
		try {
			if (player != null) {
				player.stop();
				player.deallocate();
				player = null;
			}
			player = Manager.createPlayer(new MediaLocator(new File(mp3FullPath).toURI().toURL()));
			player.addControllerListener(new ControllerAdapter() {
				public void endOfMedia(EndOfMediaEvent e) {
					Controller controller = (Controller) e.getSource();
					controller.stop();
					controller.setMediaTime(new Time(0));
					controller.deallocate();
					isPlaying = false;
				}
			});
			if (!isPlaying) {
				player.start();
				isPlaying = true;
			}
		} catch (Exception ex) {
			isPlaying = false;
		}
	}

	/**
	 * 
	 * @return
	 */
	public static ResourceBundle localizationBundle(String langDef) {
		ResourceBundle rBundle;
		String low = "", high = "";
		if (langDef == null || langDef.length() == 0) {
			low = "pl";
			high = "PL";
		} else {
			if (langDef.indexOf("_") > 0) {
				low = langDef.substring(0, langDef.indexOf("_"));
				high = langDef.substring(langDef.indexOf("_") + 1, langDef.length());
			} else {
				high = langDef;
			}
		}
		Locale l = null;
		if ((low.length() > 0) && (high.length() > 0)) {
			l = new Locale(low, high);
		} else if (high.length() > 0) {
			l = new Locale(high);
		} else {
			exitVM(-1);			
		}
		rBundle = ResourceBundle.getBundle(appProperties.getProperty("APP.NAME"), l);
		return rBundle;
	}

	/**
	 * Add quotes for long path
	 * 
	 * @param str
	 * @return
	 */
	public static String surroundByQuotes(String str) {
		return "\"" + str + "\"";
	}

	/**
	 * 
	 * @param propertyName
	 *          - key name from localized property file 'tutorlang'
	 * @return
	 */
	public static String getLocalizedString(String propertyName) {
		if (rb == null) {
			exitVM(0);
		}
		String localizedString = rb.getString(propertyName);
		if (localizedString == null || localizedString.length() == 0) {
			// hm... perhaps should keep on doing...
			exitVM(0);
		}
		return localizedString;
	}

	public static boolean isEmpty(String s) {
		if (s == null || s.trim().length() == 0)
			return true;
		return false;
	}

	/**
	 * Operates on answer text phrase - remove unwanted/unused delimiters, and
	 * text COMMENTING text in (brackets)
	 * 
	 * @param inputString
	 * @param removeComments
	 *          - removes all comments in (brackets) including brackets
	 * 
	 * @return
	 */
	public static String removeUnwantedDelimiters(String inputString, boolean removeComment) {
		// remove COMMENTING text in (brackets) FIRST
		if (removeComment) {
			inputString = inputString.replaceAll(PATTERN_REGEXP_COMMENTING_TEXT, "");
		}
		// remove from the beginning
		String patternStr = "^[" + Util.PHRASE_DELIMITER + "\\r\\s\\t]*";
		String replaceStr = "";
		Pattern pattern = Pattern.compile(patternStr);
		Matcher matcher = pattern.matcher(inputString);
		// remove from the end
		String s = matcher.replaceAll(replaceStr);
		patternStr = "[" + Util.PHRASE_DELIMITER + "\\r\\s\\t]*$";
		pattern = Pattern.compile(patternStr);
		matcher = pattern.matcher(s);
		s = matcher.replaceAll(replaceStr);
		// remove empty delimits in the middle
		patternStr = "[" + Util.PHRASE_DELIMITER + "\\r\\s\\t]*" + Util.PHRASE_DELIMITER + "[" + Util.PHRASE_DELIMITER + "\\r\\s\\t]*";
		replaceStr = Util.PHRASE_DELIMITER;
		pattern = Pattern.compile(patternStr);
		matcher = pattern.matcher(s);
		return matcher.replaceAll(replaceStr);
	}

	/**
	 * Operates on text phrase - remove doubled white spaces
	 * 
	 * @param inputString
	 * 
	 * @return
	 */
	public static String removeDoubledSpaces(String inputString) {
		// remove from the beginning
		String patternStr = "^[" + Util.SPACE + "\\r\\s\\t]*";
		String replaceStr = "";
		Pattern pattern = Pattern.compile(patternStr);
		Matcher matcher = pattern.matcher(inputString);
		// remove from the end
		String s = matcher.replaceAll(replaceStr);
		patternStr = "[" + Util.SPACE + "\\r\\s\\t]*$";
		pattern = Pattern.compile(patternStr);
		matcher = pattern.matcher(s);
		s = matcher.replaceAll(replaceStr);
		// remove empty delimits in the middle
		patternStr = "[" + Util.SPACE + "\\r\\s\\t]*" + Util.SPACE + "[" + Util.SPACE + "\\r\\s\\t]*";
		replaceStr = Util.SPACE;
		pattern = Pattern.compile(patternStr);
		matcher = pattern.matcher(s);
		return matcher.replaceAll(replaceStr);
	}

	
	public static String removeWhiteSpaces(String inputString) {
		String returnString = removeWhiteSpacesBeforeAndAfterComma(inputString);
		returnString = removeWhiteSpacesBeforeAndAfterFullStop(returnString);
		returnString = removeWhiteSpacesBeforeAndAfterQuotationMark(returnString);
		returnString = removeWhiteSpacesBeforeAndAfterExclamationMark(returnString);
		return returnString;
	}
	
	/** Clears punctuation within one phrase
	 * 
	 */
	public static String removePunctuation(String inputString) {
		String returnString = inputString;
		returnString = returnString.replaceAll("\\"+Util.EXCLAMATION_MARK, Util.SPACE);
		returnString = returnString.replaceAll("\\"+Util.INVERTED_EXCLAMATION_MARK, Util.SPACE);		
		returnString = returnString.replaceAll("\\"+Util.QUOTATION_MARK, Util.SPACE);
		returnString = returnString.replaceAll("\\"+Util.INVERTED_QUOTATION_MARK, Util.SPACE);		
		returnString = returnString.replaceAll("\\"+Util.FULL_STOP, Util.SPACE);		
		returnString = returnString.replaceAll("\\"+Util.COMMA, Util.SPACE);
		returnString = removeWhiteSpaces(removeDoubledSpaces(returnString));
		returnString = returnString.trim();
		return returnString;
	}
	/** Clears punctuation within whole List of Strings
	 * 
	 */	
	public static List<String> removePunctuation(List<String> listOfStrings) {
		List<String> returnListOfString = new ArrayList<String>();  
		for (String oneStringItem : listOfStrings) {
			returnListOfString.add(removePunctuation(oneStringItem));
		}
		return returnListOfString;
	}
	
	/**
	 * Remove doubled spaces & white spaces before and after COMMA
	 * 
	 * @param inputString
	 * @return
	 */
	public static String removeWhiteSpacesBeforeAndAfterComma(String inputString) {
		// remove from all the places
		String regex = PATTERN_REGEXP_ONE_SPACE;
		inputString = inputString.replaceAll(regex, " ");
		String patternStr = "[\\r\\t\\n\\s]*[\\,][\\r\\t\\n\\s]*";
		String replaceStr = ", ";
		Pattern pattern = Pattern.compile(patternStr);
		Matcher matcher = pattern.matcher(inputString);
		return matcher.replaceAll(replaceStr);
	}	

	/**
	 * Remove doubled spaces & white spaces before and after FULL STOP
	 * 
	 * @param inputString
	 * @return
	 */
	public static String removeWhiteSpacesBeforeAndAfterFullStop(String inputString) {
		// remove from all the places
		String regex = PATTERN_REGEXP_ONE_SPACE;
		inputString = inputString.replaceAll(regex, " ");
		String patternStr = "[\\r\\t\\n\\s]*[\\.][\\r\\t\\n\\s]*";
		String replaceStr = ". ";
		Pattern pattern = Pattern.compile(patternStr);
		Matcher matcher = pattern.matcher(inputString);
		return matcher.replaceAll(replaceStr);
	}	
	
	/**
	 * Remove doubled spaces & white spaces before and after QUOTATION MARK
	 * 
	 * @param inputString
	 * @return
	 */
	public static String removeWhiteSpacesBeforeAndAfterQuotationMark(String inputString) {
		// remove from all the places
		String regex = PATTERN_REGEXP_ONE_SPACE;
		inputString = inputString.replaceAll(regex, " ");
		String patternStr = "[\\r\\t\\n\\s]*[\\?][\\r\\t\\n\\s]*";
		String replaceStr = "? ";
		Pattern pattern = Pattern.compile(patternStr);
		Matcher matcher = pattern.matcher(inputString);
		return matcher.replaceAll(replaceStr);
	}	

	/**
	 * Remove doubled spaces & white spaces before and after QUOTATION MARK
	 * 
	 * @param inputString
	 * @return
	 */
	public static String removeWhiteSpacesBeforeAndAfterExclamationMark(String inputString) {
		// remove from all the places
		String regex = PATTERN_REGEXP_ONE_SPACE;
		inputString = inputString.replaceAll(regex, " ");
		String patternStr = "[\\r\\t\\n\\s]*[\\!][\\r\\t\\n\\s]*";
		String replaceStr = "! ";
		Pattern pattern = Pattern.compile(patternStr);
		Matcher matcher = pattern.matcher(inputString);
		return matcher.replaceAll(replaceStr);
	}	
	
	public static boolean isFileUTF8(File f) {
	    FileInputStream fis = null;
	    try {
	      fis = new FileInputStream(f);
	      long length = f.length();
	      if (length > Integer.MAX_VALUE) {
	      	return false;
	      }
	      // Create the byte array to hold the data
	      byte[] bytes = new byte[(int)length];	      
	      fis.read(bytes);	      	      
	      fis.close();
	      return Util.isValidUTF8(bytes); 
	    } catch (FileNotFoundException fnfe) {
      	  logger.error(fnfe.getMessage(), fnfe);
      	  logger.error(fnfe.getStackTrace());            	  		      
	    } catch (IOException ioe) {
			logger.error(ioe.getMessage(), ioe);
			logger.error(ioe.getStackTrace());
	    } finally {
	    	if (fis != null)
				try {
					fis.close();
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
					logger.error(e.getStackTrace());
				}
	    }
	    return false;
	}
	
	public static boolean isValidUTF8(byte[] bytes) {
		try {
			Charset.availableCharsets().get("UTF-8").newDecoder().decode(ByteBuffer.wrap(bytes));
		} catch (CharacterCodingException e) {
			return false;
		}
		return true;
	}
	
	public static String[] improvedSplitter(String text, String delimiter) {		
		ArrayList<String> arrayList = new  ArrayList<String>();
		int begIndex = 0;
		int endIndex = 0;		
		while (endIndex > -1) {
			endIndex = text.indexOf(delimiter, begIndex);
			if (endIndex < 0) {
				if (!StringUtils.isBlank(text)) {
					arrayList.add(text);
				}
				continue;			
			}
			if (text.indexOf(Util.COMMENT_TEXT_OPEN_BRACKET) < endIndex && text.indexOf(Util.COMMENT_TEXT_CLOSE_BRACKET) > endIndex) {
				begIndex = endIndex + 1;
				continue;
			}
			String subText = new String(text.substring(0, endIndex));
			if (!StringUtils.isBlank(subText)) {
				arrayList.add(subText);
			}
			text = text.substring(endIndex+1, text.length());
			begIndex = 0;			
		}		
		return arrayList.toArray(new String[0]);
	}

	public static String delimToNewLine(String text, String delimiter) {
		String[] separated = improvedSplitter(text, delimiter);
		String returnString = "";
		for (int i = 0; i < separated.length; i++ ) {
			
			if (i > 0) {
				returnString += Util.NEW_LINE_DELIMITER+separated[i]; 
			} else {
				returnString += separated[i];
			}
		}
		return returnString;
	}
	
	public static String newLineToDelim(String text, String delimiter) {
		return text.replaceAll(Util.PATTERN_REGEXP_NEW_LINE, delimiter);
	}

	public static String replaceTabsWithPhraseDelimiter(String inputTestString) {
		return inputTestString.replaceAll(" \t ", Util.PHRASE_DELIMITER+Util.SPACE);
	}

	public static String replaceNewLineWithPhraseDelimiter(String inputTestString) {
		return inputTestString.replaceAll("\n", Util.PHRASE_DELIMITER+Util.SPACE);
	}
	public static String toHex(String arg) {
		try {
			return Hex.encodeHexString(arg.getBytes("UTF8"));	
		} catch (UnsupportedEncodingException uex) {
			return "";
		}		
	}
	
	public static String formatNewlyAddedWords(String words) {
		return replaceNewLineWithPhraseDelimiter(replaceTabsWithPhraseDelimiter(words)).trim();
	}
	
	public static void exitVM(int exitCode) {
		System.exit(exitCode);
	}	
}
