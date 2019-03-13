package com.alicp.jetcache.autoconfigure;


import org.junit.Assert;
import org.junit.Test;


public class ConfigTreeTest {
    private ConfigTree configTree;

    @Test
    public void testSubTree() {
        Assert.assertEquals(8, configTree.getProperties().size());
        Assert.assertEquals(3, configTree.subTree("remote.default").getProperties().size());
        Assert.assertEquals(5, configTree.subTree("remote.A1.").getProperties().size());
        Assert.assertEquals(1, configTree.subTree("remote.default.uri").getProperties().size());
        Assert.assertEquals(3, configTree.subTree("remote.A1.uri").getProperties().size());
    }

    @Test
    public void testContainsProperty() {
        Assert.assertTrue(configTree.containsProperty("remote.default.type"));
        Assert.assertTrue(configTree.containsProperty("remote.default.uri"));
    }

    @Test
    public void testGetProperty() {
        Assert.assertEquals("redis://127.0.0.1:6379/", configTree.getProperty("remote.default.uri"));
    }
}

