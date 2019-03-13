package com.alibaba.json.bvt.parser;


import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.json.test.entity.TestEntity;
import junit.framework.TestCase;
import org.junit.Assert;


public class DefaultExtJSONParserTest_1 extends TestCase {
    public void test_0() throws Exception {
        DefaultJSONParser parser = new DefaultJSONParser("{\"f1\":true}");
        TestEntity entity = parser.parseObject(TestEntity.class);
        Assert.assertEquals(true, entity.isF1());
    }

    public void test_1() throws Exception {
        DefaultJSONParser parser = new DefaultJSONParser("{\"f2\":true}");
        TestEntity entity = parser.parseObject(TestEntity.class);
        Assert.assertEquals(Boolean.TRUE, entity.getF2());
    }
}

