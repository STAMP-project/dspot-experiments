package com.alibaba.druid.pool.qa;


import com.alibaba.druid.pool.DruidDataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import junit.framework.TestCase;


public class MySqlTestSuit extends TestCase {
    private DruidDataSource dataSource;

    public void test_suit() throws Exception {
        createTable();
        {
            Connection conn = dataSource.getConnection();
            Statement stmt = conn.createStatement();
            stmt.execute("INSERT INTO T (FID) VALUES (1)");
            stmt.close();
            stmt.close();
            conn.close();
        }
        {
            Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement("select * from T where fid = ?");
            stmt.setInt(1, 1);
            ResultSet rs = stmt.executeQuery();
            rs.close();
            stmt.close();
            stmt.close();
            conn.close();
        }
        {
            Connection conn = dataSource.getConnection();
            Statement stmt = conn.createStatement();
            stmt.execute("INSERT INTO T (FID) VALUES (2)");
            stmt.close();
            stmt.close();
            conn.close();
        }
        dropTable();
    }
}

