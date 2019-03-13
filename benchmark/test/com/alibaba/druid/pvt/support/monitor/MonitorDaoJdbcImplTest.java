package com.alibaba.druid.pvt.support.monitor;


import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.stat.JdbcSqlStatValue;
import com.alibaba.druid.support.monitor.MonitorClient;
import com.alibaba.druid.support.monitor.dao.MonitorDaoJdbcImpl;
import java.util.Collections;
import java.util.List;
import junit.framework.TestCase;


public class MonitorDaoJdbcImplTest extends TestCase {
    private DruidDataSource dataSource;

    public void testBuildSql() throws Exception {
        MonitorDaoJdbcImpl dao = new MonitorDaoJdbcImpl();
        dao.setDataSource(dataSource);
        // System.out.println(buildCreateSql(dao, new BeanInfo(WallProviderStatValue.class)));
        // System.out.println(buildCreateSql(dao, new BeanInfo(WallSqlStatValue.class)));
        // System.out.println(buildCreateSql(dao, new BeanInfo(WallTableStatValue.class)));
        // System.out.println(buildCreateSql(dao, new BeanInfo(WallFunctionStatValue.class)));
        // dao.createTables("mysql");
        MonitorClient client = new MonitorClient();
        client.setDao(dao);
        client.checkInst();
        client.collectSql();
        {
            List<JdbcSqlStatValue> sqlList = client.loadSqlList(Collections.<String, Object>emptyMap());
            for (JdbcSqlStatValue sqlStatValue : sqlList) {
                System.out.println(sqlStatValue.getData());
            }
            // Assert.assertEquals(11, sqlList.size());
        }
        client.collectSql();
        {
            List<JdbcSqlStatValue> sqlList = client.loadSqlList(Collections.<String, Object>emptyMap());
            for (JdbcSqlStatValue sqlStatValue : sqlList) {
                System.out.println(sqlStatValue.getData());
            }
            // Assert.assertEquals(14, sqlList.size());
        }
    }
}

