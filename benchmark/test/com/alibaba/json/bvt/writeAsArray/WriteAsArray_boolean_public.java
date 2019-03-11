package com.alibaba.json.bvt.writeAsArray;


import SerializerFeature.BeanToArray;
import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class WriteAsArray_boolean_public extends TestCase {
    public void test_0() throws Exception {
        WriteAsArray_boolean_public.VO vo = new WriteAsArray_boolean_public.VO();
        vo.setId(true);
        vo.setName("wenshao");
        String text = JSON.toJSONString(vo, BeanToArray);
        Assert.assertEquals("[true,\"wenshao\"]", text);
    }

    public static class VO {
        private boolean id;

        private String name;

        public boolean getId() {
            return id;
        }

        public void setId(boolean id) {
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

