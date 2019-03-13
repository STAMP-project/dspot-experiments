package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Assert;


public class Bug_for_wangran2 extends TestCase {
    public void test_for_wangran() throws Exception {
        String text = "{"// 
         + ((((("\"first\":{\"id\":1001},"// 
         + "\"second\":{\"id\":1002,\"root\":{\"$ref\":\"$\"}},")// 
         + "\"id\":23,")// 
         + "\"name\":\"xxx\",")// 
         + "\"children\":[{\"root\":{\"$ref\":\"$\"}},{\"$ref\":\"$.second\"}]")// 
         + "}");
        Bug_for_wangran2.Root root = JSON.parseObject(text, Bug_for_wangran2.Root.class);
        Assert.assertEquals(23, root.getId());
        Assert.assertEquals("xxx", root.getName());
        Assert.assertTrue((root == (root.getChildren().get(0).getRoot())));
        Assert.assertTrue((root == (root.getChildren().get(1).getRoot())));
    }

    public static class Root {
        private int id;

        private String name;

        private Bug_for_wangran2.Child first;

        private Bug_for_wangran2.Child second;

        private List<Bug_for_wangran2.Child> children = new ArrayList<Bug_for_wangran2.Child>();

        public Root() {
        }

        public Bug_for_wangran2.Child getSecond() {
            return second;
        }

        public void setSecond(Bug_for_wangran2.Child second) {
            System.out.println("setSecond");
            this.second = second;
        }

        public Bug_for_wangran2.Child getFirst() {
            return first;
        }

        public void setFirst(Bug_for_wangran2.Child first) {
            System.out.println("setFirst");
            this.first = first;
        }

        public List<Bug_for_wangran2.Child> getChildren() {
            return children;
        }

        public void setChildren(List<Bug_for_wangran2.Child> children) {
            System.out.println("setChildren");
            this.children = children;
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

    public static class Child {
        private int id;

        private Bug_for_wangran2.Root root;

        public Child() {
        }

        public Bug_for_wangran2.Root getRoot() {
            return root;
        }

        public void setRoot(Bug_for_wangran2.Root root) {
            System.out.println("setRoot");
            this.root = root;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}

/**
 * 500m / 300
 */
