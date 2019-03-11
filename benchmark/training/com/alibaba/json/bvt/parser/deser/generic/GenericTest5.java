package com.alibaba.json.bvt.parser.deser.generic;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import junit.framework.TestCase;


public class GenericTest5 extends TestCase {
    public void test_generic() {
        GenericTest5.Pair<Long> p1 = new GenericTest5.Pair<Long>();
        p1.label = "p1";
        p1.value = 3L;
        String p1Json = JSON.toJSONString(p1);
        JSON.parseObject(p1Json, GenericTest5.LongPair.class);
        JSON.parseObject(p1Json, GenericTest5.StringPair.class);
        JSONObject p1Jobj = JSON.parseObject(p1Json);
        TypeReference<GenericTest5.Pair<Long>> tr = new TypeReference<GenericTest5.Pair<Long>>() {};
        GenericTest5.Pair<Long> p2 = null;
        p2 = JSON.parseObject(p1Json, tr);
        TestCase.assertNotNull(p2);// ??JSON????????

        p2 = p1Jobj.toJavaObject(tr);
        TestCase.assertNotNull(p2);
        TestCase.assertEquals(Long.valueOf(3), p2.value);
    }

    public static class Pair<T> {
        private T value;

        public String label;

        public T getValue() {
            return value;
        }

        public void setValue(T value) {
            this.value = value;
        }
    }

    public static class LongPair extends GenericTest5.Pair<String> {}

    public static class StringPair extends GenericTest5.Pair<String> {}
}

