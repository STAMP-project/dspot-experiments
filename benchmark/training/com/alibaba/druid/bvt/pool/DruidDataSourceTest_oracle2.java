package com.alibaba.druid.bvt.pool;


import com.alibaba.druid.pool.DruidDataSource;
import junit.framework.TestCase;
import org.junit.Assert;


/**
 * ??????minIdle > maxActive
 *
 * @author wenshao [szujobs@hotmail.com]
 */
public class DruidDataSourceTest_oracle2 extends TestCase {
    private DruidDataSource dataSource;

    public void test_oracle() throws Exception {
        Assert.assertNull(dataSource.getConnectProperties().get("oracle.jdbc.FreeMemoryOnEnterImplicitCache"));
        dataSource.init();
        Assert.assertTrue(dataSource.isOracle());
        Assert.assertEquals("true", dataSource.getConnectProperties().get("oracle.jdbc.FreeMemoryOnEnterImplicitCache"));
        dataSource.setUseOracleImplicitCache(false);
        Assert.assertNull(dataSource.getConnectProperties().get("oracle.jdbc.FreeMemoryOnEnterImplicitCache"));
        dataSource.setUseOracleImplicitCache(true);
        dataSource.setUseOracleImplicitCache(true);
        Assert.assertEquals("true", dataSource.getConnectProperties().get("oracle.jdbc.FreeMemoryOnEnterImplicitCache"));
        dataSource.setUseOracleImplicitCache(false);
        Assert.assertNull(dataSource.getConnectProperties().get("oracle.jdbc.FreeMemoryOnEnterImplicitCache"));
        dataSource.setDriver(null);
        dataSource.setUseOracleImplicitCache(true);
        Assert.assertNull(dataSource.getConnectProperties().get("oracle.jdbc.FreeMemoryOnEnterImplicitCache"));
    }
}

