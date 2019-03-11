package com.alibaba.json.bvt.fullSer;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class getfTest_2 extends TestCase {
    public void test_codec() throws Exception {
        getfTest_2.VO vo = new getfTest_2.VO();
        vo.setfFlag(true);
        String text = JSON.toJSONString(vo);
        Assert.assertEquals("{\"fFlag\":true}", text);
        getfTest_2.VO vo1 = JSON.parseObject(text, getfTest_2.VO.class);
        Assert.assertEquals(true, vo1.isfFlag());
    }

    public static class VO {
        private boolean fFlag;

        public boolean isfFlag() {
            return fFlag;
        }

        public void setfFlag(boolean fFlag) {
            this.fFlag = fFlag;
        }
    }
}

