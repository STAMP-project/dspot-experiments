package com.alibaba.json.bvt.parser.deser;


import JSONToken.LITERAL_STRING;
import MiscCodec.instance;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.ParserConfig;
import java.net.URL;
import junit.framework.TestCase;
import org.junit.Assert;


public class URLDeserializerTest extends TestCase {
    public void test_url() throws Exception {
        Assert.assertEquals(new URL("http://www.alibaba.com"), JSON.parseObject("'http://www.alibaba.com'", URL.class));
        Assert.assertEquals(null, JSON.parseObject("null", URL.class));
        DefaultJSONParser parser = new DefaultJSONParser("null", ParserConfig.getGlobalInstance(), JSON.DEFAULT_PARSER_FEATURE);
        Assert.assertEquals(null, instance.deserialze(parser, null, null));
        Assert.assertEquals(LITERAL_STRING, instance.getFastMatchToken());
    }

    public void test_url_error() throws Exception {
        JSONException ex = null;
        try {
            JSON.parseObject("'123'", URL.class);
        } catch (JSONException e) {
            ex = e;
        }
        Assert.assertNotNull(ex);
    }
}

