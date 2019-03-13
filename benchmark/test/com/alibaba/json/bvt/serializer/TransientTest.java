package com.alibaba.json.bvt.serializer;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import junit.framework.TestCase;
import org.junit.Assert;


public class TransientTest extends TestCase {
    public void test_transient() throws Exception {
        TransientTest.Category parent = new TransientTest.Category();
        parent.setName("Parent");
        TransientTest.Category child = new TransientTest.Category();
        child.setName("child");
        parent.addChild(child);
        String text = JSON.toJSONString(parent);
        System.out.println(text);
        Map<String, Field> fieldCacheMap = new HashMap<String, Field>();
        ParserConfig.parserAllFieldToCache(TransientTest.Category.class, fieldCacheMap);
        Assert.assertNotNull(ParserConfig.getFieldFromCache("name", fieldCacheMap));
        Assert.assertNull(ParserConfig.getFieldFromCache("abc", fieldCacheMap));
    }

    public static class Category {
        private String name;

        private transient TransientTest.Category parent;

        private List<TransientTest.Category> children = new ArrayList<TransientTest.Category>();

        public void addChild(TransientTest.Category child) {
            children.add(child);
            child.setParent(this);
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public TransientTest.Category getParent() {
            return parent;
        }

        public void setParent(TransientTest.Category parent) {
            this.parent = parent;
        }

        public List<TransientTest.Category> getChildren() {
            return children;
        }

        public void setChildren(List<TransientTest.Category> children) {
            this.children = children;
        }
    }
}

