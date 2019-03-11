package com.alibaba.json.bvt.issue_2000;


import SerializerFeature.WriteClassName;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import junit.framework.TestCase;


public class Issue2012 extends TestCase {
    public void test_for_issue() throws Exception {
        Issue2012.Model foo = new Issue2012.Model();
        foo.bytes = new byte[0];
        String str = JSON.toJSONString(foo, WriteClassName);
        TestCase.assertEquals("{\"@type\":\"com.alibaba.json.bvt.issue_2000.Issue2012$Model\",\"bytes\":x\'\'}", str);
        ParserConfig config = new ParserConfig();
        config.setAutoTypeSupport(true);
        foo = JSON.parseObject(str, Object.class, config);
        TestCase.assertEquals(0, foo.bytes.length);
    }

    public static class Model {
        public byte[] bytes;
    }
}

