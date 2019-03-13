package com.alibaba.json.bvt.util;


import SerializerFeature.WriteMapNullValue.mask;
import com.alibaba.fastjson.annotation.JSONType;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.util.TypeUtils;
import com.alibaba.json.bvt.parser.deser.generic.GenericArrayTest;
import com.jsoniter.spi.GenericArrayTypeImpl;
import java.lang.reflect.Constructor;
import java.lang.reflect.GenericArrayType;
import junit.framework.TestCase;


public class TypeUtilsTest extends TestCase {
    public void test_0() throws Exception {
        TestCase.assertEquals(0, TypeUtils.getSerializeFeatures(Object.class));
        TestCase.assertEquals(mask, TypeUtils.getSerializeFeatures(TypeUtilsTest.Model.class));
    }

    public void test_1() throws Exception {
        TypeUtils.checkPrimitiveArray(((GenericArrayType) (TypeUtilsTest.A.class.getField("values").getGenericType())));
    }

    public void test_3() throws Exception {
        TestCase.assertTrue(TypeUtils.isHibernateInitialized(new Object()));
    }

    public void test_2() throws Exception {
        Constructor<?> constructor = GenericArrayTypeImpl.class.getDeclaredConstructors()[0];
        constructor.setAccessible(true);
        Class[] classes = new Class[]{ boolean[].class, byte[].class, short[].class, int[].class, long[].class, float[].class, double[].class, char[].class };
        for (Class clazz : classes) {
            GenericArrayType type = ((GenericArrayType) (constructor.newInstance(clazz.getComponentType())));
            TestCase.assertEquals(clazz, TypeUtils.checkPrimitiveArray(type));
        }
    }

    @JSONType(serialzeFeatures = SerializerFeature.WriteMapNullValue)
    public static class Model {}

    public static class A<T extends Number> {
        public T[] values;
    }

    public static class VO extends GenericArrayTest.A {}
}

