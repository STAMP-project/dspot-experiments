package azkaban.db;


import azkaban.metrics.MetricsTestUtility;
import org.junit.Assert;
import org.junit.Test;


public class DBMetricsTest {
    private MetricsTestUtility testUtil;

    private DBMetrics metrics;

    @Test
    public void testDBConnectionTimeMetrics() {
        this.metrics.setDBConnectionTime(14);
        Assert.assertEquals(14, this.testUtil.getGaugeValue("dbConnectionTime"));
    }
}

