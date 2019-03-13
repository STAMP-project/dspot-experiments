package com.alibaba.json.bvt.annotation;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;


/**
 * Created by wenshao on 16/9/25.
 */
public class DeserializeUsingTest extends TestCase {
    public void test_deserializeUsing() throws Exception {
        String jsonStr = "{'subjectList':['CHINESE','MATH']}";
        DeserializeUsingTest.Teacher teacher = JSON.parseObject(jsonStr, DeserializeUsingTest.Teacher.class);
        TestCase.assertEquals(DeserializeUsingTest.SubjectEnum.CHINESE.ordinal(), teacher.getSubjectList().get(0).intValue());
        TestCase.assertEquals(DeserializeUsingTest.SubjectEnum.MATH.ordinal(), teacher.getSubjectList().get(1).intValue());
    }

    public static class Teacher {
        @JSONField(deserializeUsing = DeserializeUsingTest.SubjectListDeserializer.class)
        private List<Integer> subjectList;

        public List<Integer> getSubjectList() {
            return subjectList;
        }

        public void setSubjectList(List<Integer> subjectList) {
            this.subjectList = subjectList;
        }
    }

    public static class SubjectListDeserializer implements ObjectDeserializer {
        @SuppressWarnings("unchecked")
        public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
            List<String> parseObjectList = parser.parseArray(String.class);
            if (parseObjectList != null) {
                List<Integer> resultVoList = new ArrayList<Integer>();
                for (String parseObject : parseObjectList) {
                    resultVoList.add(DeserializeUsingTest.SubjectEnum.valueOf(parseObject).ordinal());
                }
                return ((T) (resultVoList));
            }
            throw new IllegalStateException();
        }

        public int getFastMatchToken() {
            return 0;
        }
    }

    public static enum SubjectEnum {

        CHINESE,
        MATH;}
}

