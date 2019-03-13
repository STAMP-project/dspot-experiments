package com.navercorp.pinpoint.plugin.hbase;


import HbasePluginConstants.HBASE_ASYNC_CLIENT;
import HbasePluginConstants.HBASE_CLIENT;
import HbasePluginConstants.HBASE_CLIENT_ADMIN;
import HbasePluginConstants.HBASE_CLIENT_ADMIN_CONFIG;
import HbasePluginConstants.HBASE_CLIENT_CONFIG;
import HbasePluginConstants.HBASE_CLIENT_PARAMS;
import HbasePluginConstants.HBASE_CLIENT_PARAMS_CONFIG;
import HbasePluginConstants.HBASE_CLIENT_SCOPE;
import HbasePluginConstants.HBASE_CLIENT_TABLE;
import HbasePluginConstants.HBASE_CLIENT_TABLE_CONFIG;
import HbasePluginConstants.HBASE_DESTINATION_ID;
import org.junit.Assert;
import org.junit.Test;


public class HbasePluginConstantsTest {
    @Test
    public void test() {
        Assert.assertEquals(HBASE_CLIENT.getCode(), 8800);
        Assert.assertEquals(HBASE_CLIENT_ADMIN.getCode(), 8801);
        Assert.assertEquals(HBASE_CLIENT_TABLE.getCode(), 8802);
        Assert.assertEquals(HBASE_ASYNC_CLIENT.getCode(), 8803);
        Assert.assertEquals(HBASE_CLIENT_PARAMS.getCode(), 320);
        Assert.assertEquals(HBASE_CLIENT_SCOPE, "HBASE_CLIENT_SCOPE");
        Assert.assertEquals(HBASE_DESTINATION_ID, "HBASE");
        Assert.assertEquals(HBASE_CLIENT_CONFIG, "profiler.hbase.client.enable");
        Assert.assertEquals(HBASE_CLIENT_ADMIN_CONFIG, "profiler.hbase.client.admin.enable");
        Assert.assertEquals(HBASE_CLIENT_TABLE_CONFIG, "profiler.hbase.client.table.enable");
        Assert.assertEquals(HBASE_CLIENT_PARAMS_CONFIG, "profiler.hbase.client.params.enable");
    }
}

