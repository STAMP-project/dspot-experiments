package com.alibaba.json.bvt.parser.deser;


import JSONToken.LITERAL_STRING;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.MiscCodec;
import junit.framework.TestCase;
import org.junit.Assert;


public class URIDeserializerTest extends TestCase {
    public void test_null() throws Exception {
        String input = "null";
        DefaultJSONParser parser = new DefaultJSONParser(input, ParserConfig.getGlobalInstance(), JSON.DEFAULT_PARSER_FEATURE);
        MiscCodec deser = new MiscCodec();
        Assert.assertEquals(LITERAL_STRING, deser.getFastMatchToken());
        Assert.assertNull(deser.deserialze(parser, null, null));
    }
}

