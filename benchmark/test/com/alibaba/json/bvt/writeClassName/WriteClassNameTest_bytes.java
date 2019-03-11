package com.alibaba.json.bvt.writeClassName;


import SerializerFeature.WriteClassName;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import junit.framework.TestCase;


/**
 * Created by wenshao on 15/05/2017.
 */
public class WriteClassNameTest_bytes extends TestCase {
    public void test_for_bytes() throws Exception {
        List<Object> list = new ArrayList<Object>();
        list.add("a");
        byte[] bytes = WriteClassNameTest_bytes.hex("84C1F969587F5FD1942148EE9D36A0FB");
        String hex = WriteClassNameTest_bytes.hex(bytes);
        byte[] bytes_2 = WriteClassNameTest_bytes.hex(hex);
        String hex_2 = WriteClassNameTest_bytes.hex(bytes_2);
        TestCase.assertEquals(hex, hex_2);
        System.out.println(hex);
        TestCase.assertEquals("84C1F969587F5FD1942148EE9D36A0FB", hex);
        list.add(bytes);
        String str = JSON.toJSONString(list, WriteClassName);
        System.out.println(str);
        TestCase.assertEquals("[\"a\",x\'84C1F969587F5FD1942148EE9D36A0FB\']", str);
        JSONArray array = ((JSONArray) (JSON.parse(str)));
        TestCase.assertEquals("a", array.get(0));
        TestCase.assertTrue(((array.get(1)) instanceof byte[]));
        // list.add(new )
    }

    public void test_bytes2() throws Exception {
        JSON.parseArray("[x'84C1F969587F5FD1942148EE9D36A0FB']");
    }

    private static final byte[] hexBytes = new byte[71];

    private static final char[] hexChars = "0123456789ABCDEF".toCharArray();

    static {
        Arrays.fill(WriteClassNameTest_bytes.hexBytes, ((byte) (-1)));
        for (int i = '9'; i >= '0'; i--) {
            WriteClassNameTest_bytes.hexBytes[i] = ((byte) (i - '0'));
        }
        for (int i = 'F'; i >= 'A'; i--) {
            WriteClassNameTest_bytes.hexBytes[i] = ((byte) ((i - 'A') + 10));
        }
    }
}

