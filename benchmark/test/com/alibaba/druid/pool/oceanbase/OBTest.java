package com.alibaba.druid.pool.oceanbase;


import com.alibaba.druid.pool.DruidDataSource;
import java.sql.Connection;
import junit.framework.TestCase;


public class OBTest extends TestCase {
    private DruidDataSource dataSource;

    public void test_connect() throws Exception {
        {
            Connection conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            conn.close();
        }
        {
            Connection conn = dataSource.getConnection();
            conn.setAutoCommit(true);
            conn.close();
        }
    }
}

