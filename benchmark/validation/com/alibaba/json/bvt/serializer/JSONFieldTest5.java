package com.alibaba.json.bvt.serializer;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class JSONFieldTest5 extends TestCase {
    public void test_jsonField() throws Exception {
        JSONFieldTest5.VO vo = new JSONFieldTest5.VO();
        vo.setID(123);
        String text = JSON.toJSONString(vo);
        Assert.assertEquals("{\"iD\":123}", text);
        Assert.assertEquals(123, JSON.parseObject(text, JSONFieldTest5.VO.class).getID());
    }

    public static class VO {
        private int id;

        public int getID() {
            return id;
        }

        public void setID(int id) {
            this.id = id;
        }
    }
}

