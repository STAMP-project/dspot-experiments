package com.alibaba.druid.bvt.pool;


import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.vendor.OracleValidConnectionChecker;
import junit.framework.TestCase;
import org.junit.Assert;


/**
 * ??????minIdle > maxActive
 *
 * @author wenshao [szujobs@hotmail.com]
 */
public class DruidDataSourceTest_oracle3 extends TestCase {
    private DruidDataSource dataSource;

    public void test_error() throws Exception {
        dataSource.init();
        Assert.assertTrue(dataSource.isOracle());
        Assert.assertTrue(((dataSource.getValidConnectionChecker()) instanceof OracleValidConnectionChecker));
    }
}

