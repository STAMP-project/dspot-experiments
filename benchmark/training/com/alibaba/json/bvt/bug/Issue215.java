package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import junit.framework.TestCase;
import org.junit.Assert;


public class Issue215 extends TestCase {
    public void test_for_issue() throws Exception {
        byte[] bytes = new byte[128];
        new Random().nextBytes(bytes);
        Map<String, byte[]> map = new HashMap<String, byte[]>();
        map.put("val", bytes);
        String text = JSON.toJSONString(map);
        System.out.println(text);
        Map<String, byte[]> map2 = JSON.parseObject(text, new com.alibaba.fastjson.TypeReference<HashMap<String, byte[]>>() {});
        byte[] bytes2 = ((byte[]) (map2.get("val")));
        Assert.assertArrayEquals(bytes2, bytes);
    }
}

