package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class Bug_for_huling extends TestCase {
    public void test_for_0() throws Exception {
        Bug_for_huling.VO vo = new Bug_for_huling.VO();
        vo.setValue("\u0000\u0000");
        Assert.assertEquals('\u0000', vo.getValue().charAt(0));
        String text = JSON.toJSONString(vo);
        System.out.println(text);
        Assert.assertEquals("{\"value\":\"\\u0000\\u0000\"}", text);
        Bug_for_huling.VO vo2 = JSON.parseObject(text, Bug_for_huling.VO.class);
        Assert.assertEquals("\u0000\u0000", vo2.getValue());
    }

    public void test_for_1() throws Exception {
        Bug_for_huling.VO vo = new Bug_for_huling.VO();
        vo.setValue("\u0001\u0001");
        Assert.assertEquals('\u0001', vo.getValue().charAt(0));
        String text = JSON.toJSONString(vo);
        System.out.println(text);
        Assert.assertEquals("{\"value\":\"\\u0001\\u0001\"}", text);
        Bug_for_huling.VO vo2 = JSON.parseObject(text, Bug_for_huling.VO.class);
        Assert.assertEquals("\u0001\u0001", vo2.getValue());
    }

    public void test_for_2028() throws Exception {
        Bug_for_huling.VO vo = new Bug_for_huling.VO();
        vo.setValue("\u2028\u2028");
        Assert.assertEquals('\u2028', vo.getValue().charAt(0));
        String text = JSON.toJSONString(vo);
        System.out.println(text);
        Assert.assertEquals("{\"value\":\"\\u2028\\u2028\"}", text);
        Bug_for_huling.VO vo2 = JSON.parseObject(text, Bug_for_huling.VO.class);
        Assert.assertEquals("\u2028\u2028", vo2.getValue());
    }

    public void test_for_2029() throws Exception {
        Bug_for_huling.VO vo = new Bug_for_huling.VO();
        vo.setValue("\u2029\u2029");
        Assert.assertEquals('\u2029', vo.getValue().charAt(0));
        String text = JSON.toJSONString(vo);
        System.out.println(text);
        Assert.assertEquals("{\"value\":\"\\u2029\\u2029\"}", text);
        Bug_for_huling.VO vo2 = JSON.parseObject(text, Bug_for_huling.VO.class);
        Assert.assertEquals("\u2029\u2029", vo2.getValue());
    }

    public static class VO {
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}

