package com.alibaba.json.bvt.serializer;


import com.alibaba.fastjson.serializer.ListSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import java.util.Arrays;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Assert;


public class ListSerializerTest2 extends TestCase {
    public void test_0() throws Exception {
        SerializeWriter out = new SerializeWriter();
        ListSerializer listSerializer = new ListSerializer();
        Object[] array = new Object[]{ 1, 2, 3L, 4L, 5, 6, "a" };
        List<Object> list = Arrays.asList(array);
        listSerializer.write(new com.alibaba.fastjson.serializer.JSONSerializer(out), list, null, null, 0);
        // System.out.println(out.toString());
        Assert.assertEquals("[1,2,3,4,5,6,\"a\"]", out.toString());
    }
}

