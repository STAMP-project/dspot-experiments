package com.alibaba.json.bvt.support.oracle;


import com.alibaba.fastjson.JSON;
import java.sql.Timestamp;
import junit.framework.TestCase;
import oracle.sql.DATE;
import org.junit.Assert;


public class TestOracleTIMESTAMP extends TestCase {
    public void test_0() throws Exception {
        Timestamp date = new Timestamp(System.currentTimeMillis());
        DATE timestamp = new DATE(date);
        String text = JSON.toJSONString(timestamp);
        Assert.assertEquals((((date.getTime()) / 1000) * 1000), Long.parseLong(text));
    }
}

