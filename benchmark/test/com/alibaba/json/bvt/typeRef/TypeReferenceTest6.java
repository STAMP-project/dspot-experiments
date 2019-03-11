package com.alibaba.json.bvt.typeRef;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import junit.framework.TestCase;
import org.junit.Assert;


public class TypeReferenceTest6 extends TestCase {
    public void test_typeRef() throws Exception {
        TypeReference<Map<String, TypeReferenceTest6.Entity>> typeRef = new TypeReference<Map<String, TypeReferenceTest6.Entity>>() {};
        Map<String, TypeReferenceTest6.Entity> map = JSON.parseObject("{\"value\":{\"id\":\"abc\",\"list\":[{\"id\":123}]}}", typeRef);
        TypeReferenceTest6.Entity entity = map.get("value");
        Assert.assertNotNull(entity);
        Assert.assertEquals("abc", entity.getId());
        Assert.assertEquals(1, entity.getList().size());
        Assert.assertEquals(123, entity.getList().get(0).getId());
    }

    public static class Entity {
        private String id;

        private List<TypeReferenceTest6.A> list = new ArrayList<TypeReferenceTest6.A>();

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public List<TypeReferenceTest6.A> getList() {
            return list;
        }

        public void setList(List<TypeReferenceTest6.A> list) {
            this.list = list;
        }
    }

    public static class A {
        private int id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}

