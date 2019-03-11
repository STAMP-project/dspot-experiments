package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import java.util.List;
import junit.framework.TestCase;


public class Bug_for_issue_555_setter extends TestCase {
    public void test_for_issue() throws Exception {
        JSON.parseObject("{\"list\":[{\"spec\":{}}]}", Bug_for_issue_555_setter.A.class);
    }

    public static class A {
        public List<Bug_for_issue_555_setter.B> list;
    }

    public static class B {
        @JSONField(serialize = true, deserialize = false)
        private Bug_for_issue_555_setter.Spec spec;

        public Bug_for_issue_555_setter.Spec getSpec() {
            return spec;
        }

        public void setSpec(Bug_for_issue_555_setter.Spec spec) {
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

