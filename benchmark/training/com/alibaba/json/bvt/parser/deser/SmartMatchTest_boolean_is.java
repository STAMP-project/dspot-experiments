package com.alibaba.json.bvt.parser.deser;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class SmartMatchTest_boolean_is extends TestCase {
    public void test_0() throws Exception {
        String text = "{\"isVisible\":true}";
        SmartMatchTest_boolean_is.VO vo = JSON.parseObject(text, SmartMatchTest_boolean_is.VO.class);
        Assert.assertEquals(true, vo.isVisible());
    }

    public static class VO {
        private boolean visible;

        public boolean isVisible() {
            return visible;
        }

        public void setVisible(boolean visible) {
            this.visible = visible;
        }
    }
}

