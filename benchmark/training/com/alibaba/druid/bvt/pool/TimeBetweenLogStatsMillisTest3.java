package com.alibaba.druid.bvt.pool;


import com.alibaba.druid.pool.DruidDataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import junit.framework.TestCase;
import org.junit.Assert;


public class TimeBetweenLogStatsMillisTest3 extends TestCase {
    private DruidDataSource dataSource;

    public void test_0() throws Exception {
        dataSource.init();
        for (int i = 0; i < 10; ++i) {
            Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement("select ?");
            stmt.setString(1, "aaa");
            ResultSet rs = stmt.executeQuery();
            rs.close();
            stmt.close();
            conn.close();
            Thread.sleep(10);
        }
        Assert.assertEquals(10, dataSource.getTimeBetweenLogStatsMillis());
    }
}

