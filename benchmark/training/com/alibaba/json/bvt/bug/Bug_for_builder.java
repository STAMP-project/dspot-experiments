package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;


public class Bug_for_builder extends TestCase {
    public void test_for_longBuilderMethod() throws Exception {
        Bug_for_builder.VO vo = JSON.parseObject("{\"id\":123}", Bug_for_builder.VO.class);
    }

    public static class VO {
        private long id;

        public long getId() {
            return id;
        }

        public Bug_for_builder.VO setId(long id) {
            this.id = id;
            return this;
        }
    }
}

