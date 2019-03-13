package com.alibaba.druid.bvt.pool;


import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import junit.framework.TestCase;


public class InitExceptionThrowTest extends TestCase {
    private DruidDataSource dataSource = new DruidDataSource();

    private int connectCount = 0;

    public void test_pool() throws Exception {
        DruidPooledConnection conn = dataSource.getConnection();
        conn.close();
    }
}

