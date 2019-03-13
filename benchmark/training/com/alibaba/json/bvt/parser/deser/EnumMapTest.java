package com.alibaba.json.bvt.parser.deser;


import com.alibaba.fastjson.JSON;
import java.util.EnumMap;
import junit.framework.TestCase;


/**
 * Created by wenshao on 2016/10/18.
 */
public class EnumMapTest extends TestCase {
    public void test_for_enum_map() throws Exception {
        EnumMap<EnumMapTest.Type, String> enumMap = new EnumMap<EnumMapTest.Type, String>(EnumMapTest.Type.class);
        enumMap.put(EnumMapTest.Type.Big, "BIG");
        String json = JSON.toJSONString(enumMap);
        System.out.println(json);
        EnumMap<EnumMapTest.Type, String> enumMap2 = JSON.parseObject(json, new com.alibaba.fastjson.TypeReference<EnumMap<EnumMapTest.Type, String>>() {});
        TestCase.assertEquals(1, enumMap2.size());
        TestCase.assertEquals(enumMap.get(EnumMapTest.Type.Big), enumMap2.get(EnumMapTest.Type.Big));
    }

    public static enum Type {

        Big,
        Small;}
}

