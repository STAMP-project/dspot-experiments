package com.alibaba.druid.bvt.filter;


import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.stat.JdbcSqlStat;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collections;
import junit.framework.TestCase;
import org.junit.Assert;


public class StatFilterOpenClobCountTest2 extends TestCase {
    private DruidDataSource dataSource;

    public void test_stat() throws Exception {
        Connection conn = dataSource.getConnection();
        String sql = "select 'x'";
        PreparedStatement stmt = conn.prepareStatement("select 'x'");
        JdbcSqlStat sqlStat = dataSource.getDataSourceStat().getSqlStat(sql);
        Assert.assertEquals(0, sqlStat.getClobOpenCount());
        ResultSet rs = stmt.executeQuery();
        rs.next();
        rs.getObject(1);
        rs.getObject(2);
        rs.close();
        stmt.close();
        conn.close();
        Assert.assertEquals(2, sqlStat.getClobOpenCount());
        sqlStat.reset();
        Assert.assertEquals(0, sqlStat.getClobOpenCount());
    }

    public void test_stat_1() throws Exception {
        Connection conn = dataSource.getConnection();
        String sql = "select 'x'";
        PreparedStatement stmt = conn.prepareStatement("select 'x'");
        JdbcSqlStat sqlStat = dataSource.getDataSourceStat().getSqlStat(sql);
        Assert.assertEquals(0, sqlStat.getClobOpenCount());
        ResultSet rs = stmt.executeQuery();
        rs.next();
        rs.getObject("1");
        rs.getObject("2");
        rs.getObject("3");
        rs.close();
        stmt.close();
        conn.close();
        Assert.assertEquals(3, sqlStat.getClobOpenCount());
        sqlStat.reset();
        Assert.assertEquals(0, sqlStat.getClobOpenCount());
    }

    public void test_stat_2() throws Exception {
        Connection conn = dataSource.getConnection();
        String sql = "select 'x'";
        PreparedStatement stmt = conn.prepareStatement("select 'x'");
        JdbcSqlStat sqlStat = dataSource.getDataSourceStat().getSqlStat(sql);
        Assert.assertEquals(0, sqlStat.getClobOpenCount());
        ResultSet rs = stmt.executeQuery();
        rs.next();
        rs.getObject(1, Collections.<String, Class<?>>emptyMap());
        rs.getObject(2, Collections.<String, Class<?>>emptyMap());
        rs.getObject(3, Collections.<String, Class<?>>emptyMap());
        rs.getObject(4, Collections.<String, Class<?>>emptyMap());
        rs.close();
        stmt.close();
        conn.close();
        Assert.assertEquals(4, sqlStat.getClobOpenCount());
        sqlStat.reset();
        Assert.assertEquals(0, sqlStat.getClobOpenCount());
    }

    public void test_stat_4() throws Exception {
        Connection conn = dataSource.getConnection();
        String sql = "select 'x'";
        PreparedStatement stmt = conn.prepareStatement("select 'x'");
        JdbcSqlStat sqlStat = dataSource.getDataSourceStat().getSqlStat(sql);
        Assert.assertEquals(0, sqlStat.getClobOpenCount());
        ResultSet rs = stmt.executeQuery();
        rs.next();
        rs.getObject("1", Collections.<String, Class<?>>emptyMap());
        rs.getObject("2", Collections.<String, Class<?>>emptyMap());
        rs.getObject("3", Collections.<String, Class<?>>emptyMap());
        rs.getObject("4", Collections.<String, Class<?>>emptyMap());
        rs.getObject("5", Collections.<String, Class<?>>emptyMap());
        rs.close();
        stmt.close();
        conn.close();
        Assert.assertEquals(5, sqlStat.getClobOpenCount());
        sqlStat.reset();
        Assert.assertEquals(0, sqlStat.getClobOpenCount());
    }
}

