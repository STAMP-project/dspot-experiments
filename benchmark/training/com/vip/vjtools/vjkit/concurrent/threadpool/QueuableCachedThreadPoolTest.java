package com.vip.vjtools.vjkit.concurrent.threadpool;


import com.vip.vjtools.vjkit.concurrent.ThreadUtil;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import org.junit.Test;


public class QueuableCachedThreadPoolTest {
    public static class LongRunTask implements Runnable {
        @Override
        public void run() {
            ThreadUtil.sleep(5, TimeUnit.SECONDS);
        }
    }

    @Test
    public void test() {
        QueuableCachedThreadPool threadPool = null;
        try {
            threadPool = ThreadPoolBuilder.queuableCachedPool().setMinSize(0).setMaxSize(10).setQueueSize(10).build();
            // ???
            for (int i = 0; i < 10; i++) {
                threadPool.submit(new QueuableCachedThreadPoolTest.LongRunTask());
            }
            assertThat(threadPool.getActiveCount()).isEqualTo(10);
            assertThat(threadPool.getQueue().size()).isEqualTo(0);
            // queue ?
            for (int i = 0; i < 10; i++) {
                threadPool.submit(new QueuableCachedThreadPoolTest.LongRunTask());
            }
            assertThat(threadPool.getActiveCount()).isEqualTo(10);
            assertThat(threadPool.getQueue().size()).isEqualTo(10);
            // ?
            try {
                threadPool.submit(new QueuableCachedThreadPoolTest.LongRunTask());
                fail("should fail before");
            } catch (Throwable t) {
                assertThat(t).isInstanceOf(RejectedExecutionException.class);
            }
        } finally {
            ThreadPoolUtil.gracefulShutdown(threadPool, 1000);
        }
    }
}

