package com.alibaba.json.bvt.serializer;


import com.alibaba.fastjson.JSON;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import junit.framework.TestCase;
import org.junit.Assert;


public class ConcurrentHashMapTest6 extends TestCase {
    public void test_concurrentHashmap() throws Exception {
        ConcurrentHashMapTest6.OffsetSerializeWrapper wrapper = new ConcurrentHashMapTest6.OffsetSerializeWrapper();
        wrapper.offsetTable.put(new ConcurrentHashMapTest6.MessageQueue(), new WeakReference<ConcurrentHashMapTest6.A>(new ConcurrentHashMapTest6.A(true)));
        String text = JSON.toJSONString(wrapper);
        Assert.assertEquals("{\"offsetTable\":{{\"items\":[]}:{\"value\":true}}}", text);
        ConcurrentHashMapTest6.OffsetSerializeWrapper wrapper2 = JSON.parseObject(text, ConcurrentHashMapTest6.OffsetSerializeWrapper.class);
        Assert.assertEquals(1, wrapper2.getOffsetTable().size());
        Iterator<Map.Entry<ConcurrentHashMapTest6.MessageQueue, WeakReference<ConcurrentHashMapTest6.A>>> iter = wrapper2.getOffsetTable().entrySet().iterator();
        Map.Entry<ConcurrentHashMapTest6.MessageQueue, WeakReference<ConcurrentHashMapTest6.A>> entry = iter.next();
        Assert.assertEquals(0, entry.getKey().getItems().size());
        Assert.assertEquals(true, entry.getValue().get().isValue());
    }

    public static class OffsetSerializeWrapper {
        private ConcurrentHashMap<ConcurrentHashMapTest6.MessageQueue, WeakReference<ConcurrentHashMapTest6.A>> offsetTable = new ConcurrentHashMap<ConcurrentHashMapTest6.MessageQueue, WeakReference<ConcurrentHashMapTest6.A>>();

        public ConcurrentHashMap<ConcurrentHashMapTest6.MessageQueue, WeakReference<ConcurrentHashMapTest6.A>> getOffsetTable() {
            return offsetTable;
        }

        public void setOffsetTable(ConcurrentHashMap<ConcurrentHashMapTest6.MessageQueue, WeakReference<ConcurrentHashMapTest6.A>> offsetTable) {
            this.offsetTable = offsetTable;
        }
    }

    public static class MessageQueue {
        private List<Serializable> items = new LinkedList<Serializable>();

        public List<Serializable> getItems() {
            return items;
        }
    }

    public static class A implements Serializable {
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

