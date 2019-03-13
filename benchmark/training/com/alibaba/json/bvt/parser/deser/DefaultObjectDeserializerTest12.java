package com.alibaba.json.bvt.parser.deser;


import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.ParserConfig;
import java.util.List;
import junit.framework.TestCase;


public class DefaultObjectDeserializerTest12 extends TestCase {
    public void test_list() throws Exception {
        DefaultObjectDeserializerTest12.A a = new DefaultObjectDeserializerTest12.A();
        DefaultJSONParser parser = new DefaultJSONParser("{\"values\":[]}", ParserConfig.getGlobalInstance());
        parser.parseObject(a);
        parser.close();
    }

    public static class A {
        private List values;

        public List getValues() {
            return values;
        }

        public void setValues(List values) {
            this.values = values;
        }
    }
}

