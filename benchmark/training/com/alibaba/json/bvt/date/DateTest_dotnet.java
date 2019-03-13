package com.alibaba.json.bvt.date;


import com.alibaba.fastjson.JSON;
import java.util.Date;
import junit.framework.TestCase;
import org.junit.Assert;


public class DateTest_dotnet extends TestCase {
    public void test_date() throws Exception {
        String text = "{\"date\":\"/Date(1461081600000)/\"}";
        DateTest_dotnet.Model model = JSON.parseObject(text, DateTest_dotnet.Model.class);
        Assert.assertEquals(1461081600000L, model.date.getTime());
    }

    public static class Model {
        public Date date;
    }
}

