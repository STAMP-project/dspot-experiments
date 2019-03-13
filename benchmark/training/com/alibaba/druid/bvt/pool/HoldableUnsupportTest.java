package com.alibaba.druid.bvt.pool;


import com.alibaba.druid.pool.DruidDataSource;
import java.sql.Connection;
import junit.framework.TestCase;


public class HoldableUnsupportTest extends TestCase {
    private DruidDataSource dataSource;

    public void test_0() throws Exception {
        Connection[] connections = new Connection[8];
        for (int i = 0; i < (connections.length); ++i) {
            connections[i] = dataSource.getConnection();
        }
        for (int i = 0; i < (connections.length); ++i) {
            connections[i].close();
        }
    }
}

