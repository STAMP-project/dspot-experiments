package com.alibaba.json.bvt.issue_2200;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import junit.framework.TestCase;


public class Issue2201 extends TestCase {
    public void test_for_issue() throws Exception {
        ParserConfig.getGlobalInstance().register("M2001", Issue2201.Model.class);
        String json = "{\"@type\":\"M2001\",\"id\":3}";
        Issue2201.Model m = ((Issue2201.Model) (JSON.parseObject(json, Object.class)));
        TestCase.assertEquals(3, m.id);
    }

    public static class Model {
        public int id;
    }
}

