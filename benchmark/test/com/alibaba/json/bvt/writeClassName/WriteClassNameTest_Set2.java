package com.alibaba.json.bvt.writeClassName;


import SerializerFeature.WriteClassName;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;
import junit.framework.TestCase;
import org.junit.Assert;


public class WriteClassNameTest_Set2 extends TestCase {
    public void test_list() throws Exception {
        WriteClassNameTest_Set2.A a = new WriteClassNameTest_Set2.A();
        Set<WriteClassNameTest_Set2.B> set = new LinkedHashSet<WriteClassNameTest_Set2.B>();
        set.add(new WriteClassNameTest_Set2.B());
        set.add(new WriteClassNameTest_Set2.B1());
        a.setList(set);
        String text = JSON.toJSONString(a, WriteClassName);
        System.out.println(text);
        // Assert.assertEquals("{\"@type\":\"com.alibaba.json.bvt.writeClassName.WriteClassNameTest_Set2$A\",\"list\":[{},{\"@type\":\"com.alibaba.json.bvt.writeClassName.WriteClassNameTest_Set2$B1\"}]}",
        // text);
        ParserConfig parserConfig = new ParserConfig();
        parserConfig.addAccept("com.alibaba.json.bvt");
        WriteClassNameTest_Set2.A a1 = ((WriteClassNameTest_Set2.A) (JSON.parseObject(text, Object.class, parserConfig)));
        Assert.assertEquals(2, a1.getList().size());
        Assert.assertTrue("B", (((new ArrayList<WriteClassNameTest_Set2.B>(a1.getList()).get(0)) instanceof WriteClassNameTest_Set2.B) || ((new ArrayList<WriteClassNameTest_Set2.B>(a1.getList()).get(0)) instanceof WriteClassNameTest_Set2.B1)));
        Assert.assertTrue("B1", (((new ArrayList<WriteClassNameTest_Set2.B>(a1.getList()).get(1)) instanceof WriteClassNameTest_Set2.B) || ((new ArrayList<WriteClassNameTest_Set2.B>(a1.getList()).get(1)) instanceof WriteClassNameTest_Set2.B1)));
    }

    private static class A {
        private Set<WriteClassNameTest_Set2.B> list;

        public Set<WriteClassNameTest_Set2.B> getList() {
            return list;
        }

        public void setList(Set<WriteClassNameTest_Set2.B> list) {
            this.list = list;
        }
    }

    private static class B {}

    private static class B1 extends WriteClassNameTest_Set2.B {}
}

