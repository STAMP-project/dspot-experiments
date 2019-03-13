package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class Bug_for_franklee77 extends TestCase {
    public void test_0() throws Exception {
        Bug_for_franklee77.VO vo = JSON.parseObject("{\"id\":33}", Bug_for_franklee77.VO.class);
        Assert.assertEquals(33, vo.getId());
    }

    public static class VO {
        private int id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        private VO() {
        }
    }
}

