package net.openhft.chronicle.queue.impl.single;


import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import net.openhft.chronicle.core.OS;
import net.openhft.chronicle.core.time.SystemTimeProvider;
import net.openhft.chronicle.queue.impl.StoreFileListener;
import net.openhft.chronicle.wire.DocumentContext;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;


public final class AppenderFileHandleLeakTest {
    private static final int THREAD_COUNT = (Runtime.getRuntime().availableProcessors()) * 2;

    private static final int MESSAGES_PER_THREAD = 50;

    private static final SystemTimeProvider SYSTEM_TIME_PROVIDER = SystemTimeProvider.INSTANCE;

    private final ExecutorService threadPool = Executors.newFixedThreadPool(AppenderFileHandleLeakTest.THREAD_COUNT);

    private final List<Path> lastFileHandles = new ArrayList<>();

    private AppenderFileHandleLeakTest.TrackingStoreFileListener storeFileListener = new AppenderFileHandleLeakTest.TrackingStoreFileListener();

    private AtomicLong currentTime = new AtomicLong(System.currentTimeMillis());

    private File queuePath;

    @Test
    public void appenderAndTailerResourcesShouldBeCleanedUpByGarbageCollection() throws Exception {
        Assume.assumeThat(OS.isLinux(), CoreMatchers.is(true));
        // this might help the test be more stable when there is multiple tests.
        GcControls.requestGcCycle();
        Thread.sleep(100);
        final List<ExcerptTailer> gcGuard = new LinkedList<>();
        long openFileHandleCount = countFileHandlesOfCurrentProcess();
        List<Path> fileHandlesAtStart = new ArrayList<>(lastFileHandles);
        try (ChronicleQueue queue = createQueue(AppenderFileHandleLeakTest.SYSTEM_TIME_PROVIDER)) {
            final List<Future<Boolean>> futures = new LinkedList<>();
            for (int i = 0; i < (AppenderFileHandleLeakTest.THREAD_COUNT); i++) {
                futures.add(threadPool.submit(() -> {
                    for (int j = 0; j < (AppenderFileHandleLeakTest.MESSAGES_PER_THREAD); j++) {
                        AppenderFileHandleLeakTest.writeMessage(j, queue);
                        AppenderFileHandleLeakTest.readMessage(queue, false, gcGuard::add);
                    }
                    GcControls.requestGcCycle();
                    return Boolean.TRUE;
                }));
            }
            for (Future<Boolean> future : futures) {
                Assert.assertTrue(future.get(1, TimeUnit.MINUTES));
            }
            Assert.assertFalse(gcGuard.isEmpty());
            gcGuard.clear();
        }
        GcControls.waitForGcCycle();
        GcControls.waitForGcCycle();
        waitForFileHandleCountToDrop(openFileHandleCount, fileHandlesAtStart);
    }

    @Test
    public void tailerResourcesCanBeReleasedManually() throws Exception {
        Assume.assumeThat(OS.isLinux(), CoreMatchers.is(true));
        GcControls.requestGcCycle();
        Thread.sleep(100);
        try (ChronicleQueue queue = createQueue(AppenderFileHandleLeakTest.SYSTEM_TIME_PROVIDER)) {
            final long openFileHandleCount = countFileHandlesOfCurrentProcess();
            final List<Path> fileHandlesAtStart = new ArrayList<>(lastFileHandles);
            final List<Future<Boolean>> futures = new LinkedList<>();
            final List<ExcerptTailer> gcGuard = new LinkedList<>();
            for (int i = 0; i < (AppenderFileHandleLeakTest.THREAD_COUNT); i++) {
                futures.add(threadPool.submit(() -> {
                    for (int j = 0; j < (AppenderFileHandleLeakTest.MESSAGES_PER_THREAD); j++) {
                        AppenderFileHandleLeakTest.writeMessage(j, queue);
                        AppenderFileHandleLeakTest.readMessage(queue, true, gcGuard::add);
                    }
                    return Boolean.TRUE;
                }));
            }
            for (Future<Boolean> future : futures) {
                Assert.assertThat(future.get(1, TimeUnit.MINUTES), CoreMatchers.is(true));
            }
            waitForFileHandleCountToDrop(openFileHandleCount, fileHandlesAtStart);
            Assert.assertFalse(gcGuard.isEmpty());
        }
    }

    @Test
    public void tailerShouldReleaseFileHandlesAsQueueRolls() throws Exception {
        Assume.assumeThat(OS.isLinux(), CoreMatchers.is(true));
        System.gc();
        Thread.sleep(100);
        final int messagesPerThread = 10;
        try (ChronicleQueue queue = createQueue(currentTime::get)) {
            final long openFileHandleCount = countFileHandlesOfCurrentProcess();
            final List<Path> fileHandlesAtStart = new ArrayList<>(lastFileHandles);
            final List<Future<Boolean>> futures = new LinkedList<>();
            for (int i = 0; i < (AppenderFileHandleLeakTest.THREAD_COUNT); i++) {
                futures.add(threadPool.submit(() -> {
                    for (int j = 0; j < messagesPerThread; j++) {
                        AppenderFileHandleLeakTest.writeMessage(j, queue);
                        currentTime.addAndGet(500);
                    }
                    return Boolean.TRUE;
                }));
            }
            for (Future<Boolean> future : futures) {
                Assert.assertTrue(future.get(10, TimeUnit.SECONDS));
            }
            waitForFileHandleCountToDrop(openFileHandleCount, fileHandlesAtStart);
            fileHandlesAtStart.clear();
            final long tailerOpenFileHandleCount = countFileHandlesOfCurrentProcess();
            final ExcerptTailer tailer = queue.createTailer();
            tailer.toStart();
            final int expectedMessageCount = (AppenderFileHandleLeakTest.THREAD_COUNT) * messagesPerThread;
            int messageCount = 0;
            storeFileListener.reset();
            int notFoundAttempts = 5;
            while (true) {
                try (final DocumentContext ctx = tailer.readingDocument()) {
                    if (!(ctx.isPresent())) {
                        if ((--notFoundAttempts) > 0)
                            continue;

                        break;
                    }
                    messageCount++;
                }
            } 
            Assert.assertEquals(expectedMessageCount, messageCount);
            Assert.assertThat(storeFileListener.toString(), storeFileListener.releasedCount, CoreMatchers.is(AppenderFileHandleLeakTest.withinDelta(storeFileListener.acquiredCount, 3)));
            waitForFileHandleCountToDrop((tailerOpenFileHandleCount - 1), fileHandlesAtStart);
        }
    }

    private static final class TrackingStoreFileListener implements StoreFileListener {
        private final Map<String, Integer> acquiredCounts = new HashMap<>();

        private final Map<String, Integer> releasedCounts = new HashMap<>();

        private int acquiredCount = 0;

        private int releasedCount = 0;

        @Override
        public void onAcquired(final int cycle, final File file) {
            acquiredCounts.put(file.getName(), ((acquiredCounts.getOrDefault(file.getName(), 0)) + 1));
            (acquiredCount)++;
        }

        @Override
        public void onReleased(final int cycle, final File file) {
            releasedCounts.put(file.getName(), ((releasedCounts.getOrDefault(file.getName(), 0)) + 1));
            (releasedCount)++;
        }

        void reset() {
            acquiredCounts.clear();
            releasedCounts.clear();
            acquiredCount = 0;
            releasedCount = 0;
        }

        @Override
        public String toString() {
            return String.format("%nacquired: %d%nreleased: %d%ndiffs:%n%s%n", acquiredCount, releasedCount, buildDiffs());
        }

        private String buildDiffs() {
            final StringBuilder builder = new StringBuilder();
            builder.append("acquired but not released:\n");
            HashSet<String> keyDiff = new HashSet<>(acquiredCounts.keySet());
            keyDiff.removeAll(releasedCounts.keySet());
            keyDiff.forEach(( k) -> {
                builder.append(k).append("(").append(acquiredCounts.get(k)).append(")\n");
            });
            builder.append("released but not acquired:\n");
            keyDiff.clear();
            keyDiff.addAll(releasedCounts.keySet());
            keyDiff.removeAll(acquiredCounts.keySet());
            keyDiff.forEach(( k) -> {
                builder.append(k).append("(").append(releasedCounts.get(k)).append(")\n");
            });
            return builder.toString();
        }
    }
}

