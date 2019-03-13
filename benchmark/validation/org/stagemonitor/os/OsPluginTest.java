package org.stagemonitor.os;


import org.hyperic.sigar.Sigar;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.stagemonitor.core.metrics.metrics2.Metric2Registry;
import org.stagemonitor.junit.ConditionalTravisTestRunner;
import org.stagemonitor.junit.ExcludeOnTravis;


@RunWith(ConditionalTravisTestRunner.class)
public class OsPluginTest {
    private Metric2Registry metricRegistry;

    private Sigar sigar;

    private static OsPlugin osPlugin;

    @Test
    public void testCpuInfo() throws Exception {
        Assert.assertTrue(((getIntGauge(name("cpu_info_mhz").build())) > 0));
        Assert.assertTrue(((getIntGauge(name("cpu_info_cores").build())) > 0));
    }

    @Test
    public void testMemoryUsage() throws Exception {
        Assert.assertEquals(getLongGauge(name("mem_usage").type("total").build()), ((getLongGauge(name("mem_usage").type("used").build())) + (getLongGauge(name("mem_usage").type("free").build()))));
        final double usage = getDoubleGauge(name("mem_usage_percent").build());
        Assert.assertTrue(Double.toString(usage), (usage >= 0));
        Assert.assertTrue(Double.toString(usage), (usage <= 100));
    }

    @Test
    public void testSwapUsage() throws Exception {
        Assert.assertEquals(getLongGauge(name("swap_usage").type("total").build()), ((getLongGauge(name("swap_usage").type("used").build())) + (getLongGauge(name("swap_usage").type("free").build()))));
        double swapPercent = getDoubleGauge(name("swap_usage_percent").build());
        Assert.assertTrue(((swapPercent >= 0) || (Double.isNaN(swapPercent))));
        Assert.assertTrue(((swapPercent <= 100) || (Double.isNaN(swapPercent))));
        Assert.assertTrue(((getLongGauge(name("swap_pages").type("in").build())) >= 0));
        Assert.assertTrue(((getLongGauge(name("swap_pages").type("out").build())) >= 0));
    }

    @Test
    public void testGetConfigurationValid() throws Exception {
        Assert.assertEquals("bar", OsConfigurationSourceInitializer.getConfiguration(new String[]{ "foo=bar" }).getValue("foo"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetConfigurationInvalid() throws Exception {
        OsConfigurationSourceInitializer.getConfiguration(new String[]{ "foo" });
    }

    @Test
    @ExcludeOnTravis
    public void testNetworkMetrics() throws Exception {
        final String ifname = sigar.getNetRouteList()[0].getIfname();
        Assert.assertTrue(((getLongGauge(name("network_io").tag("ifname", ifname).type("read").unit("bytes").build())) >= 0));
        Assert.assertTrue(((getLongGauge(name("network_io").tag("ifname", ifname).type("read").unit("packets").build())) >= 0));
        Assert.assertTrue(((getLongGauge(name("network_io").tag("ifname", ifname).type("read").unit("errors").build())) >= 0));
        Assert.assertTrue(((getLongGauge(name("network_io").tag("ifname", ifname).type("read").unit("dropped").build())) >= 0));
        Assert.assertTrue(((getLongGauge(name("network_io").tag("ifname", ifname).type("write").unit("bytes").build())) >= 0));
        Assert.assertTrue(((getLongGauge(name("network_io").tag("ifname", ifname).type("write").unit("packets").build())) >= 0));
        Assert.assertTrue(((getLongGauge(name("network_io").tag("ifname", ifname).type("write").unit("errors").build())) >= 0));
        Assert.assertTrue(((getLongGauge(name("network_io").tag("ifname", ifname).type("write").unit("dropped").build())) >= (-1)));
    }
}

