package com.alibaba.json.bvt.path;


import Feature.DisableSpecialKeyDetect;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import junit.framework.TestCase;


public class TestSpecial_3 extends TestCase {
    public void test_special() throws Exception {
        String json = "[{\"@type\":\"NAME_CORRECTION\",\"value\":23}]";
        JSONArray array = ((JSONArray) (JSON.parse(json, DisableSpecialKeyDetect)));
        Object obj = JSONPath.eval(array, "[\\@type=\'NAME_CORRECTION\']");
        TestCase.assertNotNull(obj);
    }

    public void test_special_1() throws Exception {
        String json = "[{\":lang\":\"NAME_CORRECTION\",\"value\":23}]";
        JSONArray array = ((JSONArray) (JSON.parse(json, DisableSpecialKeyDetect)));
        Object obj = JSONPath.eval(array, "[\\:lang=\'NAME_CORRECTION\']");
        TestCase.assertNotNull(obj);
    }

    public void test_special_2() throws Exception {
        String json = "{\"cpe-item\":{\"@name\":\"cpe:/a:google:chrome:4.0.249.19\",\"cpe-23:cpe23-item\":{\"@name\":\"cpe:2.3:a:google:chrome:4.0.249.19:*:*:*:*:*:*:*\"},\"title\":[{\"#text\":\"\u30b0\u30fc\u30b0\u30eb \u30af\u30ed\u30fc\u30e0 4.0.249.19\",\"@xml:lang\":\"ja-JP\"},{\"#text\":\"Google Chrome 4.0.249.19\",\"@xml:lang\":\"en-US\"}]}}";
        String path = "[\'cpe-item\'][\'title\'][\\@xml\\:lang=\'en-US\'][\'#text\'][0]";
        JSONObject object = ((JSONObject) (JSON.parse(json, DisableSpecialKeyDetect)));
        Object obj = JSONPath.eval(object, path);
        TestCase.assertNotNull(obj);
    }
}

