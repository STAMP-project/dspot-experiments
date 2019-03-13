package org.javaee7.jms.send.receive.mdb;


import java.lang.reflect.Method;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Patrik Dudits
 */
public class ReceptionSynchronizerTest {
    @Test
    public void testWaiting() throws InterruptedException, NoSuchMethodException {
        final ReceptionSynchronizer cut = new ReceptionSynchronizer();
        ScheduledExecutorService pool = Executors.newScheduledThreadPool(1);
        final Method method = ReceptionSynchronizerTest.class.getDeclaredMethod("testWaiting");
        long startTime = System.currentTimeMillis();
        pool.schedule(new Runnable() {
            @Override
            public void run() {
                cut.registerInvocation(method);
            }
        }, 1, TimeUnit.SECONDS);
        ReceptionSynchronizer.waitFor(ReceptionSynchronizerTest.class, "testWaiting", 2000);
        long waitTime = (System.currentTimeMillis()) - startTime;
        Assert.assertTrue("Waited more than 950ms", (waitTime > 950));
        Assert.assertTrue("Waited no longer than 1050ms", (waitTime < 1050));
    }
}

