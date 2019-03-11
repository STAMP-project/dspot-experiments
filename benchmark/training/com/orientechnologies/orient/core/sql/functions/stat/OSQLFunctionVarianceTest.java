package com.orientechnologies.orient.core.sql.functions.stat;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class OSQLFunctionVarianceTest {
    private OSQLFunctionVariance variance;

    @Test
    public void testEmpty() {
        Object result = variance.getResult();
        Assert.assertNull(result);
    }

    @Test
    public void testVariance() {
        Integer[] scores = new Integer[]{ 4, 7, 15, 3 };
        for (Integer s : scores) {
            variance.execute(null, null, null, new Object[]{ s }, null);
        }
        Object result = variance.getResult();
        Assert.assertEquals(22.1875, result);
    }

    @Test
    public void testVariance1() {
        Integer[] scores = new Integer[]{ 4, 7 };
        for (Integer s : scores) {
            variance.execute(null, null, null, new Object[]{ s }, null);
        }
        Object result = variance.getResult();
        Assert.assertEquals(2.25, result);
    }

    @Test
    public void testVariance2() {
        Integer[] scores = new Integer[]{ 15, 3 };
        for (Integer s : scores) {
            variance.execute(null, null, null, new Object[]{ s }, null);
        }
        Object result = variance.getResult();
        Assert.assertEquals(36.0, result);
    }

    @Test
    public void testDistributed() {
        Map<String, Object> doc1 = new HashMap<String, Object>();
        doc1.put("n", 2L);
        doc1.put("mean", 5.5);
        doc1.put("var", 2.25);
        Map<String, Object> doc2 = new HashMap<String, Object>();
        doc2.put("n", 2L);
        doc2.put("mean", 9.0);
        doc2.put("var", 36.0);
        List<Object> results = new ArrayList<Object>(2);
        results.add(doc1);
        results.add(doc2);
        Assert.assertEquals(22.1875, mergeDistributedResult(results));
    }
}

