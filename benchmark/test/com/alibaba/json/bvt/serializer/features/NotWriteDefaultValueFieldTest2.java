package com.alibaba.json.bvt.serializer.features;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONType;
import com.alibaba.fastjson.serializer.SerializerFeature;
import junit.framework.TestCase;


/**
 * Created by wenshao on 01/04/2017.
 */
public class NotWriteDefaultValueFieldTest2 extends TestCase {
    public void test_not_write_default() throws Exception {
        TestCase.assertEquals("{}", JSON.toJSONString(new NotWriteDefaultValueFieldTest2.Model(0)));
        TestCase.assertEquals("{\"id\":1}", JSON.toJSONString(new NotWriteDefaultValueFieldTest2.Model(1)));
    }

    @JSONType(serialzeFeatures = SerializerFeature.NotWriteDefaultValue)
    public static class Model {
        public int id;

        public Model(int id) {
            this.id = id;
        }
    }
}

