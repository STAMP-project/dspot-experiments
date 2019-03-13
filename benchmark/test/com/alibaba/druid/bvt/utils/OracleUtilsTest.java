package com.alibaba.druid.bvt.utils;


import com.alibaba.druid.util.OracleUtils;
import junit.framework.TestCase;


/**
 * Created by wenshao on 19/06/2017.
 */
public class OracleUtilsTest extends TestCase {
    public void test_builtin() throws Exception {
        TestCase.assertTrue(OracleUtils.isBuiltinFunction("nvl"));
        TestCase.assertTrue(OracleUtils.isBuiltinFunction("NVL"));
        TestCase.assertFalse(OracleUtils.isBuiltinFunction("xxx_nvl"));
        TestCase.assertTrue(OracleUtils.isBuiltinTable("user_ts_quotas"));
        TestCase.assertTrue(OracleUtils.isBuiltinTable("user_TS_quotas"));
        TestCase.assertFalse(OracleUtils.isBuiltinTable("user_TS_quotas_xxxxxxxxxxxx"));
    }
}

