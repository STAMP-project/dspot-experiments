package com.alibaba.json.bvt.support.oracle;


import com.alibaba.fastjson.JSON;
import java.sql.Timestamp;
import junit.framework.TestCase;
import oracle.sql.TIMESTAMP;
import org.junit.Assert;


public class TestOracleDATE extends TestCase {
    public void test_0() throws Exception {
        Timestamp date = new Timestamp(System.currentTimeMillis());
        TIMESTAMP timestamp = new TIMESTAMP(date);
        String text = JSON.toJSONString(timestamp);
        Assert.assertEquals(date.getTime(), Long.parseLong(text));
    }
}

