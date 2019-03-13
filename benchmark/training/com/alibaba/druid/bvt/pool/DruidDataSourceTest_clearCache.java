package com.alibaba.druid.bvt.pool;


import com.alibaba.druid.pool.DruidDataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import junit.framework.TestCase;
import org.junit.Assert;


/**
 * ??????minIdle > maxActive
 *
 * @author wenshao [szujobs@hotmail.com]
 */
public class DruidDataSourceTest_clearCache extends TestCase {
    private DruidDataSource dataSource;

    public void test_clearStatementCache() throws Exception {
        {
            Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement("select 1");
            stmt.close();
            conn.close();
        }
        {
            Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement("select 1, 2");
            stmt.close();
            conn.close();
        }
        Assert.assertEquals(2, dataSource.getCachedPreparedStatementCount());
        dataSource.clearStatementCache();
        Assert.assertEquals(0, dataSource.getCachedPreparedStatementCount());
    }
}

