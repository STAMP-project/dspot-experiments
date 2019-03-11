package com.netflix.eureka.util;


import org.junit.Assert;
import org.junit.Test;


public class StatusUtilTest {
    @Test
    public void testGetStatusInfoHealthy() {
        StatusUtil statusUtil = getStatusUtil(3, 3, 2);
        Assert.assertTrue(statusUtil.getStatusInfo().isHealthy());
    }

    @Test
    public void testGetStatusInfoUnhealthy() {
        StatusUtil statusUtil = getStatusUtil(5, 3, 4);
        Assert.assertFalse(statusUtil.getStatusInfo().isHealthy());
    }

    @Test
    public void testGetStatusInfoUnsetHealth() {
        StatusUtil statusUtil = getStatusUtil(5, 3, (-1));
        StatusInfo statusInfo = statusUtil.getStatusInfo();
        try {
            statusInfo.isHealthy();
        } catch (NullPointerException e) {
            // Expected that the healthy flag is not set when the minimum value is -1
            return;
        }
        Assert.fail("Excpected NPE to be thrown when healthy threshold is not set");
    }
}

