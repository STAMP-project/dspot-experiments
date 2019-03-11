package com.alibaba.json.bvt.writeClassName;


import SerializerFeature.WriteClassName;
import com.alibaba.fastjson.JSON;
import java.util.Collection;
import java.util.Collections;
import junit.framework.TestCase;
import org.junit.Assert;


public class WriteClassNameTest_Collection2 extends TestCase {
    public void test_list() throws Exception {
        WriteClassNameTest_Collection2.A a = new WriteClassNameTest_Collection2.A();
        a.setList(Collections.singletonList(new WriteClassNameTest_Collection2.B()));
        String text = JSON.toJSONString(a, WriteClassName);
        System.out.println(text);
        Assert.assertEquals("{\"@type\":\"com.alibaba.json.bvt.writeClassName.WriteClassNameTest_Collection2$A\",\"list\":[{}]}", text);
        WriteClassNameTest_Collection2.A a1 = ((WriteClassNameTest_Collection2.A) (JSON.parse(text)));
        Assert.assertEquals(1, a1.getList().size());
        Assert.assertTrue(((a1.getList().iterator().next()) instanceof WriteClassNameTest_Collection2.B));
    }

    public static class A {
        private Collection<WriteClassNameTest_Collection2.B> list;

        public Collection<WriteClassNameTest_Collection2.B> getList() {
            return list;
        }

        public void setList(Collection<WriteClassNameTest_Collection2.B> list) {
            this.list = list;
        }
    }

    public static final class B {}
}

