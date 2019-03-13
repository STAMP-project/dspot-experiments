package com.alibaba.druid.bvt.sql;


import JdbcUtils.H2;
import com.alibaba.druid.sql.PagerUtils;
import junit.framework.TestCase;
import org.junit.Assert;


public class PagerUtilsTest_Limit_h2_0 extends TestCase {
    public void test_db2_union() throws Exception {
        String sql = "select * from t1 union select * from t2";
        String result = PagerUtils.limit("SELECT * FROM test", H2, 0, 10);
        System.out.println(result);
        Assert.assertEquals(("SELECT *\n" + ("FROM test\n" + "LIMIT 10")), result);
    }
}

