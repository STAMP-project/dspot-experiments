package com.alibaba.json.bvt.typeRef;


import java.lang.reflect.Type;
import junit.framework.TestCase;


/**
 * Created by wenshao on 2016/10/11.
 */
public class TypeReferenceTest10 extends TestCase {
    public void test_same() throws Exception {
        Type type1 = getType();
        Type type2 = getType();
        TestCase.assertEquals(type1, type2);
        TestCase.assertSame(type1, type2);
    }

    public static class Model<T> {
        public T value;
    }
}

