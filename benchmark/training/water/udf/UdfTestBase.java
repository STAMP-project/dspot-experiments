package water.udf;


import org.junit.Assert;
import org.junit.Test;
import water.TestUtil;


/**
 * All test functionality specific for udf (not actually),
 * not kosher enough to be allowed for the general public
 */
public class UdfTestBase extends TestUtil {
    {
        ClassLoader loader = getClass().getClassLoader();
        loader.setDefaultAssertionStatus(true);
    }

    // the following code exists or else gradlew will complain; also, it checks assertions
    @Test
    public void testAssertionsEnabled() throws Exception {
        try {
            assert false : "Should throw";
            Assert.fail("Expected an assertion error");
        } catch (AssertionError ae) {
            // as designed
        }
    }
}

