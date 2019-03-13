package ch.qos.logback.classic;


import ch.qos.logback.core.contention.AbstractMultiThreadedHarness;
import ch.qos.logback.core.contention.RunnableWithCounterAndDone;
import ch.qos.logback.core.testUtil.StatusChecker;
import java.util.concurrent.CyclicBarrier;
import org.junit.Ignore;
import org.junit.Test;


@Ignore
public class LoggerContextConcurrentResetTest {
    static int CONCURRENT_RESET_THREAD_COUNT = 10;

    // see http://jira.qos.ch/browse/LOGBACK-397
    @Test(timeout = 1000)
    public void concurrentReset() throws InterruptedException {
        LoggerContext loggerContext = new LoggerContext();
        CyclicBarrier cyclicBarrier = new CyclicBarrier(LoggerContextConcurrentResetTest.CONCURRENT_RESET_THREAD_COUNT);
        StatusChecker statusChecker = new StatusChecker(loggerContext);
        int desiredResetCount = 100;
        RunnableWithCounterAndDone[] runnableArray = buildRunnableArray(loggerContext, cyclicBarrier);
        LoggerContextConcurrentResetTest.Harness harness = new LoggerContextConcurrentResetTest.Harness(((LoggerContextConcurrentResetTest.Resetter) (runnableArray[0])), desiredResetCount);
        harness.execute(runnableArray);
        statusChecker.assertIsErrorFree();
    }

    class Harness extends AbstractMultiThreadedHarness {
        int desiredResetCount;

        LoggerContextConcurrentResetTest.Resetter resetter;

        Harness(LoggerContextConcurrentResetTest.Resetter resetter, int desiredResetCount) {
            this.resetter = resetter;
            this.desiredResetCount = desiredResetCount;
        }

        public void waitUntilEndCondition() throws InterruptedException {
            while ((getCounter()) < (desiredResetCount)) {
                Thread.yield();
            } 
        }
    }

    static class GetLoggerRunnable extends RunnableWithCounterAndDone {
        final int burstLength = 30;

        LoggerContext loggerContext;

        CyclicBarrier cyclicBarrier;

        String nameSuffix;

        GetLoggerRunnable(LoggerContext loggerContext, CyclicBarrier cyclicBarrier, String nameSuffix) {
            this.loggerContext = loggerContext;
            this.cyclicBarrier = cyclicBarrier;
            this.nameSuffix = nameSuffix;
        }

        public void run() {
            try {
                cyclicBarrier.await();
            } catch (Exception e) {
            }
            while (!(isDone())) {
                long i = (counter) % (burstLength);
                loggerContext.getLogger(((("org.bla." + (nameSuffix)) + ".x") + i));
                (counter)++;
                if (i == 0) {
                    Thread.yield();
                }
            } 
        }
    }

    static class Resetter extends RunnableWithCounterAndDone {
        LoggerContext loggerContext;

        CyclicBarrier cyclicBarrier;

        public int resetCount = 0;

        Resetter(LoggerContext loggerContext, CyclicBarrier cyclicBarrier) {
            this.loggerContext = loggerContext;
            this.cyclicBarrier = cyclicBarrier;
        }

        public void run() {
            try {
                cyclicBarrier.await();
            } catch (Exception e) {
            }
            while (!(isDone())) {
                loggerContext.reset();
                (counter)++;
                Thread.yield();
            } 
        }
    }
}

