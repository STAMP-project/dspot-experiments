package com.alibaba.json.bvt.typeRef;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Assert;


public class TypeReferenceTest2 extends TestCase {
    public void test_0() throws Exception {
        String text;
        {
            List<TypeReferenceTest2.Bean> list = new ArrayList<TypeReferenceTest2.Bean>();
            list.add(new TypeReferenceTest2.Bean(123, "???"));
            list.add(new TypeReferenceTest2.Bean(234, "???"));
            list.add(new TypeReferenceTest2.Bean(456, "???"));
            text = JSON.toJSONString(list);
        }
        System.out.println(text);
        {
            List<TypeReferenceTest2.Bean> list = JSON.parseObject(text, new com.alibaba.fastjson.TypeReference<List<TypeReferenceTest2.Bean>>() {});// ????

            Assert.assertEquals(3, list.size());
            Assert.assertEquals(123, ((TypeReferenceTest2.Bean) (list.get(0))).getId());
            Assert.assertEquals(234, ((TypeReferenceTest2.Bean) (list.get(1))).getId());
            Assert.assertEquals(456, ((TypeReferenceTest2.Bean) (list.get(2))).getId());
            Assert.assertEquals("???", ((TypeReferenceTest2.Bean) (list.get(0))).getName());
            Assert.assertEquals("???", ((TypeReferenceTest2.Bean) (list.get(1))).getName());
            Assert.assertEquals("???", ((TypeReferenceTest2.Bean) (list.get(2))).getName());
        }
        {
            JSONArray list = JSON.parseObject(text, new com.alibaba.fastjson.TypeReference<JSONArray>() {});// ????

            Assert.assertEquals(3, list.size());
            Assert.assertEquals(123, get("id"));
            Assert.assertEquals(234, get("id"));
            Assert.assertEquals(456, get("id"));
            Assert.assertEquals("???", get("name"));
            Assert.assertEquals("???", get("name"));
            Assert.assertEquals("???", get("name"));
        }
    }

    public static class Bean {
        private int id;

        private String name;

        public Bean() {
        }

        public Bean(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}

