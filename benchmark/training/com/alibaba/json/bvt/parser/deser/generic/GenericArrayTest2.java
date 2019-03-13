package com.alibaba.json.bvt.parser.deser.generic;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class GenericArrayTest2 extends TestCase {
    public void test_generic() throws Exception {
        GenericArrayTest2.VO vo = new GenericArrayTest2.VO();
        vo.values = new String[]{ "a", "b" };
        String text = JSON.toJSONString(vo);
        GenericArrayTest2.VO vo1 = JSON.parseObject(text, GenericArrayTest2.VO.class);
        Assert.assertNotNull(vo1.values);
        Assert.assertEquals(2, vo1.values.length);
        Assert.assertEquals("a", vo1.values[0]);
        Assert.assertEquals("b", vo1.values[1]);
    }

    public static class A<T> {
        public T[] values;
    }

    public static class VO extends GenericArrayTest2.A {}
}

