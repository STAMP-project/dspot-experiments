package com.alibaba.druid.bvt.sql.odps;


import com.alibaba.druid.sql.SQLUtils;
import junit.framework.TestCase;
import org.junit.Assert;


public class OdpsFormatCommentTest30 extends TestCase {
    public void test_drop_function() throws Exception {
        String sql = "--??????????" + (((("\nCREATE TABLE xxx (" + "\n  aa STRING,") + "\n  asdasd STRING,") + "\n  asasd STRING") + "\n);");
        Assert.assertEquals(("-- ??????????" + (((("\nCREATE TABLE xxx (" + "\n\taa STRING,") + "\n\tasdasd STRING,") + "\n\tasasd STRING") + "\n);")), SQLUtils.formatOdps(sql));
    }
}

