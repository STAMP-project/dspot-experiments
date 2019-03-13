package com.alibaba.json.bvt.writeAsArray;


import SerializerFeature.BeanToArray;
import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class WriteAsArray_long_private extends TestCase {
    public void test_0() throws Exception {
        WriteAsArray_long_private.VO vo = new WriteAsArray_long_private.VO();
        vo.setId(123);
        vo.setName("wenshao");
        String text = JSON.toJSONString(vo, BeanToArray);
        Assert.assertEquals("[123,\"wenshao\"]", text);
    }

    private static class VO {
        private long id;

        private String name;

        public long getId() {
            return id;
        }

        public void setId(long id) {
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

