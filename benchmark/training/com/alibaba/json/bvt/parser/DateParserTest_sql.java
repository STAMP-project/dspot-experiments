package com.alibaba.json.bvt.parser;


import Feature.AllowISO8601DateFormat;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.parser.ParserConfig;
import java.sql.Date;
import junit.framework.TestCase;
import org.junit.Assert;


@SuppressWarnings("deprecation")
public class DateParserTest_sql extends TestCase {
    public void test_date_1() throws Exception {
        int features = JSON.DEFAULT_PARSER_FEATURE;
        features = Feature.config(features, AllowISO8601DateFormat, true);
        DefaultJSONParser parser = new DefaultJSONParser("\"2011-01-09T13:49:53.254\"", ParserConfig.getGlobalInstance(), features);
        Date date = parser.parseObject(Date.class);
        Assert.assertEquals(new Date(1294552193254L), date);
        parser.close();
    }

    public void test_date_2() throws Exception {
        int features = JSON.DEFAULT_PARSER_FEATURE;
        DefaultJSONParser parser = new DefaultJSONParser("new Date(1294552193254)", ParserConfig.getGlobalInstance(), features);
        Date date = parser.parseObject(Date.class);
        Assert.assertEquals(new Date(1294552193254L), date);
        parser.close();
    }

    public void test_date_3() throws Exception {
        int features = JSON.DEFAULT_PARSER_FEATURE;
        features = Feature.config(features, AllowISO8601DateFormat, true);
        DefaultJSONParser parser = new DefaultJSONParser("\"2011-01-09T13:49:53\"", ParserConfig.getGlobalInstance(), features);
        Date date = parser.parseObject(Date.class);
        Assert.assertEquals(new Date(1294552193000L), date);
        parser.close();
    }

    public void test_date_4() throws Exception {
        int features = JSON.DEFAULT_PARSER_FEATURE;
        features = Feature.config(features, AllowISO8601DateFormat, true);
        DefaultJSONParser parser = new DefaultJSONParser("\"2011-01-09\"", ParserConfig.getGlobalInstance(), features);
        Date date = parser.parseObject(Date.class);
        Assert.assertEquals(new Date(1294502400000L), date);
        parser.close();
    }
}

