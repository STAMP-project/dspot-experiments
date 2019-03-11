package com.alibaba.json.bvt.serializer;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class ClassFieldTest extends TestCase {
    public void test_writer_1() throws Exception {
        ClassFieldTest.VO vo = JSON.parseObject("{\"value\":\"int\"}", ClassFieldTest.VO.class);
        Assert.assertEquals(int.class, vo.getValue());
    }

    public static class VO {
        private Class<?> value;

        public Class<?> getValue() {
            return value;
        }

        public void setValue(Class<?> value) {
            this.value = value;
        }
    }
}

