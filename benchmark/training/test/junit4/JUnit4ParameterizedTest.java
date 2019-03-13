package test.junit4;


import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class JUnit4ParameterizedTest {
    public static final String[] EXPECTED = new String[]{ "t2[0]", "t2[1]", "t4[0]" };

    public static final String[] SKIPPED = new String[]{ "t3[0]", "t3[1]", "ta[0]", "ta[1]" };

    public static final String[] FAILED = new String[]{ "t4[1]", "tf[0]", "tf[1]" };

    private int param;

    public JUnit4ParameterizedTest(int param) {
        this.param = param;
    }

    @Test
    public void t4() {
        if ((param) == 5) {
            Assert.fail("a test");
        }
    }

    @Test
    public void tf() {
        Assert.fail("a test");
    }

    @Test
    public void ta() {
        Assume.assumeTrue(false);
    }
}

