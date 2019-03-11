package aima.test.core.unit.search.framework;


import aima.core.search.framework.Metrics;
import org.junit.Assert;
import org.junit.Test;


public class MetricsTest {
    private Metrics metrics;

    @Test
    public void testGetInt() {
        int x = 893597823;
        metrics.set("abcd", x);
        Assert.assertEquals(x, metrics.getInt("abcd"));
        Assert.assertNotEquals(1234, metrics.getInt("abcd"));
    }

    @Test
    public void testGetDouble() {
        double x = 1.2313972352344846E12;
        metrics.set("abcd", x);
        Assert.assertEquals(x, metrics.getDouble("abcd"), 0);
        Assert.assertNotEquals(1234.56789, metrics.getDouble("abcd"), 0);
    }

    @Test
    public void testGetLong() {
        long x = 893597823;
        metrics.set("abcd", x);
        Assert.assertEquals(x, metrics.getLong("abcd"));
        Assert.assertNotEquals(841356458, metrics.getLong("abcd"));
    }

    @Test
    public void testGet() {
        int x = 123;
        metrics.set("abcd", x);
        Assert.assertEquals("123", metrics.get("abcd"));
        Assert.assertNotEquals("1234", metrics.get("abcd"));
    }
}

