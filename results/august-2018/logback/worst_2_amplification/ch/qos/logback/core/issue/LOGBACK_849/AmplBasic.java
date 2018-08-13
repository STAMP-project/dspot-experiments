package ch.qos.logback.core.issue.LOGBACK_849;


import ch.qos.logback.core.Context;
import ch.qos.logback.core.ContextBase;
import ch.qos.logback.core.util.ExecutorServiceUtil;
import java.util.concurrent.ExecutorService;
import org.junit.Ignore;
import org.junit.Test;


public class AmplBasic {
    ExecutorService executor = ExecutorServiceUtil.newScheduledExecutorService();

    Context context = new ContextBase();

    @Ignore
    @Test
    public void withOneSlowTask() throws InterruptedException {
        executor.execute(new AmplBasic.InterruptIgnoring(1000));
        Thread.sleep(100);
        ExecutorServiceUtil.shutdown(executor);
    }

    static class InterruptIgnoring implements Runnable {
        int delay;

        InterruptIgnoring(int delay) {
            this.delay = delay;
        }

        public void run() {
            long runUntil = (System.currentTimeMillis()) + (delay);
            while (true) {
                try {
                    long sleep = runUntil - (System.currentTimeMillis());
                    System.out.println(("will sleep " + sleep));
                    if (sleep > 0) {
                        Thread.currentThread().sleep(delay);
                    }else {
                        return;
                    }
                } catch (InterruptedException e) {

                }
            } 
        }
    }
}

