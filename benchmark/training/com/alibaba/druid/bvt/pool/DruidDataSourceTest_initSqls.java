package com.alibaba.druid.bvt.pool;


import com.alibaba.druid.pool.DruidDataSource;
import java.sql.Connection;
import junit.framework.TestCase;
import org.junit.Assert;


public class DruidDataSourceTest_initSqls extends TestCase {
    private DruidDataSource dataSource;

    public void testDefault() throws Exception {
        Connection conn = dataSource.getConnection();
        Assert.assertEquals(true, conn.isReadOnly());
        Assert.assertEquals(Connection.TRANSACTION_SERIALIZABLE, conn.getTransactionIsolation());
        Assert.assertEquals("c123", conn.getCatalog());
        conn.close();
    }
}

