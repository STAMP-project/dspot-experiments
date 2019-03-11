package com.alibaba.json.bvt.typeRef;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Assert;


public class TypeReferenceTest4 extends TestCase {
    public void test_typeRef() throws Exception {
        TypeReference<TypeReferenceTest4.VO<List<TypeReferenceTest4.A>>> typeRef = new TypeReference<TypeReferenceTest4.VO<List<TypeReferenceTest4.A>>>() {};
        TypeReferenceTest4.VO<List<TypeReferenceTest4.A>> vo = JSON.parseObject("{\"list\":[{\"id\":123}]}", typeRef);
        Assert.assertEquals(123, vo.getList().get(0).getId());
    }

    public static class VO<T> {
        private T list;

        public T getList() {
            return list;
        }

        public void setList(T list) {
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

