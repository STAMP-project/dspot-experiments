package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Assert;


public class Bug_101_for_rongganlin_case2 extends TestCase {
    public void test_for_bug() throws Exception {
        Bug_101_for_rongganlin_case2.Structure structure = new Bug_101_for_rongganlin_case2.Structure();
        List<Bug_101_for_rongganlin_case2.Group> groups = new ArrayList<Bug_101_for_rongganlin_case2.Group>();
        List<Bug_101_for_rongganlin_case2.Element> elemA = new ArrayList<Bug_101_for_rongganlin_case2.Element>();
        elemA.add(new Bug_101_for_rongganlin_case2.Text().set("t.a", "v.t.a"));
        elemA.add(new Bug_101_for_rongganlin_case2.Image().set("i.a", "v.i.a"));
        groups.add(new Bug_101_for_rongganlin_case2.Group().set(elemA));
        List<Bug_101_for_rongganlin_case2.Element> elemB = new ArrayList<Bug_101_for_rongganlin_case2.Element>();
        elemB.add(new Bug_101_for_rongganlin_case2.Text().set("t.b", "v.t.b"));
        elemB.add(new Bug_101_for_rongganlin_case2.Image().set("i.b", "v.i.b"));
        groups.add(new Bug_101_for_rongganlin_case2.Group().set(elemB));
        structure.groups = groups;
        String text = JSON.toJSONString(structure);
        System.out.println(text);
        Bug_101_for_rongganlin_case2.Structure structure2 = JSON.parseObject(text, Bug_101_for_rongganlin_case2.Structure.class);
        Assert.assertEquals(structure.groups.size(), structure2.groups.size());
        System.out.println(JSON.toJSONString(structure2));
    }

    public static class Structure {
        public List<Bug_101_for_rongganlin_case2.Group> groups;
    }

    public static class Group implements Bug_101_for_rongganlin_case2.Object {
        public List<Bug_101_for_rongganlin_case2.Element> elements;

        public Bug_101_for_rongganlin_case2.Group set(List<Bug_101_for_rongganlin_case2.Element> items) {
            this.elements = items;
            return this;
        }
    }

    public static interface Object {}

    public abstract static class Element {
        public String key;

        public String value;

        public Bug_101_for_rongganlin_case2.Element set(String key, String value) {
            this.key = key;
            this.value = value;
            return this;
        }
    }

    public static class Text extends Bug_101_for_rongganlin_case2.Element {}

    public static class Image extends Bug_101_for_rongganlin_case2.Element {}
}

