package org.jboss.netty.channel.socket.nio;


import java.util.concurrent.ExecutorService;
import org.jboss.netty.util.ThreadNameDeterminer;
import org.junit.Assert;
import org.junit.Test;


public class NioServerBossPoolTest {
    private static ExecutorService executor;

    private static final String MY_CUSTOM_THREAD_NAME = "FOO";

    private final ThreadNameDeterminer determiner = new ThreadNameDeterminer() {
        public String determineThreadName(String currentThreadName, String proposedThreadName) throws Exception {
            return NioServerBossPoolTest.MY_CUSTOM_THREAD_NAME;
        }
    };

    @Test
    public void testNioServerBossPoolExecutorIntThreadNameDeterminer() throws Exception {
        NioServerBossPool bossPool = null;
        try {
            bossPool = new NioServerBossPool(NioServerBossPoolTest.executor, 1, determiner);
            NioServerBoss nextBoss = bossPool.nextBoss();
            Assert.assertNotNull(nextBoss);
            // Wait for ThreadRenamingRunnable to be run by the executor
            Thread.sleep(1000);
            // Ok, now there should be thread
            Assert.assertNotNull(nextBoss.thread);
            Assert.assertEquals(NioServerBossPoolTest.MY_CUSTOM_THREAD_NAME, nextBoss.thread.getName());
        } finally {
            if (bossPool != null) {
                bossPool.shutdown();
            }
        }
    }
}

