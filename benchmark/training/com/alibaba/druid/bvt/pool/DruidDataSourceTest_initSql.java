package com.alibaba.druid.bvt.pool;


import com.alibaba.druid.mock.MockConnection;
import com.alibaba.druid.pool.DruidDataSource;
import java.sql.Connection;
import junit.framework.TestCase;
import org.junit.Assert;


public class DruidDataSourceTest_initSql extends TestCase {
    private DruidDataSource dataSource;

    public void testDefault() throws Exception {
        Connection conn = dataSource.getConnection();
        MockConnection mockConn = conn.unwrap(MockConnection.class);
        Assert.assertEquals("select 123", mockConn.getLastSql());
        conn.close();
    }
}

