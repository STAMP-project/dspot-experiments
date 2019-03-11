package com.alibaba.json.bvt.serializer;


import com.alibaba.fastjson.JSON;
import java.io.ByteArrayOutputStream;
import junit.framework.TestCase;
import org.junit.Assert;


public class writeJSONStringToTest extends TestCase {
    public void test_writeJSONStringTo() throws Exception {
        writeJSONStringToTest.Model model = new writeJSONStringToTest.Model();
        model.id = 1001;
        model.name = "????";
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        JSON.writeJSONString(os, model);
        os.close();
        byte[] bytes = os.toByteArray();
        String text = new String(bytes, "UTF-8");
        Assert.assertEquals("{\"id\":1001,\"name\":\"\u4e2d\u6587\u540d\u79f0\"}", text);
    }

    public static class Model {
        public int id;

        public String name;
    }
}

