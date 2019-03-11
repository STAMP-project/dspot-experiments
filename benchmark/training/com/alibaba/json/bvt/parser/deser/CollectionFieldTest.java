package com.alibaba.json.bvt.parser.deser;


import com.alibaba.fastjson.JSON;
import java.util.Collection;
import junit.framework.TestCase;
import org.junit.Assert;


public class CollectionFieldTest extends TestCase {
    public void test_null() throws Exception {
        CollectionFieldTest.Entity value = JSON.parseObject("{value:null}", CollectionFieldTest.Entity.class);
        Assert.assertNull(value.getValue());
    }

    public void test_empty() throws Exception {
        CollectionFieldTest.Entity value = JSON.parseObject("{value:[]}", CollectionFieldTest.Entity.class);
        Assert.assertEquals(0, value.getValue().size());
    }

    private static class Entity {
        private Collection value;

        public Collection getValue() {
            return value;
        }

        public void setValue(Collection value) {
            this.value = value;
        }
    }
}

