package com.alibaba.json.bvt.path;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONPath;
import junit.framework.TestCase;


// public void test_path_2() throws Exception {
// //        File file = new File("/Users/wenshao/Downloads/test");
// //        String json = FileUtils.readFileToString(file);
// String json = "{\"returnObj\":[{\"$ref\":\"$.subInvokes.com\\\\.alipay\\\\.cif\\\\.user\\\\.UserInfoQueryService\\\\@findUserInfosByCardNo\\\\(String[])[0].response[0]\"}]}";
// JSON.parseObject(json);
// }
public class JSONPath_7 extends TestCase {
    public void test_path() throws Exception {
        JSONPath_7.Model[] array = new JSONPath_7.Model[]{ new JSONPath_7.Model(101), new JSONPath_7.Model(202), new JSONPath_7.Model(303) };
        JSONArray values = ((JSONArray) (JSONPath.eval(array, "$.id")));
        TestCase.assertEquals(101, values.get(0));
        TestCase.assertEquals(202, values.get(1));
        TestCase.assertEquals(303, values.get(2));
        TestCase.assertEquals(3, JSONPath.eval(array, "$.length"));
    }

    public static class Model {
        public int id;

        public Model(int id) {
            this.id = id;
        }
    }
}

