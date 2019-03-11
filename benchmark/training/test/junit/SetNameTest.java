package test.junit;


import junit.framework.TestCase;
import org.testng.Assert;


public class SetNameTest extends TestCase {
    public static int m_ctorCount = 0;

    public SetNameTest() {
        ppp("CTOR");
        (SetNameTest.m_ctorCount)++;
    }

    public void testFoo() {
        Assert.assertEquals("testFoo", getName());
        ppp("FOO");
    }

    public void testBar() {
        Assert.assertEquals("testBar", getName());
        ppp("BAR");
    }
}

