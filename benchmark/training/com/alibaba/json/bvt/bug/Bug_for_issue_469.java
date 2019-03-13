package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class Bug_for_issue_469 extends TestCase {
    public void test_for_issue() throws Exception {
        Bug_for_issue_469.VO vo = new Bug_for_issue_469.VO();
        vo.sPhotoUrl = "xxx";
        String text = JSON.toJSONString(vo);
        Bug_for_issue_469.VO vo2 = JSON.parseObject(text, Bug_for_issue_469.VO.class);
        Assert.assertEquals(vo.getsPhotoUrl(), vo2.getsPhotoUrl());
    }

    public static class VO {
        private String sPhotoUrl;

        public String getsPhotoUrl() {
            return sPhotoUrl;
        }

        public void setsPhotoUrl(String sPhotoUrl) {
            this.sPhotoUrl = sPhotoUrl;
        }
    }
}

