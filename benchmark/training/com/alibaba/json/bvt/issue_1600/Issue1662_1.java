package com.alibaba.json.bvt.issue_1600;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import java.io.IOException;
import java.lang.reflect.Type;
import junit.framework.TestCase;


public class Issue1662_1 extends TestCase {
    public void test_for_issue() throws Exception {
        String json = "{\"value\":123}";
        Issue1662_1.Model model = JSON.parseObject(json, Issue1662_1.Model.class);
        TestCase.assertEquals("{\"value\":\"12300\u5143\"}", JSON.toJSONString(model));
    }

    public static class Model {
        @JSONField(serializeUsing = Issue1662_1.ModelValueSerializer.class, deserializeUsing = Issue1662_1.ModelValueDeserializer.class)
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
        public Integer deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
            Object val = parser.parse();
            return (((Integer) (val)).intValue()) * 100;
        }

        public int getFastMatchToken() {
            return 0;
        }
    }
}

