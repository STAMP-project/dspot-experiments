package com.alibaba.druid.bvt.sql.odps;


import com.alibaba.druid.sql.SQLUtils;
import junit.framework.TestCase;
import org.junit.Assert;


public class OdpsFormatCommentTest2 extends TestCase {
    public void test_column_comment() throws Exception {
        String sql = "--[Subject -]"// 
         + (((("\n--[Author  -\u9ad8\u94c1/035139]"// 
         + "\n--[Created -2015-06-10 13:19:18]")// 
         + "\n--[Update ]")// 
         + "\nset odps.sql.mapper.split.size=2048;")// 
         + "\nselect * from dual;");
        Assert.assertEquals(("-- [Subject -]" + (((((("\n-- [Author  -\u9ad8\u94c1/035139]" + "\n-- [Created -2015-06-10 13:19:18]") + "\n-- [Update ]") + "\nSET odps.sql.mapper.split.size = 2048;") + "\n")// 
         + "\nSELECT *") + "\nFROM dual;")), SQLUtils.formatOdps(sql));
    }
}

