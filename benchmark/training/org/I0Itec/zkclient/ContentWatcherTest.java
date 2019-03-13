package org.I0Itec.zkclient;


import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import org.I0Itec.zkclient.testutil.ZkTestSystem;
import org.apache.curator.test.TestingServer;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;


public class ContentWatcherTest {
    private static final Logger LOG = Logger.getLogger(ContentWatcherTest.class);

    private static final String FILE_NAME = "/ContentWatcherTest";

    private TestingServer _zkServer;

    private ZkClient _zkClient;

    @Test
    public void testGetContent() throws Exception {
        ContentWatcherTest.LOG.info("--- testGetContent");
        _zkClient.createPersistent(ContentWatcherTest.FILE_NAME, "a");
        final ContentWatcher<String> watcher = new ContentWatcher<String>(_zkClient, ContentWatcherTest.FILE_NAME);
        watcher.start();
        Assert.assertEquals("a", watcher.getContent());
        // update the content
        _zkClient.writeData(ContentWatcherTest.FILE_NAME, "b");
        String contentFromWatcher = TestUtil.waitUntil("b", new Callable<String>() {
            @Override
            public String call() throws Exception {
                return watcher.getContent();
            }
        }, TimeUnit.SECONDS, 5);
        Assert.assertEquals("b", contentFromWatcher);
        watcher.stop();
    }

    @Test
    public void testGetContentWaitTillCreated() throws InterruptedException {
        ContentWatcherTest.LOG.info("--- testGetContentWaitTillCreated");
        final Holder<String> contentHolder = new Holder<String>();
        Thread thread = new Thread() {
            @Override
            public void run() {
                ContentWatcher<String> watcher = new ContentWatcher<String>(_zkClient, ContentWatcherTest.FILE_NAME);
                try {
                    watcher.start();
                    contentHolder.set(watcher.getContent());
                    watcher.stop();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
        // create content after 200ms
        Thread.sleep(200);
        _zkClient.createPersistent(ContentWatcherTest.FILE_NAME, "aaa");
        // we give the thread some time to pick up the change
        thread.join(1000);
        Assert.assertEquals("aaa", contentHolder.get());
    }

    @Test
    public void testHandlingNullContent() throws InterruptedException {
        ContentWatcherTest.LOG.info("--- testHandlingNullContent");
        _zkClient.createPersistent(ContentWatcherTest.FILE_NAME, null);
        ContentWatcher<String> watcher = new ContentWatcher<String>(_zkClient, ContentWatcherTest.FILE_NAME);
        watcher.start();
        Assert.assertEquals(null, watcher.getContent());
        watcher.stop();
    }

    @Test(timeout = 20000)
    public void testHandlingOfConnectionLoss() throws Exception {
        ContentWatcherTest.LOG.info("--- testHandlingOfConnectionLoss");
        final Gateway gateway = new Gateway(4712, 4711);
        gateway.start();
        final ZkClient zkClient = ZkTestSystem.createZkClient("localhost:4712");
        // disconnect
        gateway.stop();
        // reconnect after 250ms and create file with content
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(250);
                    gateway.start();
                    zkClient.createPersistent(ContentWatcherTest.FILE_NAME, "aaa");
                    zkClient.writeData(ContentWatcherTest.FILE_NAME, "b");
                } catch (Exception e) {
                    // ignore
                }
            }
        }.start();
        final ContentWatcher<String> watcher = new ContentWatcher<String>(zkClient, ContentWatcherTest.FILE_NAME);
        watcher.start();
        TestUtil.waitUntil("b", new Callable<String>() {
            @Override
            public String call() throws Exception {
                return watcher.getContent();
            }
        }, TimeUnit.SECONDS, 5);
        Assert.assertEquals("b", watcher.getContent());
        watcher.stop();
        zkClient.close();
        gateway.stop();
    }
}

