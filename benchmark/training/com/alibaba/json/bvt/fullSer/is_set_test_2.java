package com.alibaba.json.bvt.fullSer;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class is_set_test_2 extends TestCase {
    public void test_codec() throws Exception {
        is_set_test_2.VO vo = new is_set_test_2.VO();
        vo.set_flag(true);
        String text = JSON.toJSONString(vo);
        Assert.assertEquals("{\"flag\":true}", text);
        is_set_test_2.VO vo1 = JSON.parseObject(text, is_set_test_2.VO.class);
        Assert.assertEquals(true, vo1.is_flag());
    }

    public static class VO {
        private boolean flag;

        public boolean is_flag() {
            return flag;
        }

        public void set_flag(boolean flag) {
            this.flag = flag;
        }
    }
}

