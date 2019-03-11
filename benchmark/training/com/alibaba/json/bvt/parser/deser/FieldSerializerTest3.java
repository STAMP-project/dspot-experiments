package com.alibaba.json.bvt.parser.deser;


import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;
import junit.framework.TestCase;
import org.junit.Assert;


public class FieldSerializerTest3 extends TestCase {
    public void test_writeNull() throws Exception {
        String text = FieldSerializerTest3.toJSONString(new FieldSerializerTest3.Entity());
        Assert.assertEquals("{\"v\":\"xxx\"}", text);
    }

    private static class Entity {
        private int id;

        @JSONField(name = "v", serialzeFeatures = { SerializerFeature.WriteMapNullValue })
        private String value;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}

