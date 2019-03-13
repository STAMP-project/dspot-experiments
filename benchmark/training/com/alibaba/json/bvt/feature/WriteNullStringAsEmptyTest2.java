package com.alibaba.json.bvt.feature;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;
import junit.framework.TestCase;


/**
 * Created by wenshao on 14/03/2017.
 */
public class WriteNullStringAsEmptyTest2 extends TestCase {
    public void test_features() throws Exception {
        WriteNullStringAsEmptyTest2.Model model = new WriteNullStringAsEmptyTest2.Model();
        String json = JSON.toJSONString(model);
        TestCase.assertEquals("{\"id\":\"\"}", json);
    }

    public static class Model {
        @JSONField(serialzeFeatures = SerializerFeature.WriteNullStringAsEmpty)
        public String id;
    }
}

