package com.alibaba.json.bvt.joda;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.joda.time.Duration;


public class JodaTest_6_Duration extends TestCase {
    public void test_for_joda_0() throws Exception {
        JodaTest_6_Duration.Model m = new JodaTest_6_Duration.Model();
        m.duration = new Duration((((24L * 60L) * 60L) * 1000L));
        String json = JSON.toJSONString(m);
        TestCase.assertEquals("{\"duration\":\"PT86400S\"}", json);
        JodaTest_6_Duration.Model m1 = JSON.parseObject(json, JodaTest_6_Duration.Model.class);
        TestCase.assertEquals(m.duration, m1.duration);
    }

    public static class Model {
        public Duration duration;
    }
}

