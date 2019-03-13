package com.alibaba.json.bvt.serializer;


import com.alibaba.fastjson.JSON;
import java.util.UUID;
import junit.framework.TestCase;
import org.junit.Assert;


public class UUIDTest extends TestCase {
    public void test_timezone() throws Exception {
        UUID id = UUID.randomUUID();
        String text = JSON.toJSONString(id);
        System.out.println(text);
        Assert.assertEquals(JSON.toJSONString(id.toString()), text);
        UUID id2 = JSON.parseObject(text, UUID.class);
        Assert.assertEquals(id, id2);
    }
}

