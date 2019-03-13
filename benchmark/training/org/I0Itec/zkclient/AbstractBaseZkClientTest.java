package org.I0Itec.zkclient;


import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.I0Itec.zkclient.testutil.ZkTestSystem;
import org.apache.curator.test.TestingServer;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public abstract class AbstractBaseZkClientTest {
    protected static final Logger LOG = Logger.getLogger(AbstractBaseZkClientTest.class);

    protected TestingServer _zkServer;

    protected ZkClient _client;

    @Test(expected = RuntimeException.class, timeout = 15000)
    public void testUnableToConnect() throws Exception {
        AbstractBaseZkClientTest.LOG.info("--- testUnableToConnect");
        // we are using port 4711 to avoid conflicts with the zk server that is
        // started by the Spring context
        ZkTestSystem.createZkClient("localhost:4712");
    }

    @Test
    public void testWriteAndRead() throws Exception {
        AbstractBaseZkClientTest.LOG.info("--- testWriteAndRead");
        String data = "something";
        String path = "/a";
        _client.createPersistent(path, data);
        String data2 = _client.readData(path);
        Assert.assertEquals(data, data2);
        _client.delete(path);
    }

    @Test
    public void testDelete() throws Exception {
        AbstractBaseZkClientTest.LOG.info("--- testDelete");
        String path = "/a";
        Assert.assertFalse(_client.delete(path));
        _client.createPersistent(path, null);
        Assert.assertTrue(_client.delete(path));
        Assert.assertFalse(_client.delete(path));
    }

    @Test
    public void testDeleteRecursive() throws Exception {
        AbstractBaseZkClientTest.LOG.info("--- testDeleteRecursive");
        // should be able to call this on a not existing directory
        _client.deleteRecursive("/doesNotExist");
    }

    @Test
    public void testWaitUntilExists() {
        AbstractBaseZkClientTest.LOG.info("--- testWaitUntilExists");
        // create /gaga node asynchronously
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(100);
                    _client.createPersistent("/gaga");
                } catch (Exception e) {
                    // ignore
                }
            }
        }.start();
        // wait until this was created
        Assert.assertTrue(_client.waitUntilExists("/gaga", TimeUnit.SECONDS, 5));
        Assert.assertTrue(_client.exists("/gaga"));
        // waiting for /neverCreated should timeout
        Assert.assertFalse(_client.waitUntilExists("/neverCreated", TimeUnit.MILLISECONDS, 100));
    }

    @Test
    public void testDataChanges1() throws Exception {
        AbstractBaseZkClientTest.LOG.info("--- testDataChanges1");
        String path = "/a";
        final Holder<String> holder = new Holder<String>();
        IZkDataListener listener = new IZkDataListener() {
            @Override
            public void handleDataChange(String dataPath, Object data) throws Exception {
                holder.set(((String) (data)));
            }

            @Override
            public void handleDataDeleted(String dataPath) throws Exception {
                holder.set(null);
            }
        };
        _client.subscribeDataChanges(path, listener);
        _client.createPersistent(path, "aaa");
        // wait some time to make sure the event was triggered
        String contentFromHolder = TestUtil.waitUntil("b", new Callable<String>() {
            @Override
            public String call() throws Exception {
                return holder.get();
            }
        }, TimeUnit.SECONDS, 5);
        Assert.assertEquals("aaa", contentFromHolder);
    }

    @Test
    public void testDataChanges2() throws Exception {
        AbstractBaseZkClientTest.LOG.info("--- testDataChanges2");
        String path = "/a";
        final AtomicInteger countChanged = new AtomicInteger(0);
        final AtomicInteger countDeleted = new AtomicInteger(0);
        IZkDataListener listener = new IZkDataListener() {
            @Override
            public void handleDataChange(String dataPath, Object data) throws Exception {
                countChanged.incrementAndGet();
            }

            @Override
            public void handleDataDeleted(String dataPath) throws Exception {
                countDeleted.incrementAndGet();
            }
        };
        _client.subscribeDataChanges(path, listener);
        // create node
        _client.createPersistent(path, "aaa");
        // wait some time to make sure the event was triggered
        TestUtil.waitUntil(1, new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return countChanged.get();
            }
        }, TimeUnit.SECONDS, 5);
        Assert.assertEquals(1, countChanged.get());
        Assert.assertEquals(0, countDeleted.get());
        countChanged.set(0);
        countDeleted.set(0);
        // delete node, this should trigger a delete event
        _client.delete(path);
        // wait some time to make sure the event was triggered
        TestUtil.waitUntil(1, new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return countDeleted.get();
            }
        }, TimeUnit.SECONDS, 5);
        Assert.assertEquals(0, countChanged.get());
        Assert.assertEquals(1, countDeleted.get());
        // test if watch was reinstalled after the file got deleted
        countChanged.set(0);
        _client.createPersistent(path, "aaa");
        // wait some time to make sure the event was triggered
        TestUtil.waitUntil(1, new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return countChanged.get();
            }
        }, TimeUnit.SECONDS, 5);
        Assert.assertEquals(1, countChanged.get());
        // test if changing the contents notifies the listener
        _client.writeData(path, "bbb");
        // wait some time to make sure the event was triggered
        TestUtil.waitUntil(2, new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return countChanged.get();
            }
        }, TimeUnit.SECONDS, 5);
        Assert.assertEquals(2, countChanged.get());
    }

    @Test(timeout = 15000)
    public void testHandleChildChanges() throws Exception {
        AbstractBaseZkClientTest.LOG.info("--- testHandleChildChanges");
        String path = "/a";
        final AtomicInteger count = new AtomicInteger(0);
        final Holder<List<String>> children = new Holder<List<String>>();
        IZkChildListener listener = new IZkChildListener() {
            @Override
            public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
                count.incrementAndGet();
                children.set(currentChilds);
            }
        };
        _client.subscribeChildChanges(path, listener);
        // ----
        // Create the root node should throw the first child change event
        // ----
        _client.createPersistent(path);
        // wait some time to make sure the event was triggered
        TestUtil.waitUntil(1, new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return count.get();
            }
        }, TimeUnit.SECONDS, 5);
        Assert.assertEquals(1, count.get());
        Assert.assertEquals(0, children.get().size());
        // ----
        // Creating a child node should throw another event
        // ----
        count.set(0);
        _client.createPersistent((path + "/child1"));
        // wait some time to make sure the event was triggered
        TestUtil.waitUntil(1, new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return count.get();
            }
        }, TimeUnit.SECONDS, 5);
        Assert.assertEquals(1, count.get());
        Assert.assertEquals(1, children.get().size());
        Assert.assertEquals("child1", children.get().get(0));
        // ----
        // Creating another child and deleting the node should also throw an event
        // ----
        count.set(0);
        _client.createPersistent((path + "/child2"));
        _client.deleteRecursive(path);
        // wait some time to make sure the event was triggered
        Boolean eventReceived = TestUtil.waitUntil(true, new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return ((count.get()) > 0) && ((children.get()) == null);
            }
        }, TimeUnit.SECONDS, 5);
        Assert.assertTrue(eventReceived);
        Assert.assertNull(children.get());
        // ----
        // Creating root again should throw an event
        // ----
        count.set(0);
        _client.createPersistent(path);
        // wait some time to make sure the event was triggered
        eventReceived = TestUtil.waitUntil(true, new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return ((count.get()) > 0) && ((children.get()) != null);
            }
        }, TimeUnit.SECONDS, 5);
        Assert.assertTrue(eventReceived);
        Assert.assertEquals(0, children.get().size());
        // ----
        // Creating child now should throw an event
        // ----
        count.set(0);
        _client.createPersistent((path + "/child"));
        // wait some time to make sure the event was triggered
        eventReceived = TestUtil.waitUntil(true, new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return (count.get()) > 0;
            }
        }, TimeUnit.SECONDS, 5);
        Assert.assertTrue(eventReceived);
        Assert.assertEquals(1, children.get().size());
        Assert.assertEquals("child", children.get().get(0));
        // ----
        // Deleting root node should throw an event
        // ----
        count.set(0);
        _client.deleteRecursive(path);
        // wait some time to make sure the event was triggered
        eventReceived = TestUtil.waitUntil(true, new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return ((count.get()) > 0) && ((children.get()) == null);
            }
        }, TimeUnit.SECONDS, 5);
        Assert.assertTrue(eventReceived);
        Assert.assertNull(children.get());
    }

    @Test
    public void testGetCreationTime() throws Exception {
        long start = System.currentTimeMillis();
        Thread.sleep(100);
        String path = "/a";
        _client.createPersistent(path);
        Thread.sleep(100);
        long end = System.currentTimeMillis();
        long creationTime = _client.getCreationTime(path);
        Assert.assertTrue(((start < creationTime) && (end > creationTime)));
    }

    @Test
    public void testNumberOfListeners() {
        IZkChildListener zkChildListener = Mockito.mock(IZkChildListener.class);
        _client.subscribeChildChanges("/", zkChildListener);
        Assert.assertEquals(1, _client.numberOfListeners());
        IZkDataListener zkDataListener = Mockito.mock(IZkDataListener.class);
        _client.subscribeDataChanges("/a", zkDataListener);
        Assert.assertEquals(2, _client.numberOfListeners());
        _client.subscribeDataChanges("/b", zkDataListener);
        Assert.assertEquals(3, _client.numberOfListeners());
        IZkStateListener zkStateListener = Mockito.mock(IZkStateListener.class);
        _client.subscribeStateChanges(zkStateListener);
        Assert.assertEquals(4, _client.numberOfListeners());
        _client.unsubscribeChildChanges("/", zkChildListener);
        Assert.assertEquals(3, _client.numberOfListeners());
        _client.unsubscribeDataChanges("/b", zkDataListener);
        Assert.assertEquals(2, _client.numberOfListeners());
        _client.unsubscribeDataChanges("/a", zkDataListener);
        Assert.assertEquals(1, _client.numberOfListeners());
        _client.unsubscribeStateChanges(zkStateListener);
        Assert.assertEquals(0, _client.numberOfListeners());
    }
}

