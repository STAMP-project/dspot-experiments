package water.util;


import JavaVersionUtils.UNKNOWN;
import org.junit.Assert;
import org.junit.Test;


/**
 * Java version string parser test.
 */
public class JavaVersionUtilsTest {
    @Test
    public void testPreJava10Parsing() {
        // OpenJDK 7
        Assert.assertEquals(7, JavaVersionUtils.JAVA_VERSION.parseMajor("1.7.0_75"));
        // Oracle JDK 8
        Assert.assertEquals(8, JavaVersionUtils.JAVA_VERSION.parseMajor("1.8.0_151"));
    }

    @Test
    public void testJava10AndLaterParsing() {
        // OpenJDK 9
        Assert.assertEquals(9, JavaVersionUtils.JAVA_VERSION.parseMajor("9"));
        // Oracle JDK 10
        Assert.assertEquals(10, JavaVersionUtils.JAVA_VERSION.parseMajor("10.0.2"));
    }

    @Test
    public void testNegative() {
        Assert.assertEquals(UNKNOWN, JavaVersionUtils.JAVA_VERSION.parseMajor(null));
        Assert.assertEquals(UNKNOWN, JavaVersionUtils.JAVA_VERSION.parseMajor(""));
        Assert.assertEquals(UNKNOWN, JavaVersionUtils.JAVA_VERSION.parseMajor("x"));
    }
}

