package com.alibaba.druid.pool;


import java.sql.Connection;
import junit.framework.TestCase;


public class TestMySql extends TestCase {
    private DruidDataSource dataSource = new DruidDataSource();

    public void test_0() throws Exception {
        Connection conn = dataSource.getConnection();
        conn.close();
    }
}

