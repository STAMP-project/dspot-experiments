package water.parser;


import org.junit.Test;
import water.TestUtil;


/**
 * Test parser service.
 */
public class ParserServiceTest extends TestUtil {
    // A list of REGISTERED core provider names in the expected order based on priorities.
    // Warning: The order is fixed in the test to detect any changes in the code!!!
    private static final String[] CORE_PROVIDER_NAMES = new String[]{ "GUESS", "ARFF", "XLS", "SVMLight", "CSV" };

    @Test
    public void testVerifyCoreProvidersInCaller() {
        ParserServiceTest.verifyCoreProviders();
    }

    @Test
    public void testVerifyCoreProvidersPerNode() {
        doAllNodes();
    }
}

