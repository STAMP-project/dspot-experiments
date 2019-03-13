package org.I0Itec.zkclient;


import CreateMode.PERSISTENT;
import org.junit.Assert;
import org.junit.Test;


public class MemoryZkClientTest extends AbstractBaseZkClientTest {
    @Test
    public void testGetChildren() throws Exception {
        String path1 = "/a";
        String path2 = "/a/a";
        String path3 = "/a/a/a";
        _client.create(path1, null, PERSISTENT);
        _client.create(path2, null, PERSISTENT);
        _client.create(path3, null, PERSISTENT);
        Assert.assertEquals(1, _client.getChildren(path1).size());
        Assert.assertEquals(1, _client.getChildren(path2).size());
        Assert.assertEquals(0, _client.getChildren(path3).size());
    }
}

