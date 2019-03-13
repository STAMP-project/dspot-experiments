package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class Bug_for_issue_278 extends TestCase {
    public void test_for_issue() throws Exception {
        Bug_for_issue_278.VO vo = new Bug_for_issue_278.VO();
        vo.setTest(true);
        String text = JSON.toJSONString(vo);
        Assert.assertEquals("{\"test\":true}", text);
    }

    public void test_for_issue_decode() throws Exception {
        Bug_for_issue_278.VO vo = JSON.parseObject("{\"isTest\":true}", Bug_for_issue_278.VO.class);
        Assert.assertTrue(vo.isTest);
    }

    public static class VO {
        private boolean isTest;

        public boolean isTest() {
            return isTest;
        }

        public void setTest(boolean isTest) {
            this.isTest = isTest;
        }
    }
}

