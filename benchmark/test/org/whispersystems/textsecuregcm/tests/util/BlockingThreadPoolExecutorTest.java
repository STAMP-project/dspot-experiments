package org.whispersystems.textsecuregcm.tests.util;


import org.junit.Assert;
import org.junit.Test;
import org.whispersystems.textsecuregcm.util.BlockingThreadPoolExecutor;
import org.whispersystems.textsecuregcm.util.Util;


public class BlockingThreadPoolExecutorTest {
    @Test
    public void testBlocking() {
        BlockingThreadPoolExecutor executor = new BlockingThreadPoolExecutor(1, 3);
        long start = System.currentTimeMillis();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Util.sleep(1000);
            }
        });
        Assert.assertTrue((((System.currentTimeMillis()) - start) < 500));
        start = System.currentTimeMillis();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Util.sleep(1000);
            }
        });
        Assert.assertTrue((((System.currentTimeMillis()) - start) < 500));
        start = System.currentTimeMillis();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Util.sleep(1000);
            }
        });
        Assert.assertTrue((((System.currentTimeMillis()) - start) < 500));
        start = System.currentTimeMillis();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Util.sleep(1000);
            }
        });
        Assert.assertTrue((((System.currentTimeMillis()) - start) > 500));
    }
}

