package com.alibaba.druid.bvt.sql;


import JdbcConstants.SQL_SERVER;
import com.alibaba.druid.sql.PagerUtils;
import junit.framework.TestCase;
import org.junit.Assert;


public class PagerUtilsTest_Limit_SQLServer_3 extends TestCase {
    public void test_db2_union() throws Exception {
        String sql = "select * from t1 where id > 1";
        String result = PagerUtils.limit(sql, SQL_SERVER, 0, 10);
        Assert.assertEquals(("SELECT TOP 10 *"// 
         + ("\nFROM t1"// 
         + "\nWHERE id > 1")), result);
    }
}

