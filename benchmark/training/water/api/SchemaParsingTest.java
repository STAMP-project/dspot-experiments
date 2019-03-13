package water.api;


import org.junit.Test;


/**
 * Various tests for schema parsing.
 */
public class SchemaParsingTest {
    @Test
    public void testArrayParse() {
        String[] testCases = new String[]{ "null", "[]", "[1.0]", "[2.0]", "[1]", "[\"string\"]" };
        Class[] testClasses = new Class[]{ String[].class, String[].class, float[].class, double[].class, int[].class, String[].class };
        Object[] expectedValues = new Object[]{ null, new String[]{  }, new Float[]{ 1.0F }, new Double[]{ 2.0 }, new Integer[]{ 1 }, new String[]{ "string" } };
        for (int i = 0; i < (testCases.length); i++) {
            SchemaParsingTest.assertArrayEquals(testCases[i], testClasses[i], expectedValues[i]);
        }
    }

    @Test
    public void testSingleValueAsArrayParse() {
        String[] testCases = new String[]{ "null", "1.0", "\"string\"" };
        Class[] testClasses = new Class[]{ String[].class, float[].class, String[].class };
        Object[] expectedValues = new Object[]{ null, new Float[]{ 1.0F }, new String[]{ "string" } };
        for (int i = 0; i < (testCases.length); i++) {
            SchemaParsingTest.assertArrayEquals(testCases[i], testClasses[i], expectedValues[i]);
        }
    }
}

