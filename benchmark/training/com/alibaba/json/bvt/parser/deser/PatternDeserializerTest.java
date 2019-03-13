package com.alibaba.json.bvt.parser.deser;


import JSONToken.LITERAL_STRING;
import MiscCodec.instance;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.ParserConfig;
import java.util.regex.Pattern;
import junit.framework.TestCase;
import org.junit.Assert;


public class PatternDeserializerTest extends TestCase {
    public void test_pattern() throws Exception {
        Assert.assertEquals(Pattern.compile("abc").pattern(), JSON.parseObject("'abc'", Pattern.class).pattern());
        Assert.assertEquals(null, JSON.parseObject("null", Pattern.class));
        DefaultJSONParser parser = new DefaultJSONParser("null", ParserConfig.getGlobalInstance(), JSON.DEFAULT_PARSER_FEATURE);
        Assert.assertEquals(null, instance.deserialze(parser, null, null));
        Assert.assertEquals(LITERAL_STRING, instance.getFastMatchToken());
    }
}

