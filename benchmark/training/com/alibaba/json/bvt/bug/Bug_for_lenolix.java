package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import java.util.Map;
import junit.framework.TestCase;
import org.junit.Assert;


public class Bug_for_lenolix extends TestCase {
    public void test_FieldMap() throws Exception {
        Map<String, String[]> map = JSON.parseObject("{\"key\":[\"value1\",\"value2\"]}", new com.alibaba.fastjson.TypeReference<Map<String, String[]>>() {});
        String[] array = map.get("key");
        Assert.assertEquals("value1", array[0]);
        Assert.assertEquals("value2", array[1]);
        System.out.println(Thread.currentThread().getContextClassLoader().getResource("com/alibaba/fastjson/JSON.class"));
    }
}

