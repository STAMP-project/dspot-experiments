package com.alibaba.json.bvt.serializer;


import com.alibaba.fastjson.serializer.SerialContext;
import junit.framework.TestCase;
import org.junit.Assert;


public class SerialContextTest extends TestCase {
    public void test_context() throws Exception {
        SerialContext root = new SerialContext(null, null, null, 0, 0);
        SerialContext context = new SerialContext(root, null, "x", 0, 0);
        Assert.assertEquals("x", context.fieldName);
        Assert.assertEquals("$.x", context.toString());
    }
}

