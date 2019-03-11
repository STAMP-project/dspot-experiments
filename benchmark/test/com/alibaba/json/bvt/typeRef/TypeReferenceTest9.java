package com.alibaba.json.bvt.typeRef;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import java.util.Map;
import junit.framework.TestCase;
import org.junit.Assert;


public class TypeReferenceTest9 extends TestCase {
    public void test_typeRef() throws Exception {
        TypeReference<Map<String, TypeReferenceTest9.Entity>> typeRef = new TypeReference<Map<String, TypeReferenceTest9.Entity>>() {};
        Map<String, TypeReferenceTest9.Entity> map = JSON.parseObject("{\"value\":{\"id\":\"abc\",\"list\":[{\"id\":123,\"type\":\"A\"}]}}", typeRef);
        TypeReferenceTest9.Entity entity = map.get("value");
        Assert.assertNotNull(entity);
        Assert.assertEquals("abc", entity.getId());
        Assert.assertEquals(1, entity.getList().length);
        Assert.assertEquals(123, entity.getList()[0].getId());
    }

    public static class Entity {
        private String id;

        private TypeReferenceTest9.A[] list;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public TypeReferenceTest9.A[] getList() {
            return list;
        }

        public void setList(TypeReferenceTest9.A[] list) {
            this.list = list;
        }
    }

    public static class A {
        private int id;

        private TypeReferenceTest9.Type type;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public TypeReferenceTest9.Type getType() {
            return type;
        }

        public void setType(TypeReferenceTest9.Type type) {
            this.type = type;
        }
    }

    public static enum Type {

        A;}
}

