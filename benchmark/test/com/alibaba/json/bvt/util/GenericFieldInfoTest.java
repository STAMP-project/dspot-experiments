package com.alibaba.json.bvt.util;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;


/**
 * Created by wenshao on 30/05/2017.
 */
public class GenericFieldInfoTest extends TestCase {
    public void test_generic() throws Exception {
        GenericFieldInfoTest.A a = JSON.parseObject("{\"data\":3}", GenericFieldInfoTest.A4.class);
        TestCase.assertTrue(((a.data) instanceof Long));
    }

    public void test_generic_1() throws Exception {
        GenericFieldInfoTest.A a = JSON.parseObject("{\"data\":3}", new com.alibaba.fastjson.TypeReference<GenericFieldInfoTest.A3<Long>>() {});
        TestCase.assertEquals(a.data.getClass(), Long.class);
    }

    public static class A<T> {
        public T data;
    }

    public static class A1<T> extends GenericFieldInfoTest.A<T> {}

    public static class A2<T> extends GenericFieldInfoTest.A1<T> {}

    public static class A3<T> extends GenericFieldInfoTest.A2<T> {}

    public static class A4<T> extends GenericFieldInfoTest.A3<Long> {}
}

