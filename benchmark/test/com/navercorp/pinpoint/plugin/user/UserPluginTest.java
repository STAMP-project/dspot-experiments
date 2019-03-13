package com.navercorp.pinpoint.plugin.user;


import org.junit.Assert;
import org.junit.Test;


public class UserPluginTest {
    @Test
    public void test() {
        UserPlugin plugin = new UserPlugin();
        Assert.assertEquals("org.apache.commons.pool.impl.GenericKeyedObjectPool", plugin.toClassName("org.apache.commons.pool.impl.GenericKeyedObjectPool.borrowObject"));
        Assert.assertEquals("borrowObject", plugin.toMethodName("org.apache.commons.pool.impl.GenericKeyedObjectPool.borrowObject"));
    }
}

