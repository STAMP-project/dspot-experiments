package net.openhft.chronicle.queue.impl.single;


import java.io.File;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.openhft.chronicle.core.Jvm;
import net.openhft.chronicle.queue.DirectoryUtils;
import net.openhft.chronicle.threads.NamedThreadFactory;
import org.junit.Assert;
import org.junit.Test;


public class PretoucherStressTest extends RollCycleMultiThreadStressTest {
    @Test
    public void stress() {
        final File path = Optional.ofNullable(System.getProperty("stress.test.dir")).map(( s) -> new File(s, UUID.randomUUID().toString())).orElse(DirectoryUtils.tempDir("pretouchStress"));
        System.out.printf("Queue dir: %s at %s%n", path.getAbsolutePath(), Instant.now());
        final ExecutorService executorService = Executors.newFixedThreadPool(3, new NamedThreadFactory("pretouch"));
        final AtomicInteger wrote = new AtomicInteger();
        final int expectedNumberOfMessages = ((int) (((RollCycleMultiThreadStressTest.TEST_TIME) * 1.0E9) / (RollCycleMultiThreadStressTest.SLEEP_PER_WRITE_NANOS)));
        System.out.printf("Writing %d messages with %dns interval%n", expectedNumberOfMessages, RollCycleMultiThreadStressTest.SLEEP_PER_WRITE_NANOS);
        System.out.printf("Should take ~%dsec%n", TimeUnit.NANOSECONDS.toSeconds((expectedNumberOfMessages * (RollCycleMultiThreadStressTest.SLEEP_PER_WRITE_NANOS))));
        final List<Future<Throwable>> results = new ArrayList<>();
        final List<RollCycleMultiThreadStressTest.Reader> readers = new ArrayList<>();
        final List<RollCycleMultiThreadStressTest.Writer> writers = new ArrayList<>();
        final RollCycleMultiThreadStressTest.PretoucherThread pretoucherThread = new RollCycleMultiThreadStressTest.PretoucherThread(path);
        executorService.submit(pretoucherThread);
        {
            final RollCycleMultiThreadStressTest.Reader reader = new RollCycleMultiThreadStressTest.Reader(path, expectedNumberOfMessages);
            readers.add(reader);
            results.add(executorService.submit(reader));
            final RollCycleMultiThreadStressTest.Writer writer = new RollCycleMultiThreadStressTest.Writer(path, wrote, expectedNumberOfMessages);
            writers.add(writer);
            results.add(executorService.submit(writer));
        }
        // TODO: dedupe with super
        final long maxWritingTime = (TimeUnit.SECONDS.toMillis(((RollCycleMultiThreadStressTest.TEST_TIME) + 1))) + (queueBuilder(path).timeoutMS());
        long startTime = System.currentTimeMillis();
        final long giveUpWritingAt = startTime + maxWritingTime;
        long nextRollTime = (System.currentTimeMillis()) + (RollCycleMultiThreadStressTest.ROLL_EVERY_MS);
        long nextCheckTime = (System.currentTimeMillis()) + 5000;
        int i = 0;
        long now;
        while ((now = System.currentTimeMillis()) < giveUpWritingAt) {
            if ((wrote.get()) >= expectedNumberOfMessages)
                break;

            if (now > nextRollTime) {
                timeProvider.advanceMillis(1000);
                nextRollTime += RollCycleMultiThreadStressTest.ROLL_EVERY_MS;
            }
            if (now > nextCheckTime) {
                String readersLastRead = readers.stream().map(( reader) -> Integer.toString(reader.lastRead)).collect(Collectors.joining(","));
                System.out.printf("Writer has written %d of %d messages after %dms. Readers at %s. Waiting...%n", ((wrote.get()) + 1), expectedNumberOfMessages, (i * 10), readersLastRead);
                readers.stream().filter(( r) -> !(r.isMakingProgress())).findAny().ifPresent(( reader) -> {
                    if ((reader.exception) != null) {
                        throw new AssertionError("Reader encountered exception, so stopped reading messages", reader.exception);
                    }
                    throw new AssertionError("Reader is stuck");
                });
                if ((pretoucherThread.exception) != null)
                    throw new AssertionError("Preloader encountered exception", pretoucherThread.exception);

                nextCheckTime = (System.currentTimeMillis()) + 10000L;
            }
            i++;
            Jvm.pause(5);
        } 
        double timeToWriteSecs = ((System.currentTimeMillis()) - startTime) / 1000.0;
        final StringBuilder writerExceptions = new StringBuilder();
        writers.stream().filter(( w) -> (w.exception) != null).forEach(( w) -> {
            writerExceptions.append("Writer failed due to: ").append(w.exception.getMessage()).append("\n");
        });
        Assert.assertTrue(((((("Wrote " + (wrote.get())) + " which is less than ") + expectedNumberOfMessages) + " within timeout. ") + writerExceptions), ((wrote.get()) >= expectedNumberOfMessages));
        readers.stream().filter(( r) -> (r.exception) != null).findAny().ifPresent(( reader) -> {
            throw new AssertionError("Reader encountered exception, so stopped reading messages", reader.exception);
        });
        System.out.println(String.format("All messages written in %,.0fsecs at rate of %,.0f/sec %,.0f/sec per writer (actual writeLatency %,.0fns)", timeToWriteSecs, (expectedNumberOfMessages / timeToWriteSecs), (expectedNumberOfMessages / timeToWriteSecs), (1000000000 / (expectedNumberOfMessages / timeToWriteSecs))));
        final long giveUpReadingAt = (System.currentTimeMillis()) + 60000L;
        final long dumpThreadsAt = giveUpReadingAt - 15000L;
        while ((System.currentTimeMillis()) < giveUpReadingAt) {
            boolean allReadersComplete = RollCycleMultiThreadStressTest.areAllReadersComplete(expectedNumberOfMessages, readers);
            if (allReadersComplete) {
                break;
            }
            if (dumpThreadsAt < (System.currentTimeMillis())) {
                Thread.getAllStackTraces().forEach(( n, st) -> {
                    System.out.println((("\n\n" + n) + "\n\n"));
                    Arrays.stream(st).forEach(System.out::println);
                });
            }
            System.out.printf("Not all readers are complete. Waiting...%n");
            LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(1L));
        } 
        Assert.assertTrue("Readers did not catch up", RollCycleMultiThreadStressTest.areAllReadersComplete(expectedNumberOfMessages, readers));
        executorService.shutdownNow();
        results.forEach(( f) -> {
            try {
                final Throwable exception = f.get();
                if (exception != null) {
                    exception.printStackTrace();
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
        System.out.println("Test complete");
        DirectoryUtils.deleteDir(path);
    }
}

