package com.alibaba.csp.sentinel.cluster.server.connection;


import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Eric Zhao
 */
public class ConnectionManagerTest {
    @Test
    public void testAndConnectionAndGetConnectedCount() {
        String namespace = "test-namespace";
        Assert.assertEquals(0, ConnectionManager.getConnectedCount(namespace));
        // Put one connection.
        ConnectionManager.addConnection(namespace, "12.23.34.45:1997");
        Assert.assertEquals(1, ConnectionManager.getConnectedCount(namespace));
        // Put duplicate connection.
        ConnectionManager.addConnection(namespace, "12.23.34.45:1997");
        Assert.assertEquals(1, ConnectionManager.getConnectedCount(namespace));
        // Put another connection.
        ConnectionManager.addConnection(namespace, "12.23.34.49:22123");
        Assert.assertEquals(2, ConnectionManager.getConnectedCount(namespace));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetOrCreateGroupBadNamespace() {
        ConnectionManager.getOrCreateGroup("");
    }

    @Test
    public void testGetOrCreateGroupMultipleThread() throws Exception {
        final String namespace = "test-namespace";
        int threadCount = 32;
        final List<ConnectionGroup> groups = new CopyOnWriteArrayList<>();
        final CountDownLatch latch = new CountDownLatch(threadCount);
        for (int i = 0; i < threadCount; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    groups.add(ConnectionManager.getOrCreateGroup(namespace));
                    latch.countDown();
                }
            }).start();
        }
        latch.await();
        for (int i = 1; i < (groups.size()); i++) {
            Assert.assertSame(groups.get((i - 1)).getNamespace(), groups.get(i).getNamespace());
        }
    }

    @Test
    public void testRemoveConnection() {
        String namespace = "test-namespace-remove";
        String address1 = "12.23.34.45:1997";
        String address2 = "12.23.34.46:1998";
        String address3 = "12.23.34.47:1999";
        ConnectionManager.addConnection(namespace, address1);
        ConnectionManager.addConnection(namespace, address2);
        ConnectionManager.addConnection(namespace, address3);
        Assert.assertEquals(3, ConnectionManager.getConnectedCount(namespace));
        ConnectionManager.removeConnection(namespace, address3);
        Assert.assertEquals(2, ConnectionManager.getConnectedCount(namespace));
        Assert.assertFalse(ConnectionManager.getOrCreateConnectionGroup(namespace).getConnectionSet().contains(new ConnectionDescriptor().setAddress(address3)));
    }

    @Test
    public void testGetOrCreateConnectionGroup() {
        String namespace = "test-namespace";
        Assert.assertNull(ConnectionManager.getConnectionGroup(namespace));
        ConnectionGroup group1 = ConnectionManager.getOrCreateConnectionGroup(namespace);
        Assert.assertNotNull(group1);
        // Put one connection.
        ConnectionManager.addConnection(namespace, "12.23.34.45:1997");
        ConnectionGroup group2 = ConnectionManager.getOrCreateConnectionGroup(namespace);
        Assert.assertSame(group1, group2);
    }
}

