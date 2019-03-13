package com.alibaba.json.bvt.bug;


import StringCodec.instance;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.JSONToken;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import junit.framework.TestCase;


/**
 * Created by wenshao on 08/01/2017.
 */
public class Issue963 extends TestCase {
    public void test_for_issue() throws Exception {
        Issue963.Mock mock = JSON.parseObject("{\"type\":\"boolean\"}", Issue963.Mock.class);
        TestCase.assertEquals(Issue963.EnumType.BOOLEAN, mock.getType());
    }

    public enum EnumType {

        BOOLEAN;
        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }

    public static class Mock {
        @JSONField(serializeUsing = Issue963.EnumTypeCodec.class, deserializeUsing = Issue963.EnumTypeCodec.class)
        private Issue963.EnumType type;

        public Issue963.EnumType getType() {
            return type;
        }

        public void setType(Issue963.EnumType type) {
            this.type = type;
        }
    }

    public static class EnumTypeCodec implements ObjectDeserializer , ObjectSerializer {
        public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
            String uncasedSensitive = instance.deserialze(parser, type, fieldName);
            return ((T) (Issue963.EnumType.valueOf(uncasedSensitive.toUpperCase())));
        }

        public int getFastMatchToken() {
            return JSONToken.LITERAL_STRING;
        }

        public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) throws IOException {
            SerializeWriter out = serializer.out;
            if (object == null) {
                out.writeNull();
                return;
            }
            instance.write(serializer, ((Issue963.EnumType) (object)).name().toLowerCase(), fieldName, fieldType, features);
        }
    }
}

