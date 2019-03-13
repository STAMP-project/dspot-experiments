package com.alibaba.json.bvt;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import org.junit.Assert;
import org.junit.Test;


public class Bug89 {
    @Test
    public void testBug89() {
        try {
            String s = "{\"a\":\u0437\u300d\u2220)_,\"}";
            JSON.parseObject(s);
            Assert.fail("Expect JSONException");
        } catch (JSONException e) {
            // good
        }
    }
}

