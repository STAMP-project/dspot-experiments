package net.openhft.chronicle.queue.impl.single;


import java.io.File;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Callable;
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
import net.openhft.chronicle.core.time.SetTimeProvider;
import net.openhft.chronicle.queue.DirectoryUtils;
import net.openhft.chronicle.queue.impl.RollingChronicleQueue;
import net.openhft.chronicle.threads.NamedThreadFactory;
import net.openhft.chronicle.wire.DocumentContext;
import net.openhft.chronicle.wire.ValueIn;
import net.openhft.chronicle.wire.ValueOut;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RollCycleMultiThreadStressTest {
    static final long SLEEP_PER_WRITE_NANOS;

    static final int TEST_TIME;

    static final int ROLL_EVERY_MS;

    static final int DELAY_READER_RANDOM_MS;

    static final int DELAY_WRITER_RANDOM_MS;

    static final int WRITE_ONE_THEN_WAIT_MS;

    static final int CORES;

    static final Random random;

    static final int NUMBER_OF_INTS;

    static final boolean PRETOUCH;

    static final boolean READERS_READ_ONLY;

    static final boolean DUMP_QUEUE;

    static {
        SLEEP_PER_WRITE_NANOS = Long.getLong("writeLatency", 40000);
        TEST_TIME = Integer.getInteger("testTime", 2);
        ROLL_EVERY_MS = Integer.getInteger("rollEvery", 100);
        DELAY_READER_RANDOM_MS = Integer.getInteger("delayReader", 1);
        DELAY_WRITER_RANDOM_MS = Integer.getInteger("delayWriter", 1);
        WRITE_ONE_THEN_WAIT_MS = Integer.getInteger("writeOneThenWait", 0);
        CORES = Integer.getInteger("cores", Runtime.getRuntime().availableProcessors());
        random = new Random(99);
        NUMBER_OF_INTS = Integer.getInteger("numberInts", 18);// 1060 / 4;

        PRETOUCH = Boolean.getBoolean("pretouch");
        READERS_READ_ONLY = Boolean.getBoolean("read_only");
        DUMP_QUEUE = Boolean.getBoolean("dump_queue");
        System.setProperty("org.slf4j.simpleLogger.showDateTime", "true");
        System.setProperty("disableFastForwardHeaderNumber", "true");
        System.setProperty("org.slf4j.simpleLogger.dateTimeFormat", "HH:mm:ss.SSS");
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "WARN");
    }

    final Logger LOG = LoggerFactory.getLogger(getClass());

    final SetTimeProvider timeProvider = new SetTimeProvider();

    @Rule
    public RollCycleMultiThreadStressTest.RepeatRule repeatRule = new RollCycleMultiThreadStressTest.RepeatRule();

    // @Test
    // @RepeatRule.Repeat(times = 10000)
    // @Ignore("run manually")
    // public void repeatStress() throws InterruptedException {
    // stress();
    // }
    @Test
    public void stress() throws InterruptedException {
        final File path = Optional.ofNullable(System.getProperty("stress.test.dir")).map(( s) -> new File(s, UUID.randomUUID().toString())).orElse(DirectoryUtils.tempDir("rollCycleStress"));
        System.out.printf("Queue dir: %s at %s%n", path.getAbsolutePath(), Instant.now());
        final int numThreads = RollCycleMultiThreadStressTest.CORES;
        final int numWriters = (numThreads / 4) + 1;
        final ExecutorService executorServicePretouch = Executors.newSingleThreadExecutor(new NamedThreadFactory("pretouch"));
        final ExecutorService executorServiceWrite = Executors.newFixedThreadPool(numWriters, new NamedThreadFactory("writer"));
        final ExecutorService executorServiceRead = Executors.newFixedThreadPool((numThreads - numWriters), new NamedThreadFactory("reader"));
        final AtomicInteger wrote = new AtomicInteger();
        final int expectedNumberOfMessages = ((int) (((RollCycleMultiThreadStressTest.TEST_TIME) * 1.0E9) / (RollCycleMultiThreadStressTest.SLEEP_PER_WRITE_NANOS)));
        System.out.printf("Running test with %d writers and %d readers, sleep %dns%n", numWriters, (numThreads - numWriters), RollCycleMultiThreadStressTest.SLEEP_PER_WRITE_NANOS);
        System.out.printf("Writing %d messages with %dns interval%n", expectedNumberOfMessages, RollCycleMultiThreadStressTest.SLEEP_PER_WRITE_NANOS);
        System.out.printf("Should take ~%dsec%n", ((TimeUnit.NANOSECONDS.toSeconds((expectedNumberOfMessages * (RollCycleMultiThreadStressTest.SLEEP_PER_WRITE_NANOS)))) / numWriters));
        final List<Future<Throwable>> results = new ArrayList<>();
        final List<RollCycleMultiThreadStressTest.Reader> readers = new ArrayList<>();
        final List<RollCycleMultiThreadStressTest.Writer> writers = new ArrayList<>();
        RollCycleMultiThreadStressTest.PretoucherThread pretoucherThread = null;
        if (RollCycleMultiThreadStressTest.PRETOUCH) {
            pretoucherThread = new RollCycleMultiThreadStressTest.PretoucherThread(path);
            executorServicePretouch.submit(pretoucherThread);
        }
        if ((RollCycleMultiThreadStressTest.WRITE_ONE_THEN_WAIT_MS) > 0) {
            final RollCycleMultiThreadStressTest.Writer tempWriter = new RollCycleMultiThreadStressTest.Writer(path, wrote, expectedNumberOfMessages);
            try (ChronicleQueue queue = tempWriter.queue()) {
                tempWriter.write(queue.acquireAppender());
            }
        }
        for (int i = 0; i < (numThreads - numWriters); i++) {
            final RollCycleMultiThreadStressTest.Reader reader = new RollCycleMultiThreadStressTest.Reader(path, expectedNumberOfMessages);
            readers.add(reader);
            results.add(executorServiceRead.submit(reader));
        }
        if ((RollCycleMultiThreadStressTest.WRITE_ONE_THEN_WAIT_MS) > 0) {
            LOG.warn("Wrote one now waiting for {}ms", RollCycleMultiThreadStressTest.WRITE_ONE_THEN_WAIT_MS);
            Jvm.pause(RollCycleMultiThreadStressTest.WRITE_ONE_THEN_WAIT_MS);
        }
        for (int i = 0; i < numWriters; i++) {
            final RollCycleMultiThreadStressTest.Writer writer = new RollCycleMultiThreadStressTest.Writer(path, wrote, expectedNumberOfMessages);
            writers.add(writer);
            results.add(executorServiceWrite.submit(writer));
        }
        final long maxWritingTime = (TimeUnit.SECONDS.toMillis(((RollCycleMultiThreadStressTest.TEST_TIME) + 5))) + (queueBuilder(path).timeoutMS());
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
                if ((pretoucherThread != null) && ((pretoucherThread.exception) != null))
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
        System.out.println(String.format("All messages written in %,.0fsecs at rate of %,.0f/sec %,.0f/sec per writer (actual writeLatency %,.0fns)", timeToWriteSecs, (expectedNumberOfMessages / timeToWriteSecs), ((expectedNumberOfMessages / timeToWriteSecs) / numWriters), (1000000000 / ((expectedNumberOfMessages / timeToWriteSecs) / numWriters))));
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
        executorServiceRead.shutdown();
        executorServiceWrite.shutdown();
        executorServicePretouch.shutdown();
        if (!(executorServiceRead.awaitTermination(1, TimeUnit.SECONDS)))
            executorServiceRead.shutdownNow();

        if (!(executorServiceWrite.awaitTermination(1, TimeUnit.SECONDS)))
            executorServiceWrite.shutdownNow();

        if (!(executorServicePretouch.awaitTermination(1, TimeUnit.SECONDS)))
            executorServicePretouch.shutdownNow();

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

    public static class RepeatRule implements TestRule {
        @Override
        public Statement apply(Statement statement, Description description) {
            Statement result = statement;
            RollCycleMultiThreadStressTest.RepeatRule.Repeat repeat = description.getAnnotation(RollCycleMultiThreadStressTest.RepeatRule.Repeat.class);
            if (repeat != null) {
                int times = repeat.times();
                result = new RollCycleMultiThreadStressTest.RepeatRule.RepeatStatement(times, statement);
            }
            return result;
        }

        @Retention(RetentionPolicy.RUNTIME)
        @Target({ ElementType.METHOD })
        public @interface Repeat {
            int times();
        }

        private static class RepeatStatement extends Statement {
            private final int times;

            private final Statement statement;

            private RepeatStatement(int times, Statement statement) {
                this.times = times;
                this.statement = statement;
            }

            @Override
            public void evaluate() throws Throwable {
                for (int i = 0; i < (times); i++) {
                    statement.evaluate();
                }
            }
        }
    }

    final class Reader implements Callable<Throwable> {
        final File path;

        final int expectedNumberOfMessages;

        volatile int lastRead = -1;

        volatile Throwable exception;

        int readSequenceAtLastProgressCheck = -1;

        Reader(final File path, final int expectedNumberOfMessages) {
            this.path = path;
            this.expectedNumberOfMessages = expectedNumberOfMessages;
        }

        boolean isMakingProgress() {
            if ((readSequenceAtLastProgressCheck) == (-1)) {
                return true;
            }
            final boolean makingProgress = (lastRead) > (readSequenceAtLastProgressCheck);
            readSequenceAtLastProgressCheck = lastRead;
            return makingProgress;
        }

        @Override
        public Throwable call() {
            SingleChronicleQueueBuilder builder = queueBuilder(path);
            if (RollCycleMultiThreadStressTest.READERS_READ_ONLY)
                builder.readOnly(true);

            try (final RollingChronicleQueue queue = builder.build()) {
                final ExcerptTailer tailer = queue.createTailer();
                int lastTailerCycle = -1;
                int lastQueueCycle = -1;
                Jvm.pause(RollCycleMultiThreadStressTest.random.nextInt(RollCycleMultiThreadStressTest.DELAY_READER_RANDOM_MS));
                while ((lastRead) != ((expectedNumberOfMessages) - 1)) {
                    try (DocumentContext dc = tailer.readingDocument()) {
                        if (dc.isPresent()) {
                            int v = -1;
                            final ValueIn valueIn = dc.wire().getValueIn();
                            final long documentAcquireTimestamp = valueIn.int64();
                            if (documentAcquireTimestamp == 0L) {
                                throw new AssertionError("No timestamp");
                            }
                            for (int i = 0; i < (RollCycleMultiThreadStressTest.NUMBER_OF_INTS); i++) {
                                v = valueIn.int32();
                                if (((lastRead) + 1) != v) {
                                    System.out.println(dc.wire());
                                    String failureMessage = (((((((("Expected: " + ((lastRead) + 1)) + ", actual: ") + v) + ", pos: ") + i) + ", index: ") + (Long.toHexString(dc.index()))) + ", cycle: ") + (tailer.cycle());
                                    if (lastTailerCycle != (-1)) {
                                        failureMessage += (((((((". Tailer cycle at last read: " + lastTailerCycle) + " (current: ") + (tailer.cycle())) + "), queue cycle at last read: ") + lastQueueCycle) + " (current: ") + (queue.cycle())) + ")";
                                    }
                                    if (RollCycleMultiThreadStressTest.DUMP_QUEUE)
                                        DumpQueueMain.dump(queue.file(), System.out, Long.MAX_VALUE);

                                    throw new AssertionError(failureMessage);
                                }
                            }
                            lastRead = v;
                            lastTailerCycle = tailer.cycle();
                            lastQueueCycle = queue.cycle();
                        }
                    }
                } 
            } catch (Throwable e) {
                e.printStackTrace();
                exception = e;
                return e;
            }
            return null;
        }
    }

    final class Writer implements Callable<Throwable> {
        final File path;

        final AtomicInteger wrote;

        final int expectedNumberOfMessages;

        volatile Throwable exception;

        Writer(final File path, final AtomicInteger wrote, final int expectedNumberOfMessages) {
            this.path = path;
            this.wrote = wrote;
            this.expectedNumberOfMessages = expectedNumberOfMessages;
        }

        @Override
        public Throwable call() {
            try (final ChronicleQueue queue = queue()) {
                final ExcerptAppender appender = queue.acquireAppender();
                Jvm.pause(RollCycleMultiThreadStressTest.random.nextInt(RollCycleMultiThreadStressTest.DELAY_WRITER_RANDOM_MS));
                final long startTime = System.nanoTime();
                int loopIteration = 0;
                while (true) {
                    final int value = write(appender);
                    while ((System.nanoTime()) < (startTime + (loopIteration * (RollCycleMultiThreadStressTest.SLEEP_PER_WRITE_NANOS)))) {
                        // spin
                    } 
                    loopIteration++;
                    if (value >= (expectedNumberOfMessages)) {
                        return null;
                    }
                } 
            } catch (Throwable e) {
                exception = e;
                return e;
            }
        }

        private net.openhft.chronicle.queue.ChronicleQueue queue() {
            return queueBuilder(path).build();
        }

        private int write(ExcerptAppender appender) {
            int value;
            try (DocumentContext writingDocument = appender.writingDocument()) {
                final long documentAcquireTimestamp = System.nanoTime();
                value = wrote.getAndIncrement();
                ValueOut valueOut = writingDocument.wire().getValueOut();
                // make the message longer
                valueOut.int64(documentAcquireTimestamp);
                for (int i = 0; i < (RollCycleMultiThreadStressTest.NUMBER_OF_INTS); i++) {
                    valueOut.int32(value);
                }
                writingDocument.wire().padToCacheAlign();
            }
            return value;
        }
    }

    class PretoucherThread implements Callable<Throwable> {
        final File path;

        volatile Throwable exception;

        PretoucherThread(File path) {
            this.path = path;
        }

        @SuppressWarnings("resource")
        @Override
        public Throwable call() throws Exception {
            ChronicleQueue queue0 = null;
            try (ChronicleQueue queue = queueBuilder(path).build()) {
                queue0 = queue;
                ExcerptAppender appender = queue.acquireAppender();
                System.out.println("Starting pretoucher");
                while ((!(Thread.currentThread().isInterrupted())) && (!(queue.isClosed()))) {
                    Jvm.pause(50);
                    appender.pretouch();
                } 
            } catch (Throwable e) {
                if ((queue0 != null) && (queue0.isClosed()))
                    return null;

                exception = e;
                return e;
            }
            return null;
        }
    }
}

