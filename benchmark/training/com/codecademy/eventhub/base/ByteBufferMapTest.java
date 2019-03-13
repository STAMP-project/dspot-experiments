package com.codecademy.eventhub.base;


import com.google.common.collect.Maps;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class ByteBufferMapTest {
    @Test
    public void testAll() throws Exception {
        Map<String, String> properties = Maps.newHashMap();
        properties.put("a", "b");
        properties.put("hello", "world");
        properties.put("foo", "bar");
        ByteBufferMap byteBufferMap = ByteBufferMap.build(properties);
        Assert.assertEquals("b", byteBufferMap.get("a"));
        Assert.assertEquals("bar", byteBufferMap.get("foo"));
        Assert.assertEquals("world", byteBufferMap.get("hello"));
        Assert.assertNull(byteBufferMap.get("key_not_exists"));
        byteBufferMap = new ByteBufferMap(byteBufferMap.toByteBuffer());
        Assert.assertEquals("b", byteBufferMap.get("a"));
        Assert.assertEquals("bar", byteBufferMap.get("foo"));
        Assert.assertEquals("world", byteBufferMap.get("hello"));
        Assert.assertNull(byteBufferMap.get("key_not_exists"));
        ByteBufferMapTest.MyCallback callback = new ByteBufferMapTest.MyCallback(new String[]{ "a", "foo", "hello" }, new String[]{ "b", "bar", "world" });
        byteBufferMap.enumerate(callback);
        callback.verify(3);
    }

    private static class MyCallback implements KeyValueCallback {
        private final String[] expectedKeys;

        private final String[] expectedValues;

        private int counter;

        private MyCallback(String[] expectedKeys, String[] expectedValues) {
            this.expectedKeys = expectedKeys;
            this.expectedValues = expectedValues;
            this.counter = 0;
        }

        @Override
        public void callback(String key, String value) {
            Assert.assertEquals(expectedKeys[counter], key);
            Assert.assertEquals(expectedValues[counter], value);
            (counter)++;
        }

        public void verify(int expectedCount) {
            Assert.assertEquals(expectedCount, counter);
        }
    }
}

