package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import java.util.Date;
import junit.framework.TestCase;


/**
 * Created by wenshao on 16/9/2.
 */
public class Issue801 extends TestCase {
    public void test_for_issue() throws Exception {
        String json = "{\"date\":\"0001-01-01T00:00:00\"}";
        Issue801.Model model = JSON.parseObject(json, Issue801.Model.class);
    }

    public static class Model {
        @JSONField(format = "yyyy-MM-ddTHH:mm:ss.SSS")
        public Date date;
    }
}

