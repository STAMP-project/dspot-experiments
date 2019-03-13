package com.alibaba.druid.bvt.pool;


import com.alibaba.druid.pool.DruidDataSource;
import java.sql.Connection;
import junit.framework.TestCase;
import org.junit.Assert;


/**
 * ??????initialSize > maxActive
 *
 * @author wenshao [szujobs@hotmail.com]
 */
public class DruidDataSourceTest_getPoolingPeakTime extends TestCase {
    private DruidDataSource dataSource;

    public void test_error() throws Exception {
        Assert.assertNull(dataSource.getPoolingPeakTime());
        Assert.assertNull(dataSource.getActivePeakTime());
        Connection conn = dataSource.getConnection();
        conn.close();
        Assert.assertNotNull(dataSource.getPoolingPeakTime());
        Assert.assertNotNull(dataSource.getActivePeakTime());
    }
}

