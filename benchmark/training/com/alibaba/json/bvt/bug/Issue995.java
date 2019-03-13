package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSONPath;
import junit.framework.TestCase;


/**
 * Created by wenshao on 15/01/2017.
 */
public class Issue995 extends TestCase {
    public void test_for_issue() throws Exception {
        Issue995.Person person = new Issue995.Person();
        JSONPath.set(person, "$.nose.name", "xxx");
    }

    public static class Person {
        public Issue995.Nose nose;
    }

    public static class Nose {
        public String name;
    }
}

