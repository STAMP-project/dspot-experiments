package com.alibaba.otter.canal.client.adapter.es.test.sync;


import com.alibaba.otter.canal.client.adapter.es.ESAdapter;
import com.alibaba.otter.canal.client.adapter.es.config.ESSyncConfig;
import com.alibaba.otter.canal.client.adapter.support.Dml;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.elasticsearch.action.get.GetResponse;
import org.junit.Assert;
import org.junit.Test;


// @After
// public void after() {
// esAdapter.destroy();
// DatasourceConfig.DATA_SOURCES.values().forEach(DruidDataSource::close);
// }
public class UserSyncSingleTest {
    private ESAdapter esAdapter;

    /**
     * ????
     */
    @Test
    public void test01() {
        Dml dml = new Dml();
        dml.setDestination("example");
        dml.setTs(new Date().getTime());
        dml.setType("INSERT");
        dml.setDatabase("mytest");
        dml.setTable("user");
        List<Map<String, Object>> dataList = new ArrayList<>();
        Map<String, Object> data = new LinkedHashMap<>();
        dataList.add(data);
        data.put("id", 1L);
        data.put("name", "Eric");
        data.put("role_id", 1L);
        data.put("c_time", new Date());
        dml.setData(dataList);
        String database = dml.getDatabase();
        String table = dml.getTable();
        Map<String, ESSyncConfig> esSyncConfigs = esAdapter.getDbTableEsSyncConfig().get(((database + "-") + table));
        esAdapter.getEsSyncService().sync(esSyncConfigs.values(), dml);
        GetResponse response = esAdapter.getTransportClient().prepareGet("mytest_user", "_doc", "1").get();
        Assert.assertEquals("Eric", response.getSource().get("_name"));
    }

    /**
     * ????
     */
    @Test
    public void test02() {
        Dml dml = new Dml();
        dml.setDestination("example");
        dml.setTs(new Date().getTime());
        dml.setType("UPDATE");
        dml.setDatabase("mytest");
        dml.setTable("user");
        List<Map<String, Object>> dataList = new ArrayList<>();
        Map<String, Object> data = new LinkedHashMap<>();
        dataList.add(data);
        data.put("id", 1L);
        data.put("name", "Eric2");
        dml.setData(dataList);
        List<Map<String, Object>> oldList = new ArrayList<>();
        Map<String, Object> old = new LinkedHashMap<>();
        oldList.add(old);
        old.put("name", "Eric");
        dml.setOld(oldList);
        String database = dml.getDatabase();
        String table = dml.getTable();
        Map<String, ESSyncConfig> esSyncConfigs = esAdapter.getDbTableEsSyncConfig().get(((database + "-") + table));
        esAdapter.getEsSyncService().sync(esSyncConfigs.values(), dml);
        GetResponse response = esAdapter.getTransportClient().prepareGet("mytest_user", "_doc", "1").get();
        Assert.assertEquals("Eric2", response.getSource().get("_name"));
    }

    /**
     * ????
     */
    @Test
    public void test03() {
        Dml dml = new Dml();
        dml.setDestination("example");
        dml.setTs(new Date().getTime());
        dml.setType("DELETE");
        dml.setDatabase("mytest");
        dml.setTable("user");
        List<Map<String, Object>> dataList = new ArrayList<>();
        Map<String, Object> data = new LinkedHashMap<>();
        dataList.add(data);
        data.put("id", 1L);
        data.put("name", "Eric");
        data.put("role_id", 1L);
        data.put("c_time", new Date());
        dml.setData(dataList);
        String database = dml.getDatabase();
        String table = dml.getTable();
        Map<String, ESSyncConfig> esSyncConfigs = esAdapter.getDbTableEsSyncConfig().get(((database + "-") + table));
        esAdapter.getEsSyncService().sync(esSyncConfigs.values(), dml);
        GetResponse response = esAdapter.getTransportClient().prepareGet("mytest_user", "_doc", "1").get();
        Assert.assertNull(response.getSource());
    }
}

