package com.alibaba.json.bvt.issue_1100;


import SerializerFeature.NotWriteRootClassName;
import SerializerFeature.WriteClassName;
import com.alibaba.fastjson.JSON;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;


/**
 * Created by wenshao on 19/04/2017.
 */
public class Issue1151 extends TestCase {
    public void test_for_issue() throws Exception {
        Issue1151.A a = new Issue1151.A();
        a.list.add(new Issue1151.C(1001));
        a.list.add(new Issue1151.C(1002));
        String json = JSON.toJSONString(a, NotWriteRootClassName, WriteClassName);
        TestCase.assertEquals("{\"list\":[{\"@type\":\"com.alibaba.json.bvt.issue_1100.Issue1151$C\",\"id\":1001},{\"@type\":\"com.alibaba.json.bvt.issue_1100.Issue1151$C\",\"id\":1002}]}", json);
        Issue1151.A a2 = JSON.parseObject(json, Issue1151.A.class);
        TestCase.assertSame(a2.list.get(0).getClass(), Issue1151.C.class);
    }

    public static class A {
        public List<Issue1151.B> list = new ArrayList<Issue1151.B>();
    }

    public static interface B {}

    public static class C implements Issue1151.B {
        public int id;

        public C() {
        }

        public C(int id) {
            this.id = id;
        }
    }
}

