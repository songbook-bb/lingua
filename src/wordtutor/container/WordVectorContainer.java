package wordtutor.container;
import java.util.*;
import wordtutor.util.TranslateDirection;

/**
 * Implements the IWordContainer interface.
 * Has a nested class - WordElem which implements IWord interface
 * nested class can access to the fields of his owner and it uses
 * the maxScore, incScore and decScores fields to work with
 * score of the word
 * The storage uses java.util.vector to keep the words
 * @author moradan  
 */
public class WordVectorContainer implements IWordContainer{

	 /**
	  * Nested class implementing IWord interface.
	  * Uses the maxScore, incScore and decScores fields of owner class to work with
      * score of the word
	  */
	class WordElem implements IWord
	{
		private String foreignWord;  
		private String nativeWord;  
		private int score;                  
		private int idSound;		
		private boolean isLearned;  
		
		/**
		 * zeros the score of the word and marks it not learned
		 *  
		 */
		private void initStatistics()
		{
			this.score = 0;
			this.isLearned = false;
		}
		/** default constructor
		 *  zeros the foreign and native spelling
		 */
		public WordElem() 
		{
			this.foreignWord = "";
			this.nativeWord = "";
		    initStatistics();
		}
		/**  constructor which creates the word 
		 *  with foreign spelling  and native spelling 
		 */
		public WordElem(String foreignWord,String nativeWord, int idSound)
		{
		 this.foreignWord = foreignWord;
		 this.nativeWord = nativeWord;
		 this.idSound = idSound;
		 initStatistics();
		}

		/** marks the word learned
		 *  
		 */
		public void setLearned(boolean isLearned)
		{
			this.isLearned = isLearned;
		}
		
		 /**
		  * tells if word is learned
		  */
		public boolean isLearned()
		{
			return isLearned;
		}
		
		/**
		  * sets the foreign spelling of the word 
		  */
	    public	void setForeignWord(String foreignWord)
		{
			this.foreignWord = foreignWord;
		}
	    /**
	     * gets the native spelling of the word 
	     */
		public String getNativeWord()
		{
			return this.nativeWord;
		}
	    /**
	     * sets the native spelling of the word 
	     */
    	public void setNativeWord(String nativeWord)
		{
			this.nativeWord = nativeWord;
		}
	    /**
	     * gets the foreign spelling of the word 
	     */
		public String getForeignWord()
		{
			return this.foreignWord;
		}
	    /**
	     * gets the current score of the word 
	     */
		public int getScore()
		{
			return this.score;
		}
	    /**
	     * sets the score of the word 
	     */
		public void setScore(int score)
		{
			this.score = score;
		}
	    /**
	     * gets the idSound of the word 
	     */
		public int getIdSound()
		{
			return this.idSound;
		}
	    /**
	     * sets the idSound of the word 
	     */
		public void setIdSound(int idSound)
		{
			this.idSound = idSound;
		}		
		/**
	     * compares the words 
	     */
//		private boolean wordEqual(String word1,String word2)
//		{
//			return word1.trim().equals(word2.trim());
//		}
		
		 /**
		  * checks the user's answer for corresponding the right answer
		  * its work depends on the current direction of the translating.
		  * If the direction is straight, this method will compare the answer of user
		  * with all variants of translations of the word.
		  * If the direction is reverse, then it will compare the answer just with
		  * the foreign spelling
		  * 
		  * @param answer is the user's answer
		  * @param direction is a direction of translation - straight or reverse
    	  */
    	public boolean checkAnswer(String answer,TranslateDirection direction)
		{
			boolean isRight = true;
//			if (direction == TranslateDirection.STRAIGHT)
//			{
//				String[] nativeWords = nativeWord.split(";");
//				int cnt = nativeWords.length;
//				for(int i=0;i<cnt;i++)
//				{
//					isRight = wordEqual(answer,nativeWords[i]);
//					if(isRight) break;
//				}
//			}
//			else
//			{
//				isRight = wordEqual(answer,foreignWord);
//			}
			
			if (isRight) {
				setLearned(true);
			}
			return isRight;
		}
	}
//	private int  maxScore;
//	private int  incScore;
//	private int  decScore;
	/**
	 * storage of the word
	 */
	private Vector<IWord> items;
	
	/**
	 * fabric method creating the new Word
	 */
	public IWord createWord()
	{
		return new WordElem();
	}
	/**
	 * fabric method creating the new Word
	  *   returns instance of created with  foreignWord foreign spelling
	  *   and nativeWord native spelling
       */
	public IWord createWord(String foreignWord,String nativeWord,int idSound)
	{
		return new WordElem(foreignWord, nativeWord, idSound);
	}
	
	public void add(String foreignWord, String nativeWord, int idSound)
	{
		IWord newWord = new WordElem(foreignWord, nativeWord, idSound);
		items.add(newWord);
	}
	
	public WordVectorContainer()
	{
      items = new Vector<IWord>();		
	}
	 public void add(IWord elem)
	 {
		items.add(elem); 
	 }
	 public IWord get(int i) 
	 {
		 return items.get(i);
     }
	 public int size()
	 {
		 return items.size();
	 }
	 public void clear()
	 {
		items.clear(); 
	 }
	 public void fill(IWordContainer container)
	 {
		 int cnt = container.size();
		 for(int i=0;i<cnt;i++)
		 {
			 add(container.get(i));
		 }	 
	 }
	 
	 public void remove(int index)
	 {
		 items.remove(index);
	 }

}
