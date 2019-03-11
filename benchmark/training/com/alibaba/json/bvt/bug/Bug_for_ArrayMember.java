package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class Bug_for_ArrayMember extends TestCase {
    public void test_arrayMember() throws Exception {
        Bug_for_ArrayMember.A a = new Bug_for_ArrayMember.A();
        a.setValues(new Bug_for_ArrayMember.B[]{ new Bug_for_ArrayMember.B() });
        String text = JSON.toJSONString(a);
        Assert.assertEquals("{\"values\":[{}]}", text);
        Assert.assertEquals("{}", JSON.toJSONString(new Bug_for_ArrayMember.A()));
        Assert.assertEquals("null", JSON.toJSONString(new Bug_for_ArrayMember.A().getValues()));
        Assert.assertEquals("[]", JSON.toJSONString(new Bug_for_ArrayMember.A[0]));
        Assert.assertEquals("[{},{}]", JSON.toJSONString(new Bug_for_ArrayMember.A[]{ new Bug_for_ArrayMember.A(), new Bug_for_ArrayMember.A() }));
    }

    public static class A {
        private Bug_for_ArrayMember.B[] values;

        public Bug_for_ArrayMember.B[] getValues() {
            return values;
        }

        public void setValues(Bug_for_ArrayMember.B[] values) {
            this.values = values;
        }
    }

    public static class B {}
}

