package com.alibaba.json.bvt.bug;


import SerializerFeature.WriteClassName;
import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class Bug_for_SpitFire_4 extends TestCase {
    public void test_for_SpitFire() {
        Bug_for_SpitFire_4.Generic<Bug_for_SpitFire_4.Payload> q = new Bug_for_SpitFire_4.Generic<Bug_for_SpitFire_4.Payload>();
        q.setHeader(new Bug_for_SpitFire_4.Header());
        q.setPayload(new Bug_for_SpitFire_4.Payload());
        String text = JSON.toJSONString(q, WriteClassName);
        System.out.println(text);
        Bug_for_SpitFire_4.Generic<Bug_for_SpitFire_4.Payload> o = ((Bug_for_SpitFire_4.Generic<Bug_for_SpitFire_4.Payload>) (JSON.parseObject(text, q.getClass())));
        Assert.assertNotNull(o.getPayload());
    }

    public abstract static class AbstractDTO {}

    public static class Header {}

    public static class Payload extends Bug_for_SpitFire_4.AbstractDTO {}

    public static class Generic<T extends Bug_for_SpitFire_4.AbstractDTO> extends Bug_for_SpitFire_4.AbstractDTO {
        Bug_for_SpitFire_4.Header header;

        T payload;

        public Bug_for_SpitFire_4.Header getHeader() {
            return header;
        }

        public void setHeader(Bug_for_SpitFire_4.Header header) {
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

