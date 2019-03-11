package com.alibaba.json.bvt.path;


import com.alibaba.fastjson.JSONPath;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import junit.framework.TestCase;


// public void test_path_2() throws Exception {
// //        File file = new File("/Users/wenshao/Downloads/test");
// //        String json = FileUtils.readFileToString(file);
// String json = "{\"returnObj\":[{\"$ref\":\"$.subInvokes.com\\\\.alipay\\\\.cif\\\\.user\\\\.UserInfoQueryService\\\\@findUserInfosByCardNo\\\\(String[])[0].response[0]\"}]}";
// JSON.parseObject(json);
// }
public class JSONPath_9 extends TestCase {
    public void test_paths() throws Exception {
        JSONPath_9.Model m = new JSONPath_9.Model();
        m.f0 = 101;
        m.f1 = 102;
        Map<String, Object> paths = JSONPath.paths(m);
        TestCase.assertEquals(3, paths.size());
    }

    public void test_paths_1() throws Exception {
        Map map = new HashMap();
        map.put("f0", 1001);
        map.put("f1", 1002);
        Map<String, Object> paths = JSONPath.paths(map);
        TestCase.assertEquals(3, paths.size());
    }

    public void test_paths_2() throws Exception {
        Map map = new HashMap();
        map.put("f0", 1001);
        map.put("f1", 1002);
        JSONPath path = new JSONPath("$.f0");
        TestCase.assertEquals("$.f0", path.getPath());
        TestCase.assertEquals(1001, path.eval(map));
        path.remove(null);
    }

    public void test_paths_3() throws Exception {
        JSONPath.paths(null);
        JSONPath.paths(1);
        JSONPath.paths("1");
        JSONPath.paths(TimeUnit.DAYS);
    }

    public static class Model {
        public Integer f0;

        public Integer f1;
    }
}

