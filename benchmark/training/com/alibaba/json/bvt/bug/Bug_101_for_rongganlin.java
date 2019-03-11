package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;


public class Bug_101_for_rongganlin extends TestCase {
    public void test_for_bug() throws Exception {
        Bug_101_for_rongganlin.Structure structure = new Bug_101_for_rongganlin.Structure();
        List<Bug_101_for_rongganlin.Group> groups = new ArrayList<Bug_101_for_rongganlin.Group>();
        List<Bug_101_for_rongganlin.Element> elemA = new ArrayList<Bug_101_for_rongganlin.Element>();
        elemA.add(new Bug_101_for_rongganlin.Text().set("t.a", "v.t.a"));
        elemA.add(new Bug_101_for_rongganlin.Image().set("i.a", "v.i.a"));
        groups.add(new Bug_101_for_rongganlin.Group().set(elemA));
        List<Bug_101_for_rongganlin.Element> elemB = new ArrayList<Bug_101_for_rongganlin.Element>();
        elemB.add(new Bug_101_for_rongganlin.Text().set("t.b", "v.t.b"));
        elemB.add(new Bug_101_for_rongganlin.Image().set("i.b", "v.i.b"));
        groups.add(new Bug_101_for_rongganlin.Group().set(elemB));
        structure.groups = groups;
        System.out.println(JSON.toJSON(structure));
        System.out.println(JSON.toJSONString(structure));
    }

    class Structure {
        public List<Bug_101_for_rongganlin.Group> groups;
    }

    class Group implements Bug_101_for_rongganlin.Object {
        public List<Bug_101_for_rongganlin.Element> elements;

        public Bug_101_for_rongganlin.Group set(List<Bug_101_for_rongganlin.Element> items) {
            this.elements = items;
            return this;
        }
    }

    interface Object {}

    abstract class Element {
        public String key;

        public String value;

        public Bug_101_for_rongganlin.Element set(String key, String value) {
            this.key = key;
            this.value = value;
            return this;
        }
    }

    class Text extends Bug_101_for_rongganlin.Element {}

    class Image extends Bug_101_for_rongganlin.Element {}
}

