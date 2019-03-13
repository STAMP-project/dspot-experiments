package com.navercorp.pinpoint.plugin.druid;


import DruidConstants.PLUGIN_ENABLE;
import DruidConstants.PROFILE_CONNECTIONCLOSE_ENABLE;
import DruidConstants.SCOPE;
import DruidConstants.SERVICE_TYPE;
import org.junit.Assert;
import org.junit.Test;


public class DruidConstantsTest {
    @Test
    public void test() {
        Assert.assertEquals(SCOPE, "DRUID_SCOPE");
        Assert.assertEquals(SERVICE_TYPE.getName(), "DRUID");
        Assert.assertEquals("com.navercorp.pinpoint.plugin.druid.DataSourceMonitorAccessor", "com.navercorp.pinpoint.plugin.druid.DataSourceMonitorAccessor");
        Assert.assertEquals("com.navercorp.pinpoint.plugin.druid.interceptor.DataSourceConstructorInterceptor", "com.navercorp.pinpoint.plugin.druid.interceptor.DataSourceConstructorInterceptor");
        Assert.assertEquals("com.navercorp.pinpoint.plugin.druid.interceptor.DataSourceCloseInterceptor", "com.navercorp.pinpoint.plugin.druid.interceptor.DataSourceCloseInterceptor");
        Assert.assertEquals("com.navercorp.pinpoint.plugin.druid.interceptor.DataSourceGetConnectionInterceptor", "com.navercorp.pinpoint.plugin.druid.interceptor.DataSourceGetConnectionInterceptor");
        Assert.assertEquals("com.navercorp.pinpoint.plugin.druid.interceptor.DataSourceCloseConnectionInterceptor", "com.navercorp.pinpoint.plugin.druid.interceptor.DataSourceCloseConnectionInterceptor");
        Assert.assertEquals(PLUGIN_ENABLE, "profiler.jdbc.druid");
        Assert.assertEquals(PROFILE_CONNECTIONCLOSE_ENABLE, "profiler.jdbc.druid.connectionclose");
    }
}

