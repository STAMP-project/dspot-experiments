package com.alibaba.json.demo;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;


public class Demo2 extends TestCase {
    public void test_0() throws Exception {
        Demo2.Department dep = new Demo2.Department();
        dep.setId(123);
        dep.setName("????");
        dep.setParent(dep);
        String text = JSON.toJSONString(dep);
        System.out.println(text);
        JSON.parseObject(text, Demo2.Department.class);
    }

    public static class Department {
        private int id;

        private String name;

        private Demo2.Department parent;

        private transient List<Demo2.Department> children = new ArrayList<Demo2.Department>();

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

        @JSONField(serialize = false)
        public Demo2.Department getParent() {
            return parent;
        }

        public void setParent(Demo2.Department parent) {
            this.parent = parent;
        }

        public List<Demo2.Department> getChildren() {
            return children;
        }

        public void setChildren(List<Demo2.Department> children) {
            this.children = children;
        }
    }
}

