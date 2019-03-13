package com.alibaba.json.bvt.issue_2200;


import com.alibaba.fastjson.JSON;
import com.alibaba.json.bvt.issue_2200.issue2224.PersonCollection;
import junit.framework.TestCase;


public class Issue2224 extends TestCase {
    public void test_for_issue() throws Exception {
        String json = "[{\"idNo\":\"123456\",\"name\":\"tom\"},{\"idNo\":\"123457\",\"name\":\"jack\"}]";
        PersonCollection personCollection = JSON.parseObject(json, PersonCollection.class);
        TestCase.assertNotNull(personCollection);
        TestCase.assertEquals(2, personCollection.size());
        TestCase.assertEquals("tom", personCollection.get("123456").getName());
        TestCase.assertEquals("jack", personCollection.get("123457").getName());
        String json2 = JSON.toJSONString(personCollection);
        TestCase.assertNotNull(json2);
    }
}

