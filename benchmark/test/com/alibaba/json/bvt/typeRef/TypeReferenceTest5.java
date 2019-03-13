package com.alibaba.json.bvt.typeRef;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import java.util.LinkedHashMap;
import junit.framework.TestCase;
import org.junit.Assert;


public class TypeReferenceTest5 extends TestCase {
    public void test_typeRef() throws Exception {
        TypeReference<TypeReferenceTest5.A<TypeReferenceTest5.B>> typeRef = new TypeReference<TypeReferenceTest5.A<TypeReferenceTest5.B>>() {};
        TypeReferenceTest5.A<TypeReferenceTest5.B> a = JSON.parseObject("{\"body\":{\"id\":123}}", typeRef);
        TypeReferenceTest5.B b = a.getBody();
        Assert.assertEquals(123, b.get("id"));
    }

    public static class A<T> {
        private T body;

        public T getBody() {
            return body;
        }

        public void setBody(T body) {
            this.body = body;
        }
    }

    public static class B extends LinkedHashMap<String, Object> {
        private static final long serialVersionUID = 1L;
    }
}

