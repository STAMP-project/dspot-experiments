package com.alibaba.json.test;


import SerializerFeature.BrowserCompatible;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.IOUtils;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import junit.framework.TestCase;


/**
 * Created by wenshao on 14/03/2017.
 */
public class A1 extends TestCase {
    public void test_a() throws Exception {
        Object obj = JSON.parse("[{\"feature\":\"\\u3A56\\u3A26\"}]");
        String json = JSON.toJSONString(obj, BrowserCompatible);
        System.out.println(json);
    }

    public void test_ser() throws Exception {
        // JSONObject obj = new JSONObject();
        // 
        // ByteArrayOutputStream out = new ByteArrayOutputStream();
        // ObjectOutputStream objOut = new ObjectOutputStream(out);
        // objOut.writeObject(obj);
        // objOut.flush();
        // objOut.close();
        // byte[] bytes = out.toByteArray();
        // 
        // String str = Base64.encodeToString(bytes, false);
        // System.out.println(str);
        byte[] bytes2 = IOUtils.decodeBase64("rO0ABXNyAB9jb20uYWxpYmFiYS5mYXN0anNvbi5KU09OT2JqZWN0AAAAAAAAAAECAAFMAANtYXB0AA9MamF2YS91dGlsL01hcDt4cHNyABFqYXZhLnV0aWwuSGFzaE1hcAUH2sHDFmDRAwACRgAKbG9hZEZhY3RvckkACXRocmVzaG9sZHhwP0AAAAAAABB3CAAAABAAAAAAeA==");
        ByteArrayInputStream byteIn = new ByteArrayInputStream(bytes2);
        ObjectInputStream objIn = new ObjectInputStream(byteIn);
        Object obj = objIn.readObject();
        TestCase.assertEquals(JSONObject.class, obj.getClass());
    }
}

