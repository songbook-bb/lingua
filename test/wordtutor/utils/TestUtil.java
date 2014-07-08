/**
 * 
 */
package wordtutor.utils;


import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import wordtutor.utils.Util;
import junit.framework.TestCase;

/**
 * @author qn
 *
 */
public class TestUtil extends TestCase {
	public static Util utilInstance;
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Before
	protected void setUp() throws Exception {
		super.setUp();
		utilInstance = new Util();
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	@After
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Test
	public void test() {
		Util.loadAppProperties();
		System.out.println(Util.getAppProperties());
	}
	@Test
	public void testTabsWithPhraseDelimiter() {
		String inputTestString = " \t ";
		String resultTestString = Util.replaceTabsWithPhraseDelimiter(inputTestString);
		assertEquals(resultTestString, Util.PHRASE_DELIMITER+Util.SPACE);
		inputTestString = "Tomasz 	 Kazimierz 	 Vincent 	 Nakonieczny";
		resultTestString = Util.replaceTabsWithPhraseDelimiter(inputTestString);
		assertEquals(resultTestString, "Tomasz"+Util.PHRASE_DELIMITER+Util.SPACE+ 	 "Kazimierz"+Util.PHRASE_DELIMITER+Util.SPACE+ 	 "Vincent"+Util.PHRASE_DELIMITER+Util.SPACE+ 	 "Nakonieczny");		
		//System.out.println(Util.getAppProperties());
	}

	
}
