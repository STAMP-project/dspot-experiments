package com.alibaba.json.bvt.issue_1500;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import junit.framework.TestCase;


public class Issue1513 extends TestCase {
    public void test_for_issue() throws Exception {
        {
            Issue1513.Model<Object> model = JSON.parseObject("{\"values\":[{\"id\":123}]}", new com.alibaba.fastjson.TypeReference<Issue1513.Model<Object>>() {});
            TestCase.assertNotNull(model.values);
            TestCase.assertEquals(1, model.values.length);
            JSONObject object = ((JSONObject) (model.values[0]));
            TestCase.assertEquals(123, object.getIntValue("id"));
        }
        {
            Issue1513.Model<Issue1513.A> model = JSON.parseObject("{\"values\":[{\"id\":123}]}", new com.alibaba.fastjson.TypeReference<Issue1513.Model<Issue1513.A>>() {});
            TestCase.assertNotNull(model.values);
            TestCase.assertEquals(1, model.values.length);
            Issue1513.A a = model.values[0];
            TestCase.assertEquals(123, a.id);
        }
        {
            Issue1513.Model<Issue1513.B> model = JSON.parseObject("{\"values\":[{\"value\":123}]}", new com.alibaba.fastjson.TypeReference<Issue1513.Model<Issue1513.B>>() {});
            TestCase.assertNotNull(model.values);
            TestCase.assertEquals(1, model.values.length);
            Issue1513.B b = model.values[0];
            TestCase.assertEquals(123, b.value);
        }
        {
            Issue1513.Model<Issue1513.C> model = JSON.parseObject("{\"values\":[{\"age\":123}]}", new com.alibaba.fastjson.TypeReference<Issue1513.Model<Issue1513.C>>() {});
            TestCase.assertNotNull(model.values);
            TestCase.assertEquals(1, model.values.length);
            Issue1513.C c = model.values[0];
            TestCase.assertEquals(123, c.age);
        }
    }

    public static class Model<T> {
        public T[] values;
    }

    public static class A {
        public int id;
    }

    public static class B {
        public int value;
    }

    public static class C {
        public int age;
    }
}

