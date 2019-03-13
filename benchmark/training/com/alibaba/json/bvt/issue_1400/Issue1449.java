package com.alibaba.json.bvt.issue_1400;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONType;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.alibaba.fastjson.serializer.JSONSerializable;
import com.alibaba.fastjson.serializer.JSONSerializer;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import junit.framework.TestCase;


public class Issue1449 extends TestCase {
    public void test_for_issue() throws Exception {
        Issue1449.Student student = new Issue1449.Student();
        student.name = "name";
        student.id = 1L;
        student.sex = Issue1449.Sex.MAN;
        System.out.println(JSON.toJSON(student).toString());
        System.out.println(JSON.toJSONString(student));
        String str1 = "{\"id\":1,\"name\":\"name\",\"sex\":\"MAN\"}";
        Issue1449.Student stu1 = JSON.parseObject(str1, Issue1449.Student.class);
        System.out.println(JSON.toJSONString(stu1));
        String str2 = "{\"id\":1,\"name\":\"name\",\"sex\":{\"code\":\"1\",\"des\":\"\u7537\"}}";
        JSON.parseObject(str2, Issue1449.Student.class);
    }

    @JSONType(deserializer = Issue1449.SexDeserializer.class)
    public static enum Sex implements JSONSerializable {

        NONE("0", "NONE"),
        MAN("1", "?"),
        WOMAN("2", "?");
        private final String code;

        private final String des;

        private Sex(String code, String des) {
            this.code = code;
            this.des = des;
        }

        public String getCode() {
            return code;
        }

        public String getDes() {
            return des;
        }

        public void write(JSONSerializer serializer, Object fieldName, Type fieldType, int features) throws IOException {
            JSONObject object = new JSONObject();
            object.put("code", code);
            object.put("des", des);
            serializer.write(object);
        }
    }

    public static class SexDeserializer implements ObjectDeserializer {
        public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
            String code;
            Object object = parser.parse();
            if (object instanceof JSONObject) {
                JSONObject jsonObject = ((JSONObject) (object));
                code = jsonObject.getString("code");
            } else {
                code = ((String) (object));
            }
            if ("0".equals(code)) {
                return ((T) (Issue1449.Sex.NONE));
            } else
                if ("1".equals(code)) {
                    return ((T) (Issue1449.Sex.MAN));
                } else
                    if ("2".equals(code)) {
                        return ((T) (Issue1449.Sex.WOMAN));
                    }


            return ((T) (Issue1449.Sex.NONE));
        }

        public int getFastMatchToken() {
            return 0;
        }
    }

    public static class Student implements Serializable {
        public Long id;

        public String name;

        public Issue1449.Sex sex;
    }
}

