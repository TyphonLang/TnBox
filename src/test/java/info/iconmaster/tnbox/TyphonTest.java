package info.iconmaster.tnbox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 * The base class for Typhon tests.
 * 
 * @author iconmaster
 *
 */
@Ignore
@RunWith(TyphonTestRunner.class)
public abstract class TyphonTest {
	@Parameterized.Parameters
    public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {});
	}
    
    /**
     * A convenience method for setting up data().
     * 
     * @param testCases
     * @return
     */
    public static Collection<Object[]> makeData(Runnable... testCases) {
    	ArrayList<Object[]> result = new ArrayList<>();
    	for (Runnable testCase : testCases) {
    		result.add(new Object[] {testCase});
    	}
    	return result;
    }
	
	@Parameterized.Parameter
	public Runnable r;
	
	@Test
	public void test() {
		r.run();
	}
}
