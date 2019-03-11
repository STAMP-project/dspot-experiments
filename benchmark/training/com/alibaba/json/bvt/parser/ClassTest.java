package com.alibaba.json.bvt.parser;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class ClassTest extends TestCase {
    public void test_class_array() throws Exception {
        String text = "{\"clazz\":\"[Ljava.lang.String;\",\"value\":\"[\\\"\u6b66\u6c49\u94f6\u884c\\\"]\"}";
        ClassTest.VO vo = JSON.parseObject(text, ClassTest.VO.class);
        Assert.assertEquals(String[].class, vo.getClazz());
    }

    public void test_class() throws Exception {
        String text = "{\"clazz\":\"Ljava.lang.String;\",\"value\":\"[\\\"\u6b66\u6c49\u94f6\u884c\\\"]\"}";
        ClassTest.VO vo = JSON.parseObject(text, ClassTest.VO.class);
        Assert.assertEquals(String.class, vo.getClazz());
    }

    public static class VO {
        private Class<?> clazz;

        private Object value;

        public Class<?> getClazz() {
            return clazz;
        }

        public void setClazz(Class<?> clazz) {
            this.clazz = clazz;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }
    }
}

