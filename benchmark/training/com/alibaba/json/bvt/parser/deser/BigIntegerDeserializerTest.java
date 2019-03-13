package com.alibaba.json.bvt.parser.deser;


import com.alibaba.fastjson.JSON;
import java.math.BigInteger;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Assert;


public class BigIntegerDeserializerTest extends TestCase {
    public void test_1() throws Exception {
        BigInteger value = JSON.parseObject("'123'", BigInteger.class);
        Assert.assertEquals(new BigInteger("123"), value);
    }

    public void test_vo() throws Exception {
        BigIntegerDeserializerTest.VO vo = JSON.parseObject("{\"value\":123}", BigIntegerDeserializerTest.VO.class);
        Assert.assertEquals(new BigInteger("123"), vo.getValue());
    }

    public void test_vo_null() throws Exception {
        BigIntegerDeserializerTest.VO vo = JSON.parseObject("{\"value\":null}", BigIntegerDeserializerTest.VO.class);
        Assert.assertEquals(null, vo.getValue());
    }

    public void test_vo2() throws Exception {
        BigIntegerDeserializerTest.VO2 vo = JSON.parseObject("{\"value\":123}", BigIntegerDeserializerTest.VO2.class);
        Assert.assertEquals(new BigInteger("123"), vo.getValue());
    }

    public void test_array() throws Exception {
        List<BigInteger> list = JSON.parseArray("[123,345]", BigInteger.class);
        Assert.assertEquals(new BigInteger("123"), list.get(0));
        Assert.assertEquals(new BigInteger("345"), list.get(1));
    }

    public static class VO {
        private BigInteger value;

        public BigInteger getValue() {
            return value;
        }

        public void setValue(BigInteger value) {
            this.value = value;
        }
    }

    private static class VO2 {
        private BigInteger value;

        public BigInteger getValue() {
            return value;
        }

        public void setValue(BigInteger value) {
            this.value = value;
        }
    }
}

