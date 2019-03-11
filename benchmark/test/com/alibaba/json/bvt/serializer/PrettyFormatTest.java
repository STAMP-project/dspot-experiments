package com.alibaba.json.bvt.serializer;


import SerializerFeature.PrettyFormat;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.JSONSerializer;
import junit.framework.TestCase;
import org.junit.Assert;


public class PrettyFormatTest extends TestCase {
    public void test_0() throws Exception {
        Assert.assertEquals(0, new JSONSerializer().getIndentCount());
        Assert.assertEquals("[\n\t{},\n\t{}\n]", JSON.toJSONString(new Object[]{ new Object(), new Object() }, PrettyFormat));
    }
}

