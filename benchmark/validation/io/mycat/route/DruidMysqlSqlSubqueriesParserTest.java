package io.mycat.route;


import io.mycat.MycatServer;
import io.mycat.SimpleCachePool;
import io.mycat.cache.LayerCachePool;
import io.mycat.config.loader.SchemaLoader;
import io.mycat.config.loader.xml.XMLSchemaLoader;
import io.mycat.config.model.SchemaConfig;
import io.mycat.route.factory.RouteStrategyFactory;
import java.sql.SQLNonTransientException;
import java.util.Map;
import org.junit.Test;


@SuppressWarnings("deprecation")
public class DruidMysqlSqlSubqueriesParserTest {
    protected Map<String, SchemaConfig> schemaMap;

    protected LayerCachePool cachePool = new SimpleCachePool();

    protected RouteStrategy routeStrategy;

    public DruidMysqlSqlSubqueriesParserTest() {
        String schemaFile = "/route/schema.xml";
        String ruleFile = "/route/rule.xml";
        SchemaLoader schemaLoader = new XMLSchemaLoader(schemaFile, ruleFile);
        schemaMap = schemaLoader.getSchemas();
        MycatServer.getInstance().getConfig().getSchemas().putAll(schemaMap);
        RouteStrategyFactory.init();
        routeStrategy = RouteStrategyFactory.getRouteStrategy("druidparser");
    }

    @Test
    public void testSubQueries() throws SQLNonTransientException {
        // ?????????ServerConnection. ???????????.??????????
    }
}

