package com.alibaba.json.bvt.writeAsArray;


import SerializerFeature.BeanToArray;
import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class WriteAsArray_byte_public extends TestCase {
    public void test_0() throws Exception {
        WriteAsArray_byte_public.VO vo = new WriteAsArray_byte_public.VO();
        vo.setId(((byte) (123)));
        vo.setName("wenshao");
        String text = JSON.toJSONString(vo, BeanToArray);
        Assert.assertEquals("[123,\"wenshao\"]", text);
    }

    public static class VO {
        private byte id;

        private String name;

        public byte getId() {
            return id;
        }

        public void setId(byte id) {
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

