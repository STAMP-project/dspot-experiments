package com.alibaba.json.bvt.serializer;


import ReferenceCodec.instance;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import java.lang.ref.WeakReference;
import junit.framework.TestCase;
import org.junit.Assert;


public class ReferenceDeserializerTest extends TestCase {
    public void test_0() throws Exception {
        ParserConfig config = new ParserConfig();
        config.putDeserializer(ReferenceDeserializerTest.MyRef.class, instance);
        Exception error = null;
        try {
            JSON.parseObject("{\"ref\":{}}", ReferenceDeserializerTest.VO.class, config, 0);
        } catch (Exception ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public static class VO {
        private ReferenceDeserializerTest.MyRef<Object> ref;

        public ReferenceDeserializerTest.MyRef<Object> getRef() {
            return ref;
        }

        public void setRef(ReferenceDeserializerTest.MyRef<Object> ref) {
            this.ref = ref;
        }
    }

    public static class MyRef<T> extends WeakReference<T> {
        MyRef(T referent) {
            super(referent);
        }
    }
}

