package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;
import junit.framework.TestCase;


public class Bug_KimShen extends TestCase {
    public void test_0() throws Exception {
        String text = JSON.toJSONString(new Bug_KimShen.Entity());
        JSON.parseObject(text, Bug_KimShen.Entity.class);
    }

    public static class Entity {
        private Set<Object> value = new HashSet<Object>();

        private TreeSet<Object> treeSet = new TreeSet<Object>();

        private LinkedList<Object> linkedList = new LinkedList<Object>();

        private Bug_KimShen.MySet<Object> mySet = new Bug_KimShen.MySet<Object>();

        public Bug_KimShen.MySet<Object> getMySet() {
            return mySet;
        }

        public void setMySet(Bug_KimShen.MySet<Object> mySet) {
            this.mySet = mySet;
        }

        public LinkedList<Object> getLinkedList() {
            return linkedList;
        }

        public void setLinkedList(LinkedList<Object> linkedList) {
            this.linkedList = linkedList;
        }

        public Set<Object> getValue() {
            return value;
        }

        public void setValue(Set<Object> value) {
            this.value = value;
        }

        public TreeSet<Object> getTreeSet() {
            return treeSet;
        }

        public void setTreeSet(TreeSet<Object> treeSet) {
            this.treeSet = treeSet;
        }
    }

    public static class MySet<T> extends TreeSet<T> {}
}

