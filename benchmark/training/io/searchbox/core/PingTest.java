package io.searchbox.core;


import ElasticsearchVersion.UNKNOWN;
import org.junit.Assert;
import org.junit.Test;


public class PingTest {
    @Test
    public void testBasicUriGeneration() {
        Ping ping = new Ping.Builder().build();
        Assert.assertEquals("GET", ping.getRestMethodName());
        Assert.assertNull(ping.getData(null));
        Assert.assertEquals("", ping.getURI(UNKNOWN));
    }
}

