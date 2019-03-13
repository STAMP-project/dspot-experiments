package com.alibaba.druid.mysql;


import com.alibaba.druid.pool.DruidDataSource;
import java.sql.Connection;
import junit.framework.TestCase;


/**
 * Created by wenshao on 10/08/2017.
 */
public class MySqlConnectFailTest extends TestCase {
    DruidDataSource dataSource;

    public void test_0() throws Exception {
        Connection conn = dataSource.getConnection();
        conn.close();
    }
}

