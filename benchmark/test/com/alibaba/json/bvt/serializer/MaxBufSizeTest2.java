package com.alibaba.json.bvt.serializer;


import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import java.util.Arrays;
import junit.framework.TestCase;


/**
 * Created by wenshao on 01/04/2017.
 */
public class MaxBufSizeTest2 extends TestCase {
    public void test_max_buf() throws Exception {
        char[] chars = new char[4096];
        Arrays.fill(chars, '0');
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("val", new String(chars));
        Throwable error = null;
        try {
            toJSONString(jsonObject);
        } catch (JSONException e) {
            error = e;
        }
        TestCase.assertNotNull(error);
    }
}

