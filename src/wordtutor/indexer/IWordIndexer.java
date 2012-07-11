package wordtutor.indexer;
/**
 * describes the indexer of the words.
 * indexer is used to create a lesson.
 * it keeps just a indexes of the words belonging to storage
 * 
 * @author moradan
 */
public interface IWordIndexer {
	/**
	 * clear the indexer
	 */
	public void clear();
	/**
	 * add new index 
	 */
	 public void add(int index);
	 /**
	  * gets the index of the word which have number i in the indexer
	  */
	 public int get(int i);
	 /**
	  *  gets size of the indexer
	  */
	 public int size();
	 /**
	  * 
	  * remove word from the indexer
	  */
	 public void remove (int i);
}
