package junit.samples;


import junit.framework.TestCase;


/**
 * Some simple tests.
 */
public class SimpleTest extends TestCase {
    protected int fValue1;

    protected int fValue2;

    public void testAdd() {
        double result = (fValue1) + (fValue2);
        // forced failure result == 5
        TestCase.assertTrue((result == 6));
    }

    public int unused;

    public void testDivideByZero() {
        int zero = 0;
        int result = 8 / zero;
        unused = result;// avoid warning for not using result

    }

    public void testEquals() {
        TestCase.assertEquals(12, 12);
        TestCase.assertEquals(12L, 12L);
        TestCase.assertEquals(new Long(12), new Long(12));
        TestCase.assertEquals("Size", 12, 13);
        TestCase.assertEquals("Capacity", 12.0, 11.99, 0.0);
    }
}

