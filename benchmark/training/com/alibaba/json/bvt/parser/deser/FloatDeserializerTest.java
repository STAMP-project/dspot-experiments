package com.alibaba.json.bvt.parser.deser;


import FloatCodec.instance;
import JSONToken.LITERAL_INT;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.ParserConfig;
import junit.framework.TestCase;
import org.junit.Assert;


public class FloatDeserializerTest extends TestCase {
    public void test_bigdecimal() throws Exception {
        Assert.assertEquals(0, JSON.parseObject("0", Float.class).intValue());
        Assert.assertEquals(0, JSON.parseObject("0.0", Float.class).intValue());
        Assert.assertEquals(0, JSON.parseObject("'0'", Float.class).intValue());
        Assert.assertEquals(null, JSON.parseObject("null", Float.class));
        DefaultJSONParser parser = new DefaultJSONParser("null", ParserConfig.getGlobalInstance(), JSON.DEFAULT_PARSER_FEATURE);
        Assert.assertEquals(null, instance.deserialze(parser, null, null));
        Assert.assertEquals(LITERAL_INT, instance.getFastMatchToken());
    }
}

