package com.alibaba.json.bvt.serializer;


import com.alibaba.fastjson.JSON;
import java.io.Serializable;
import junit.framework.TestCase;


public class GenericTypeTest extends TestCase {
    public void test_gerneric() throws Exception {
        GenericTypeTest.MyResultResult result = new GenericTypeTest.MyResultResult();
        JSON.toJSONString(result);
    }

    public static class MyResultResult extends GenericTypeTest.BaseResult<String> {}

    public static class BaseResult<T> implements Serializable {
        public T data;
    }
}

