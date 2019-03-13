package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import java.util.List;
import junit.framework.TestCase;


public class Bug_for_issue_555 extends TestCase {
    public void test_for_issue() throws Exception {
        JSON.parseObject("{\"list\":[{\"spec\":{}}]}", Bug_for_issue_555.A.class);
    }

    public static class A {
        public List<Bug_for_issue_555.B> list;
    }

    public static class B {
        @JSONField(serialize = true, deserialize = false)
        public Bug_for_issue_555.Spec spec;
    }

    public static class Spec {
        private int id;

        public Spec(int id) {
            this.id = id;
        }
    }
}

