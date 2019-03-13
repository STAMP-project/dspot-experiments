package com.alibaba.json.bvt.writeClassName;


import SerializerFeature.WriteClassName;
import com.alibaba.fastjson.JSON;
import java.util.Collections;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Assert;


public class WriteClassNameTest_List2 extends TestCase {
    public void test_list() throws Exception {
        WriteClassNameTest_List2.A a = new WriteClassNameTest_List2.A();
        a.setList(Collections.singletonList(new WriteClassNameTest_List2.B()));
        String text = JSON.toJSONString(a, WriteClassName);
        System.out.println(text);
        Assert.assertEquals("{\"@type\":\"com.alibaba.json.bvt.writeClassName.WriteClassNameTest_List2$A\",\"list\":[{\"id\":0}]}", text);
        WriteClassNameTest_List2.A a1 = ((WriteClassNameTest_List2.A) (JSON.parse(text)));
        Assert.assertEquals(1, a1.getList().size());
        Assert.assertTrue(((a1.getList().get(0)) instanceof WriteClassNameTest_List2.B));
    }

    public static class A {
        private List<WriteClassNameTest_List2.B> list;

        public List<WriteClassNameTest_List2.B> getList() {
            return list;
        }

        public void setList(List<WriteClassNameTest_List2.B> list) {
            this.list = list;
        }
    }

    public static final class B {
        private int id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}

