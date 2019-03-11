package test.junit4;


import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;


/**
 *
 *
 * @author lukas
 */
public class JUnit4Sample2 {
    public static final String[] EXPECTED = new String[]{ "t2", "t4" };

    public static final String[] SKIPPED = new String[]{ "t3", "ta" };

    public static final String[] FAILED = new String[]{ "tf" };

    @Test
    public void tf() {
        Assert.fail("a test");
    }

    @Test
    public void ta() {
        Assume.assumeTrue(false);
    }
}

