package com.alibaba.json.bvt.parser.deser;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class SmartMatchTest2 extends TestCase {
    public void test_vo2() throws Exception {
        String text = "{\"_id\":1001}";
        SmartMatchTest2.VO2 vo = JSON.parseObject(text, SmartMatchTest2.VO2.class);
        Assert.assertEquals(1001, vo.getId());
    }

    private static class VO {
        private int id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }

    public static class VO2 {
        private int id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}

