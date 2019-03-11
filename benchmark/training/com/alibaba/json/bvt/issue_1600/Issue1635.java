package com.alibaba.json.bvt.issue_1600;


import SerializerFeature.BeanToArray;
import SerializerFeature.WriteNullBooleanAsFalse;
import SerializerFeature.WriteNullListAsEmpty;
import SerializerFeature.WriteNullNumberAsZero;
import SerializerFeature.WriteNullStringAsEmpty;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.PascalNameFilter;
import com.alibaba.fastjson.serializer.SerializeConfig;
import java.util.List;
import junit.framework.TestCase;


public class Issue1635 extends TestCase {
    public static class Foo {
        public String name;

        public Integer BarCount;

        public Boolean flag;

        public List list;

        public Foo(String name, Integer barCount) {
            this.name = name;
            BarCount = barCount;
        }
    }

    public void test_issue() throws Exception {
        SerializeConfig config = new SerializeConfig();
        config.setAsmEnable(false);
        Issue1635.Foo foo = new Issue1635.Foo(null, null);
        String json = JSON.toJSONString(foo, config, new PascalNameFilter(), WriteNullBooleanAsFalse, WriteNullNumberAsZero, WriteNullStringAsEmpty, WriteNullListAsEmpty);
        TestCase.assertEquals("{\"BarCount\":0,\"Flag\":false,\"List\":[],\"Name\":\"\"}", json);
    }

    public void test_issue_1() throws Exception {
        SerializeConfig config = new SerializeConfig();
        config.setAsmEnable(false);
        Issue1635.Foo foo = new Issue1635.Foo(null, null);
        String json = JSON.toJSONString(foo, config, new PascalNameFilter(), WriteNullBooleanAsFalse, WriteNullNumberAsZero, WriteNullStringAsEmpty, WriteNullListAsEmpty, BeanToArray);
        TestCase.assertEquals("[0,false,[],\"\"]", json);
    }
}

