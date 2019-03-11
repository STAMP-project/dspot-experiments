package com.alibaba.json.bvt.writeClassName;


import SerializerFeature.WriteClassName;
import com.alibaba.fastjson.JSON;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import junit.framework.TestCase;
import org.junit.Assert;


public class WriteClassNameTest_Set4 extends TestCase {
    public void test_list() throws Exception {
        WriteClassNameTest_Set4.A a = new WriteClassNameTest_Set4.A();
        LinkedHashSet<WriteClassNameTest_Set4.B> set = new LinkedHashSet<WriteClassNameTest_Set4.B>();
        set.add(new WriteClassNameTest_Set4.B());
        set.add(new WriteClassNameTest_Set4.B1());
        a.setList(set);
        String text = JSON.toJSONString(a, WriteClassName);
        System.out.println(text);
        Assert.assertEquals("{\"@type\":\"com.alibaba.json.bvt.writeClassName.WriteClassNameTest_Set4$A\",\"list\":[{\"valueB\":100},{\"@type\":\"com.alibaba.json.bvt.writeClassName.WriteClassNameTest_Set4$B1\",\"valueB\":100,\"valueB1\":200}]}", text);
        WriteClassNameTest_Set4.A a1 = ((WriteClassNameTest_Set4.A) (JSON.parse(text)));
        Assert.assertEquals(2, a1.getList().size());
        Assert.assertTrue(((new ArrayList<WriteClassNameTest_Set4.B>(a1.getList()).get(0)) instanceof WriteClassNameTest_Set4.B));
        Assert.assertTrue(((new ArrayList<WriteClassNameTest_Set4.B>(a1.getList()).get(1)) instanceof WriteClassNameTest_Set4.B1));
    }

    public static class A {
        private LinkedHashSet<WriteClassNameTest_Set4.B> list;

        public LinkedHashSet<WriteClassNameTest_Set4.B> getList() {
            return list;
        }

        public void setList(LinkedHashSet<WriteClassNameTest_Set4.B> list) {
            this.list = list;
        }
    }

    public static class B {
        private int valueB = 100;

        public int getValueB() {
            return valueB;
        }
    }

    public static class B1 extends WriteClassNameTest_Set4.B {
        private int valueB1 = 200;

        public int getValueB1() {
            return valueB1;
        }
    }
}

