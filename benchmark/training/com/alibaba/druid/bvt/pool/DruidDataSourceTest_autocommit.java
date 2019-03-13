package com.alibaba.druid.bvt.pool;


import com.alibaba.druid.pool.DruidDataSource;
import java.sql.Connection;
import junit.framework.TestCase;
import org.junit.Assert;


/**
 * ??????defaultAutoCommit
 *
 * @author wenshao [szujobs@hotmail.com]
 */
public class DruidDataSourceTest_autocommit extends TestCase {
    private DruidDataSource dataSource;

    public void test_autoCommit() throws Exception {
        Connection conn = dataSource.getConnection();
        Assert.assertTrue(conn.getAutoCommit());
        conn.close();
    }
}

