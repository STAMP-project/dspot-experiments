package com.alibaba.druid.bvt.sql.odps;


import com.alibaba.druid.sql.SQLUtils;
import junit.framework.TestCase;
import org.junit.Assert;


public class OdpsUDFTest extends TestCase {
    public void test_if() throws Exception {
        String sql = "select secods:ip_region('192.168.1.1', 'city') from dual";
        Assert.assertEquals(("SELECT secods:ip_region('192.168.1.1', 'city')"// 
         + "\nFROM dual"), SQLUtils.formatOdps(sql));
    }
}

