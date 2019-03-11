package io.mycat.route;


import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import io.mycat.MycatServer;
import io.mycat.SimpleCachePool;
import io.mycat.cache.LayerCachePool;
import io.mycat.config.loader.SchemaLoader;
import io.mycat.config.loader.xml.XMLSchemaLoader;
import io.mycat.config.model.SchemaConfig;
import io.mycat.route.factory.RouteStrategyFactory;
import io.mycat.route.parser.druid.DruidShardingParseInfo;
import io.mycat.route.parser.druid.MycatSchemaStatVisitor;
import io.mycat.route.parser.druid.MycatStatementParser;
import io.mycat.route.parser.druid.RouteCalculateUnit;
import java.sql.SQLSyntaxErrorException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import junit.framework.Assert;
import org.junit.Test;


public class DQLRouteTest {
    protected Map<String, SchemaConfig> schemaMap;

    protected LayerCachePool cachePool = new SimpleCachePool();

    protected RouteStrategy routeStrategy = RouteStrategyFactory.getRouteStrategy("druidparser");

    private Map<String, String> tableAliasMap = new HashMap<String, String>();

    protected DruidShardingParseInfo ctx;

    public DQLRouteTest() {
        String schemaFile = "/route/schema.xml";
        String ruleFile = "/route/rule.xml";
        SchemaLoader schemaLoader = new XMLSchemaLoader(schemaFile, ruleFile);
        schemaMap = schemaLoader.getSchemas();
        MycatServer.getInstance().getConfig().getSchemas().putAll(schemaMap);
    }

    @Test
    public void test() throws Exception {
        String stmt = "select * from `offer` where id = 100";
        SchemaConfig schema = schemaMap.get("mysqldb");
        RouteResultset rrs = new RouteResultset(stmt, 7);
        SQLStatementParser parser = null;
        if (schema.isNeedSupportMultiDBType()) {
            parser = new MycatStatementParser(stmt);
        } else {
            parser = new MySqlStatementParser(stmt);
        }
        SQLStatement statement;
        MycatSchemaStatVisitor visitor = null;
        try {
            statement = parser.parseStatement();
            visitor = new MycatSchemaStatVisitor();
        } catch (Exception t) {
            throw new SQLSyntaxErrorException(t);
        }
        ctx = new DruidShardingParseInfo();
        ctx.setSql(stmt);
        List<RouteCalculateUnit> taskList = visitorParse(rrs, statement, visitor);
        Assert.assertEquals(true, (!(taskList.get(0).getTablesAndConditions().isEmpty())));
    }
}

