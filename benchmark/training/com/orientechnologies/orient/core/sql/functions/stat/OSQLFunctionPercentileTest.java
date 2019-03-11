package com.orientechnologies.orient.core.sql.functions.stat;


import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class OSQLFunctionPercentileTest {
    private OSQLFunctionPercentile percentile;

    @Test
    public void testEmpty() {
        Object result = percentile.getResult();
        Assert.assertNull(result);
    }

    @Test
    public void testSingleValueLower() {
        percentile.execute(null, null, null, new Object[]{ 10, 0.25 }, null);
        Assert.assertEquals(10, percentile.getResult());
    }

    @Test
    public void testSingleValueUpper() {
        percentile.execute(null, null, null, new Object[]{ 10, 0.75 }, null);
        Assert.assertEquals(10, percentile.getResult());
    }

    @Test
    public void test50thPercentileOdd() {
        int[] scores = new int[]{ 1, 2, 3, 4, 5 };
        for (int s : scores) {
            percentile.execute(null, null, null, new Object[]{ s, 0.5 }, null);
        }
        Object result = percentile.getResult();
        Assert.assertEquals(3.0, result);
    }

    @Test
    public void test50thPercentileOddWithNulls() {
        Integer[] scores = new Integer[]{ null, 1, 2, null, 3, 4, null, 5 };
        for (Integer s : scores) {
            percentile.execute(null, null, null, new Object[]{ s, 0.5 }, null);
        }
        Object result = percentile.getResult();
        Assert.assertEquals(3.0, result);
    }

    @Test
    public void test50thPercentileEven() {
        int[] scores = new int[]{ 1, 2, 4, 5 };
        for (int s : scores) {
            percentile.execute(null, null, null, new Object[]{ s, 0.5 }, null);
        }
        Object result = percentile.getResult();
        Assert.assertEquals(3.0, result);
    }

    @Test
    public void testFirstQuartile() {
        int[] scores = new int[]{ 1, 2, 3, 4, 5 };
        for (int s : scores) {
            percentile.execute(null, null, null, new Object[]{ s, 0.25 }, null);
        }
        Object result = percentile.getResult();
        Assert.assertEquals(1.5, result);
    }

    @Test
    public void testThirdQuartile() {
        int[] scores = new int[]{ 1, 2, 3, 4, 5 };
        for (int s : scores) {
            percentile.execute(null, null, null, new Object[]{ s, 0.75 }, null);
        }
        Object result = percentile.getResult();
        Assert.assertEquals(4.5, result);
    }

    @Test
    public void testMultiQuartile() {
        int[] scores = new int[]{ 1, 2, 3, 4, 5 };
        for (int s : scores) {
            percentile.execute(null, null, null, new Object[]{ s, 0.25, 0.75 }, null);
        }
        List<Number> result = ((List<Number>) (percentile.getResult()));
        Assert.assertEquals(1.5, result.get(0).doubleValue(), 0);
        Assert.assertEquals(4.5, result.get(1).doubleValue(), 0);
    }
}

