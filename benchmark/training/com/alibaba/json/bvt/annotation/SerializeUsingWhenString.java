package com.alibaba.json.bvt.annotation;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import java.io.IOException;
import java.lang.reflect.Type;
import junit.framework.TestCase;
import org.junit.Assert;


/**
 *
 *
 * @author by laugh on 16/9/28 12:08.
 */
public class SerializeUsingWhenString extends TestCase {
    public void test_annotation() throws Exception {
        SerializeUsingWhenString.Model model = new SerializeUsingWhenString.Model();
        model.value = "100";
        Assert.assertEquals("{\"value\":\"100\u5143\"}", JSON.toJSONString(model));
        SerializeUsingWhenString.ModelWithOutJsonField modelWithOutJsonField = new SerializeUsingWhenString.ModelWithOutJsonField();
        modelWithOutJsonField.value = "100";
        Assert.assertEquals("{\"value\":\"100\"}", JSON.toJSONString(modelWithOutJsonField));
    }

    public static class Model {
        @JSONField(serializeUsing = SerializeUsingWhenString.ModelValueSerializer.class)
        public String value;
    }

    public static class ModelWithOutJsonField {
        public String value;
    }

    public static class ModelValueSerializer implements ObjectSerializer {
        public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) throws IOException {
            String value = ((String) (object));
            String text = value + "?";
            serializer.write(text);
        }
    }
}

