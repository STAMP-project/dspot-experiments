package com.alibaba.druid.test;


import com.alibaba.druid.pool.DruidDataSource;
import java.sql.Connection;
import junit.framework.TestCase;


public class AlibTest extends TestCase {
    protected DruidDataSource dataSource;

    public void test_for_alib() throws Exception {
        Connection conn = dataSource.getConnection();
        conn.close();
    }
}

