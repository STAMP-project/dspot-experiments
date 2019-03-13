package com.vaadin.tests.server;


import com.vaadin.server.KeyMapper;
import org.junit.Assert;
import org.junit.Test;


public class KeyMapperTest {
    @Test
    public void testAdd() {
        KeyMapper<Object> mapper = createKeyMapper();
        Object o1 = createObject();
        Object o2 = createObject();
        Object o3 = createObject();
        // Create new ids
        String key1 = mapper.key(o1);
        String key2 = mapper.key(o2);
        String key3 = mapper.key(o3);
        Assert.assertSame(mapper.get(key1), o1);
        Assert.assertSame(mapper.get(key2), o2);
        Assert.assertSame(mapper.get(key3), o3);
        Assert.assertNotSame(key1, key2);
        Assert.assertNotSame(key1, key3);
        Assert.assertNotSame(key2, key3);
        assertSize(mapper, 3);
        // Key should not add if there already is a mapping
        Assert.assertEquals(mapper.key(o3), key3);
        assertSize(mapper, 3);
        // Remove -> add should return a new key
        mapper.remove(o1);
        String newkey1 = mapper.key(o1);
        Assert.assertNotSame(key1, newkey1);
    }

    @Test
    public void testRemoveAll() {
        KeyMapper<Object> mapper = createKeyMapper();
        Object o1 = createObject();
        Object o2 = createObject();
        Object o3 = createObject();
        // Create new ids
        mapper.key(o1);
        mapper.key(o2);
        mapper.key(o3);
        assertSize(mapper, 3);
        mapper.removeAll();
        assertSize(mapper, 0);
    }

    @Test
    public void testRemove() {
        KeyMapper<Object> mapper = createKeyMapper();
        Object o1 = createObject();
        Object o2 = createObject();
        Object o3 = createObject();
        // Create new ids
        mapper.key(o1);
        mapper.key(o2);
        mapper.key(o3);
        assertSize(mapper, 3);
        mapper.remove(o1);
        assertSize(mapper, 2);
        mapper.key(o1);
        assertSize(mapper, 3);
        mapper.remove(o1);
        assertSize(mapper, 2);
        mapper.remove(o2);
        mapper.remove(o3);
        assertSize(mapper, 0);
    }
}

