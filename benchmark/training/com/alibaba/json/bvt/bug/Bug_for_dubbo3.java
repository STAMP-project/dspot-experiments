package com.alibaba.json.bvt.bug;


import SerializerFeature.WriteClassName;
import com.alibaba.fastjson.JSON;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import junit.framework.TestCase;
import org.junit.Assert;


public class Bug_for_dubbo3 extends TestCase {
    public void test_0() throws Exception {
        String text;
        {
            HashSet<String> tigers = new HashSet<String>();
            tigers.add("???");
            tigers.add("???");
            HashMap<String, Collection<String>> tiger = new HashMap<String, Collection<String>>();
            tiger.put("??", tigers);
            text = JSON.toJSONString(tiger, WriteClassName);
        }
        System.out.println(text);
        HashMap<String, Collection<String>> tigger2 = ((HashMap<String, Collection<String>>) (JSON.parseObject(text, Map.class)));
        Assert.assertEquals(1, tigger2.size());
        Assert.assertEquals(2, tigger2.get("??").size());
    }
}

