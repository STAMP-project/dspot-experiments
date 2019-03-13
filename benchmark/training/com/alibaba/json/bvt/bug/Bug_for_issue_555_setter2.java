package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import java.util.List;
import junit.framework.TestCase;


public class Bug_for_issue_555_setter2 extends TestCase {
    public void test_for_issue() throws Exception {
        JSON.parseObject("{\"list\":[{\"spec\":{}}]}", Bug_for_issue_555_setter2.A.class);
    }

    public static class A {
        public List<Bug_for_issue_555_setter2.B> list;
    }

    public static class B {
        private Bug_for_issue_555_setter2.Spec spec;

        @JSONField(serialize = true, deserialize = false)
        public Bug_for_issue_555_setter2.Spec getSpec() {
            return spec;
        }

        @JSONField(serialize = true, deserialize = false)
        public void setSpec(Bug_for_issue_555_setter2.Spec spec) {
            this.spec = spec;
        }
    }

    public static class Spec {
        private int id;

        public Spec(int id) {
            this.id = id;
        }
    }
}

