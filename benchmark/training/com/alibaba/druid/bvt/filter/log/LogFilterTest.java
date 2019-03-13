package com.alibaba.druid.bvt.filter.log;


import com.alibaba.druid.pool.DruidDataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import junit.framework.TestCase;


public class LogFilterTest extends TestCase {
    private DruidDataSource dataSource;

    public void test_select() throws Exception {
        Connection conn = dataSource.getConnection();
        Statement stmt = conn.createStatement();
        for (int i = 0; i < 10; ++i) {
            ResultSet rs = stmt.executeQuery("SELECT * FROM ITEM WHERE LISTPRICE > 10");
            rs.close();
        }
        stmt.close();
        conn.close();
    }
}

