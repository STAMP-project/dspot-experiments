package com.alibaba.json.bvt.value;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class LongValueTest extends TestCase {
    public void test_value() {
        long step = (((long) (Integer.MAX_VALUE)) * 1000) * 1000;
        for (long i = Long.MIN_VALUE; i <= 0; i += step) {
            LongValueTest.VO vo = new LongValueTest.VO();
            vo.value = i;
            String text = JSON.toJSONString(vo);
            LongValueTest.VO vo2 = JSON.parseObject(text, LongValueTest.VO.class);
            Assert.assertEquals(vo.value, vo2.value);
        }
        for (long i = Long.MAX_VALUE; i >= 0; i -= step) {
            LongValueTest.VO vo = new LongValueTest.VO();
            vo.value = i;
            String text = JSON.toJSONString(vo);
            LongValueTest.VO vo2 = JSON.parseObject(text, LongValueTest.VO.class);
            Assert.assertEquals(vo.value, vo2.value);
        }
    }

    public void test_value_1() {
        long step = (((long) (Integer.MAX_VALUE)) * 1000) * 1000;
        for (long i = Long.MIN_VALUE; i <= 0; i += step) {
            LongValueTest.V1 vo = new LongValueTest.V1();
            vo.value = i;
            String text = JSON.toJSONString(vo);
            LongValueTest.V1 vo2 = JSON.parseObject(text, LongValueTest.V1.class);
            Assert.assertEquals(vo.value, vo2.value);
        }
        for (long i = Long.MAX_VALUE; i >= 0; i -= step) {
            LongValueTest.V1 vo = new LongValueTest.V1();
            vo.value = i;
            String text = JSON.toJSONString(vo);
            LongValueTest.V1 vo2 = JSON.parseObject(text, LongValueTest.V1.class);
            Assert.assertEquals(vo.value, vo2.value);
        }
    }

    public void test_value_2() {
        long step = (((long) (Integer.MAX_VALUE)) * 1000) * 1000;
        for (long i = Long.MIN_VALUE; i <= 0; i += step) {
            LongValueTest.V2 vo = new LongValueTest.V2();
            vo.value = i;
            String text = JSON.toJSONString(vo);
            LongValueTest.V2 vo2 = JSON.parseObject(text, LongValueTest.V2.class);
            Assert.assertEquals(vo.value, vo2.value);
        }
        for (long i = Long.MAX_VALUE; i >= 0; i -= step) {
            LongValueTest.V2 vo = new LongValueTest.V2();
            vo.value = i;
            String text = JSON.toJSONString(vo);
            LongValueTest.V2 vo2 = JSON.parseObject(text, LongValueTest.V2.class);
            Assert.assertEquals(vo.value, vo2.value);
        }
    }

    public static class VO {
        public long value;
    }

    static class V1 {
        public long value;
    }

    public static class V2 {
        private long value;

        public long getValue() {
            return value;
        }

        public void setValue(long value) {
            this.value = value;
        }
    }
}

