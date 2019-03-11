package com.alibaba.json.bvt.parser.deser.generic;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class GenericArrayTest3 extends TestCase {
    public void test_generic() throws Exception {
        GenericArrayTest3.VO vo = new GenericArrayTest3.VO();
        vo.values = new GenericArrayTest3.Pair[]{ null, null };
        String text = JSON.toJSONString(vo);
        // VO vo1 = JSON.parseObject(text, new TypeReference<VO<Number, String>>(){} );
        GenericArrayTest3.VO vo1 = JSON.parseObject(text, GenericArrayTest3.VO.class);
        Assert.assertNotNull(vo1.values);
        Assert.assertEquals(2, vo1.values.length);
        // Assert.assertEquals("a", vo1.values[0]);
        // Assert.assertEquals("b", vo1.values[1]);
    }

    public static class A<T extends Number, S> {
        public GenericArrayTest3.Pair<T, S>[] values;
    }

    public static class VO extends GenericArrayTest3.A<Number, String> {}

    public static class Pair<A, B> {
        public A a;

        public B b;
    }
}

