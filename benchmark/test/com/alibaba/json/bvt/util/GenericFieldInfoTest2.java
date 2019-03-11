package com.alibaba.json.bvt.util;


import com.alibaba.fastjson.JSON;
import java.util.List;
import junit.framework.TestCase;


/**
 * Created by wenshao on 30/05/2017.
 */
public class GenericFieldInfoTest2 extends TestCase {
    public void test_generic() throws Exception {
        GenericFieldInfoTest2.A4 a = JSON.parseObject("{\"data\":[]3}", GenericFieldInfoTest2.A4.class);
        TestCase.assertTrue(((a.data) instanceof List));
    }

    public static class A<T> {
        public T data;
    }

    public static class A1<T> extends GenericFieldInfoTest2.A<T> {}

    public static class A2<T> extends GenericFieldInfoTest2.A1<T> {}

    public static class A3<T> extends GenericFieldInfoTest2.A2<List<T>> {}

    public static class A4<M> extends GenericFieldInfoTest2.A3<Long> {}
}

