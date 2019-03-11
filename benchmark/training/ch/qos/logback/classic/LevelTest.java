package ch.qos.logback.classic;


import Level.DEBUG;
import Level.ERROR;
import Level.INFO;
import Level.TRACE;
import Level.WARN;
import org.junit.Assert;
import org.junit.Test;


public class LevelTest {
    @Test
    public void smoke() {
        Assert.assertEquals(TRACE, Level.toLevel("TRACE"));
        Assert.assertEquals(DEBUG, Level.toLevel("DEBUG"));
        Assert.assertEquals(INFO, Level.toLevel("INFO"));
        Assert.assertEquals(WARN, Level.toLevel("WARN"));
        Assert.assertEquals(ERROR, Level.toLevel("ERROR"));
    }

    @Test
    public void withSpaceSuffix() {
        Assert.assertEquals(INFO, Level.toLevel("INFO "));
    }
}

