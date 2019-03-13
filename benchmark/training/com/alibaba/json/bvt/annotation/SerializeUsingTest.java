package com.alibaba.json.bvt.annotation;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import java.io.IOException;
import java.lang.reflect.Type;
import junit.framework.TestCase;
import org.junit.Assert;


public class SerializeUsingTest extends TestCase {
    public void test_annotation() throws Exception {
        SerializeUsingTest.Model model = new SerializeUsingTest.Model();
        model.value = 100;
        String json = JSON.toJSONString(model);
        Assert.assertEquals("{\"value\":\"100\u5143\"}", json);
        SerializeUsingTest.Model model2 = JSON.parseObject(json, SerializeUsingTest.Model.class);
        Assert.assertEquals(model.value, model2.value);
    }

    public static class Model {
        @JSONField(serializeUsing = SerializeUsingTest.ModelValueSerializer.class, deserializeUsing = SerializeUsingTest.ModelValueDeserializer.class)
        public int value;
    }

    public static class ModelValueSerializer implements ObjectSerializer {
        public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) throws IOException {
            Integer value = ((Integer) (object));
            String text = value + "?";
            serializer.write(text);
        }
    }

    public static class ModelValueDeserializer implements ObjectDeserializer {
        public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
            String text = ((String) (parser.parse()));
            if (text != null) {
                text = text.replaceAll("?", "");
            }
            return ((T) (Integer.valueOf(Integer.parseInt(text))));
        }

        @Override
        public int getFastMatchToken() {
            return 0;
        }
    }
}

