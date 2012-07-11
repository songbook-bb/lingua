package wordtutor.indexer;
import java.util.*;
/**
 * implements the IWordIndexer interface
 * uses java.util.Vector to store the indexes
 * 
 */
public class WordVectorIndexer implements IWordIndexer{
	Vector<Integer> items = new Vector<Integer>();
     public void clear(){items.clear();}	
	 public void add(int index){items.add(index);}
	 public int get(int i){return items.get(i);}
	 public int size(){return items.size();}
	 public void remove (int i){items.remove(i);}
}
