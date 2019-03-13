package com.alibaba.json.bvt.issue_2200;


import com.alibaba.fastjson.JSON;
import java.util.LinkedList;
import java.util.Queue;
import junit.framework.TestCase;


public class Issue2251 extends TestCase {
    public void test_for_issue() throws Exception {
        Issue2251.Model m = new Issue2251.Model();
        m.queue = new LinkedList();
        m.queue.add(1);
        m.queue.add(2);
        String str = JSON.toJSONString(m);
        Issue2251.Model m2 = JSON.parseObject(str, Issue2251.Model.class);
        TestCase.assertNotNull(m2.queue);
    }

    public static class Model {
        public Queue queue;
    }
}

