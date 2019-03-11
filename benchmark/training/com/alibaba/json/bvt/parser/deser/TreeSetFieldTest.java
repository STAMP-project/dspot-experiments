package com.alibaba.json.bvt.parser.deser;


import com.alibaba.fastjson.JSON;
import java.util.TreeSet;
import junit.framework.TestCase;
import org.junit.Assert;


public class TreeSetFieldTest extends TestCase {
    public void test_null() throws Exception {
        TreeSetFieldTest.Entity value = JSON.parseObject("{value:null}", TreeSetFieldTest.Entity.class);
        Assert.assertNull(value.getValue());
    }

    public void test_empty() throws Exception {
        TreeSetFieldTest.Entity value = JSON.parseObject("{value:[]}", TreeSetFieldTest.Entity.class);
        Assert.assertEquals(0, value.getValue().size());
    }

    private static class Entity {
        private TreeSet value;

        public TreeSet getValue() {
            return value;
        }

        public void setValue(TreeSet value) {
            this.value = value;
        }
    }
}

