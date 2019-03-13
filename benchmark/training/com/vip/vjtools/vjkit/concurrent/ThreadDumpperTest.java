package com.vip.vjtools.vjkit.concurrent;


import com.vip.vjtools.test.log.LogbackListAppender;
import com.vip.vjtools.vjkit.concurrent.threadpool.ThreadPoolBuilder;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import org.junit.Test;


public class ThreadDumpperTest {
    public static class LongRunTask implements Runnable {
        private CountDownLatch countDownLatch;

        public LongRunTask(CountDownLatch countDownLatch) {
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            countDownLatch.countDown();
            ThreadUtil.sleep(5, TimeUnit.SECONDS);
        }
    }

    @Test
    public void test() throws InterruptedException {
        ExecutorService executor = ThreadPoolBuilder.fixedPool().setPoolSize(10).build();
        CountDownLatch countDownLatch = Concurrents.countDownLatch(10);
        for (int i = 0; i < 10; i++) {
            executor.execute(new ThreadDumpperTest.LongRunTask(countDownLatch));
        }
        countDownLatch.await();
        ThreadDumpper dumpper = new ThreadDumpper();
        dumpper.tryThreadDump();
        LogbackListAppender appender = new LogbackListAppender();
        appender.addToLogger(ThreadDumpper.class);
        // ??????,???
        dumpper.setLeastInterval(1800);
        dumpper.tryThreadDump();// ????????????????,??????????????

        dumpper.tryThreadDump();
        assertThat(appender.getAllLogs()).hasSize(3);
        executor.shutdownNow();
    }
}

