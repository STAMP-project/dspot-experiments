package com.alibaba.json.bvt.parser;


import com.alibaba.fastjson.parser.ParseContext;
import junit.framework.TestCase;
import org.junit.Assert;


public class ParseContextTest extends TestCase {
    public void test_toString() throws Exception {
        Assert.assertEquals("$", new ParseContext(null, new Object(), "id").toString());
    }
}

