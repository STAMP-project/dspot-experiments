package com.alibaba.json.bvt.serializer.filters;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.PropertyPreFilter;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Assert;


public class PropertyPathTest2 extends TestCase {
    public void test_path() throws Exception {
        PropertyPathTest2.Person p1 = new PropertyPathTest2.Person();
        p1.setId(100);
        PropertyPathTest2.Person c1 = new PropertyPathTest2.Person();
        c1.setId(1000);
        PropertyPathTest2.Person c2 = new PropertyPathTest2.Person();
        c2.setId(2000);
        p1.getChildren().add(c1);
        p1.getChildren().add(c2);
        Assert.assertEquals("{\"children\":[{\"id\":1000},{\"id\":2000}],\"id\":100}", JSON.toJSONString(p1, new PropertyPathTest2.MyPropertyPreFilter()));
    }

    public static class MyPropertyPreFilter implements PropertyPreFilter {
        public boolean apply(JSONSerializer serializer, Object source, String name) {
            String path = ((serializer.getContext().toString()) + ".") + name;
            if (path.endsWith("].children")) {
                return false;
            }
            return true;
        }
    }

    public static class Person {
        private int id;

        private List<PropertyPathTest2.Person> children = new ArrayList<PropertyPathTest2.Person>();

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public List<PropertyPathTest2.Person> getChildren() {
            return children;
        }

        public void setChildren(List<PropertyPathTest2.Person> children) {
            this.children = children;
        }
    }
}

