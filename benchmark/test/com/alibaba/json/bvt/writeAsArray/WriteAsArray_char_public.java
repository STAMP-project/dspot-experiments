package com.alibaba.json.bvt.writeAsArray;


import SerializerFeature.BeanToArray;
import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class WriteAsArray_char_public extends TestCase {
    public void test_0() throws Exception {
        WriteAsArray_char_public.VO vo = new WriteAsArray_char_public.VO();
        vo.setId('x');
        vo.setName("wenshao");
        String text = JSON.toJSONString(vo, BeanToArray);
        Assert.assertEquals("[\"x\",\"wenshao\"]", text);
    }

    public static class VO {
        private char id;

        private String name;

        public char getId() {
            return id;
        }

        public void setId(char id) {
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

