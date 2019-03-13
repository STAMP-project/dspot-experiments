package org.I0Itec.zkclient;


import KeeperState.Disconnected;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import org.I0Itec.zkclient.exception.ZkBadVersionException;
import org.I0Itec.zkclient.exception.ZkInterruptedException;
import org.I0Itec.zkclient.exception.ZkNoNodeException;
import org.I0Itec.zkclient.testutil.ZkTestSystem;
import org.apache.zookeeper.data.Stat;
import org.junit.Assert;
import org.junit.Test;


public class ServerZkClientTest extends AbstractBaseZkClientTest {
    @Test(timeout = 15000)
    public void testRetryUntilConnected() throws Exception {
        AbstractBaseZkClientTest.LOG.info("--- testRetryUntilConnected");
        Gateway gateway = new Gateway(4712, 4711);
        gateway.start();
        final IZkConnection zkConnection = ZkTestSystem.createZkConnection("localhost:4712");
        final ZkClient zkClient = new ZkClient(zkConnection, 5000);
        gateway.stop();
        // start server in 250ms
        new DeferredGatewayStarter(gateway, 250).start();
        // this should work as soon as the connection is reestablished, if it
        // fails it throws a ConnectionLossException
        zkClient.retryUntilConnected(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                zkConnection.exists("/a", false);
                return null;
            }
        });
        zkClient.close();
        gateway.stop();
    }

    @Test(timeout = 15000)
    public void testWaitUntilConnected() throws Exception {
        AbstractBaseZkClientTest.LOG.info("--- testWaitUntilConnected");
        ZkClient _client = ZkTestSystem.createZkClient("localhost:4711");
        _zkServer.close();
        // the _client state should change to KeeperState.Disconnected
        Assert.assertTrue(_client.waitForKeeperState(Disconnected, 1, TimeUnit.SECONDS));
        // connection should not be possible and timeout after 100ms
        Assert.assertFalse(_client.waitUntilConnected(100, TimeUnit.MILLISECONDS));
    }

    /* JLZ - can't emulate

    @Test(timeout = 15000)
    public void testRetryUntilConnected_SessionExpiredException() {
    LOG.info("--- testRetryUntilConnected_SessionExpiredException");

    // Use a tick time of 100ms, because the minimum session timeout is 2 x tick-time.
    // ZkServer zkServer = TestUtil.startZkServer("ZkClientTest-testSessionExpiredException", 4711, 100);
    Gateway gateway = new Gateway(4712, 4711);
    gateway.start();

    // Use a session timeout of 200ms
    final ZkClient zkClient = ZkTestSystem.createZkClient("localhost:4712", 200, 5000);

    gateway.stop();

    // Start server in 600ms, the session should have expired by then
    new DeferredGatewayStarter(gateway, 600).start();

    // This should work as soon as a new session has been created (and the connection is reestablished), if it fails
    // it throws a SessionExpiredException
    zkClient.retryUntilConnected(new Callable<Object>() {

    @Override
    public Object call() throws Exception {
    zkClient.exists("/a");
    return null;
    }
    });

    zkClient.close();
    // zkServer.shutdown();
    gateway.stop();
    }
     */
    /* JLZ - can't emulate

    @Test(timeout = 15000)
    public void testChildListenerAfterSessionExpiredException() throws Exception {
    LOG.info("--- testChildListenerAfterSessionExpiredException");

    int sessionTimeout = 200;
    ZkClient connectedClient = _zkServer.getZkClient();
    connectedClient.createPersistent("/root");

    Gateway gateway = new Gateway(4712, 4711);
    gateway.start();

    final ZkClient disconnectedZkClient = new ZkClient("localhost:4712", sessionTimeout, 5000);
    final Holder<List<String>> children = new Holder<List<String>>();
    disconnectedZkClient.subscribeChildChanges("/root", new IZkChildListener() {

    @Override
    public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
    children.set(currentChilds);
    }
    });

    gateway.stop();

    // The connected client now created a new child node
    connectedClient.createPersistent("/root/node");

    // Wait for 3 x sessionTimeout, the session should have expired by then and start the gateway again
    Thread.sleep(sessionTimeout * 3);
    gateway.start();

    Boolean hasOneChild = TestUtil.waitUntil(true, new Callable<Boolean>() {

    @Override
    public Boolean call() throws Exception {
    return children.get() != null && children.get().size() == 1;
    }
    }, TimeUnit.SECONDS, 5);

    assertTrue(hasOneChild);

    disconnectedZkClient.close();
    gateway.stop();
    }
     */
    @Test(timeout = 10000)
    public void testZkClientConnectedToGatewayClosesQuickly() throws Exception {
        AbstractBaseZkClientTest.LOG.info("--- testZkClientConnectedToGatewayClosesQuickly");
        final Gateway gateway = new Gateway(4712, 4711);
        gateway.start();
        ZkClient zkClient = ZkTestSystem.createZkClient("localhost:4712");
        zkClient.close();
        gateway.stop();
    }

    @Test
    public void testCountChildren() throws InterruptedException {
        Assert.assertEquals(0, _client.countChildren("/a"));
        _client.createPersistent("/a");
        Assert.assertEquals(0, _client.countChildren("/a"));
        _client.createPersistent("/a/b");
        Assert.assertEquals(1, _client.countChildren("/a"));
        // test concurrent access
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    while (!(isInterrupted())) {
                        _client.createPersistent("/test");
                        _client.delete("/test");
                    } 
                } catch (ZkInterruptedException e) {
                    // ignore and finish
                }
            }
        };
        thread.start();
        for (int i = 0; i < 1000; i++) {
            Assert.assertEquals(0, _client.countChildren("/test"));
        }
        thread.interrupt();
        thread.join();
    }

    @Test
    public void testReadDataWithStat() {
        _client.createPersistent("/a", "data");
        Stat stat = new Stat();
        _client.readData("/a", stat);
        Assert.assertEquals(0, stat.getVersion());
        Assert.assertTrue(((stat.getDataLength()) > 0));
    }

    @Test
    public void testWriteDataWithExpectedVersion() {
        _client.createPersistent("/a", "data");
        _client.writeData("/a", "data2", 0);
        try {
            _client.writeData("/a", "data3", 0);
            Assert.fail("expected exception");
        } catch (ZkBadVersionException e) {
            // expected
        }
    }

    @Test
    public void testCreateWithParentDirs() {
        String path = "/a/b";
        try {
            _client.createPersistent(path, false);
            Assert.fail("should throw exception");
        } catch (ZkNoNodeException e) {
            Assert.assertFalse(_client.exists(path));
        }
        _client.createPersistent(path, true);
        Assert.assertTrue(_client.exists(path));
    }

    @Test
    public void testUpdateSerialized() throws InterruptedException {
        _client.createPersistent("/a", 0);
        int numberOfThreads = 2;
        final int numberOfIncrementsPerThread = 100;
        List<Thread> threads = new ArrayList<Thread>();
        for (int i = 0; i < numberOfThreads; i++) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    for (int j = 0; j < numberOfIncrementsPerThread; j++) {
                        _client.updateDataSerialized("/a", new DataUpdater<Integer>() {
                            @Override
                            public Integer update(Integer integer) {
                                return integer + 1;
                            }
                        });
                    }
                }
            };
            thread.start();
            threads.add(thread);
        }
        for (Thread thread : threads) {
            thread.join();
        }
        Integer finalValue = _client.readData("/a");
        Assert.assertEquals((numberOfIncrementsPerThread * numberOfThreads), finalValue.intValue());
    }
}

