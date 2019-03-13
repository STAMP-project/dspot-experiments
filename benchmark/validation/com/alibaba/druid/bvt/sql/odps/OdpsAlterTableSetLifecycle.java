package com.alibaba.druid.bvt.sql.odps;


import com.alibaba.druid.sql.SQLUtils;
import junit.framework.TestCase;
import org.junit.Assert;


public class OdpsAlterTableSetLifecycle extends TestCase {
    public void test_if() throws Exception {
        String sql = "alter table test_lifecycle set lifecycle 50;";
        Assert.assertEquals(("ALTER TABLE test_lifecycle"// 
         + "\n\tSET LIFECYCLE 50;"), SQLUtils.formatOdps(sql));
    }
}

