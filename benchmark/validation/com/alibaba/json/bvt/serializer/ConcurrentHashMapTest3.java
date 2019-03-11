package com.alibaba.json.bvt.serializer;


import com.alibaba.fastjson.JSON;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import junit.framework.TestCase;
import org.junit.Assert;


public class ConcurrentHashMapTest3 extends TestCase {
    public void test_concurrentHashmap() throws Exception {
        ConcurrentHashMapTest3.OffsetSerializeWrapper wrapper = new ConcurrentHashMapTest3.OffsetSerializeWrapper();
        wrapper.getOffsetTable().put(new ConcurrentHashMapTest3.MessageQueue(), new AtomicBoolean(true));
        String text = JSON.toJSONString(wrapper);
        Assert.assertEquals("{\"offsetTable\":{{\"items\":[]}:true}}", text);
        ConcurrentHashMapTest3.OffsetSerializeWrapper wrapper2 = JSON.parseObject(text, ConcurrentHashMapTest3.OffsetSerializeWrapper.class);
        Assert.assertEquals(1, wrapper2.getOffsetTable().size());
        Iterator<Map.Entry<ConcurrentHashMapTest3.MessageQueue, AtomicBoolean>> iter = wrapper2.getOffsetTable().entrySet().iterator();
        Map.Entry<ConcurrentHashMapTest3.MessageQueue, AtomicBoolean> entry = iter.next();
        Assert.assertEquals(0, entry.getKey().getItems().size());
        Assert.assertEquals(true, entry.getValue().get());
    }

    public static class OffsetSerializeWrapper {
        private ConcurrentHashMap<ConcurrentHashMapTest3.MessageQueue, AtomicBoolean> offsetTable = new ConcurrentHashMap<ConcurrentHashMapTest3.MessageQueue, AtomicBoolean>();

        public ConcurrentHashMap<ConcurrentHashMapTest3.MessageQueue, AtomicBoolean> getOffsetTable() {
            return offsetTable;
        }

        public void setOffsetTable(ConcurrentHashMap<ConcurrentHashMapTest3.MessageQueue, AtomicBoolean> offsetTable) {
            this.offsetTable = offsetTable;
        }
    }

    public static class MessageQueue {
        private List<Object> items = new LinkedList<Object>();

        public List<Object> getItems() {
            return items;
        }
    }
}

