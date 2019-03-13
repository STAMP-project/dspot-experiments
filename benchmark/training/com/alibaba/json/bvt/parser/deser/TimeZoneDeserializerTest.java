package com.alibaba.json.bvt.parser.deser;


import JSONToken.LITERAL_STRING;
import MiscCodec.instance;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.ParserConfig;
import junit.framework.TestCase;
import org.junit.Assert;


public class TimeZoneDeserializerTest extends TestCase {
    public void test_timezone() throws Exception {
        DefaultJSONParser parser = new DefaultJSONParser("null", ParserConfig.getGlobalInstance(), JSON.DEFAULT_PARSER_FEATURE);
        Assert.assertEquals(null, instance.deserialze(parser, null, null));
        Assert.assertEquals(LITERAL_STRING, instance.getFastMatchToken());
    }
}

