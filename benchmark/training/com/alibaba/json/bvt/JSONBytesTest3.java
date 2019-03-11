package com.alibaba.json.bvt;


import com.alibaba.fastjson.JSON;
import java.nio.charset.Charset;
import junit.framework.TestCase;
import org.junit.Assert;


public class JSONBytesTest3 extends TestCase {
    public void test_codec() throws Exception {
        JSONBytesTest3.Model model = new JSONBytesTest3.Model();
        model.value = "??????????????????????????????????????????????????????????????";
        byte[] bytes = JSON.toJSONBytes(model);
        JSONBytesTest3.Model model2 = JSON.parseObject(bytes, 0, bytes.length, Charset.forName("UTF8").newDecoder(), JSONBytesTest3.Model.class);
        Assert.assertEquals(model.value.length(), model2.value.length());
        for (int i = 0; i < (model.value.length()); ++i) {
            char c1 = model.value.charAt(i);
            char c2 = model2.value.charAt(i);
            Assert.assertEquals(c1, c2);
        }
    }

    public static class Model {
        public String value;
    }
}

