/**
 *
 */
package water.api;


import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import water.api.Request.API;
import water.api.RequestArguments.Argument;


/**
 * The objective of test is to test a stability of API.
 *
 * It is filled by REST api calls and their arguments used by Python/R code.
 * The test tests if the arguments are published by REST API in Java code.
 *
 * Note: this is a pure JUnit test, no cloud is launched
 */
public class StableAPITest {
    /**
     * Mapping between REST API methods and their attributes used by Python code.
     */
    static Map<Class<? extends Request>, String[]> pyAPI = new HashMap<Class<? extends Request>, String[]>();

    /**
     * Mapping between REST API methods and their attributes used by R code.
     */
    static Map<Class<? extends Request>, String[]> rAPI = new HashMap<Class<? extends Request>, String[]>();

    /**
     * Test compatibility of defined Python calls with REST API published by Java code.
     */
    @Test
    public void testPyAPICompatibility() {
        testAPICompatibility("Python API", StableAPITest.pyAPI);
    }

    /**
     * Test compatibility of defined R calls with REST API published by Java code.
     */
    @Test
    public void testRAPICompatibility() {
        testAPICompatibility("R API", StableAPITest.rAPI);
    }

    abstract static class FFilter {
        abstract boolean involve(Field f);
    }

    static StableAPITest.FFilter Request1FFilter = new StableAPITest.FFilter() {
        @Override
        boolean involve(Field f) {
            return Argument.class.isAssignableFrom(f.getType());
        }
    };

    static StableAPITest.FFilter Request2FFilter = new StableAPITest.FFilter() {
        @Override
        boolean involve(Field f) {
            return StableAPITest.contains(API.class, f.getDeclaredAnnotations());
        }
    };
}

