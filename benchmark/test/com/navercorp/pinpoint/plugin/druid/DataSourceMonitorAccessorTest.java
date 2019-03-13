package com.navercorp.pinpoint.plugin.druid;


import org.junit.Test;


public class DataSourceMonitorAccessorTest implements DataSourceMonitorAccessor {
    @Test
    public void test() {
        DataSourceMonitorAccessorTest test = new DataSourceMonitorAccessorTest();
        test._$PINPOINT$_setDataSourceMonitor(test._$PINPOINT$_getDataSourceMonitor());
    }
}

