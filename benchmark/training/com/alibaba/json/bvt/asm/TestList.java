package com.alibaba.json.bvt.asm;


import com.alibaba.fastjson.JSON;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import junit.framework.TestCase;
import org.junit.Assert;


public class TestList extends TestCase {
    public void test_0() throws Exception {
        TestList.VO o = new TestList.VO();
        o.setId(123);
        {
            Map<String, List<String>> item = new HashMap<String, List<String>>();
            item.put("1", Arrays.asList("a1", "b1"));
            o.getItems().add(item);
        }
        {
            Map<String, List<String>> item = new HashMap<String, List<String>>();
            item.put("2", Arrays.asList("a2", "b2"));
            o.getItems().add(item);
        }
        String text = JSON.toJSONString(o);
        TestList.VO o1 = JSON.parseObject(text, TestList.VO.class);
        String text1 = JSON.toJSONString(o1);
        Assert.assertEquals(text1, text);
        Assert.assertEquals("{\"id\":123,\"items\":[{\"1\":[\"a1\",\"b1\"]},{\"2\":[\"a2\",\"b2\"]}]}", text);
    }

    public static class VO {
        private int id;

        private List<Map<String, List<String>>> items = new ArrayList<Map<String, List<String>>>();

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public List<Map<String, List<String>>> getItems() {
            return items;
        }

        public void setItems(List<Map<String, List<String>>> items) {
            this.items = items;
        }
    }
}

