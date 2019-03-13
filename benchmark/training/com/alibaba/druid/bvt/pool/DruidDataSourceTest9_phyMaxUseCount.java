package com.alibaba.druid.bvt.pool;


import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import java.sql.Connection;
import junit.framework.TestCase;


public class DruidDataSourceTest9_phyMaxUseCount extends TestCase {
    private DruidDataSource dataSource;

    public void test_for_phyMaxUseCount() throws Exception {
        Connection phyConn = null;
        for (int i = 0; i < 100; ++i) {
            DruidPooledConnection conn = dataSource.getConnection();
            if ((i % 10) == 0) {
                if ((conn.getConnection()) == phyConn) {
                    throw new IllegalStateException();
                }
            }
            phyConn = conn.getConnection();
            conn.close();
            // System.out.println(i);
        }
    }
}

