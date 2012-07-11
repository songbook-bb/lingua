package wordtutor.container;


/**
 * describes the storage of the Words.
 * It allows to keep, access and change the Words,
 * and also it keeps the settings which are responsible
 * for operation with the word scores 
 */

public interface IWordContainer {
  /**
	* clears the storage 
   */
 public void clear();
 /**
  * adds the word to the storage 
  */
 public void add(IWord elem);
 /**
  * create a word with @param foreignWord foreign spelling
  * and @param nativeWord native spelling and adds it to the storage 
  */
 public void add(String foreignWord, String nativeWord,int idSound);
 /**
  * gets the word which has index @param i 
  */
 public IWord get(int i);
 /**
  * gets the size of the storage 
  */
 public int size();
 /**
  * fills the storage by elements of another storage @param container 
  */
 public void fill(IWordContainer container);
 /**
  * removes the word which has index @param index
  */
 public void remove(int index);
 
 /**
  *  fabric method for IWord
  *   returns instance of created word, implementing IWord interface  
  */
 public IWord createWord();
 /**
  *   fabric method for IWord
  *   returns instance of created with  foreignWord foreign spelling
  *   and nativeWord native spelling
  *   implementing IWord interface  
  */
 public IWord createWord(String foreignWord,String nativeWord, int idSound);
 /**
  * sets maximum score which the word can have
  */ 
}
