package io.mycat.config;


import io.mycat.backend.datasource.PhysicalDBPool;
import io.mycat.backend.datasource.PhysicalDatasource;
import io.mycat.config.loader.xml.XMLConfigLoader;
import io.mycat.config.loader.xml.XMLSchemaLoader;
import io.mycat.config.model.DataHostConfig;
import io.mycat.config.model.SchemaConfig;
import io.mycat.config.model.SystemConfig;
import io.mycat.config.model.TableConfig;
import io.mycat.config.model.UserConfig;
import java.util.ArrayList;
import java.util.Map;
import junit.framework.Assert;
import org.junit.Test;


public class ConfigTest {
    private SystemConfig system;

    private final Map<String, UserConfig> users;

    private Map<String, SchemaConfig> schemas;

    private Map<String, PhysicalDBPool> dataHosts;

    public ConfigTest() {
        String schemaFile = "/config/schema.xml";
        String ruleFile = "/config/rule.xml";
        XMLSchemaLoader schemaLoader = new XMLSchemaLoader(schemaFile, ruleFile);
        XMLConfigLoader configLoader = new XMLConfigLoader(schemaLoader);
        this.system = configLoader.getSystemConfig();
        this.users = configLoader.getUserConfigs();
        this.schemas = configLoader.getSchemaConfigs();
        this.dataHosts = initDataHosts(configLoader);
    }

    /**
     * ?? ????? ??
     */
    @Test
    public void testTempReadHostAvailable() {
        PhysicalDBPool pool = this.dataHosts.get("localhost2");
        DataHostConfig hostConfig = pool.getSource().getHostConfig();
        Assert.assertTrue(((hostConfig.isTempReadHostAvailable()) == true));
    }

    /**
     * ?? ?????? ?? ??
     */
    @Test
    public void testReadUserBenchmark() {
        UserConfig userConfig = this.users.get("test");
        int benchmark = userConfig.getBenchmark();
        Assert.assertTrue((benchmark == 11111));
    }

    /**
     * ?? ???? ??
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testReadHostWeight() throws Exception {
        ArrayList<PhysicalDatasource> okSources = new ArrayList<PhysicalDatasource>();
        PhysicalDBPool pool = this.dataHosts.get("localhost2");
        okSources.addAll(pool.getAllDataSources());
        PhysicalDatasource source = pool.randomSelect(okSources);
        Assert.assertTrue((source != null));
    }

    /**
     * ?? ?????
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testDynamicYYYYMMTable() throws Exception {
        SchemaConfig sc = this.schemas.get("dbtest1");
        Map<String, TableConfig> tbm = sc.getTables();
        Assert.assertTrue(((tbm.size()) == 32));
    }
}

