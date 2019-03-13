package com.alibaba.json.bvt.serializer.enum_;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONType;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import junit.framework.TestCase;


public class EnumCustomCodecTest extends TestCase {
    public void test_for_enum() throws Exception {
        EnumCustomCodecTest.Type type = EnumCustomCodecTest.Type.A;
        String str = JSON.toJSONString(type);
        TestCase.assertEquals("{\"id\":1001,\"name\":\"aaa\"}", str);
        EnumCustomCodecTest.Type type2 = JSON.parseObject(str, EnumCustomCodecTest.Type.class);
        EnumCustomCodecTest.Type type3 = JSON.parseObject(str, EnumCustomCodecTest.Type.class);
        TestCase.assertSame(type, type2);
        TestCase.assertSame(type, type3);
    }

    @JSONType(serializeEnumAsJavaBean = true, deserializer = EnumCustomCodecTest.TypeDeser.class)
    public static enum Type {

        A(1001, "aaa"),
        B(1002, "bbb");
        public int id;

        public String name;

        Type(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public static EnumCustomCodecTest.Type valueOf(long id) {
            if (id == 1001) {
                return EnumCustomCodecTest.Type.A;
            } else
                if (id == 1002) {
                    return EnumCustomCodecTest.Type.B;
                }

            return null;
        }
    }

    public static class TypeDeser implements ObjectDeserializer {
        public <T> T deserialze(DefaultJSONParser parser, java.lang.reflect.Type type, Object fieldName) {
            JSONObject object = parser.parseObject();
            long id = object.getLongValue("id");
            return ((T) (EnumCustomCodecTest.Type.valueOf(id)));
        }

        public int getFastMatchToken() {
            return 0;
        }
    }
}

