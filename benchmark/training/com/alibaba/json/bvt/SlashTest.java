package com.alibaba.json.bvt;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import junit.framework.TestCase;
import org.junit.Assert;


public class SlashTest extends TestCase {
    public void test_0() throws Exception {
        String text = "{\"errorMessage\":\"resource \'/rpc/hello/none.json\' is not found !\"}";
        JSONObject json = ((JSONObject) (JSON.parse(text)));
        Assert.assertEquals("{\"errorMessage\":\"resource \'/rpc/hello/none.json\' is not found !\"}", json.toString());
    }
}

