package org.stagemonitor.core.util;


import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import org.junit.Test;
import org.mockito.Mockito;
import org.stagemonitor.core.CorePlugin;


public class ExecutorUtilsTest {
    private final ThreadPoolExecutor lowCapacityPool = ExecutorUtils.createSingleThreadDeamonPool("test-pool", 1, Mockito.mock(CorePlugin.class));

    private Runnable sleepABit = new Runnable() {
        @Override
        public void run() {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

    @Test(expected = RejectedExecutionException.class)
    public void testRejectedExecution() throws Exception {
        for (int i = 0; i < 10; i++) {
            lowCapacityPool.submit(sleepABit);
        }
    }
}

