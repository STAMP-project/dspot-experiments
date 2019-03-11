package com.alibaba.json.bvt.serializer.prettyFormat;


import SerializerFeature.PrettyFormat;
import SerializerFeature.UseSingleQuotes;
import com.alibaba.fastjson.JSON;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;


public class ArrayListFieldTest extends TestCase {
    public void test_prettyFormat() throws Exception {
        ArrayListFieldTest.VO vo = new ArrayListFieldTest.VO();
        vo.getEntries().add(new ArrayListFieldTest.Entity(123, "aaa"));
        vo.getEntries().add(new ArrayListFieldTest.Entity(234, "bbb"));
        vo.getEntries().add(new ArrayListFieldTest.Entity(3, "ccc"));
        String text = JSON.toJSONString(vo, PrettyFormat, UseSingleQuotes);
        System.out.println(text);
    }

    public static class VO {
        private final List<ArrayListFieldTest.Entity> entries = new ArrayList<ArrayListFieldTest.Entity>();

        public List<ArrayListFieldTest.Entity> getEntries() {
            return entries;
        }
    }

    public static class Entity {
        private int id;

        private String name;

        public Entity() {
        }

        public Entity(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}

