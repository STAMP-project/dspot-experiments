package com.alibaba.druid.bvt.sql.odps;


import com.alibaba.druid.sql.SQLUtils;
import junit.framework.TestCase;
import org.junit.Assert;


public class OdpsFormatCommentTest7 extends TestCase {
    public void test_column_comment() throws Exception {
        String sql = "--?????"// 
         + ("\nselect * from table1;"// 
         + "\nselect * from table2;;");// 

        Assert.assertEquals(("-- ?????" + (((("\nSELECT *" + "\nFROM table1;") + "\n") + "\nSELECT *") + "\nFROM table2;")), SQLUtils.formatOdps(sql));
    }
}

