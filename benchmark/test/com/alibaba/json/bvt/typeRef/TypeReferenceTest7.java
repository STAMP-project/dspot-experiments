package com.alibaba.json.bvt.typeRef;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import java.util.Map;
import junit.framework.TestCase;
import org.junit.Assert;


public class TypeReferenceTest7 extends TestCase {
    public void test_typeRef() throws Exception {
        TypeReference<Map<String, TypeReferenceTest7.Entity>> typeRef = new TypeReference<Map<String, TypeReferenceTest7.Entity>>() {};
        Map<String, TypeReferenceTest7.Entity> map = JSON.parseObject("{\"value\":{\"id\":\"abc\",\"a\":{\"id\":123}}}", typeRef);
        TypeReferenceTest7.Entity entity = map.get("value");
        Assert.assertNotNull(entity);
        Assert.assertEquals("abc", entity.getId());
        Assert.assertEquals(123, entity.getA().getId());
    }

    public static class Entity {
        private String id;

        private TypeReferenceTest7.A a;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public TypeReferenceTest7.A getA() {
            return a;
        }

        public void setA(TypeReferenceTest7.A a) {
            this.a = a;
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

