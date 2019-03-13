package com.alibaba.json.bvt.serializer;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class DoubleTest extends TestCase {
    public void test_double() throws Exception {
        DoubleTest.VO vo = new DoubleTest.VO();
        vo.setF1(Integer.MAX_VALUE);
        vo.setF2(Double.MAX_VALUE);
        vo.setF3(Integer.MAX_VALUE);
        String text = JSON.toJSONString(vo);
        System.out.println(text);
        DoubleTest.VO vo1 = JSON.parseObject(text, DoubleTest.VO.class);
        Assert.assertEquals(vo.getF1(), vo1.getF1());
        Assert.assertTrue(((vo.getF2()) == (vo1.getF2())));
        Assert.assertEquals(vo.getF3(), vo1.getF3());
    }

    public static class VO {
        private int f1;

        private double f2;

        private int f3;

        public int getF1() {
            return f1;
        }

        public void setF1(int f1) {
            this.f1 = f1;
        }

        public double getF2() {
            return f2;
        }

        public void setF2(double f2) {
            this.f2 = f2;
        }

        public int getF3() {
            return f3;
        }

        public void setF3(int f3) {
            this.f3 = f3;
        }
    }
}

