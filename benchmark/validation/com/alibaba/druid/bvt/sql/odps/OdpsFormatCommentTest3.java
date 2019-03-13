package com.alibaba.druid.bvt.sql.odps;


import com.alibaba.druid.sql.SQLUtils;
import junit.framework.TestCase;
import org.junit.Assert;


public class OdpsFormatCommentTest3 extends TestCase {
    public void test_column_comment() throws Exception {
        String sql = "-- ??????"// 
         + (((((("\nset odps.service.mode=all;"// 
         + "\n-- \u4f7f\u7528\u65b0\u5f15\u64ce")// 
         + "\nset odps.nvm.enabled=true;")// 
         + "\nselect f1 -- aa")// 
         + "\nfrom t0;")// 
         + "\nselect f2 -- aa")// 
         + "\nfrom t1;");// 

        Assert.assertEquals(("-- ??????"// 
         + (((((((("\nSET odps.service.mode = all;"// 
         + "\n-- \u4f7f\u7528\u65b0\u5f15\u64ce")// 
         + "\nSET odps.nvm.enabled = true;")// 
         + "\n")// 
         + "\nSELECT f1 -- aa")// 
         + "\nFROM t0;")// 
         + "\n")// 
         + "\nSELECT f2 -- aa")// 
         + "\nFROM t1;")), SQLUtils.formatOdps(sql));
    }
}

