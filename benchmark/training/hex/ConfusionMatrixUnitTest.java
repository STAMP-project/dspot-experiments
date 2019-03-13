package hex;


import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ProvideSystemProperty;
import org.junit.contrib.java.lang.system.RestoreSystemProperties;


public class ConfusionMatrixUnitTest {
    @Rule
    public final ProvideSystemProperty provideSystemProperty = new ProvideSystemProperty("sys.ai.h2o.cm.maxClasses", "7");

    @Rule
    public final RestoreSystemProperties restoreSystemProperties = new RestoreSystemProperties();

    @Test
    public void tooLarge() {
        Assert.assertFalse(new ConfusionMatrix(null, new String[7]).tooLarge());
        Assert.assertTrue(new ConfusionMatrix(null, new String[8]).tooLarge());
    }

    @Test
    public void maxClasses() {
        Assert.assertEquals(7, ConfusionMatrix.maxClasses());
    }

    @Test
    public void parseMaxClasses() {
        Assert.assertEquals(1000, ConfusionMatrix.parseMaxClasses("-1"));
        Assert.assertEquals(42, ConfusionMatrix.parseMaxClasses("42"));
        Assert.assertEquals(1000, ConfusionMatrix.parseMaxClasses("NA"));
    }
}

