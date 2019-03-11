package com.alibaba.json.bvt.writeClassName;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONType;
import com.alibaba.fastjson.serializer.SerializerFeature;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;


public class WriteClassNameTest3 extends TestCase {
    public void test_list() throws Exception {
        Map root = new HashMap();
        root.put("val", new WriteClassNameTest3.Model());
        String str = JSON.toJSONString(root);
        TestCase.assertEquals("{\"val\":{\"@type\":\"com.alibaba.json.bvt.writeClassName.WriteClassNameTest3$Model\"}}", str);
        JSON.parseObject(str);
    }

    @JSONType(serialzeFeatures = SerializerFeature.WriteClassName)
    public static final class Model {
        public String value;
    }
}

