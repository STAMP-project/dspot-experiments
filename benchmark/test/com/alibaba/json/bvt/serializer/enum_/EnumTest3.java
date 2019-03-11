/**
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */
/**
 * ????:
 * qzhanbo@yiji.com 2015-03-01 00:55 ??
 */
package com.alibaba.json.bvt.serializer.enum_;


import SerializerFeature.WriteEnumUsingName;
import SerializerFeature.WriteEnumUsingToString;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeWriter;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author bohr.qiu@gmail.com
 */
public class EnumTest3 {
    @Test
    public void testDefault() throws Exception {
        String json = JSON.toJSONString(EnumTest3.Sex.M);
        Assert.assertEquals(json, "\"M\"");
        EnumTest3.Pojo pojo = new EnumTest3.Pojo();
        pojo.setSex(EnumTest3.Sex.M);
        json = JSON.toJSONString(pojo);
        Assert.assertEquals(json, "{\"sex\":\"M\"}");
        try {
            JSON.parseObject(json, EnumTest3.Pojo.class);
            Assert.assertTrue(true);
        } catch (Exception e) {
            Assert.fail("???????name??????????");
        }
        Map<String, EnumTest3.Pojo> map = new HashMap<String, EnumTest3.Pojo>();
        map.put("a", pojo);
        json = JSON.toJSONString(map);
        Assert.assertEquals(json, "{\"a\":{\"sex\":\"M\"}}");
        Map<EnumTest3.Sex, EnumTest3.Pojo> enumMap = new EnumMap<EnumTest3.Sex, EnumTest3.Pojo>(EnumTest3.Sex.class);
        enumMap.put(EnumTest3.Sex.M, pojo);
        json = JSON.toJSONString(enumMap);
        Assert.assertEquals(json, "{\"M\":{\"sex\":\"M\"}}");
    }

    @Test
    public void testDefault1() throws Exception {
        // JSON.DUMP_CLASS = "/Users/bohr/Downloads/tmp";
        String json = JSON.toJSONString(EnumTest3.Sex.M, WriteEnumUsingToString);
        Assert.assertEquals(json, "\"\u7537\"");
    }

    @Test
    public void testDefault2() throws Exception {
        EnumTest3.Pojo pojo = new EnumTest3.Pojo();
        pojo.setSex(EnumTest3.Sex.M);
        String json = JSON.toJSONString(pojo, WriteEnumUsingToString);
        Assert.assertEquals(json, "{\"sex\":\"\u7537\"}");
        try {
            EnumTest3.Pojo pojo1 = JSON.parseObject(json, EnumTest3.Pojo.class);
            Assert.assertNull(pojo1.getSex());
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
        Map<String, EnumTest3.Pojo> map = new HashMap<String, EnumTest3.Pojo>();
        map.put("a", pojo);
        json = JSON.toJSONString(map, WriteEnumUsingToString);
        Assert.assertEquals(json, "{\"a\":{\"sex\":\"\u7537\"}}");
        Map<EnumTest3.Sex, EnumTest3.Pojo> enumMap = new EnumMap<EnumTest3.Sex, EnumTest3.Pojo>(EnumTest3.Sex.class);
        enumMap.put(EnumTest3.Sex.M, pojo);
        json = JSON.toJSONString(enumMap, WriteEnumUsingToString);
        Assert.assertEquals(json, "{\"\u7537\":{\"sex\":\"\u7537\"}}");
    }

    @Test
    public void testName() throws Exception {
        Assert.assertEquals("\"\u7537\"", JSON.toJSONString(EnumTest3.Sex.M, WriteEnumUsingToString));
        Assert.assertEquals("\"\u5973\"", JSON.toJSONString(EnumTest3.Sex.W, WriteEnumUsingToString));
    }

    @Test
    public void testWriterSerializerFeature() throws Exception {
        SerializeWriter writer = new SerializeWriter();
        writer.config(WriteEnumUsingToString, true);
        Assert.assertTrue(writer.isEnabled(WriteEnumUsingToString));
        writer.config(WriteEnumUsingName, true);
        Assert.assertTrue(writer.isEnabled(WriteEnumUsingName));
        Assert.assertFalse(writer.isEnabled(WriteEnumUsingToString));
        writer.config(WriteEnumUsingToString, true);
        Assert.assertTrue(writer.isEnabled(WriteEnumUsingToString));
        Assert.assertFalse(writer.isEnabled(WriteEnumUsingName));
    }

    public static enum Sex {

        M("?"),
        W("?");
        private String msg;

        Sex(String msg) {
            this.msg = msg;
        }

        @Override
        public String toString() {
            return msg;
        }
    }

    public static class Pojo {
        private EnumTest3.Sex sex;

        public EnumTest3.Sex getSex() {
            return sex;
        }

        public void setSex(EnumTest3.Sex sex) {
            this.sex = sex;
        }
    }
}

