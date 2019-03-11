package org.stagemonitor.alerting;


import Threshold.Operator;
import Threshold.Operator.GREATER;
import org.junit.Assert;
import org.junit.Test;
import org.stagemonitor.alerting.check.Threshold;
import org.stagemonitor.core.util.JsonUtils;


public class ThresholdTest {
    @Test
    public void testLess() throws Exception {
        Threshold threshold = new Threshold("value", Operator.LESS, 0);
        Assert.assertFalse(threshold.isExceeded((-1)));
        Assert.assertTrue(threshold.isExceeded(0));
        Assert.assertTrue(threshold.isExceeded(1));
    }

    @Test
    public void testLessEqual() throws Exception {
        Threshold threshold = new Threshold("value", Operator.LESS_EQUAL, 0);
        Assert.assertFalse(threshold.isExceeded((-1)));
        Assert.assertFalse(threshold.isExceeded(0));
        Assert.assertTrue(threshold.isExceeded(1));
    }

    @Test
    public void testGreater() throws Exception {
        Threshold threshold = new Threshold("value", Operator.GREATER, 0);
        Assert.assertTrue(threshold.isExceeded((-1)));
        Assert.assertTrue(threshold.isExceeded(0));
        Assert.assertFalse(threshold.isExceeded(1));
    }

    @Test
    public void testGreaterEqual() throws Exception {
        Threshold threshold = new Threshold("value", Operator.GREATER_EQUAL, 0);
        Assert.assertTrue(threshold.isExceeded((-1)));
        Assert.assertFalse(threshold.isExceeded(0));
        Assert.assertFalse(threshold.isExceeded(1));
    }

    @Test
    public void testJson() throws Exception {
        final String json = JsonUtils.toJson(new Threshold("value", Operator.GREATER, 0));
        Assert.assertEquals(GREATER, JsonUtils.getMapper().readValue(json, Threshold.class).getOperator());
    }
}

