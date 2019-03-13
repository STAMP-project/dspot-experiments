package com.alibaba.json.bvt.writeAsArray;


import SerializerFeature.BeanToArray;
import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class WriteAsArray_float_public extends TestCase {
    public void test_0() throws Exception {
        WriteAsArray_float_public.VO vo = new WriteAsArray_float_public.VO();
        vo.setId(123.0F);
        vo.setName("wenshao");
        String text = JSON.toJSONString(vo, BeanToArray);
        Assert.assertEquals("[123.0,\"wenshao\"]", text);
    }

    public static class VO {
        private float id;

        private String name;

        public float getId() {
            return id;
        }

        public void setId(float id) {
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

