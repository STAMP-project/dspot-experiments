package com.alibaba.druid.bvt.sql.odps;


import com.alibaba.druid.sql.SQLUtils;
import junit.framework.TestCase;
import org.junit.Assert;


public class OdpsFormatCommentTest27 extends TestCase {
    public void test_drop_function() throws Exception {
        String sql = "select split(val, ',')[1] from dual";
        Assert.assertEquals(("SELECT SPLIT(val, ',')[1]" + "\nFROM dual"), SQLUtils.formatOdps(sql));
    }
}

