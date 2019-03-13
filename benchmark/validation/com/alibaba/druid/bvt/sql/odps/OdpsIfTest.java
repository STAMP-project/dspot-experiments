package com.alibaba.druid.bvt.sql.odps;


import com.alibaba.druid.sql.SQLUtils;
import junit.framework.TestCase;
import org.junit.Assert;


public class OdpsIfTest extends TestCase {
    public void test_if() throws Exception {
        String sql = "select sum(if(a > 0, 1, 0)) from t1";
        Assert.assertEquals(("SELECT SUM(IF(a > 0, 1, 0))"// 
         + "\nFROM t1"), SQLUtils.formatOdps(sql));
    }
}

