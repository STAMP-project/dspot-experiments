package com.alibaba.json.bvt.parser;


import Feature.AllowArbitraryCommas;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import junit.framework.TestCase;
import org.junit.Assert;


public class DefaultExtJSONParserTest_5 extends TestCase {
    public void test_0() throws Exception {
        DefaultJSONParser parser = new DefaultJSONParser("{,,,,\"value\":3,\"id\":1}");
        parser.config(AllowArbitraryCommas, true);
        DefaultExtJSONParserTest_5.Entity entity = new DefaultExtJSONParserTest_5.Entity();
        parser.parseObject(entity);
        Assert.assertEquals(3, entity.getValue());
    }

    public void test_1() throws Exception {
        DefaultJSONParser parser = new DefaultJSONParser("{\"value\":3,\"id\":1}");
        parser.config(AllowArbitraryCommas, false);
        DefaultExtJSONParserTest_5.Entity entity = new DefaultExtJSONParserTest_5.Entity();
        parser.parseObject(entity);
        Assert.assertEquals(3, entity.getValue());
    }

    public static class Entity {
        private int value;

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }
}

