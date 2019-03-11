package com.alibaba.json.bvt.bug;


import SerializerFeature.WriteClassName;
import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;


public class Bug_for_SpitFire_2 extends TestCase {
    public void test_for_SpringFire() {
        Bug_for_SpitFire_2.Generic<String> q = new Bug_for_SpitFire_2.Generic<String>();
        String text = JSON.toJSONString(q, WriteClassName);
        System.out.println(text);
        JSON.parseObject(text, Bug_for_SpitFire_2.Generic.class);
    }

    public static class Generic<T> {
        String header;

        T payload;

        public String getHeader() {
            return header;
        }

        public void setHeader(String header) {
            this.header = header;
        }

        public T getPayload() {
            return payload;
        }

        public void setPayload(T payload) {
            this.payload = payload;
        }
    }
}

