package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


/**
 * Created by wuwen on 2017/2/24.
 */
public class Issue1036 extends TestCase {
    /**
     *
     *
     * @see BeanToArrayTest3_private#test_array()
     * @see com.alibaba.fastjson.parser.deserializer.DefaultFieldDeserializer#parseField
     */
    public void test_for_issue() throws Exception {
        NullPointerException exception = new NullPointerException("test");
        Issue1036.Result<String> result = new Issue1036.Result<String>();
        result.setException(exception);
        String json = JSON.toJSONString(result);
        Issue1036.Result<String> a = JSON.parseObject(json, new com.alibaba.fastjson.TypeReference<Issue1036.Result<String>>() {});
        Assert.assertEquals("test", a.getException().getMessage());
    }

    public static class Result<T> {
        private T data;

        private Throwable exception;

        public Result() {
        }

        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }

        public Throwable getException() {
            return exception;
        }

        public void setException(Throwable exception) {
            this.exception = exception;
        }

        @Override
        public String toString() {
            return ((((("Result{" + "data='") + (data)) + '\'') + ", exception=") + (exception)) + '}';
        }
    }
}

