package io.mycat.route;


import io.mycat.MycatServer;
import io.mycat.SimpleCachePool;
import io.mycat.cache.LayerCachePool;
import io.mycat.config.loader.SchemaLoader;
import io.mycat.config.loader.xml.XMLSchemaLoader;
import io.mycat.config.model.SchemaConfig;
import io.mycat.config.model.SystemConfig;
import io.mycat.route.factory.RouteStrategyFactory;
import java.sql.SQLNonTransientException;
import java.util.Map;
import junit.framework.Assert;
import org.junit.Test;


public class DruidMysqlCreateTableTest {
    protected Map<String, SchemaConfig> schemaMap;

    protected LayerCachePool cachePool = new SimpleCachePool();

    protected RouteStrategy routeStrategy;

    private static final String originSql1 = "CREATE TABLE autoslot" + ((((("(" + "	ID BIGINT AUTO_INCREMENT,") + "	CHANNEL_ID INT(11),") + "	CHANNEL_INFO varchar(128),") + "	CONSTRAINT RETL_MARK_ID PRIMARY KEY (ID)") + ") ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;");

    public DruidMysqlCreateTableTest() {
        String schemaFile = "/route/schema.xml";
        String ruleFile = "/route/rule.xml";
        SchemaLoader schemaLoader = new XMLSchemaLoader(schemaFile, ruleFile);
        schemaMap = schemaLoader.getSchemas();
        MycatServer.getInstance().getConfig().getSchemas().putAll(schemaMap);
        RouteStrategyFactory.init();
        routeStrategy = RouteStrategyFactory.getRouteStrategy("druidparser");
    }

    @Test
    public void testCreate() throws SQLNonTransientException {
        SchemaConfig schema = schemaMap.get("mysqldb");
        RouteResultset rrs = routeStrategy.route(new SystemConfig(), schema, (-1), DruidMysqlCreateTableTest.originSql1, null, null, cachePool);
        Assert.assertEquals(2, rrs.getNodes().length);
        String sql = rrs.getNodes()[0].getStatement();
        Assert.assertTrue(parseSql(sql));
    }
}

