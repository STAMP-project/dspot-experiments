package com.alibaba.json.bvt.path;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONPath;
import junit.framework.TestCase;


// public void test_path_2() throws Exception {
// //        File file = new File("/Users/wenshao/Downloads/test");
// //        String json = FileUtils.readFileToString(file);
// String json = "{\"returnObj\":[{\"$ref\":\"$.subInvokes.com\\\\.alipay\\\\.cif\\\\.user\\\\.UserInfoQueryService\\\\@findUserInfosByCardNo\\\\(String[])[0].response[0]\"}]}";
// JSON.parseObject(json);
// }
public class JSONPath_10_contains extends TestCase {
    public void test() {
        String json = "{\n" + (((((((((((((((((((((((((((((((((((((("    \"queryScene\":{\n" + "        \"scene\":[\n") + "            {\n") + "                \"innerSceneId\":3,\n") + "                \"name\":\"\u573a\u666f\u7ed18\u6d4b\u8bd5-\u7b11\u5e7d\",\n") + "                \"sceneSetId\":8,\n") + "                \"formInfo\":\"{}\",\n") + "                \"queryDataSet\":{\n") + "                    \"dataSet\":[\n") + "                        {\n") + "                            \"id\":6,\n") + "                            \"sceneId\":3,\n") + "                            \"name\":\"\u6d4b\u8bd5\u5546\u54c1\u96c6\",\n") + "                            \"dataSetRuleCode\":null,\n") + "                            \"resourceId\":null,\n") + "                            \"udsOffer\":{\n") + "                                \"offer\":[\n") + "\n") + "                                ]\n") + "                            }\n") + "                        },\n") + "                        {\n") + "                            \"id\":5,\n") + "                            \"sceneId\":3,\n") + "                            \"name\":\"\u6d4b\u8bd5\u5356\u5bb6\u96c6\",\n") + "                            \"dataSetRuleCode\":null,\n") + "                            \"resourceId\":null,\n") + "                            \"udsOffer\":{\n") + "                                \"offer\":[\n") + "\n") + "                                ]\n") + "                            }\n") + "                        }\n") + "                    ]\n") + "                }\n") + "            }\n") + "        ]\n") + "    }\n") + "}");
        TestCase.assertTrue(JSONPath.contains(JSON.parseObject(json), "$.queryScene.scene.queryDataSet.dataSet"));
        TestCase.assertFalse(JSONPath.contains(JSON.parseObject(json), "$.queryScene.scene.queryDataSet.dataSet.abcd"));
        TestCase.assertTrue(JSONPath.contains(JSON.parseObject(json), "$.queryScene.scene.queryDataSet.dataSet.name"));
    }
}

