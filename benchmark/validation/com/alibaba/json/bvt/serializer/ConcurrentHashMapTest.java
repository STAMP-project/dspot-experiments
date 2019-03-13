package com.alibaba.json.bvt.serializer;


import com.alibaba.fastjson.JSON;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import junit.framework.TestCase;
import org.junit.Assert;


public class ConcurrentHashMapTest extends TestCase {
    public void test_concurrentHashmap() throws Exception {
        ConcurrentHashMapTest.OffsetSerializeWrapper wrapper = new ConcurrentHashMapTest.OffsetSerializeWrapper();
        wrapper.getOffsetTable().put(new ConcurrentHashMapTest.MessageQueue(), new AtomicLong(123));
        String text = JSON.toJSONString(wrapper);
        Assert.assertEquals("{\"offsetTable\":{{\"items\":[]}:123}}", text);
        ConcurrentHashMapTest.OffsetSerializeWrapper wrapper2 = JSON.parseObject(text, ConcurrentHashMapTest.OffsetSerializeWrapper.class);
        Assert.assertEquals(1, wrapper2.getOffsetTable().size());
        Iterator<Map.Entry<ConcurrentHashMapTest.MessageQueue, AtomicLong>> iter = wrapper2.getOffsetTable().entrySet().iterator();
        Map.Entry<ConcurrentHashMapTest.MessageQueue, AtomicLong> entry = iter.next();
        Assert.assertEquals(0, entry.getKey().getItems().size());
        Assert.assertEquals(123L, entry.getValue().longValue());
    }

    public static class OffsetSerializeWrapper {
        private ConcurrentHashMap<ConcurrentHashMapTest.MessageQueue, AtomicLong> offsetTable = new ConcurrentHashMap<ConcurrentHashMapTest.MessageQueue, AtomicLong>();

        public ConcurrentHashMap<ConcurrentHashMapTest.MessageQueue, AtomicLong> getOffsetTable() {
            return offsetTable;
        }

        public void setOffsetTable(ConcurrentHashMap<ConcurrentHashMapTest.MessageQueue, AtomicLong> offsetTable) {
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

