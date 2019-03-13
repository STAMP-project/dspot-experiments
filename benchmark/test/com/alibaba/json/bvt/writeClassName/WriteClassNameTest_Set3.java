package com.alibaba.json.bvt.writeClassName;


import SerializerFeature.WriteClassName;
import com.alibaba.fastjson.JSON;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import junit.framework.TestCase;
import org.junit.Assert;


public class WriteClassNameTest_Set3 extends TestCase {
    public void test_list() throws Exception {
        WriteClassNameTest_Set3.A a = new WriteClassNameTest_Set3.A();
        LinkedHashSet<WriteClassNameTest_Set3.B> set = new LinkedHashSet<WriteClassNameTest_Set3.B>();
        set.add(new WriteClassNameTest_Set3.B());
        set.add(new WriteClassNameTest_Set3.B1());
        a.setList(set);
        String text = JSON.toJSONString(a, WriteClassName);
        System.out.println(text);
        // Assert.assertEquals("{\"@type\":\"com.alibaba.json.bvt.writeClassName.WriteClassNameTest_Set3$A\",\"list\":[{},{\"@type\":\"com.alibaba.json.bvt.writeClassName.WriteClassNameTest_Set3$B1\"}]}",
        // text);
        WriteClassNameTest_Set3.A a1 = ((WriteClassNameTest_Set3.A) (JSON.parse(text)));
        Assert.assertEquals(2, a1.getList().size());
        Assert.assertTrue(((new ArrayList<WriteClassNameTest_Set3.B>(a1.getList()).get(0)) instanceof WriteClassNameTest_Set3.B));
        Assert.assertTrue(((new ArrayList<WriteClassNameTest_Set3.B>(a1.getList()).get(1)) instanceof WriteClassNameTest_Set3.B1));
    }

    private static class A {
        private LinkedHashSet<WriteClassNameTest_Set3.B> list;

        public LinkedHashSet<WriteClassNameTest_Set3.B> getList() {
            return list;
        }

        public void setList(LinkedHashSet<WriteClassNameTest_Set3.B> list) {
            this.list = list;
        }
    }

    private static class B {}

    private static class B1 extends WriteClassNameTest_Set3.B {}
}

