package com.alibaba.json.bvt.issue_1500;


import SerializerFeature.WriteClassName;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;


public class Issue1503 extends TestCase {
    public void test_for_issue() throws Exception {
        ParserConfig config = new ParserConfig();
        config.setAutoTypeSupport(true);
        Map<Long, Issue1503.Bean> map = new HashMap<Long, Issue1503.Bean>();
        map.put(null, new Issue1503.Bean());
        Map<Long, Issue1503.Bean> rmap = ((Map<Long, Issue1503.Bean>) (JSON.parse(JSON.toJSONString(map, WriteClassName), config)));
        System.out.println(rmap);
    }

    public static class Bean {}
}

