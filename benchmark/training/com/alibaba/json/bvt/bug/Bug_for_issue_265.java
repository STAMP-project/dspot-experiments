package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.deserializer.ExtraProcessable;
import com.alibaba.fastjson.serializer.JSONSerializable;
import com.alibaba.fastjson.serializer.JSONSerializer;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;
import org.junit.Assert;


public class Bug_for_issue_265 extends TestCase {
    public void test_for_issue() throws Exception {
        Bug_for_issue_265.User user = new Bug_for_issue_265.User();
        user.setName("wenshao");
        String text = JSON.toJSONString(user);
        Assert.assertEquals("{\"name\":\"wenshao\"}", text);
    }

    public void test_for_issue_decode() throws Exception {
        String text = "{\"name\":\"wenshao\",\"id\":1001}";
        Bug_for_issue_265.User user = JSON.parseObject(text, Bug_for_issue_265.User.class);
        Assert.assertEquals("wenshao", user.getName());
        Assert.assertEquals(1001, user.getAttribute("id"));
    }

    public static class Model implements ExtraProcessable , JSONSerializable {
        protected Map<String, Object> attributes = new HashMap<String, Object>();

        public Map<String, Object> getAttributes() {
            return attributes;
        }

        public Object getAttribute(String name) {
            return attributes.get(name);
        }

        @Override
        public void write(JSONSerializer serializer, Object fieldName, Type fieldType, int features) throws IOException {
            serializer.write(attributes);
        }

        @Override
        public void processExtra(String key, Object value) {
            attributes.put(key, value);
        }
    }

    public static class User extends Bug_for_issue_265.Model {
        public String getName() {
            return ((String) (attributes.get("name")));
        }

        public void setName(String name) {
            attributes.put("name", name);
        }
    }
}

