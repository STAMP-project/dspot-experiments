package com.alibaba.json.bvt.date;


import com.alibaba.fastjson.JSON;
import java.util.Date;
import junit.framework.TestCase;
import org.junit.Assert;


public class DateTest_dotnet_3 extends TestCase {
    public void test_date() throws Exception {
        String text = "{\"date\":\"/Date(1461081600321+0500)/\"}";
        DateTest_dotnet_3.Model model = JSON.parseObject(text, DateTest_dotnet_3.Model.class);
        Assert.assertEquals(1461081600321L, model.date.getTime());
    }

    private static class Model {
        private Date date;

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }
    }
}

