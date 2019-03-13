package com.alibaba.json.bvt.bug;


import SerializerFeature.WriteClassName;
import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;


public class Bug_for_SpitFire_3 extends TestCase {
    public void test_for_SpitFire() {
        Bug_for_SpitFire_3.Generic<Bug_for_SpitFire_3.Payload> q = new Bug_for_SpitFire_3.Generic<Bug_for_SpitFire_3.Payload>();
        q.setHeader("Sdfdf");
        q.setPayload(new Bug_for_SpitFire_3.Payload());
        String text = JSON.toJSONString(q, WriteClassName);
        System.out.println(text);
        JSON.parseObject(text, Bug_for_SpitFire_3.Generic.class);
    }

    public abstract static class AbstractDTO {
        private String test;

        public String getTest() {
            return test;
        }

        public void setTest(String test) {
            this.test = test;
        }
    }

    public static class Payload extends Bug_for_SpitFire_3.AbstractDTO {}

    public static class Generic<T extends Bug_for_SpitFire_3.AbstractDTO> extends Bug_for_SpitFire_3.AbstractDTO {
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

