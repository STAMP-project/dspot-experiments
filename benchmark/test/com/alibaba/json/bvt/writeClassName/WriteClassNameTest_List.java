package com.alibaba.json.bvt.writeClassName;


import SerializerFeature.WriteClassName;
import com.alibaba.fastjson.JSON;
import java.util.Collections;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Assert;


public class WriteClassNameTest_List extends TestCase {
    public void test_list() throws Exception {
        WriteClassNameTest_List.A a = new WriteClassNameTest_List.A();
        a.setList(Collections.singletonList(new WriteClassNameTest_List.B()));
        String text = JSON.toJSONString(a, WriteClassName);
        System.out.println(text);
        Assert.assertEquals("{\"@type\":\"com.alibaba.json.bvt.writeClassName.WriteClassNameTest_List$A\",\"list\":[{}]}", text);
        WriteClassNameTest_List.A a1 = ((WriteClassNameTest_List.A) (JSON.parse(text)));
        Assert.assertEquals(1, a1.getList().size());
        Assert.assertTrue(((a1.getList().get(0)) instanceof WriteClassNameTest_List.B));
    }

    private static class A {
        private List<WriteClassNameTest_List.B> list;

        public List<WriteClassNameTest_List.B> getList() {
            return list;
        }

        public void setList(List<WriteClassNameTest_List.B> list) {
            this.list = list;
        }
    }

    private static final class B {}
}

