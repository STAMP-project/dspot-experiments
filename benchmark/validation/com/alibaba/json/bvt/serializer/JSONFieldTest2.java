package com.alibaba.json.bvt.serializer;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import junit.framework.TestCase;
import org.junit.Assert;


public class JSONFieldTest2 extends TestCase {
    public void test_jsonField() throws Exception {
        JSONFieldTest2.VO vo = new JSONFieldTest2.VO();
        vo.setId(123);
        vo.setFlag(true);
        String text = JSON.toJSONString(vo);
        Assert.assertEquals("{\"id\":123}", text);
    }

    public static class VO {
        private int id;

        @JSONField(serialize = false)
        private boolean flag;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public boolean isFlag() {
            return flag;
        }

        public void setFlag(boolean flag) {
            this.flag = flag;
        }
    }
}

