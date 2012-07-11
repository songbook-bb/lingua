package wordtutor.container;

import wordtutor.util.TranslateDirection;

/**
 * Describes the Word entity.
 * The Word contains its spelling in foreign(under study) language,
 * spelling in native language(translation), a current score of
 * the word, and provide possibility to check the answer given by user for
 * equivalence to the right answer    
 * @author moradan
 */
public interface IWord {
 /**
  * marks the word learned 	
  */
 public void setLearned(boolean isLearned);
 /**
  * checks for being learned
  */
 public boolean isLearned();
 /**
  * sets the foreign spelling of the word 
  */
 public	void setForeignWord(String foreignWord);
 /**
  * sets the native spelling of the word 
  */
 public	void setNativeWord(String foreignWord);
 /**
  * gets the native spelling of the word 
 */
 public String getNativeWord();
 /**
  * gets the foreign spelling of the word 
  */
 public String getForeignWord();
 /**
  * sets the exact value of score 
  */
 public void setScore(int score);
 /**
  * gets the current value of score 
  */
 public int getScore();
 /**
  * sets the exact value of idSound
  */
 public void setIdSound(int idSound);
 /**
  * gets the current value of idSound 
  */
 public int getIdSound();

 /**
  * checks the user's answer for corresponding the right answer
  * its work should depend on the current direction of the translating.
  * If the direction is straight, this method should compare the answer of user
  * with all variants of translations of the word.
  * If the direction is reverse, then it should compare the answer just with
  * the foreign spelling

  * @param answer is the user's answer
  * @param direction is a direction of translation - straight or reverse
  */
 public boolean checkAnswer(String answer,TranslateDirection direction);
 
}
