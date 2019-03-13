package test.junit;


import junit.framework.TestCase;


/**
 * Test that the correct number of constructors is called
 *
 * Created on Aug 9, 2005
 *
 * @author cbeust
 */
public class JUnitConstructorTest extends TestCase {
    private static int m_constructorCount = 0;

    private static int m_createCount = 0;

    private static int m_queryCount = 0;

    /* String string */
    public JUnitConstructorTest() {
        // super(string);
        // setName(string);
        JUnitConstructorTest.ppp("CONSTRUCTING");
        (JUnitConstructorTest.m_constructorCount)++;
    }

    // public void test1() {
    // ppp("TEST1");
    // }
    // 
    // public void test2() {
    // ppp("TEST2");
    // }
    public void testCreate() {
        JUnitConstructorTest.ppp("TEST_CREATE");
        (JUnitConstructorTest.m_createCount)++;
    }

    public void testQuery() {
        JUnitConstructorTest.ppp("TEST_QUERY");
        (JUnitConstructorTest.m_queryCount)++;
    }
}

