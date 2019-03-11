package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.JSONToken;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import java.lang.reflect.Type;
import junit.framework.TestCase;
import org.junit.Assert;


public class Bug_for_Issue_603 extends TestCase {
    public void test_for_issue() throws Exception {
        ParserConfig.getGlobalInstance().putDeserializer(Bug_for_Issue_603.OrderActionEnum.class, new Bug_for_Issue_603.OrderActionEnumDeser());
        {
            Bug_for_Issue_603.Msg msg = JSON.parseObject("{\"actionEnum\":1,\"body\":\"A\"}", Bug_for_Issue_603.Msg.class);
            Assert.assertEquals(msg.body, "A");
            Assert.assertEquals(msg.actionEnum, Bug_for_Issue_603.OrderActionEnum.FAIL);
        }
        {
            Bug_for_Issue_603.Msg msg = JSON.parseObject("{\"actionEnum\":0,\"body\":\"B\"}", Bug_for_Issue_603.Msg.class);
            Assert.assertEquals(msg.body, "B");
            Assert.assertEquals(msg.actionEnum, Bug_for_Issue_603.OrderActionEnum.SUCC);
        }
    }

    public static class OrderActionEnumDeser implements ObjectDeserializer {
        @SuppressWarnings("unchecked")
        @Override
        public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
            Integer intValue = parser.parseObject(int.class);
            if (intValue == 1) {
                return ((T) (Bug_for_Issue_603.OrderActionEnum.FAIL));
            } else
                if (intValue == 0) {
                    return ((T) (Bug_for_Issue_603.OrderActionEnum.SUCC));
                }

            throw new IllegalStateException();
        }

        @Override
        public int getFastMatchToken() {
            return JSONToken.LITERAL_INT;
        }
    }

    public static enum OrderActionEnum {

        FAIL(1),
        SUCC(0);
        private int code;

        OrderActionEnum(int code) {
            this.code = code;
        }
    }

    public static class Msg {
        public Bug_for_Issue_603.OrderActionEnum actionEnum;

        public String body;
    }
}

