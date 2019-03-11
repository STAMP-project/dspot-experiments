package com.ctrip.framework.apollo.util;


import ConfigConsts.APOLLO_CLUSTER_KEY;
import java.io.File;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 *
 *
 * @author Jason Song(song_s@ctrip.com)
 */
public class ConfigUtilTest {
    @Test
    public void testApolloCluster() throws Exception {
        String someCluster = "someCluster";
        System.setProperty(APOLLO_CLUSTER_KEY, someCluster);
        ConfigUtil configUtil = new ConfigUtil();
        Assert.assertEquals(someCluster, configUtil.getCluster());
    }

    @Test
    public void testCustomizeConnectTimeout() throws Exception {
        int someConnectTimeout = 1;
        System.setProperty("apollo.connectTimeout", String.valueOf(someConnectTimeout));
        ConfigUtil configUtil = new ConfigUtil();
        Assert.assertEquals(someConnectTimeout, configUtil.getConnectTimeout());
    }

    @Test
    public void testCustomizeInvalidConnectTimeout() throws Exception {
        String someInvalidConnectTimeout = "a";
        System.setProperty("apollo.connectTimeout", someInvalidConnectTimeout);
        ConfigUtil configUtil = new ConfigUtil();
        Assert.assertTrue(((configUtil.getConnectTimeout()) > 0));
    }

    @Test
    public void testCustomizeReadTimeout() throws Exception {
        int someReadTimeout = 1;
        System.setProperty("apollo.readTimeout", String.valueOf(someReadTimeout));
        ConfigUtil configUtil = new ConfigUtil();
        Assert.assertEquals(someReadTimeout, configUtil.getReadTimeout());
    }

    @Test
    public void testCustomizeInvalidReadTimeout() throws Exception {
        String someInvalidReadTimeout = "a";
        System.setProperty("apollo.readTimeout", someInvalidReadTimeout);
        ConfigUtil configUtil = new ConfigUtil();
        Assert.assertTrue(((configUtil.getReadTimeout()) > 0));
    }

    @Test
    public void testCustomizeRefreshInterval() throws Exception {
        int someRefreshInterval = 1;
        System.setProperty("apollo.refreshInterval", String.valueOf(someRefreshInterval));
        ConfigUtil configUtil = new ConfigUtil();
        Assert.assertEquals(someRefreshInterval, configUtil.getRefreshInterval());
    }

    @Test
    public void testCustomizeInvalidRefreshInterval() throws Exception {
        String someInvalidRefreshInterval = "a";
        System.setProperty("apollo.refreshInterval", someInvalidRefreshInterval);
        ConfigUtil configUtil = new ConfigUtil();
        Assert.assertTrue(((configUtil.getRefreshInterval()) > 0));
    }

    @Test
    public void testCustomizeLoadConfigQPS() throws Exception {
        int someQPS = 1;
        System.setProperty("apollo.loadConfigQPS", String.valueOf(someQPS));
        ConfigUtil configUtil = new ConfigUtil();
        Assert.assertEquals(someQPS, configUtil.getLoadConfigQPS());
    }

    @Test
    public void testCustomizeInvalidLoadConfigQPS() throws Exception {
        String someInvalidQPS = "a";
        System.setProperty("apollo.loadConfigQPS", someInvalidQPS);
        ConfigUtil configUtil = new ConfigUtil();
        Assert.assertTrue(((configUtil.getLoadConfigQPS()) > 0));
    }

    @Test
    public void testCustomizeLongPollQPS() throws Exception {
        int someQPS = 1;
        System.setProperty("apollo.longPollQPS", String.valueOf(someQPS));
        ConfigUtil configUtil = new ConfigUtil();
        Assert.assertEquals(someQPS, configUtil.getLongPollQPS());
    }

    @Test
    public void testCustomizeInvalidLongPollQPS() throws Exception {
        String someInvalidQPS = "a";
        System.setProperty("apollo.longPollQPS", someInvalidQPS);
        ConfigUtil configUtil = new ConfigUtil();
        Assert.assertTrue(((configUtil.getLongPollQPS()) > 0));
    }

    @Test
    public void testCustomizeMaxConfigCacheSize() throws Exception {
        long someCacheSize = 1;
        System.setProperty("apollo.configCacheSize", String.valueOf(someCacheSize));
        ConfigUtil configUtil = new ConfigUtil();
        Assert.assertEquals(someCacheSize, configUtil.getMaxConfigCacheSize());
    }

    @Test
    public void testCustomizeInvalidMaxConfigCacheSize() throws Exception {
        String someInvalidCacheSize = "a";
        System.setProperty("apollo.configCacheSize", someInvalidCacheSize);
        ConfigUtil configUtil = new ConfigUtil();
        Assert.assertTrue(((configUtil.getMaxConfigCacheSize()) > 0));
    }

    @Test
    public void testCustomizeLongPollingInitialDelayInMills() throws Exception {
        long someLongPollingDelayInMills = 1;
        System.setProperty("apollo.longPollingInitialDelayInMills", String.valueOf(someLongPollingDelayInMills));
        ConfigUtil configUtil = new ConfigUtil();
        Assert.assertEquals(someLongPollingDelayInMills, configUtil.getLongPollingInitialDelayInMills());
    }

    @Test
    public void testCustomizeInvalidLongPollingInitialDelayInMills() throws Exception {
        String someInvalidLongPollingDelayInMills = "a";
        System.setProperty("apollo.longPollingInitialDelayInMills", someInvalidLongPollingDelayInMills);
        ConfigUtil configUtil = new ConfigUtil();
        Assert.assertTrue(((configUtil.getLongPollingInitialDelayInMills()) > 0));
    }

    @Test
    public void testCustomizeAutoUpdateInjectedSpringProperties() throws Exception {
        boolean someAutoUpdateInjectedSpringProperties = false;
        System.setProperty("apollo.autoUpdateInjectedSpringProperties", String.valueOf(someAutoUpdateInjectedSpringProperties));
        ConfigUtil configUtil = new ConfigUtil();
        Assert.assertEquals(someAutoUpdateInjectedSpringProperties, configUtil.isAutoUpdateInjectedSpringPropertiesEnabled());
    }

    @Test
    public void testLocalCacheDirWithSystemProperty() throws Exception {
        String someCacheDir = "someCacheDir";
        String someAppId = "someAppId";
        System.setProperty("apollo.cacheDir", someCacheDir);
        ConfigUtil configUtil = Mockito.spy(new ConfigUtil());
        Mockito.doReturn(someAppId).when(configUtil).getAppId();
        Assert.assertEquals(((someCacheDir + (File.separator)) + someAppId), configUtil.getDefaultLocalCacheDir());
    }

    @Test
    public void testDefaultLocalCacheDir() throws Exception {
        String someAppId = "someAppId";
        ConfigUtil configUtil = Mockito.spy(new ConfigUtil());
        Mockito.doReturn(someAppId).when(configUtil).getAppId();
        Mockito.doReturn(true).when(configUtil).isOSWindows();
        Assert.assertEquals(("C:\\opt\\data\\" + someAppId), configUtil.getDefaultLocalCacheDir());
        Mockito.doReturn(false).when(configUtil).isOSWindows();
        Assert.assertEquals(("/opt/data/" + someAppId), configUtil.getDefaultLocalCacheDir());
    }
}

