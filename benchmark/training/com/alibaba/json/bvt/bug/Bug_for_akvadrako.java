package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.parser.DefaultJSONParser;
import junit.framework.TestCase;


public class Bug_for_akvadrako extends TestCase {
    public void testNakedFields() throws Exception {
        Bug_for_akvadrako.Naked naked = new Bug_for_akvadrako.Naked();
        DefaultJSONParser parser = new DefaultJSONParser("{ \"field\": 3 }");
        parser.parseObject(naked);
    }

    public static class Naked {
        public int field;
    }
}

