package com.alibaba.druid.bvt.filter.log;


import com.alibaba.druid.pool.DruidDataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import junit.framework.TestCase;


public class LogFilterTest2 extends TestCase {
    private DruidDataSource dataSource;

    public void test_select() throws Exception {
        Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM ITEM WHERE LISTPRICE > 10");
        for (int i = 0; i < 10; ++i) {
            ResultSet rs = stmt.executeQuery();
            rs.close();
        }
        stmt.close();
        conn.close();
    }
}

