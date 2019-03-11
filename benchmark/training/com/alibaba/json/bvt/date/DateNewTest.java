package com.alibaba.json.bvt.date;


import com.alibaba.fastjson.JSON;
import java.util.Date;
import junit.framework.TestCase;
import org.junit.Assert;


public class DateNewTest extends TestCase {
    public void test_date() throws Exception {
        Assert.assertEquals(1324138987429L, ((Date) (JSON.parse("new Date(1324138987429)"))).getTime());
        Assert.assertEquals(1324138987429L, ((Date) (JSON.parse("new \n\t\r\f\bDate(1324138987429)"))).getTime());
    }
}

