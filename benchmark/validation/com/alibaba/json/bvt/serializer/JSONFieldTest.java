package com.alibaba.json.bvt.serializer;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import junit.framework.TestCase;
import org.junit.Assert;


public class JSONFieldTest extends TestCase {
    public void test_jsonField() throws Exception {
        JSONFieldTest.VO vo = new JSONFieldTest.VO();
        vo.setId(123);
        vo.setName("xx");
        String text = JSON.toJSONString(vo);
        Assert.assertEquals("{\"id\":123}", text);
    }

    public static class VO {
        private int id;

        @JSONField(serialize = false)
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

