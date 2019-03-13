package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONReader;
import com.alibaba.fastjson.annotation.JSONField;
import java.io.StringReader;
import java.util.Date;
import junit.framework.TestCase;


public class Issue744 extends TestCase {
    public static class Model {
        @JSONField(format = "yyyy-MM-dd'T'HH:mm:ss")
        public Date date;
    }

    public void test() {
        String text = "{\"date\":\"9999-09-08T00:00:00\"}";
        Issue744.Model model = JSON.parseObject(text, Issue744.Model.class);
        String text2 = JSON.toJSONString(model);
        System.out.println(text2);
    }

    public void test_reader() {
        String text = "{\"date\":\"9999-09-08T00:00:00\"}";
        Issue744.Model model = new JSONReader(new StringReader(text)).readObject(Issue744.Model.class);
        String text2 = JSON.toJSONString(model);
        System.out.println(text2);
    }
}

