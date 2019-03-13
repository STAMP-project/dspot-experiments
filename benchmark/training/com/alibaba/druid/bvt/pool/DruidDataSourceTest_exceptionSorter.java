package com.alibaba.druid.bvt.pool;


import com.alibaba.druid.pool.DruidDataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import junit.framework.TestCase;
import org.junit.Assert;


/**
 * ??????defaultAutoCommit
 *
 * @author wenshao [szujobs@hotmail.com]
 */
public class DruidDataSourceTest_exceptionSorter extends TestCase {
    private DruidDataSource dataSource;

    public void test_event_error() throws Exception {
        {
            Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement("select ?");
            try {
                stmt.executeQuery();
            } catch (SQLException e) {
            }
            conn.close();
        }
        {
            Connection conn = dataSource.getConnection();
            conn.close();
        }
        Assert.assertEquals(2, dataSource.getCreateCount());
        Assert.assertEquals(1, dataSource.getDiscardCount());
        Assert.assertEquals(1, dataSource.getPoolingCount());
    }
}

