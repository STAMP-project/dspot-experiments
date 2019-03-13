package com.alibaba.druid.bvt.sql.odps;


import com.alibaba.druid.sql.SQLUtils;
import junit.framework.TestCase;
import org.junit.Assert;


public class OdpsCaseWhenTest extends TestCase {
    public void test_if() throws Exception {
        String sql = "select case when f1 = 'aaa' then 1 when f1 = 'bbb' then 2 else 3 end from dual";
        Assert.assertEquals(("SELECT CASE " + (((("\n\t\tWHEN f1 = \'aaa\' THEN 1" + "\n\t\tWHEN f1 = \'bbb\' THEN 2") + "\n\t\tELSE 3") + "\n\tEND") + "\nFROM dual")), SQLUtils.formatOdps(sql));
    }
}

