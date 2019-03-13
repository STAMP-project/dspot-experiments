package com.alibaba.json.bvt.writeClassName;


import SerializerFeature.WriteClassName;
import com.alibaba.fastjson.JSON;
import java.util.Collection;
import java.util.Collections;
import junit.framework.TestCase;
import org.junit.Assert;


public class WriteClassNameTest_Collection extends TestCase {
    public void test_list() throws Exception {
        WriteClassNameTest_Collection.A a = new WriteClassNameTest_Collection.A();
        a.setList(Collections.singletonList(new WriteClassNameTest_Collection.B()));
        String text = JSON.toJSONString(a, WriteClassName);
        System.out.println(text);
        Assert.assertEquals("{\"@type\":\"com.alibaba.json.bvt.writeClassName.WriteClassNameTest_Collection$A\",\"list\":[{}]}", text);
        WriteClassNameTest_Collection.A a1 = ((WriteClassNameTest_Collection.A) (JSON.parse(text)));
        Assert.assertEquals(1, a1.getList().size());
        Assert.assertTrue(((a1.getList().iterator().next()) instanceof WriteClassNameTest_Collection.B));
    }

    private static class A {
        private Collection<WriteClassNameTest_Collection.B> list;

        public Collection<WriteClassNameTest_Collection.B> getList() {
            return list;
        }

        public void setList(Collection<WriteClassNameTest_Collection.B> list) {
            this.list = list;
        }
    }

    private static final class B {}
}

