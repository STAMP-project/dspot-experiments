package net.openhft.chronicle.queue.impl.single;


import java.io.File;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.concurrent.TimeUnit;
import net.openhft.chronicle.queue.DirectoryUtils;
import net.openhft.chronicle.queue.RollCycle;
import net.openhft.chronicle.queue.impl.RollingChronicleQueue;
import net.openhft.chronicle.queue.impl.StoreFileListener;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import static RollCycles.DAILY;


public final class QueueEpochTest {
    private static final boolean DEBUG = false;

    private static final long MIDNIGHT_UTC_BASE_TIME = 1504569600000L;

    // 17:15 EDT, 21:15 UTC
    private static final long UTC_OFFSET = (TimeUnit.HOURS.toMillis(21)) + (TimeUnit.MINUTES.toMillis(15));

    private static final long ROLL_TIME = (QueueEpochTest.MIDNIGHT_UTC_BASE_TIME) + (QueueEpochTest.UTC_OFFSET);

    private static final long TEN_MINUTES_BEFORE_ROLL_TIME = (QueueEpochTest.ROLL_TIME) - (TimeUnit.MINUTES.toMillis(10L));

    private static final long FIVE_MINUTES_BEFORE_ROLL_TIME = (QueueEpochTest.ROLL_TIME) - (TimeUnit.MINUTES.toMillis(5L));

    private static final long ONE_SECOND_BEFORE_ROLL_TIME = (QueueEpochTest.ROLL_TIME) - (TimeUnit.SECONDS.toMillis(1L));

    private static final long ONE_SECOND_AFTER_ROLL_TIME = (QueueEpochTest.ROLL_TIME) + (TimeUnit.SECONDS.toMillis(1L));

    private static final long ONE_DAY = TimeUnit.DAYS.toMillis(1L);

    private static final RollCycle DAILY_ROLL = DAILY;

    private long currentTime;

    @Test
    public void shouldRollQueueFilesAccordingToUtcOffset() throws Exception {
        QueueEpochTest.logDebug("UTC offset is %dms%n", QueueEpochTest.UTC_OFFSET);
        final File queueDir = DirectoryUtils.tempDir(QueueEpochTest.class.getSimpleName());
        final QueueEpochTest.CapturingStoreFileListener fileListener = new QueueEpochTest.CapturingStoreFileListener();
        setCurrentTime(QueueEpochTest.MIDNIGHT_UTC_BASE_TIME);
        try (final RollingChronicleQueue queue = // capture file-roll events
        // override the clock used by the queue to detect roll-over
        // epoch is deprecated in favour of rollTime
        ChronicleQueue.singleBuilder(queueDir).rollTime(LocalTime.of(21, 15), ZoneOffset.UTC).timeProvider(this::getCurrentTime).storeFileListener(fileListener).rollCycle(QueueEpochTest.DAILY_ROLL).build()) {
            QueueEpochTest.logDebug("Queue epoch offset is %d%n", queue.epoch());
            final ExcerptAppender appender = queue.acquireAppender();
            final QueueEpochTest.TestEvent eventWriter = appender.methodWriter(QueueEpochTest.TestEvent.class);
            setCurrentTime(QueueEpochTest.TEN_MINUTES_BEFORE_ROLL_TIME);
            eventWriter.setOrGetEvent(Long.toString(QueueEpochTest.TEN_MINUTES_BEFORE_ROLL_TIME));
            setCurrentTime(QueueEpochTest.FIVE_MINUTES_BEFORE_ROLL_TIME);
            eventWriter.setOrGetEvent(Long.toString(QueueEpochTest.FIVE_MINUTES_BEFORE_ROLL_TIME));
            setCurrentTime(QueueEpochTest.ONE_SECOND_BEFORE_ROLL_TIME);
            eventWriter.setOrGetEvent(Long.toString(QueueEpochTest.ONE_SECOND_BEFORE_ROLL_TIME));
            Assert.assertThat(fileListener.numberOfRollEvents(), CoreMatchers.is(0));
            setCurrentTime(QueueEpochTest.ONE_SECOND_AFTER_ROLL_TIME);
            eventWriter.setOrGetEvent(Long.toString(QueueEpochTest.ONE_SECOND_AFTER_ROLL_TIME));
            Assert.assertThat(fileListener.numberOfRollEvents(), CoreMatchers.is(1));
            setCurrentTime(((QueueEpochTest.ONE_SECOND_BEFORE_ROLL_TIME) + (QueueEpochTest.ONE_DAY)));
            eventWriter.setOrGetEvent(Long.toString(((QueueEpochTest.ONE_SECOND_BEFORE_ROLL_TIME) + (QueueEpochTest.ONE_DAY))));
            Assert.assertThat(fileListener.numberOfRollEvents(), CoreMatchers.is(1));
            setCurrentTime(((QueueEpochTest.ONE_SECOND_AFTER_ROLL_TIME) + (QueueEpochTest.ONE_DAY)));
            eventWriter.setOrGetEvent(Long.toString(((QueueEpochTest.ONE_SECOND_AFTER_ROLL_TIME) + (QueueEpochTest.ONE_DAY))));
            Assert.assertThat(fileListener.numberOfRollEvents(), CoreMatchers.is(2));
        }
    }

    @FunctionalInterface
    interface TestEvent {
        void setOrGetEvent(String event);
    }

    private final class CapturingStoreFileListener implements StoreFileListener {
        private int numberOfRollEvents = 0;

        @Override
        public void onAcquired(final int cycle, final File file) {
            logFileAction(cycle, file, "acquired");
        }

        @Override
        public void onReleased(final int cycle, final File file) {
            logFileAction(cycle, file, "released");
            (numberOfRollEvents)++;
        }

        int numberOfRollEvents() {
            return numberOfRollEvents;
        }

        private void logFileAction(final int cycle, final File file, final String action) {
            QueueEpochTest.logDebug("%s file %s for cycle %d at %s%n", action, file.getName(), cycle, Instant.ofEpochMilli(getCurrentTime()));
        }
    }
}

