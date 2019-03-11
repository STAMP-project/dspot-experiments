package com.alibaba.json.bvt.ref;


import SerializerFeature.PrettyFormat;
import com.alibaba.fastjson.JSON;
import java.util.ArrayList;
import java.util.Collection;
import junit.framework.TestCase;
import org.junit.Assert;


public class RefTest11 extends TestCase {
    public void test_ref() throws Exception {
        RefTest11.Department tech = new RefTest11.Department(1, "???");
        tech.setRoot(tech);
        {
            RefTest11.Department pt = new RefTest11.Department(2, "?????");
            pt.setParent(tech);
            pt.setRoot(tech);
            tech.getChildren().add(pt);
            {
                RefTest11.Department sysbase = new RefTest11.Department(3, "????");
                sysbase.setParent(pt);
                sysbase.setRoot(tech);
                pt.getChildren().add(sysbase);
            }
        }
        {
            RefTest11.Department cn = new RefTest11.Department(4, "??????");
            cn.setParent(tech);
            cn.setRoot(tech);
            tech.getChildren().add(cn);
        }
        {
            // JSON.toJSONString(tech);
        }
        {
            String prettyText = JSON.toJSONString(tech, PrettyFormat);
            System.out.println(prettyText);
            String text = JSON.toJSONString(tech);
            RefTest11.Department dept = JSON.parseObject(text, RefTest11.Department.class);
            Assert.assertTrue((dept == (dept.getRoot())));
            System.out.println(JSON.toJSONString(dept, PrettyFormat));
        }
    }

    public static class Department {
        private int id;

        private String name;

        private RefTest11.Department parent;

        private RefTest11.Department root;

        private Collection<RefTest11.Department> children = new ArrayList<RefTest11.Department>();

        public Department() {
        }

        public RefTest11.Department getRoot() {
            return root;
        }

        public void setRoot(RefTest11.Department root) {
            this.root = root;
        }

        public Department(int id, String name) {
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

        public RefTest11.Department getParent() {
            return parent;
        }

        public void setParent(RefTest11.Department parent) {
            this.parent = parent;
        }

        public Collection<RefTest11.Department> getChildren() {
            return children;
        }

        public void setChildren(Collection<RefTest11.Department> children) {
            this.children = children;
        }

        public String toString() {
            return ((("{id:" + (id)) + ",name:") + (name)) + "}";
        }
    }
}

