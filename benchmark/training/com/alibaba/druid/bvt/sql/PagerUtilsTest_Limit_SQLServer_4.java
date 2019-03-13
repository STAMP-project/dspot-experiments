package com.alibaba.druid.bvt.sql;


import JdbcConstants.SQL_SERVER;
import com.alibaba.druid.sql.PagerUtils;
import junit.framework.TestCase;
import org.junit.Assert;


public class PagerUtilsTest_Limit_SQLServer_4 extends TestCase {
    public void test_db2_union() throws Exception {
        String sql = "select * from t1 where id > 1";
        String result = PagerUtils.limit(sql, SQL_SERVER, 100, 10);
        Assert.assertEquals(("SELECT *\n" + (((((("FROM (\n" + "\tSELECT *, ROW_NUMBER() AS ROWNUM\n") + "\tFROM t1\n") + "\tWHERE id > 1\n") + ") XX\n") + "WHERE ROWNUM > 100\n") + "\tAND ROWNUM <= 110")), result);
    }
}

