package com.alibaba.otter.canal.client.adapter.es.test;


import ESSyncConfig.ESMapping;
import com.alibaba.otter.canal.client.adapter.es.config.ESSyncConfig;
import com.alibaba.otter.canal.client.adapter.es.config.ESSyncConfigLoader;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class ConfigLoadTest {
    @Test
    public void testLoad() {
        Map<String, ESSyncConfig> configMap = ESSyncConfigLoader.load(null);
        ESSyncConfig config = configMap.get("mytest_user.yml");
        Assert.assertNotNull(config);
        Assert.assertEquals("defaultDS", config.getDataSourceKey());
        ESSyncConfig.ESMapping esMapping = config.getEsMapping();
        Assert.assertEquals("mytest_user", esMapping.get_index());
        Assert.assertEquals("_doc", esMapping.get_type());
        Assert.assertEquals("id", esMapping.get_id());
        Assert.assertNotNull(esMapping.getSql());
        // Map<String, List<ESSyncConfig>> dbTableEsSyncConfig =
        // ESSyncConfigLoader.getDbTableEsSyncConfig();
        // Assert.assertFalse(dbTableEsSyncConfig.isEmpty());
    }
}

