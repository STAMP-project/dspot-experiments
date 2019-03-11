package com.alibaba.json.bvt.serializer;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;


public class GenericTypeTest2 extends TestCase {
    public void test_gerneric() throws Exception {
        GenericTypeTest2.MyResultResult result = new GenericTypeTest2.MyResultResult();
        JSON.toJSONString(result);
    }

    public static class MyResultResult extends GenericTypeTest2.BaseResult<String> {}

    public static class BaseResult<T> {
        private T data;

        public T getData() {
            return data;
        }
    }
}

