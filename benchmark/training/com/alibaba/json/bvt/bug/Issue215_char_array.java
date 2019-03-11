package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import junit.framework.TestCase;
import org.junit.Assert;


public class Issue215_char_array extends TestCase {
    public void test_for_issue() throws Exception {
        char[] chars = new char[128];
        Random random = new Random();
        for (int i = 0; i < (chars.length); ++i) {
            chars[i] = ((char) (Math.abs(((short) (random.nextInt())))));
        }
        Map<String, char[]> map = new HashMap<String, char[]>();
        map.put("val", chars);
        String text = JSON.toJSONString(map);
        System.out.println(text);
        Map<String, char[]> map2 = JSON.parseObject(text, new com.alibaba.fastjson.TypeReference<HashMap<String, char[]>>() {});
        char[] chars2 = ((char[]) (map2.get("val")));
        Assert.assertArrayEquals(chars2, chars);
    }
}

