package com.alibaba.json.bvt.ref;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import junit.framework.TestCase;


/**
 * Created by wenshao on 16/8/23.
 */
public class RefTest22 extends TestCase {
    public void test_ref() throws Exception {
        String json = "{\"name\":\"123\",\"assetSize\":{},\"items\":[{\"id\":123}],\"refItems\":{\"$ref\":\"$.items[0]\"}}";
        JSONObject root = JSON.parseObject(json);
        TestCase.assertSame(root.getJSONArray("items").get(0), root.getJSONObject("refItems"));
    }
}

