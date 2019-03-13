package com.alibaba.druid.bvt.utils;


import JdbcConstants.GBASE_DRIVER;
import JdbcConstants.KINGBASE_DRIVER;
import JdbcConstants.ORACLE_DRIVER;
import JdbcConstants.XUGU;
import JdbcConstants.XUGU_DRIVER;
import com.alibaba.druid.util.JdbcUtils;
import junit.framework.TestCase;


public class JdbcUtilsTest2 extends TestCase {
    public void test_get_0() throws Exception {
        TestCase.assertEquals(ORACLE_DRIVER, JdbcUtils.getDriverClassName("JDBC:oracle:"));
    }

    public void test_gbase() throws Exception {
        TestCase.assertEquals(GBASE_DRIVER, JdbcUtils.getDriverClassName("jdbc:gbase:"));
    }

    public void test_kingbase() throws Exception {
        TestCase.assertEquals(KINGBASE_DRIVER, JdbcUtils.getDriverClassName("jdbc:kingbase:"));
    }

    public void test_xugu_dbtype() throws Exception {
        TestCase.assertEquals(XUGU, JdbcUtils.getDbType("jdbc:xugu://127.0.0.1:5138/TEST", "com.xugu.cloudjdbc.Driver"));
    }

    public void test_xugu_driver() throws Exception {
        TestCase.assertEquals(XUGU_DRIVER, JdbcUtils.getDriverClassName("jdbc:xugu:"));
    }
}

