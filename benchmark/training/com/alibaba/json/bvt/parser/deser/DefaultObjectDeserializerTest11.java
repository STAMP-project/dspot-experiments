package com.alibaba.json.bvt.parser.deser;


import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.ParserConfig;
import junit.framework.TestCase;


public class DefaultObjectDeserializerTest11 extends TestCase {
    public void test_0() throws Exception {
        DefaultObjectDeserializerTest11.A a = new DefaultObjectDeserializerTest11.A();
        DefaultJSONParser parser = new DefaultJSONParser("{\"id\":123}", ParserConfig.getGlobalInstance());
        parser.parseObject(a);
    }

    public static class A {
        private long id;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }
    }
}

