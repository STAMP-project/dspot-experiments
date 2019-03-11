package com.alibaba.json.bvt.serializer;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.JSONScanner;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;
import org.junit.Assert;


public class SpecicalStringTest extends TestCase {
    public void test_0() throws Exception {
        String text;
        {
            JSONObject json = new JSONObject();
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("name", "??");
            json.put("text", JSON.toJSONString(map));
            text = JSON.toJSONString(json);
        }
        Assert.assertEquals("{\"text\":\"{\\\"name\\\":\\\"\u5f20\u4e09\\\"}\"}", text);
    }

    public void test_string2() throws Exception {
        StringBuilder buf = new StringBuilder();
        buf.append('"');
        for (int i = 0; i < 200; ++i) {
            buf.append(("\\\\\\/\\b\\f\\n\\r\\t\\u" + (Integer.toHexString('?'))));
        }
        buf.append('"');
        String text = buf.toString();
        JSONScanner lexer = new JSONScanner(text.toCharArray(), text.length());
        lexer.nextToken();
        Assert.assertEquals(0, lexer.pos());
        lexer.stringVal();
        // Assert.assertEquals("\"\\\\\\/\\b\\f\\n\\r\\t?\"",
        // JSON.toJSONString(stringVal));
    }
}

