package com.alibaba.druid.bvt.filter;


import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.stat.JdbcSqlStat;
import com.alibaba.druid.util.JdbcUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import junit.framework.TestCase;
import org.junit.Assert;


public class StatFilterExecErrorTest extends TestCase {
    private DruidDataSource dataSource;

    public void test_stat() throws Exception {
        Connection conn = dataSource.getConnection();
        String sql = "select 'x'";
        PreparedStatement stmt = conn.prepareStatement("select 'x'");
        JdbcSqlStat sqlStat = dataSource.getDataSourceStat().getSqlStat(sql);
        Assert.assertEquals(0, sqlStat.getReadStringLength());
        try {
            stmt.executeQuery();
        } catch (SQLException eror) {
        } finally {
            JdbcUtils.close(stmt);
            JdbcUtils.close(conn);
        }
        Assert.assertEquals(1, sqlStat.getErrorCount());
        Assert.assertEquals(0, sqlStat.getRunningCount());
        sqlStat.reset();
    }
}

