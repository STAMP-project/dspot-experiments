package com.alibaba.druid.pvt.pool;


import com.alibaba.druid.mock.MockPreparedStatement;
import com.alibaba.druid.pool.DruidDataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import junit.framework.TestCase;
import org.junit.Assert;


public class PSCacheTest5 extends TestCase {
    private DruidDataSource dataSource;

    public void test_0() throws Exception {
        MockPreparedStatement mockStmt = null;
        {
            Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement("select 1");
            mockStmt = ps.unwrap(MockPreparedStatement.class);
            ps.execute();
            conn.close();
        }
        for (int i = 0; i < 1000; ++i) {
            Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement("select 1");
            Assert.assertSame(mockStmt, ps.unwrap(MockPreparedStatement.class));
            ps.execute();
            ps.close();
            conn.close();
        }
    }
}

