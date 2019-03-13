package com.alibaba.json.bvt;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;


public class CastTest extends TestCase {
    public void test_0() throws Exception {
        String text;
        {
            List<Object> list = new ArrayList<Object>();
            list.add(new CastTest.Header());
            CastTest.Body body = new CastTest.Body("??");
            body.getItems().add(new CastTest.Item());
            list.add(body);
            text = JSON.toJSONString(list);
            System.out.println(text);
        }
        JSONArray array = JSON.parseArray(text);
        // Body body = array.getObject(1, Body.class);
        // Assert.assertEquals(1, body.getItems().size());
        // Assert.assertEquals("??", body.getName());
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

        private List<CastTest.Item> items = new ArrayList<CastTest.Item>();

        public List<CastTest.Item> getItems() {
            return items;
        }

        public void setItems(List<CastTest.Item> items) {
            this.items = items;
        }
    }

    public static class Item {}
}

