package com.alibaba.json.bvt;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class TestForPascalStyle extends TestCase {
    public void test_for_pascal_style() throws Exception {
        String text = "{\"ID\":12,\"Name\":\"Jobs\"}";
        TestForPascalStyle.VO vo = JSON.parseObject(text, TestForPascalStyle.VO.class);
        Assert.assertEquals(vo.getId(), 12);
        Assert.assertEquals(vo.getName(), "Jobs");
    }

    public static class VO {
        private int id;

        private String name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}

