package com.alibaba.druid.pool.ha;


import java.util.Map;
import javax.sql.DataSource;
import org.junit.Assert;
import org.junit.Test;


public class DataSourceCreatorTest {
    @Test
    public void createMap() throws Exception {
        HighAvailableDataSource highAvailableDataSource = new HighAvailableDataSource();
        String file = "/com/alibaba/druid/pool/ha/ha-datasource.properties";
        DataSourceCreator creator = new DataSourceCreator(file);
        Map<String, DataSource> map = creator.createMap(highAvailableDataSource);
        Assert.assertEquals(2, map.size());
        Assert.assertNotNull(map.get("foo"));
        Assert.assertNotNull(map.get("bar"));
    }
}

