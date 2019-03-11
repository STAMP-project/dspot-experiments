package com.alibaba.json.bvt;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.deserializer.ExtraProcessor;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;
import org.junit.Assert;


public class JSONTest3 extends TestCase {
    public void test_json() throws Exception {
        ExtraProcessor extraProcessor = new ExtraProcessor() {
            public void processExtra(Object object, String key, Object value) {
                JSONTest3.Model model = ((JSONTest3.Model) (object));
                model.attributes.put(key, value);
            }
        };
        JSONTest3.Model model = JSON.parseObject("{\"id\":1001}", ((Type) (JSONTest3.Model.class)), extraProcessor);
        Assert.assertEquals(1, model.attributes.size());
        Assert.assertEquals(1001, model.attributes.get("id"));
    }

    public static class Model {
        private Map<String, Object> attributes = new HashMap<String, Object>();
    }
}

