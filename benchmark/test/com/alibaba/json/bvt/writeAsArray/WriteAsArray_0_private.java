package com.alibaba.json.bvt.writeAsArray;


import Feature.SupportArrayToBean;
import SerializerFeature.BeanToArray;
import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class WriteAsArray_0_private extends TestCase {
    public void test_0() throws Exception {
        WriteAsArray_0_private.VO vo = new WriteAsArray_0_private.VO();
        vo.setId(123);
        vo.setName("wenshao");
        String text = JSON.toJSONString(vo, BeanToArray);
        Assert.assertEquals("[123,\"wenshao\"]", text);
        WriteAsArray_0_private.VO vo2 = JSON.parseObject(text, WriteAsArray_0_private.VO.class, SupportArrayToBean);
        Assert.assertEquals(vo.getId(), vo2.getId());
        Assert.assertEquals(vo.getName(), vo2.getName());
    }

    private static class VO {
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

