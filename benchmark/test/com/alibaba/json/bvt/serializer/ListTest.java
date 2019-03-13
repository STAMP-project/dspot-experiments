package com.alibaba.json.bvt.serializer;


import SerializerFeature.WriteClassName;
import com.alibaba.fastjson.JSON;
import java.util.LinkedList;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Assert;


public class ListTest extends TestCase {
    public void test_null() throws Exception {
        List list = new LinkedList();
        list.add(23L);
        list.add(45L);
        Assert.assertEquals("[23L,45L]", JSON.toJSONString(list, WriteClassName));
    }

    public static class VO {
        private Object value;

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }
    }
}

