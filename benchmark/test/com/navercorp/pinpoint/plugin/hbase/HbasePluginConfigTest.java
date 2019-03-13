package com.navercorp.pinpoint.plugin.hbase;


import com.navercorp.pinpoint.bootstrap.config.DefaultProfilerConfig;
import com.navercorp.pinpoint.bootstrap.config.ProfilerConfig;
import org.junit.Assert;
import org.junit.Test;


public class HbasePluginConfigTest {
    @Test
    public void isClientProfile() {
        ProfilerConfig profilerConfig = new DefaultProfilerConfig();
        HbasePluginConfig config = new HbasePluginConfig(profilerConfig);
        Assert.assertTrue(config.isClientProfile());
        System.out.println(config);
    }

    @Test
    public void isAdminProfile() {
        ProfilerConfig profilerConfig = new DefaultProfilerConfig();
        HbasePluginConfig config = new HbasePluginConfig(profilerConfig);
        Assert.assertTrue(config.isAdminProfile());
        System.out.println(config);
    }

    @Test
    public void isTableProfile() {
        ProfilerConfig profilerConfig = new DefaultProfilerConfig();
        HbasePluginConfig config = new HbasePluginConfig(profilerConfig);
        Assert.assertTrue(config.isTableProfile());
        System.out.println(config);
    }

    @Test
    public void isParamsProfile() {
        ProfilerConfig profilerConfig = new DefaultProfilerConfig();
        HbasePluginConfig config = new HbasePluginConfig(profilerConfig);
        Assert.assertFalse(config.isParamsProfile());
        System.out.println(config);
    }
}

