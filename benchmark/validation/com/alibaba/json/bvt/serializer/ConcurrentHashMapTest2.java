package com.alibaba.json.bvt.serializer;


import com.alibaba.fastjson.JSON;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import junit.framework.TestCase;
import org.junit.Assert;


public class ConcurrentHashMapTest2 extends TestCase {
    public void test_concurrentHashmap() throws Exception {
        ConcurrentHashMapTest2.OffsetSerializeWrapper wrapper = new ConcurrentHashMapTest2.OffsetSerializeWrapper();
        wrapper.getOffsetTable().put(new ConcurrentHashMapTest2.MessageQueue(), new AtomicInteger(123));
        String text = JSON.toJSONString(wrapper);
        Assert.assertEquals("{\"offsetTable\":{{\"items\":[]}:123}}", text);
        ConcurrentHashMapTest2.OffsetSerializeWrapper wrapper2 = JSON.parseObject(text, ConcurrentHashMapTest2.OffsetSerializeWrapper.class);
        Assert.assertEquals(1, wrapper2.getOffsetTable().size());
        Iterator<Map.Entry<ConcurrentHashMapTest2.MessageQueue, AtomicInteger>> iter = wrapper2.getOffsetTable().entrySet().iterator();
        Map.Entry<ConcurrentHashMapTest2.MessageQueue, AtomicInteger> entry = iter.next();
        Assert.assertEquals(0, entry.getKey().getItems().size());
        Assert.assertEquals(123, entry.getValue().intValue());
    }

    public static class OffsetSerializeWrapper {
        private ConcurrentHashMap<ConcurrentHashMapTest2.MessageQueue, AtomicInteger> offsetTable = new ConcurrentHashMap<ConcurrentHashMapTest2.MessageQueue, AtomicInteger>();

        public ConcurrentHashMap<ConcurrentHashMapTest2.MessageQueue, AtomicInteger> getOffsetTable() {
            return offsetTable;
        }

        public void setOffsetTable(ConcurrentHashMap<ConcurrentHashMapTest2.MessageQueue, AtomicInteger> offsetTable) {
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

