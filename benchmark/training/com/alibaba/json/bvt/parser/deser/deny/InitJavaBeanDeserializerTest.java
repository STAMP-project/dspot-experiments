package com.alibaba.json.bvt.parser.deser.deny;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.parser.ParserConfig;
import junit.framework.TestCase;


/**
 * Created by wenshao on 28/01/2017.
 */
public class InitJavaBeanDeserializerTest extends TestCase {
    ParserConfig config = new ParserConfig();

    public void test_desktop() throws Exception {
        DenyTest11.Model model = new DenyTest11.Model();
        model.a = new DenyTest11.B();
        String text = "{\"@type\":\"com.alibaba.json.bvt.parser.deser.deny.InitJavaBeanDeserializerTest$Model\"}";
        Exception error = null;
        try {
            Object obj = JSON.parseObject(text, Object.class, config);
            System.out.println(obj.getClass());
        } catch (JSONException ex) {
            error = ex;
        }
        TestCase.assertNotNull(error);
        config.initJavaBeanDeserializers(InitJavaBeanDeserializerTest.Model.class);
        Object obj = JSON.parseObject(text, Object.class, config);
        TestCase.assertEquals(InitJavaBeanDeserializerTest.Model.class, obj.getClass());
    }

    public static class Model {}
}

