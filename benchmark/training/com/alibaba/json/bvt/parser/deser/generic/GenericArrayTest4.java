package com.alibaba.json.bvt.parser.deser.generic;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class GenericArrayTest4 extends TestCase {
    public void test_generic() throws Exception {
        GenericArrayTest4.VO vo = new GenericArrayTest4.VO();
        vo.values = new GenericArrayTest4.Pair[]{ null, null };
        String text = JSON.toJSONString(vo);
        // VO vo1 = JSON.parseObject(text, new TypeReference<VO<Number, String>>(){} );
        GenericArrayTest4.VO vo1 = JSON.parseObject(text, GenericArrayTest4.VO.class);
        Assert.assertNotNull(vo1.values);
        Assert.assertEquals(2, vo1.values.length);
        // Assert.assertEquals("a", vo1.values[0]);
        // Assert.assertEquals("b", vo1.values[1]);
    }

    public static class A<T extends Number, S> {
        public GenericArrayTest4.Pair<? extends T, ? extends S>[] values;
    }

    public static class VO extends GenericArrayTest4.A<Number, String> {}

    public static class Pair<A, B> {
        public A a;

        public B b;
    }
}

