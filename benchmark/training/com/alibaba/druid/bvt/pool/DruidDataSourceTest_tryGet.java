package com.alibaba.druid.bvt.pool;


import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import junit.framework.TestCase;
import org.junit.Assert;


public class DruidDataSourceTest_tryGet extends TestCase {
    private DruidDataSource dataSource;

    public void test_0() throws Exception {
        DruidPooledConnection conn1 = this.dataSource.tryGetConnection();
        Assert.assertNotNull(conn1);
        DruidPooledConnection conn2 = this.dataSource.tryGetConnection();
        Assert.assertNull(conn2);
        conn1.close();
    }
}

