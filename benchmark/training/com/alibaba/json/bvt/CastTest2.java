package com.alibaba.json.bvt;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import junit.framework.TestCase;
import org.junit.Assert;


public class CastTest2 extends TestCase {
    public void test_0() throws Exception {
        String text;
        {
            List<Object> list = new ArrayList<Object>();
            list.add(new CastTest2.Header());
            CastTest2.Body body = new CastTest2.Body("??");
            body.getItems().put("1", new CastTest2.Item());
            list.add(body);
            text = JSON.toJSONString(list);
            System.out.println(text);
        }
        JSONArray array = JSON.parseArray(text);
        CastTest2.Body body = array.getObject(1, CastTest2.Body.class);
        Assert.assertEquals("??", body.getName());
        Assert.assertEquals(1, body.getItems().size());
    }

    public static class Header {}

    public static class Body {
        private String name;

        public Body() {
        }

        public Body(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        private Map<String, CastTest2.Item> items = new HashMap<String, CastTest2.Item>();

        public Map<String, CastTest2.Item> getItems() {
            return items;
        }

        public void setItems(Map<String, CastTest2.Item> items) {
            this.items = items;
        }
    }

    public static class Item {}
}

