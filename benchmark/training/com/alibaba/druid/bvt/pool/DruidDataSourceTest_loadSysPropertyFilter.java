package com.alibaba.druid.bvt.pool;


import com.alibaba.druid.pool.DruidDataSource;
import junit.framework.TestCase;
import org.junit.Assert;


/**
 * ??????defaultAutoCommit
 *
 * @author wenshao [szujobs@hotmail.com]
 */
public class DruidDataSourceTest_loadSysPropertyFilter extends TestCase {
    private DruidDataSource dataSource;

    public void test_autoCommit() throws Exception {
        dataSource.init();
        Assert.assertEquals(2, dataSource.getProxyFilters().size());
    }
}

