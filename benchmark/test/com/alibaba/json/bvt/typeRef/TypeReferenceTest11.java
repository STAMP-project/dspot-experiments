package com.alibaba.json.bvt.typeRef;


import java.lang.reflect.Type;
import junit.framework.TestCase;


/**
 * Created by wenshao on 2016/10/11.
 */
public class TypeReferenceTest11 extends TestCase {
    public void test_same() throws Exception {
        Type type1 = getType(Integer.class);
        Type type2 = getType(Integer.class);
        TestCase.assertEquals(type1, type2);
        TestCase.assertSame(type1, type2);
    }

    public static class Model<T> {
        public T value;
    }

    public static class Response<T> {
        public T data;
    }
}

