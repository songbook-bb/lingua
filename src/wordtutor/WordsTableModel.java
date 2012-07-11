package wordtutor;

import javax.swing.table.DefaultTableModel;

import wordtutor.container.IWordContainer;
import wordtutor.utils.Util;

/**
 * represents the model of the JTable consisting of the words 
 * 
 * @author moradan
 *
 */
class WordsTableModel extends DefaultTableModel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2260658973627500139L;
	/**
	 * refers to the word storage 
	 */
	IWordContainer words;
	/**
	 * names of the columns
	 */
	
	Util util = new Util();
	
	protected String[] colName = {Util.getLocalizedString("WTM.NUMBER"),Util.getLocalizedString("WTM.FOREIGN"),Util.getLocalizedString("WTM.NATIVE"),
			Util.getLocalizedString("WTM.SCORE"),Util.getLocalizedString("WTM.LEARNED"), Util.getLocalizedString("WTM.ID_SOUND")};
	/**
	 * classes of the columns
	 */
	@SuppressWarnings({ "rawtypes" })
	protected Class[] colClass = new Class[]{Integer.class,String.class,String.class,Integer.class,Boolean.class, Integer.class};
	/**
	 * default constructor, setting the number of the columns
	 */
	public WordsTableModel()
	{
		super(0,6);
	}
	/**
	 * assigns the word storage
	 * 
	 */
	public void assignWordContainer(IWordContainer words)
	{
		this.words = words;
	}
	/**
	 * returns column name
	 */
	public String getColumnName(int col){return colName[col];}
	/**
	 * returns column class
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Class getColumnClass(int col){return colClass[col];}
	/**
	 * checks if the cell is editable
	 */
	public boolean isCellEditable(int row,int col){return false;}
	/**
	 * getting the value of current cell.
	 * uses the word storage directly
	 */
	public Object getValueAt(int row,int col)
	{
		switch(col)
		{
		case 0: return new Integer(row+1);
		case 1: return new String(words.get(row).getForeignWord());
		case 2: return new String( words.get(row).getNativeWord());
		case 3: return new Integer(words.get(row).getScore());
		case 4: return new Boolean(words.get(row).isLearned());
		case 5: return new Integer(words.get(row).getIdSound());		
		default: return null;
		}
		
	}
	                                       
}
