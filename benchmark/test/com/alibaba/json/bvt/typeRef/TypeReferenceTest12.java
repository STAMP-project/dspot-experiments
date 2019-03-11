package com.alibaba.json.bvt.typeRef;


import java.lang.reflect.ParameterizedType;
import junit.framework.TestCase;


/**
 * Created by wuwen on 2016/12/7.
 */
public class TypeReferenceTest12 extends TestCase {
    public void test_same() throws Exception {
        ParameterizedType type1 = getType(Integer.class);
        ParameterizedType type2 = getType();
        TestCase.assertEquals(type1.getRawType(), type2.getRawType());
        TestCase.assertSame(type1.getRawType(), type2.getRawType());
    }

    public static class Model<T> {
        public T value;
    }
}

