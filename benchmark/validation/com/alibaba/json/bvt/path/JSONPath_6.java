package com.alibaba.json.bvt.path;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import junit.framework.TestCase;


// public void test_path_2() throws Exception {
// //        File file = new File("/Users/wenshao/Downloads/test");
// //        String json = FileUtils.readFileToString(file);
// String json = "{\"returnObj\":[{\"$ref\":\"$.subInvokes.com\\\\.alipay\\\\.cif\\\\.user\\\\.UserInfoQueryService\\\\@findUserInfosByCardNo\\\\(String[])[0].response[0]\"}]}";
// JSON.parseObject(json);
// }
public class JSONPath_6 extends TestCase {
    public void test_path() throws Exception {
        String json = "{\"hello\":\"world\"}";
        JSONObject object = JSON.parseObject(json);
        TestCase.assertTrue(JSONPath.contains(object, "$.hello"));
        TestCase.assertTrue(JSONPath.contains(object, "hello"));
    }
}

