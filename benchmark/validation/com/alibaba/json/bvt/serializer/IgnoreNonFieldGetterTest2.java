package com.alibaba.json.bvt.serializer;


import SerializerFeature.IgnoreNonFieldGetter;
import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class IgnoreNonFieldGetterTest2 extends TestCase {
    public void test_int() throws Exception {
        IgnoreNonFieldGetterTest2.VO vo = new IgnoreNonFieldGetterTest2.VO();
        vo.setId(123);
        String text = JSON.toJSONString(vo, IgnoreNonFieldGetter);
        Assert.assertEquals("{\"id\":123}", text);
    }

    private static class VO {
        private int id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getNextId() {
            return (id) + 1;
        }
    }
}

