package com.alibaba.json.bvt.joda;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.joda.time.Period;


public class JodaTest_6_Period extends TestCase {
    public void test_for_joda_0() throws Exception {
        JodaTest_6_Period.Model m = new JodaTest_6_Period.Model();
        m.period = Period.days(3);
        String json = JSON.toJSONString(m);
        TestCase.assertEquals("{\"period\":\"P3D\"}", json);
        JodaTest_6_Period.Model m1 = JSON.parseObject(json, JodaTest_6_Period.Model.class);
        TestCase.assertEquals(m.period, m1.period);
    }

    public static class Model {
        public Period period;
    }
}

