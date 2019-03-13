package com.alibaba.json.bvt.serializer;


import com.alibaba.fastjson.JSON;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import junit.framework.TestCase;
import org.junit.Assert;


public class ConcurrentHashMapTest4 extends TestCase {
    public void test_concurrentHashmap() throws Exception {
        ConcurrentHashMapTest4.OffsetSerializeWrapper wrapper = new ConcurrentHashMapTest4.OffsetSerializeWrapper();
        wrapper.getOffsetTable().put(new ConcurrentHashMapTest4.MessageQueue(), new AtomicReference<ConcurrentHashMapTest4.A>(new ConcurrentHashMapTest4.A(true)));
        String text = JSON.toJSONString(wrapper);
        Assert.assertEquals("{\"offsetTable\":{{\"items\":[]}:{\"value\":true}}}", text);
        ConcurrentHashMapTest4.OffsetSerializeWrapper wrapper2 = JSON.parseObject(text, ConcurrentHashMapTest4.OffsetSerializeWrapper.class);
        Assert.assertEquals(1, wrapper2.getOffsetTable().size());
        Iterator<Map.Entry<ConcurrentHashMapTest4.MessageQueue, AtomicReference<ConcurrentHashMapTest4.A>>> iter = wrapper2.getOffsetTable().entrySet().iterator();
        Map.Entry<ConcurrentHashMapTest4.MessageQueue, AtomicReference<ConcurrentHashMapTest4.A>> entry = iter.next();
        Assert.assertEquals(0, entry.getKey().getItems().size());
        Assert.assertEquals(true, entry.getValue().get().isValue());
    }

    public static class OffsetSerializeWrapper {
        private ConcurrentHashMap<ConcurrentHashMapTest4.MessageQueue, AtomicReference<ConcurrentHashMapTest4.A>> offsetTable = new ConcurrentHashMap<ConcurrentHashMapTest4.MessageQueue, AtomicReference<ConcurrentHashMapTest4.A>>();

        public ConcurrentHashMap<ConcurrentHashMapTest4.MessageQueue, AtomicReference<ConcurrentHashMapTest4.A>> getOffsetTable() {
            return offsetTable;
        }

        public void setOffsetTable(ConcurrentHashMap<ConcurrentHashMapTest4.MessageQueue, AtomicReference<ConcurrentHashMapTest4.A>> offsetTable) {
            this.offsetTable = offsetTable;
        }
    }

    public static class MessageQueue {
        private List<Object> items = new LinkedList<Object>();

        public List<Object> getItems() {
            return items;
        }
    }

    public static class A {
        private boolean value;

        public A() {
        }

        public A(boolean value) {
            super();
            this.value = value;
        }

        public boolean isValue() {
            return value;
        }

        public void setValue(boolean value) {
            this.value = value;
        }
    }
}

