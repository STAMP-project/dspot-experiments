package com.alibaba.csp.sentinel.config;


import SentinelConfig.COLD_FACTOR;
import SentinelConfig.DEFAULT_CHARSET;
import SentinelConfig.DEFAULT_COLD_FACTOR;
import SentinelConfig.DEFAULT_SINGLE_METRIC_FILE_SIZE;
import SentinelConfig.DEFAULT_STATISTIC_MAX_RT;
import SentinelConfig.DEFAULT_TOTAL_METRIC_FILE_COUNT;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test cases for {@link SentinelConfig}.
 *
 * @author cdfive
 */
public class SentinelConfigTest {
    @Test
    public void testDefaultConfig() {
        Assert.assertEquals(DEFAULT_CHARSET, SentinelConfig.charset());
        Assert.assertEquals(DEFAULT_SINGLE_METRIC_FILE_SIZE, SentinelConfig.singleMetricFileSize());
        Assert.assertEquals(DEFAULT_TOTAL_METRIC_FILE_COUNT, SentinelConfig.totalMetricFileCount());
        Assert.assertEquals(DEFAULT_COLD_FACTOR, SentinelConfig.coldFactor());
        Assert.assertEquals(DEFAULT_STATISTIC_MAX_RT, SentinelConfig.statisticMaxRt());
    }

    /**
     * when set code factor alue equal or smaller than 1, get value
     * in SentinelConfig.coldFactor() will return DEFAULT_COLD_FACTOR
     * see {@link SentinelConfig#coldFactor()}
     */
    @Test
    public void testColdFactorEqualOrSmallerThanOne() {
        SentinelConfig.setConfig(COLD_FACTOR, "0.5");
        Assert.assertEquals(DEFAULT_COLD_FACTOR, SentinelConfig.coldFactor());
        SentinelConfig.setConfig(COLD_FACTOR, "1");
        Assert.assertEquals(DEFAULT_COLD_FACTOR, SentinelConfig.coldFactor());
    }

    @Test
    public void testColdFactoryLargerThanOne() {
        SentinelConfig.setConfig(COLD_FACTOR, "2");
        Assert.assertEquals(2, SentinelConfig.coldFactor());
        SentinelConfig.setConfig(COLD_FACTOR, "4");
        Assert.assertEquals(4, SentinelConfig.coldFactor());
    }
}

