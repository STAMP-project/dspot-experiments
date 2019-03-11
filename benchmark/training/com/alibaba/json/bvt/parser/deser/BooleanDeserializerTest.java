package com.alibaba.json.bvt.parser.deser;


import BooleanCodec.instance;
import JSONToken.TRUE;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.ParserConfig;
import junit.framework.TestCase;
import org.junit.Assert;


public class BooleanDeserializerTest extends TestCase {
    public void test_boolean() throws Exception {
        Assert.assertEquals(Boolean.TRUE, JSON.parseObject("true", Boolean.class));
        Assert.assertEquals(Boolean.FALSE, JSON.parseObject("false", Boolean.class));
        Assert.assertEquals(Boolean.TRUE, JSON.parseObject("'true'", Boolean.class));
        Assert.assertEquals(Boolean.FALSE, JSON.parseObject("'false'", Boolean.class));
        Assert.assertEquals(Boolean.TRUE, JSON.parseObject("1", Boolean.class));
        Assert.assertEquals(Boolean.FALSE, JSON.parseObject("0", Boolean.class));
        Assert.assertEquals(null, JSON.parseObject("null", Boolean.class));
        {
            DefaultJSONParser parser = new DefaultJSONParser("null", ParserConfig.getGlobalInstance(), JSON.DEFAULT_PARSER_FEATURE);
            Assert.assertEquals(null, instance.deserialze(parser, null, null));
            parser.close();
        }
        Assert.assertEquals(TRUE, instance.getFastMatchToken());
    }
}

