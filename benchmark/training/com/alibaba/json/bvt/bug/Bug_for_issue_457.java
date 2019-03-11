package com.alibaba.json.bvt.bug;


import ParserConfig.global;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.JSONToken;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import java.lang.reflect.Type;
import junit.framework.TestCase;
import org.junit.Assert;


public class Bug_for_issue_457 extends TestCase {
    public void test_for_issue() throws Exception {
        global.putDeserializer(Bug_for_issue_457.MyEnum.class, new Bug_for_issue_457.MyEnumDeser());
        Bug_for_issue_457.VO entity = JSON.parseObject("{\"myEnum\":\"AA\"}", Bug_for_issue_457.VO.class);
        Assert.assertEquals(Bug_for_issue_457.MyEnum.A, entity.myEnum);
    }

    public static class MyEnumDeser implements ObjectDeserializer {
        @Override
        public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
            String text = ((String) (parser.parse()));
            if (text.equals("AA")) {
                return ((T) (Bug_for_issue_457.MyEnum.A));
            }
            if (text.equals("BB")) {
                return ((T) (Bug_for_issue_457.MyEnum.B));
            }
            return null;
        }

        @Override
        public int getFastMatchToken() {
            return JSONToken.LITERAL_STRING;
        }
    }

    public static class VO {
        private Bug_for_issue_457.MyEnum myEnum;

        public void setMyEnum(Bug_for_issue_457.MyEnum myEnum) {
            this.myEnum = myEnum;
        }

        public Bug_for_issue_457.MyEnum getMyEnum() {
            return myEnum;
        }
    }

    public static enum MyEnum {

        A,
        B;}
}

