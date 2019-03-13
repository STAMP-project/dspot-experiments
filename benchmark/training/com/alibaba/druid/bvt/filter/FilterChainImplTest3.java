package com.alibaba.druid.bvt.filter;


import com.alibaba.druid.pool.DruidDataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Types;
import junit.framework.TestCase;
import org.junit.Assert;


public class FilterChainImplTest3 extends TestCase {
    private DruidDataSource dataSource;

    public void test_executeQuery() throws Exception {
        Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement("select ?");
        stmt.setNull(1, Types.VARCHAR);
        Assert.assertNull(stmt.executeQuery());
        stmt.close();
        conn.close();
    }

    public void test_executeQuery_2() throws Exception {
        Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareCall("select ?");
        stmt.setNull(1, Types.VARCHAR);
        Assert.assertNull(stmt.executeQuery());
        stmt.close();
        conn.close();
    }

    public void test_executeQuery_3() throws Exception {
        Connection conn = dataSource.getConnection();
        Statement stmt = conn.createStatement();
        Assert.assertNull(stmt.executeQuery("select 1"));
        stmt.close();
        conn.close();
    }

    public void test_execute() throws Exception {
        Connection conn = dataSource.getConnection();
        Statement stmt = conn.createStatement();
        stmt.execute("select 1");
        Assert.assertNull(stmt.getResultSet());
        stmt.close();
        conn.close();
    }
}

