package com.alibaba.druid.bvt.pool;


import com.alibaba.druid.pool.DruidDataSource;
import java.sql.Connection;
import junit.framework.TestCase;
import org.junit.Assert;


public class FullTest extends TestCase {
    private DruidDataSource dataSource;

    public void test_restart() throws Exception {
        Assert.assertEquals(false, dataSource.isFull());
        dataSource.fill();
        Assert.assertEquals(true, dataSource.isFull());
        Connection conn = dataSource.getConnection();
        Assert.assertEquals(true, dataSource.isFull());
        conn.close();
        Assert.assertEquals(true, dataSource.isFull());
    }
}

