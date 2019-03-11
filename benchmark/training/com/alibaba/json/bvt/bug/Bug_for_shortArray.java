package com.alibaba.json.bvt.bug;


import SerializerFeature.WriteClassName;
import com.alibaba.fastjson.JSON;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;
import org.junit.Assert;


public class Bug_for_shortArray extends TestCase {
    public void test_for_shor_array() throws Exception {
        HashMap map = new HashMap();
        map.put(((short) (1)), ((short) (-1)));
        String text = JSON.toJSONString(map, WriteClassName);
        System.out.println(text);
        Map map2 = JSON.parseObject(text, HashMap.class);
        Map.Entry entry = ((Map.Entry) (map2.entrySet().iterator().next()));
        Assert.assertEquals(entry.getKey().getClass(), Short.class);
        Assert.assertTrue(((entry.getValue()) instanceof Short));
    }
}

