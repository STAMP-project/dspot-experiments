package org.stagemonitor.dispatcher;


import __redirected.org.stagemonitor.dispatcher.Dispatcher;
import org.junit.Assert;
import org.junit.Test;


public class DispatcherTest {
    @Test
    public void testInjectDispatcherToBootstrapClasspath() throws ClassNotFoundException {
        Dispatcher.put("foo", "bar");
        Assert.assertEquals("bar", Dispatcher.get("foo"));
    }
}

