package com.alibaba.json.bvt.parser;


import Feature.AllowArbitraryCommas;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import junit.framework.TestCase;
import org.junit.Assert;


public class DefaultExtJSONParserTest_6 extends TestCase {
    public void test_0() throws Exception {
        DefaultJSONParser parser = new DefaultJSONParser("{value:{,,,,\"value\":3,\"id\":1}}");
        parser.config(AllowArbitraryCommas, true);
        DefaultExtJSONParserTest_6.Entity entity = new DefaultExtJSONParserTest_6.Entity();
        parser.parseObject(entity);
        Assert.assertEquals(3, entity.getValue().getValue());
    }

    public void test_1() throws Exception {
        DefaultJSONParser parser = new DefaultJSONParser("{\'value\':{\"value\":3,\"id\":1}}");
        parser.config(AllowArbitraryCommas, false);
        DefaultExtJSONParserTest_6.Entity entity = new DefaultExtJSONParserTest_6.Entity();
        parser.parseObject(entity);
        Assert.assertEquals(3, entity.getValue().getValue());
    }

    public static class Entity {
        private DefaultExtJSONParserTest_6.V1 value;

        public DefaultExtJSONParserTest_6.V1 getValue() {
            return value;
        }

        public void setValue(DefaultExtJSONParserTest_6.V1 value) {
            this.value = value;
        }
    }

    public static class V1 {
        private int value;

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }
}

