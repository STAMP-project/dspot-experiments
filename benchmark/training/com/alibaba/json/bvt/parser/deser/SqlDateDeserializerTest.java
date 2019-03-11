package com.alibaba.json.bvt.parser.deser;


import JSONToken.LITERAL_INT;
import SqlDateDeserializer.instance;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.ParserConfig;
import java.sql.Date;
import junit.framework.TestCase;
import org.junit.Assert;


public class SqlDateDeserializerTest extends TestCase {
    public void test_bigdecimal() throws Exception {
        Assert.assertEquals(1309861159710L, JSON.parseObject("1309861159710", Date.class).getTime());
        Assert.assertEquals(1309861159710L, JSON.parseObject("1309861159710.0", Date.class).getTime());
        Assert.assertEquals(1309861159710L, JSON.parseObject("'1309861159710'", Date.class).getTime());
        Assert.assertEquals(null, JSON.parseObject("null", Integer.class));
        DefaultJSONParser parser = new DefaultJSONParser("null", ParserConfig.getGlobalInstance(), JSON.DEFAULT_PARSER_FEATURE);
        Assert.assertEquals(null, instance.deserialze(parser, null, null));
        Assert.assertEquals(LITERAL_INT, instance.getFastMatchToken());
    }
}

