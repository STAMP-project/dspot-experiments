package org.I0Itec.zkclient;


import java.util.List;
import org.apache.zookeeper.KeeperException;
import org.junit.Assert;
import org.junit.Test;


public abstract class AbstractConnectionTest {
    private final IZkConnection _connection;

    public AbstractConnectionTest(IZkConnection connection) {
        _connection = connection;
    }

    @Test
    public void testGetChildren_OnEmptyFileSystem() throws InterruptedException, KeeperException {
        InMemoryConnection connection = new InMemoryConnection();
        List<String> children = connection.getChildren("/", false);
        Assert.assertEquals(0, children.size());
    }
}

