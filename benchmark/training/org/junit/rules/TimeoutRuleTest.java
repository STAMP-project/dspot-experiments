package org.junit.rules;


import java.io.File;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;


public class TimeoutRuleTest {
    private static final ReentrantLock run1Lock = new ReentrantLock();

    private static volatile boolean run4done = false;

    public abstract static class AbstractTimeoutTest {
        public static final StringBuffer logger = new StringBuffer();

        @Rule
        public final TemporaryFolder tmpFile = new TemporaryFolder();

        @Test
        public void run1() throws InterruptedException {
            TimeoutRuleTest.AbstractTimeoutTest.logger.append("run1");
            TimeoutRuleTest.run1Lock.lockInterruptibly();
            TimeoutRuleTest.run1Lock.unlock();
        }

        @Test
        public void run2() throws InterruptedException {
            TimeoutRuleTest.AbstractTimeoutTest.logger.append("run2");
            Thread.currentThread().join();
        }

        @Test
        public synchronized void run3() throws InterruptedException {
            TimeoutRuleTest.AbstractTimeoutTest.logger.append("run3");
            wait();
        }

        @Test
        public void run4() {
            TimeoutRuleTest.AbstractTimeoutTest.logger.append("run4");
            while (!(TimeoutRuleTest.run4done)) {
            } 
        }

        @Test
        public void run5() throws IOException {
            TimeoutRuleTest.AbstractTimeoutTest.logger.append("run5");
            Random rnd = new Random();
            byte[] data = new byte[1024];
            File tmp = tmpFile.newFile();
            while (true) {
                RandomAccessFile randomAccessFile = new RandomAccessFile(tmp, "rw");
                try {
                    FileChannel channel = randomAccessFile.getChannel();
                    rnd.nextBytes(data);
                    ByteBuffer buffer = ByteBuffer.wrap(data);
                    // Interrupted thread closes channel and throws ClosedByInterruptException.
                    channel.write(buffer);
                } finally {
                    randomAccessFile.close();
                }
                tmp.delete();
            } 
        }

        @Test
        public void run6() throws InterruptedIOException {
            TimeoutRuleTest.AbstractTimeoutTest.logger.append("run6");
            // Java IO throws InterruptedIOException only on SUN machines.
            throw new InterruptedIOException();
        }
    }

    public static class HasGlobalLongTimeout extends TimeoutRuleTest.AbstractTimeoutTest {
        @Rule
        public final TestRule globalTimeout = Timeout.millis(200);
    }

    public static class HasGlobalTimeUnitTimeout extends TimeoutRuleTest.AbstractTimeoutTest {
        @Rule
        public final TestRule globalTimeout = new Timeout(200, TimeUnit.MILLISECONDS);
    }

    public static class HasNullTimeUnit {
        @Rule
        public final TestRule globalTimeout = new Timeout(200, null);

        @Test
        public void wouldPass() {
        }
    }

    @Test
    public void timeUnitTimeout() {
        TimeoutRuleTest.HasGlobalTimeUnitTimeout.logger.setLength(0);
        Result result = JUnitCore.runClasses(TimeoutRuleTest.HasGlobalTimeUnitTimeout.class);
        Assert.assertEquals(6, result.getFailureCount());
        Assert.assertThat(TimeoutRuleTest.HasGlobalTimeUnitTimeout.logger.toString(), CoreMatchers.containsString("run1"));
        Assert.assertThat(TimeoutRuleTest.HasGlobalTimeUnitTimeout.logger.toString(), CoreMatchers.containsString("run2"));
        Assert.assertThat(TimeoutRuleTest.HasGlobalTimeUnitTimeout.logger.toString(), CoreMatchers.containsString("run3"));
        Assert.assertThat(TimeoutRuleTest.HasGlobalTimeUnitTimeout.logger.toString(), CoreMatchers.containsString("run4"));
        Assert.assertThat(TimeoutRuleTest.HasGlobalTimeUnitTimeout.logger.toString(), CoreMatchers.containsString("run5"));
        Assert.assertThat(TimeoutRuleTest.HasGlobalTimeUnitTimeout.logger.toString(), CoreMatchers.containsString("run6"));
    }

    @Test
    public void longTimeout() {
        TimeoutRuleTest.HasGlobalLongTimeout.logger.setLength(0);
        Result result = JUnitCore.runClasses(TimeoutRuleTest.HasGlobalLongTimeout.class);
        Assert.assertEquals(6, result.getFailureCount());
        Assert.assertThat(TimeoutRuleTest.HasGlobalLongTimeout.logger.toString(), CoreMatchers.containsString("run1"));
        Assert.assertThat(TimeoutRuleTest.HasGlobalLongTimeout.logger.toString(), CoreMatchers.containsString("run2"));
        Assert.assertThat(TimeoutRuleTest.HasGlobalLongTimeout.logger.toString(), CoreMatchers.containsString("run3"));
        Assert.assertThat(TimeoutRuleTest.HasGlobalLongTimeout.logger.toString(), CoreMatchers.containsString("run4"));
        Assert.assertThat(TimeoutRuleTest.HasGlobalLongTimeout.logger.toString(), CoreMatchers.containsString("run5"));
        Assert.assertThat(TimeoutRuleTest.HasGlobalLongTimeout.logger.toString(), CoreMatchers.containsString("run6"));
    }

    @Test
    public void nullTimeUnit() {
        Result result = JUnitCore.runClasses(TimeoutRuleTest.HasNullTimeUnit.class);
        Assert.assertEquals(1, result.getFailureCount());
        Failure failure = result.getFailures().get(0);
        Assert.assertThat(failure.getException().getMessage(), CoreMatchers.containsString("Invalid parameters for Timeout"));
        Throwable cause = failure.getException().getCause();
        Assert.assertThat(cause.getMessage(), CoreMatchers.containsString("TimeUnit cannot be null"));
    }
}

