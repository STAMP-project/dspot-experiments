package com.alibaba.json.bvt.serializer.filters;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.PropertyPreFilter;
import com.alibaba.fastjson.serializer.SerialContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import junit.framework.TestCase;
import org.junit.Assert;


public class PropertyPathTest3 extends TestCase {
    /**
     * ??????????????
     */
    public void test_path() throws Exception {
        PropertyPathTest3.Person p1 = new PropertyPathTest3.Person();
        p1.setId(100);
        PropertyPathTest3.Person c1 = new PropertyPathTest3.Person();
        c1.setId(1000);
        PropertyPathTest3.Person c2 = new PropertyPathTest3.Person();
        c2.setId(2000);
        p1.getChildren().add(c1);
        p1.getChildren().add(c2);
        // ???children.id?????id
        String s = JSON.toJSONString(p1, new PropertyPathTest3.MyPropertyPreFilter(new String[]{ "children.id", "id" }));
        Assert.assertEquals("{\"children\":[{\"id\":1000},{\"id\":2000}],\"id\":100}", s);
    }

    /**
     * ????????map??????
     */
    public void test_path2() throws Exception {
        PropertyPathTest3.Person2 p1 = new PropertyPathTest3.Person2();
        p1.setId(1);
        Map<String, String> infoMap = new HashMap<String, String>();
        infoMap.put("name", "??");
        infoMap.put("height", "168");
        p1.setInfoMap(infoMap);
        // ???infoMap.name
        String s = JSON.toJSONString(p1, new PropertyPathTest3.MyPropertyPreFilter(new String[]{ "infoMap.name" }));
        Assert.assertEquals("{\"infoMap\":{\"name\":\"\u674e\u4e09\"}}", s);
    }

    public static class MyPropertyPreFilter implements PropertyPreFilter {
        String[] onlyProperties;

        public MyPropertyPreFilter(String[] onlyProperties) {
            this.onlyProperties = onlyProperties;
        }

        private static boolean containInclude(String[] ss, String s) {
            if (((ss == null) || ((ss.length) == 0)) || (s == null))
                return false;

            for (String st : ss)
                if (st.startsWith(s))
                    return true;


            return false;
        }

        public boolean apply(JSONSerializer serializer, Object source, String name) {
            SerialContext nowContext = new SerialContext(serializer.getContext(), source, name, 0, 0);
            String nowPath = PropertyPathTest3.getLinkedPath(nowContext);
            System.out.println(("path->" + nowPath));
            // ???children.id
            return PropertyPathTest3.MyPropertyPreFilter.containInclude(onlyProperties, nowPath);
        }
    }

    public static class Person {
        private int id;

        private int id2;

        private List<PropertyPathTest3.Person> children = new ArrayList<PropertyPathTest3.Person>();

        public int getId2() {
            return id2;
        }

        public void setId2(int id2) {
            this.id2 = id2;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public List<PropertyPathTest3.Person> getChildren() {
            return children;
        }

        public void setChildren(List<PropertyPathTest3.Person> children) {
            this.children = children;
        }
    }

    public static class Person2 {
        private int id;

        private Map<String, String> infoMap;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public Map<String, String> getInfoMap() {
            return infoMap;
        }

        public void setInfoMap(Map<String, String> infoMap) {
            this.infoMap = infoMap;
        }
    }
}

