package com.alibaba.json.bvt.issue_1900;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import junit.framework.TestCase;


public class Issue1901 extends TestCase {
    protected Locale locale;

    protected TimeZone timeZone;

    public void test_for_issue() throws Exception {
        Issue1901.Model m = JSON.parseObject("{\"time\":\"Thu Mar 22 08:58:37 +0000 2018\"}", Issue1901.Model.class);
        TestCase.assertEquals("{\"time\":\"\u661f\u671f\u56db \u4e09\u6708 22 16:58:37 CST 2018\"}", JSON.toJSONString(m));
    }

    public void test_for_issue_1() throws Exception {
        Issue1901.Model m = JSON.parseObject("{\"time\":\"\u661f\u671f\u56db \u4e09\u6708 22 16:58:37 CST 2018\"}", Issue1901.Model.class);
        TestCase.assertEquals("{\"time\":\"\u661f\u671f\u56db \u4e09\u6708 22 16:58:37 CST 2018\"}", JSON.toJSONString(m));
    }

    public static class Model {
        @JSONField(format = "EEE MMM dd HH:mm:ss zzz yyyy")
        public Date time;
    }
}

