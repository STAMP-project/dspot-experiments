package com.alibaba.json.bvt.serializer;


import SerializerFeature.BrowserSecure;
import com.alibaba.fastjson.JSON;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;
import org.junit.Assert;


public class SerializeWriterTest_BrowserSecure extends TestCase {
    public void test_0() throws Exception {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < 1024; ++i) {
            buf.append('a');
        }
        buf.append("??");
        buf.append("\u0000");
        JSON.toJSONString(buf.toString(), BrowserSecure);
    }

    public void test_1() throws Exception {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < 1024; ++i) {
            buf.append('a');
        }
        buf.append("??");
        buf.append("\u0000");
        StringWriter out = new StringWriter();
        JSON.writeJSONStringTo(buf.toString(), out, BrowserSecure);
    }

    public void test_zh() throws Exception {
        Assert.assertEquals("\"\u4e2d\u56fd\"", JSON.toJSONString("??", BrowserSecure));
    }

    public void test_all() throws Exception {
        String value = ".,_~!@<>\'\"\\/hello world 0123;\u6c49\u5b57\uff1b\u2028\u2028\r\n<script></scirpt>";
        String expect = "\".,_~!@\\u003C\\u003E\'\\\"\\\\/hello world 0123;\u6c49\u5b57\uff1b\\u2028\\u2028\\r\\n\\u003Cscript\\u003E\\u003C/scirpt\\u003E\"";
        Assert.assertEquals(expect, JSON.toJSONString(value, BrowserSecure));
    }

    public void test_all_map() throws Exception {
        String value = ".,_~!@<>\'\"\\/hello world 0123;\u6c49\u5b57\uff1b\u2028\u2028\r\n<script></scirpt>";
        Map<String, String> map = new HashMap<String, String>();
        map.put("value", value);
        String expect = "{\"value\":\".,_~!@\\u003C\\u003E\'\\\"\\\\/hello world 0123;\u6c49\u5b57\uff1b\\u2028\\u2028\\r\\n\\u003Cscript\\u003E\\u003C/scirpt\\u003E\"}";
        String json = JSON.toJSONString(map, BrowserSecure);
        TestCase.assertEquals(expect, json);
        TestCase.assertEquals(value, JSON.parseObject(json).get("value"));
    }

    public void test_all_entity() throws Exception {
        String value = ".,_~!@<>\'\"\\/hello world 0123;\u6c49\u5b57\uff1b\u2028\u2028\r\n<script></scirpt>";
        SerializeWriterTest_BrowserSecure.VO vo = new SerializeWriterTest_BrowserSecure.VO();
        vo.setValue(value);
        String expect = "{\"value\":\".,_~!@\\u003C\\u003E\'\\\"\\\\/hello world 0123;\u6c49\u5b57\uff1b\\u2028\\u2028\\r\\n\\u003Cscript\\u003E\\u003C/scirpt\\u003E\"}";
        String json = JSON.toJSONString(vo, BrowserSecure);
        TestCase.assertEquals(expect, json);
        TestCase.assertEquals(value, JSON.parseObject(json, SerializeWriterTest_BrowserSecure.VO.class).value);
    }

    public static class VO {
        String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}

