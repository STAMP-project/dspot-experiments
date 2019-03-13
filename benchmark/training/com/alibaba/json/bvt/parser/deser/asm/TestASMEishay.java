package com.alibaba.json.bvt.parser.deser.asm;


import SerializerFeature.WriteEnumUsingToString;
import com.alibaba.fastjson.JSON;
import com.alibaba.json.test.benchmark.encode.EishayEncode;
import data.media.MediaContent;
import junit.framework.TestCase;


public class TestASMEishay extends TestCase {
    public void test_asm() throws Exception {
        String text = JSON.toJSONString(EishayEncode.mediaContent, WriteEnumUsingToString);
        System.out.println(text);
        System.out.println(text.getBytes().length);
        MediaContent object = JSON.parseObject(text, MediaContent.class);
        String text2 = JSON.toJSONString(object, WriteEnumUsingToString);
        System.out.println(text2);
    }
}

