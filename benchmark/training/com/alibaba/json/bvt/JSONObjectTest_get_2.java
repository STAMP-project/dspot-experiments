package com.alibaba.json.bvt;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;
import org.junit.Assert;


public class JSONObjectTest_get_2 extends TestCase {
    public void test_get() throws Exception {
        JSONObject obj = JSON.parseObject("{\"value\":{}}");
        JSONObject value = ((JSONObject) (obj.getObject("value", Object.class)));
        Assert.assertEquals(0, value.size());
    }

    public void test_get_obj() throws Exception {
        JSONObject obj = new JSONObject();
        {
            Map<String, Object> value = new HashMap<String, Object>();
            value.put("@type", "com.alibaba.json.bvt.JSONObjectTest_get_2$VO");
            value.put("id", 1001);
            obj.put("value", value);
        }
        JSONObjectTest_get_2.VO value = ((JSONObjectTest_get_2.VO) (obj.getObject("value", Object.class)));
        Assert.assertEquals(1001, value.getId());
    }

    public static interface VO {
        @JSONField
        int getId();

        @JSONField
        void setId(int val);
    }
}

