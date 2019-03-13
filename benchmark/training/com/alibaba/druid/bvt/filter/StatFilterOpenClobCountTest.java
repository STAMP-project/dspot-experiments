package com.alibaba.druid.bvt.filter;


import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.stat.JdbcSqlStat;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import junit.framework.TestCase;
import org.junit.Assert;


public class StatFilterOpenClobCountTest extends TestCase {
    private DruidDataSource dataSource;

    public void test_stat() throws Exception {
        Connection conn = dataSource.getConnection();
        String sql = "select 'x'";
        PreparedStatement stmt = conn.prepareStatement("select 'x'");
        JdbcSqlStat sqlStat = dataSource.getDataSourceStat().getSqlStat(sql);
        Assert.assertEquals(0, sqlStat.getClobOpenCount());
        ResultSet rs = stmt.executeQuery();
        rs.next();
        rs.getClob(1);
        rs.getClob(2);
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
        rs.getClob("1");
        rs.getClob("2");
        rs.getClob("3");
        rs.close();
        stmt.close();
        conn.close();
        Assert.assertEquals(3, sqlStat.getClobOpenCount());
        sqlStat.reset();
        Assert.assertEquals(0, sqlStat.getClobOpenCount());
    }
}

