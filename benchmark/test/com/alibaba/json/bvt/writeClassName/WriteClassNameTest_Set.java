package com.alibaba.json.bvt.writeClassName;


import SerializerFeature.WriteClassName;
import com.alibaba.fastjson.JSON;
import java.util.LinkedHashSet;
import java.util.Set;
import junit.framework.TestCase;
import org.junit.Assert;


public class WriteClassNameTest_Set extends TestCase {
    public void test_list() throws Exception {
        WriteClassNameTest_Set.A a = new WriteClassNameTest_Set.A();
        Set<WriteClassNameTest_Set.B> set = new LinkedHashSet<WriteClassNameTest_Set.B>();
        set.add(new WriteClassNameTest_Set.B());
        set.add(new WriteClassNameTest_Set.B1());
        a.setList(set);
        String text = JSON.toJSONString(a, WriteClassName);
        System.out.println(text);
        Assert.assertEquals("{\"@type\":\"com.alibaba.json.bvt.writeClassName.WriteClassNameTest_Set$A\",\"list\":[{},{\"@type\":\"com.alibaba.json.bvt.writeClassName.WriteClassNameTest_Set$B1\"}]}", text);
        WriteClassNameTest_Set.A a1 = ((WriteClassNameTest_Set.A) (JSON.parse(text)));
        Assert.assertEquals(2, a1.getList().size());
    }

    public static class A {
        private Set<WriteClassNameTest_Set.B> list;

        public Set<WriteClassNameTest_Set.B> getList() {
            return list;
        }

        public void setList(Set<WriteClassNameTest_Set.B> list) {
            this.list = list;
        }
    }

    public static class B {}

    public static class B1 extends WriteClassNameTest_Set.B {}
}

