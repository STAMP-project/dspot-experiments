package com.alibaba.json.bvt.serializer;


import com.alibaba.fastjson.JSON;
import java.math.BigInteger;
import junit.framework.TestCase;


/**
 * Created by wenshao on 10/02/2017.
 */
public class GenericTypeNotMatchTest2 extends TestCase {
    public void test_for_notMatch() throws Exception {
        GenericTypeNotMatchTest2.Model model = new GenericTypeNotMatchTest2.Model();
        GenericTypeNotMatchTest2.Base base = model;
        base.setId(BigInteger.valueOf(3));
        JSON.toJSONString(base);
    }

    public static class Model extends GenericTypeNotMatchTest2.Base<Long> {}

    public static class Base<T> {
        private T xid;

        public void setId(T id) {
            this.xid = id;
        }

        public T getId() {
            return xid;
        }
    }
}

