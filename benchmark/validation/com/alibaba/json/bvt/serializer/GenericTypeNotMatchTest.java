package com.alibaba.json.bvt.serializer;


import com.alibaba.fastjson.JSON;
import java.math.BigInteger;
import junit.framework.TestCase;


/**
 * Created by wenshao on 10/02/2017.
 */
public class GenericTypeNotMatchTest extends TestCase {
    public void test_for_notMatch() throws Exception {
        GenericTypeNotMatchTest.Model model = new GenericTypeNotMatchTest.Model();
        GenericTypeNotMatchTest.Base base = model;
        base.id = BigInteger.valueOf(3);
        JSON.toJSONString(base);
    }

    public static class Model extends GenericTypeNotMatchTest.Base<Long> {}

    public static class Base<T> {
        public T id;
    }
}

