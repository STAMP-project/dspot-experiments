package com.alibaba.otter.canal.client.adapter.rdb.test;


import com.alibaba.otter.canal.client.adapter.rdb.config.ConfigLoader;
import com.alibaba.otter.canal.client.adapter.rdb.config.MappingConfig;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class ConfigLoadTest {
    @Test
    public void testLoad() {
        Map<String, MappingConfig> configMap = ConfigLoader.load(null);
        Assert.assertFalse(configMap.isEmpty());
    }
}

