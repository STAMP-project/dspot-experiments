package com.alibaba.json.bvt.parser.deser.generic;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class GenericArrayTest extends TestCase {
    public void test_generic() throws Exception {
        GenericArrayTest.VO vo = new GenericArrayTest.VO();
        vo.values = new Number[]{ 1, 1 };
        String text = JSON.toJSONString(vo);
        GenericArrayTest.VO vo1 = JSON.parseObject(text, GenericArrayTest.VO.class);
        Assert.assertNotNull(vo1.values);
        Assert.assertEquals(2, vo1.values.length);
        // Assert.assertEquals("a", vo1.values[0]);
        // Assert.assertEquals("b", vo1.values[1]);
    }

    public static class A<T extends Number> {
        public T[] values;
    }

    public static class VO extends GenericArrayTest.A {}
}

