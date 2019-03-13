package com.alibaba.druid.bvt.sql.odps;


import com.alibaba.druid.sql.SQLUtils;
import junit.framework.TestCase;
import org.junit.Assert;


public class OdpsFormatCommentTest29 extends TestCase {
    public void test_drop_function() throws Exception {
        String sql = "create table xxxx001(   --??" + (("\ncol string,  --\u6d4b\u8bd52" + "\ncol2 string  --\u6d4b\u8bd53") + "\n)");
        Assert.assertEquals(("CREATE TABLE xxxx001 ( -- ??" + (("\n\tcol STRING, -- \u6d4b\u8bd52" + "\n\tcol2 STRING -- \u6d4b\u8bd53") + "\n)")), SQLUtils.formatOdps(sql));
    }
}

