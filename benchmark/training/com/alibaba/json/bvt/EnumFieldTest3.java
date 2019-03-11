package com.alibaba.json.bvt;


import SerializerFeature.WriteMapNullValue;
import com.alibaba.fastjson.JSON;
import java.io.StringWriter;
import junit.framework.TestCase;
import org.junit.Assert;


public class EnumFieldTest3 extends TestCase {
    public void test_1() throws Exception {
        EnumFieldTest3.Model[] array = new EnumFieldTest3.Model[2048];
        for (int i = 0; i < (array.length); ++i) {
            array[i] = new EnumFieldTest3.Model();
            array[i].value = EnumFieldTest3.Type.A;
        }
        String text = JSON.toJSONString(array);
        EnumFieldTest3.Model[] array2 = JSON.parseObject(text, EnumFieldTest3.Model[].class);
        Assert.assertEquals(array.length, array2.length);
        for (int i = 0; i < (array.length); ++i) {
            Assert.assertEquals(array[i].value, array2[i].value);
        }
    }

    public void test_1_writer() throws Exception {
        EnumFieldTest3.Model[] array = new EnumFieldTest3.Model[2048];
        for (int i = 0; i < (array.length); ++i) {
            array[i] = new EnumFieldTest3.Model();
            array[i].value = EnumFieldTest3.Type.A;
        }
        StringWriter writer = new StringWriter();
        JSON.writeJSONString(writer, array);
        String text = writer.toString();
        EnumFieldTest3.Model[] array2 = JSON.parseObject(text, EnumFieldTest3.Model[].class);
        Assert.assertEquals(array.length, array2.length);
        for (int i = 0; i < (array.length); ++i) {
            Assert.assertEquals(array[i].value, array2[i].value);
        }
    }

    public void test_null() throws Exception {
        EnumFieldTest3.Model[] array = new EnumFieldTest3.Model[2048];
        for (int i = 0; i < (array.length); ++i) {
            array[i] = new EnumFieldTest3.Model();
            array[i].value = null;
        }
        String text = JSON.toJSONString(array, WriteMapNullValue);
        EnumFieldTest3.Model[] array2 = JSON.parseObject(text, EnumFieldTest3.Model[].class);
        Assert.assertEquals(array.length, array2.length);
        for (int i = 0; i < (array.length); ++i) {
            Assert.assertEquals(array[i].value, array2[i].value);
        }
    }

    public static class Model {
        public EnumFieldTest3.Type value;
    }

    public static enum Type {

        A,
        B,
        C;}
}

