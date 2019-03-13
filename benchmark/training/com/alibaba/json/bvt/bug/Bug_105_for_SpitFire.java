package com.alibaba.json.bvt.bug;


import SerializerFeature.WriteMapNullValue;
import com.alibaba.fastjson.JSON;
import java.util.List;
import junit.framework.TestCase;


public class Bug_105_for_SpitFire extends TestCase {
    private static class Foo {
        private List<String> names;

        private List<String> codes;

        public List<String> getNames() {
            return names;
        }

        public void setNames(List<String> names) {
            this.names = names;
        }

        public List<String> getCodes() {
            return codes;
        }

        public void setCodes(List<String> codes) {
            this.codes = codes;
        }
    }

    public void test_listErrorTest() {
        Bug_105_for_SpitFire.Foo foo = new Bug_105_for_SpitFire.Foo();
        String json = JSON.toJSONString(foo, WriteMapNullValue);
        System.out.println(json);
        Bug_105_for_SpitFire.Foo f = JSON.parseObject(json, Bug_105_for_SpitFire.Foo.class);
        System.out.println(f);
    }
}

