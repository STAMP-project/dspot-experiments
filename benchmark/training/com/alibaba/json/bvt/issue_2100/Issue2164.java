package com.alibaba.json.bvt.issue_2100;


import com.alibaba.fastjson.JSON;
import java.sql.Timestamp;
import junit.framework.TestCase;


public class Issue2164 extends TestCase {
    public void test_for_issue() throws Exception {
        Timestamp ts = new Timestamp((-65001600000L));
        String json = JSON.toJSONString(ts);
        TestCase.assertEquals("-65001600000", json);
        Timestamp ts2 = JSON.parseObject(json, Timestamp.class);
        TestCase.assertEquals(ts.getTime(), ts2.getTime());
    }

    public void test_for_issue_1() throws Exception {
        Issue2164.Model m = new Issue2164.Model((-65001600000L));
        String json = JSON.toJSONString(m);
        TestCase.assertEquals("{\"time\":-65001600000}", json);
        Issue2164.Model m2 = JSON.parseObject(json, Issue2164.Model.class);
        TestCase.assertEquals(m.time.getTime(), m2.time.getTime());
    }

    public static class Model {
        public Timestamp time;

        public Model() {
        }

        public Model(long ts) {
            this.time = new Timestamp(ts);
        }
    }
}

