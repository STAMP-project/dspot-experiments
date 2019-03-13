package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;


public class Bug_for_long_whitespace extends TestCase {
    public void test() throws Exception {
        String json = "{\"f1\":11222509, \"f2\":7}";
        Bug_for_long_whitespace.VO v = JSON.parseObject(json, Bug_for_long_whitespace.VO.class);
        System.out.println(v.getF1());
        System.out.println(v.getF2());
    }

    public static class VO {
        private long f1;

        private int f2;

        public long getF1() {
            return f1;
        }

        public void setF1(long f1) {
            this.f1 = f1;
        }

        public int getF2() {
            return f2;
        }

        public void setF2(int f2) {
            this.f2 = f2;
        }
    }
}

