package com.alibaba.druid.bvt.utils;


import JdbcConstants.ODPS;
import JdbcConstants.ODPS_DRIVER;
import com.alibaba.druid.util.JdbcUtils;
import junit.framework.TestCase;
import org.junit.Assert;


public class JdbcUtilsTest_for_odps extends TestCase {
    public void test_odps() throws Exception {
        Assert.assertEquals(ODPS_DRIVER, JdbcUtils.getDriverClassName("jdbc:odps:"));
    }

    public void test_odps_dbtype() throws Exception {
        Assert.assertEquals(ODPS, JdbcUtils.getDbType("jdbc:odps:", null));
    }
}

