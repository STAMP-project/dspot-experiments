package com.alibaba.json.bvt.parser;


import com.alibaba.fastjson.util.TypeUtils;
import junit.framework.TestCase;
import org.junit.Assert;


public class TypeUtilsTest_loadClass extends TestCase {
    public void test_loadClass() throws Exception {
        Assert.assertSame(TypeUtilsTest_loadClass.Entity.class, TypeUtils.loadClass("com.alibaba.json.bvt.parser.TypeUtilsTest_loadClass$Entity", TypeUtilsTest_loadClass.Entity.class.getClassLoader()));
        Assert.assertSame(TypeUtilsTest_loadClass.Entity.class, TypeUtils.loadClass("com.alibaba.json.bvt.parser.TypeUtilsTest_loadClass$Entity", null));
    }

    public void test_error() throws Exception {
        Assert.assertNull(TypeUtils.loadClass("com.alibaba.json.bvt.parser.TypeUtilsTest_loadClass.Entity", TypeUtilsTest_loadClass.Entity.class.getClassLoader()));
    }

    public static class Entity {}
}

