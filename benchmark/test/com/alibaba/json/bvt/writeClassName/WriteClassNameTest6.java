package com.alibaba.json.bvt.writeClassName;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONType;
import junit.framework.TestCase;


/**
 * Created by wenshao on 14/08/2017.
 */
public class WriteClassNameTest6 extends TestCase {
    public void test_for_writeClassName() throws Exception {
        String json = "{\"@type\":\"java.util.HashMap\",\"@type\":\"com.alibaba.json.bvt.writeClassName.WriteClassNameTest6$Model\",\"id\":1001}";
        WriteClassNameTest6.Model model = ((WriteClassNameTest6.Model) (JSON.parse(json)));
        TestCase.assertNotNull(model);
    }

    public void test_for_writeClassName_1() throws Exception {
        String json = "{\"@type\":\"java.util.HashMap\",\"@type\":\"com.alibaba.json.bvt.writeClassName.WriteClassNameTest6$Model\",\"id\":1001}";
        WriteClassNameTest6.Model model = JSON.parseObject(json, WriteClassNameTest6.Model.class);
        TestCase.assertNotNull(model);
    }

    @JSONType
    public static class Model {
        public int id;
    }
}

