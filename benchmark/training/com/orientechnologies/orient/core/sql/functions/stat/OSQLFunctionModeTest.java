package com.orientechnologies.orient.core.sql.functions.stat;


import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class OSQLFunctionModeTest {
    private OSQLFunctionMode mode;

    @Test
    public void testEmpty() {
        Object result = mode.getResult();
        Assert.assertNull(result);
    }

    @Test
    public void testSingleMode() {
        int[] scores = new int[]{ 1, 2, 3, 3, 3, 2 };
        for (int s : scores) {
            mode.execute(null, null, null, new Object[]{ s }, null);
        }
        Object result = mode.getResult();
        Assert.assertEquals(3, ((int) (((List<Integer>) (result)).get(0))));
    }

    @Test
    public void testMultiMode() {
        int[] scores = new int[]{ 1, 2, 3, 3, 3, 2, 2 };
        for (int s : scores) {
            mode.execute(null, null, null, new Object[]{ s }, null);
        }
        Object result = mode.getResult();
        List<Integer> modes = ((List<Integer>) (result));
        Assert.assertEquals(2, modes.size());
        Assert.assertTrue(modes.contains(2));
        Assert.assertTrue(modes.contains(3));
    }

    @Test
    public void testMultiValue() {
        List[] scores = new List[2];
        scores[0] = Arrays.asList(new Integer[]{ 1, 2, null, 3, 4 });
        scores[1] = Arrays.asList(new Integer[]{ 1, 1, 1, 2, null });
        for (List s : scores) {
            mode.execute(null, null, null, new Object[]{ s }, null);
        }
        Object result = mode.getResult();
        Assert.assertEquals(1, ((int) (((List<Integer>) (result)).get(0))));
    }
}

