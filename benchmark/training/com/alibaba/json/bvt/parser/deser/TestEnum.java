package com.alibaba.json.bvt.parser.deser;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class TestEnum extends TestCase {
    public static enum Type {

        Big,
        Medium,
        Small;}

    public void test_enum() throws Exception {
        Assert.assertEquals(TestEnum.Type.Big, JSON.parseObject("{value:\"Big\"}", TestEnum.VO.class).getValue());
        Assert.assertEquals(TestEnum.Type.Big, JSON.parseObject("{\"value\":\"Big\"}", TestEnum.VO.class).getValue());
        Assert.assertEquals(TestEnum.Type.Big, JSON.parseObject("{value:0}", TestEnum.VO.class).getValue());
        Assert.assertEquals(TestEnum.Type.Big, JSON.parseObject("{\"value\":0}", TestEnum.VO.class).getValue());
    }

    public static class VO {
        private TestEnum.Type value;

        public TestEnum.Type getValue() {
            return value;
        }

        public void setValue(TestEnum.Type value) {
            this.value = value;
        }
    }
}

