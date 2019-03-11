package com.alibaba.json.bvt.serializer;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import junit.framework.TestCase;
import org.junit.Assert;


public class ConcurrentHashMapTest5 extends TestCase {
    public void test_concurrentHashmap() throws Exception {
        ConcurrentHashMapTest5.OffsetSerializeWrapper wrapper = new ConcurrentHashMapTest5.OffsetSerializeWrapper();
        wrapper.offsetTable.put(new ConcurrentHashMapTest5.MessageQueue(), new WeakReference<ConcurrentHashMapTest5.A>(new ConcurrentHashMapTest5.A(true)));
        String text = JSON.toJSONString(wrapper, new SerializeConfig());
        Assert.assertEquals("{\"offsetTable\":{{\"items\":[]}:{\"value\":true}}}", text);
        ConcurrentHashMapTest5.OffsetSerializeWrapper wrapper2 = JSON.parseObject(text, ConcurrentHashMapTest5.OffsetSerializeWrapper.class);
        Assert.assertEquals(1, wrapper2.getOffsetTable().size());
        Iterator<Map.Entry<ConcurrentHashMapTest5.MessageQueue, WeakReference<ConcurrentHashMapTest5.A>>> iter = wrapper2.getOffsetTable().entrySet().iterator();
        Map.Entry<ConcurrentHashMapTest5.MessageQueue, WeakReference<ConcurrentHashMapTest5.A>> entry = iter.next();
        Assert.assertEquals(0, entry.getKey().getItems().size());
        Assert.assertEquals(true, entry.getValue().get().isValue());
    }

    public static class OffsetSerializeWrapper {
        private ConcurrentHashMap<ConcurrentHashMapTest5.MessageQueue, WeakReference<ConcurrentHashMapTest5.A>> offsetTable = new ConcurrentHashMap<ConcurrentHashMapTest5.MessageQueue, WeakReference<ConcurrentHashMapTest5.A>>();

        public ConcurrentHashMap<ConcurrentHashMapTest5.MessageQueue, WeakReference<ConcurrentHashMapTest5.A>> getOffsetTable() {
            return offsetTable;
        }

        public void setOffsetTable(ConcurrentHashMap<ConcurrentHashMapTest5.MessageQueue, WeakReference<ConcurrentHashMapTest5.A>> offsetTable) {
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

