package com.alibaba.json.bvt.fullSer;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class getfTest extends TestCase {
    public void test_codec() throws Exception {
        getfTest.VO vo = new getfTest.VO();
        vo.setfId(123);
        String text = JSON.toJSONString(vo);
        Assert.assertEquals("{\"fId\":123}", text);
        getfTest.VO vo1 = JSON.parseObject(text, getfTest.VO.class);
        Assert.assertEquals(123, vo1.getfId());
    }

    public static class VO {
        private int fId;

        public int getfId() {
            return fId;
        }

        public void setfId(int fId) {
            this.fId = fId;
        }
    }
}

