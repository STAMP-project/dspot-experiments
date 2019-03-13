package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import junit.framework.TestCase;


/**
 * Created by wenshao on 2016/11/13.
 */
public class Issue744_1 extends TestCase {
    public void test_for_issue() throws Exception {
        Issue744_1.C c = new Issue744_1.C();
        c.setName("name");
        String json = JSON.toJSONString(c);
        TestCase.assertEquals("{}", json);
    }

    public abstract static class B {
        @JSONField(serialize = false, deserialize = false)
        public abstract String getName();
    }

    public static class C extends Issue744_1.B {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}

