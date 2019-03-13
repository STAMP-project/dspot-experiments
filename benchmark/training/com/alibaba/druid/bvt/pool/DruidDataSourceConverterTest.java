package com.alibaba.druid.bvt.pool;


import com.alibaba.druid.pool.DruidDataSource;
import java.sql.Connection;
import junit.framework.TestCase;
import org.junit.Assert;


public class DruidDataSourceConverterTest extends TestCase {
    private DruidDataSource dataSource;

    public void test_conn() throws Exception {
        Assert.assertEquals(true, dataSource.isInited());
        Connection conn = dataSource.getConnection();
        Assert.assertEquals(1, dataSource.getActiveCount());
        conn.close();
        Assert.assertEquals(0, dataSource.getActiveCount());
    }
}

