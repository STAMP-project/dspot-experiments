package com.alibaba.json.bvt.annotation;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONReader;
import com.alibaba.fastjson.annotation.JSONType;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import java.lang.reflect.Type;
import junit.framework.TestCase;
import org.junit.Assert;


public class CustomDeserializerTest extends TestCase {
    public void test_0() throws Exception {
        String text = "{\"xid\":1001}";
        CustomDeserializerTest.Model model = JSON.parseObject(text, CustomDeserializerTest.Model.class);
        Assert.assertEquals(1001, model.id);
    }

    @JSONType(deserializer = CustomDeserializerTest.ModelDeserializer.class)
    public static class Model {
        public int id;
    }

    public static class ModelDeserializer implements ObjectDeserializer {
        @Override
        public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
            JSONReader reader = new JSONReader(parser);
            reader.startObject();
            String key = reader.readString();
            Integer value = reader.readInteger();
            CustomDeserializerTest.Model model = new CustomDeserializerTest.Model();
            model.id = value;
            reader.endObject();
            // TODO Auto-generated method stub
            return ((T) (model));
        }

        @Override
        public int getFastMatchToken() {
            return 0;
        }
    }
}

