package com.alibaba.json.bvt.issue_1100;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import java.io.Serializable;
import junit.framework.TestCase;


/**
 * Created by wenshao on 02/05/2017.
 */
public class Issue1178 extends TestCase {
    public void test_for_issue() throws Exception {
        String json = "{\n" + (((" \"info\": {\n" + "        \"test\": \"\", \n") + "    }\n") + "}");
        JSONObject jsonObject = JSON.parseObject(json);
        Issue1178.TestModel loginResponse = JSON.toJavaObject(jsonObject, Issue1178.TestModel.class);
    }

    public static class TestModel implements Serializable {
        public String info;
    }
}

