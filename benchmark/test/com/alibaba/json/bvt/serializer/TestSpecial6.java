package com.alibaba.json.bvt.serializer;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class TestSpecial6 extends TestCase {
    public void test_1() throws Exception {
        TestSpecial6.VO vo = new TestSpecial6.VO();
        vo.setValue("??");
        String text = JSON.toJSONString(vo);
        TestSpecial6.VO vo2 = JSON.parseObject(text, TestSpecial6.VO.class);
        Assert.assertEquals(vo.value, vo2.value);
        TestCase.assertEquals("{\"value\":\"\u9a6c\u4dae\"}", text);
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

