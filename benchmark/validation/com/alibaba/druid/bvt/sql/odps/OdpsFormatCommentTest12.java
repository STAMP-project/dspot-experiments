package com.alibaba.druid.bvt.sql.odps;


import com.alibaba.druid.sql.SQLUtils;
import junit.framework.TestCase;
import org.junit.Assert;


public class OdpsFormatCommentTest12 extends TestCase {
    public void test_column_comment() throws Exception {
        String sql = "select * from t --abc";
        Assert.assertEquals(("SELECT *"// 
         + "\nFROM t -- abc"), SQLUtils.formatOdps(sql));
    }
}

