package com.alibaba.json.demo;


import com.alibaba.fastjson.parser.DefaultJSONParser;
import junit.framework.TestCase;


/**
 * Created by wenshao on 11/02/2017.
 */
public class ReuseObject extends TestCase {
    public void test_reuse() throws Exception {
        ReuseObject.Model model = new ReuseObject.Model();
        {
            DefaultJSONParser parser = new DefaultJSONParser("{\"id\":123,\"name\":\"wangsai-silence\"}");
            parser.parseObject(model);
            parser.close();// ??close???buf?????

            TestCase.assertEquals(123, model.id);
            TestCase.assertEquals("wangsai-silence", model.name);
        }
        {
            DefaultJSONParser parser = new DefaultJSONParser("{\"id\":234,\"name\":\"wenshao\"}");
            parser.parseObject(model);
            parser.close();// ??close???buf?????

            TestCase.assertEquals(234, model.id);
            TestCase.assertEquals("wenshao", model.name);
        }
    }

    public static class Model {
        public int id;

        public String name;
    }
}

