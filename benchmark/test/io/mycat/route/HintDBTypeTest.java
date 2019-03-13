package io.mycat.route;


import ServerParse.SELECT;
import io.mycat.MycatServer;
import io.mycat.SimpleCachePool;
import io.mycat.cache.CacheService;
import io.mycat.cache.LayerCachePool;
import io.mycat.config.loader.SchemaLoader;
import io.mycat.config.loader.xml.XMLSchemaLoader;
import io.mycat.config.model.SchemaConfig;
import io.mycat.config.model.SystemConfig;
import io.mycat.route.factory.RouteStrategyFactory;
import java.util.Map;
import junit.framework.Assert;
import org.junit.Test;


public class HintDBTypeTest {
    protected Map<String, SchemaConfig> schemaMap;

    protected LayerCachePool cachePool = new SimpleCachePool();

    protected RouteStrategy routeStrategy;

    public HintDBTypeTest() {
        String schemaFile = "/route/schema.xml";
        String ruleFile = "/route/rule.xml";
        SchemaLoader schemaLoader = new XMLSchemaLoader(schemaFile, ruleFile);
        schemaMap = schemaLoader.getSchemas();
        MycatServer.getInstance().getConfig().getSchemas().putAll(schemaMap);
        RouteStrategyFactory.init();
        routeStrategy = RouteStrategyFactory.getRouteStrategy("druidparser");
    }

    /**
     * ????
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testHint() throws Exception {
        SchemaConfig schema = schemaMap.get("TESTDB");
        // ?????????/*!mycat*/??runOnSlave=false ??????
        String sql = "/*!mycat:db_type=master*/select * from employee where sharding_id=1";
        CacheService cacheService = new CacheService();
        RouteService routerService = new RouteService(cacheService);
        RouteResultset rrs = routerService.route(new SystemConfig(), schema, SELECT, sql, "UTF-8", null);
        Assert.assertTrue((!(rrs.getRunOnSlave())));
        // ?????????/*#mycat*/??runOnSlave=false ??????
        sql = "/*#mycat:db_type=master*/select * from employee where sharding_id=1";
        rrs = routerService.route(new SystemConfig(), schema, SELECT, sql, "UTF-8", null);
        Assert.assertTrue((!(rrs.getRunOnSlave())));
        // ?????????/*mycat*/??runOnSlave=false ??????
        sql = "/*mycat:db_type=master*/select * from employee where sharding_id=1";
        rrs = routerService.route(new SystemConfig(), schema, SELECT, sql, "UTF-8", null);
        Assert.assertTrue((!(rrs.getRunOnSlave())));
        // ??????runOnSlave=null, ????????????
        sql = "select * from employee where sharding_id=1";
        rrs = routerService.route(new SystemConfig(), schema, SELECT, sql, "UTF-8", null);
        Assert.assertTrue(((rrs.getRunOnSlave()) == null));
    }
}

