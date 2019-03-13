package com.alibaba.json.bvt.issue_1300;


import com.alibaba.fastjson.JSONObject;
import junit.framework.TestCase;


/**
 * Created by wenshao on 03/08/2017.
 */
public class Issue1362 extends TestCase {
    public void test_for_issue() throws Exception {
        JSONObject object = new JSONObject();
        object.put("val", "null");
        TestCase.assertEquals(0.0, object.getDoubleValue("val"));
        TestCase.assertEquals(0.0F, object.getFloatValue("val"));
        TestCase.assertEquals(0, object.getIntValue("val"));
        TestCase.assertEquals(0L, object.getLongValue("val"));
        TestCase.assertEquals(((short) (0)), object.getShortValue("val"));
        TestCase.assertEquals(((byte) (0)), object.getByteValue("val"));
        TestCase.assertEquals(false, object.getBooleanValue("val"));
    }
}

