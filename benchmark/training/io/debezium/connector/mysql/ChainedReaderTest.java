/**
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package io.debezium.connector.mysql;


import Clock.SYSTEM;
import ConfigurationDefaults.RETURN_CONTROL_INTERVAL;
import State.RUNNING;
import State.STOPPED;
import State.STOPPING;
import io.debezium.connector.mysql.Reader.State;
import io.debezium.util.Collect;
import io.debezium.util.Threads;
import io.debezium.util.Threads.Timer;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import org.apache.kafka.connect.source.SourceRecord;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Randall Hauch
 */
public class ChainedReaderTest {
    private static final List<SourceRecord> RL1 = Collect.arrayListOf(ChainedReaderTest.record());

    private static final List<SourceRecord> RL2 = Collect.arrayListOf(ChainedReaderTest.record());

    private static final List<SourceRecord> RL3 = Collect.arrayListOf(ChainedReaderTest.record());

    private static final List<SourceRecord> RL4 = Collect.arrayListOf(ChainedReaderTest.record());

    private static final List<SourceRecord> RL5 = Collect.arrayListOf(ChainedReaderTest.record());

    @SuppressWarnings("unchecked")
    private static final List<List<SourceRecord>> SOURCE_RECORDS = Collect.arrayListOf(ChainedReaderTest.RL1, ChainedReaderTest.RL2, ChainedReaderTest.RL3, ChainedReaderTest.RL4, ChainedReaderTest.RL5);

    private ChainedReader reader;

    @Test
    public void shouldNotStartWithoutReaders() throws InterruptedException {
        reader = new ChainedReader.Builder().build();
        assertThat(reader.state()).isEqualTo(STOPPED);
        reader.start();
        assertThat(reader.state()).isEqualTo(STOPPED);
        assertPollReturnsNoMoreRecords();
    }

    @Test
    public void shouldStartAndStopSingleReaderBeforeReaderStopsItself() throws InterruptedException {
        reader = new ChainedReader.Builder().addReader(new ChainedReaderTest.MockReader("r1", ChainedReaderTest.records())).completionMessage("Stopped the r1 reader").build();
        reader.start();
        assertThat(reader.state()).isEqualTo(RUNNING);
        assertThat(reader.poll()).isSameAs(ChainedReaderTest.RL1);
        assertThat(reader.poll()).isSameAs(ChainedReaderTest.RL2);
        assertThat(reader.poll()).isSameAs(ChainedReaderTest.RL3);
        assertThat(reader.poll()).isSameAs(ChainedReaderTest.RL4);
        reader.stop();
        assertThat(reader.state()).isEqualTo(STOPPING);
        assertThat(reader.poll()).isNull();
        assertThat(reader.state()).isEqualTo(STOPPED);
        assertPollReturnsNoMoreRecords();
    }

    @Test
    public void shouldStartSingleReaderThatStopsAutomatically() throws InterruptedException {
        reader = new ChainedReader.Builder().addReader(new ChainedReaderTest.MockReader("r2", ChainedReaderTest.records())).completionMessage("Stopped the r2 reader").build();
        reader.start();
        assertThat(reader.state()).isEqualTo(RUNNING);
        assertThat(reader.poll()).isSameAs(ChainedReaderTest.RL1);
        assertThat(reader.poll()).isSameAs(ChainedReaderTest.RL2);
        assertThat(reader.poll()).isSameAs(ChainedReaderTest.RL3);
        assertThat(reader.poll()).isSameAs(ChainedReaderTest.RL4);
        assertThat(reader.poll()).isSameAs(ChainedReaderTest.RL5);
        assertThat(reader.poll()).isNull();// cause the mock reader to stop itself

        assertThat(reader.state()).isEqualTo(STOPPED);
        assertPollReturnsNoMoreRecords();
    }

    @Test
    public void shouldStartAndStopMultipleReaders() throws InterruptedException {
        reader = new ChainedReader.Builder().addReader(new ChainedReaderTest.MockReader("r3", ChainedReaderTest.records())).addReader(new ChainedReaderTest.MockReader("r4", ChainedReaderTest.records())).completionMessage("Stopped the r3+r4 reader").build();
        reader.start();
        assertThat(reader.state()).isEqualTo(RUNNING);
        assertThat(reader.poll()).isSameAs(ChainedReaderTest.RL1);
        assertThat(reader.poll()).isSameAs(ChainedReaderTest.RL2);
        assertThat(reader.poll()).isSameAs(ChainedReaderTest.RL3);
        assertThat(reader.poll()).isSameAs(ChainedReaderTest.RL4);
        assertThat(reader.poll()).isSameAs(ChainedReaderTest.RL5);
        // Wait for 2nd reader to start
        List<SourceRecord> records = reader.poll();
        final Timer timeout = Threads.timer(SYSTEM, RETURN_CONTROL_INTERVAL);
        while (records == null) {
            if (timeout.expired()) {
                Assert.fail("Subsequent reader has not started");
            }
            Thread.sleep(100);
            records = reader.poll();
        } 
        assertThat(records).isSameAs(ChainedReaderTest.RL1);
        assertThat(reader.poll()).isSameAs(ChainedReaderTest.RL2);
        assertThat(reader.poll()).isSameAs(ChainedReaderTest.RL3);
        assertThat(reader.poll()).isSameAs(ChainedReaderTest.RL4);
        assertThat(reader.poll()).isSameAs(ChainedReaderTest.RL5);
        assertThat(reader.poll()).isNull();// cause the 2nd mock reader to stop itself

        assertThat(reader.state()).isEqualTo(STOPPED);
        assertPollReturnsNoMoreRecords();
    }

    @Test
    public void shouldStartAndStopReaderThatContinuesProducingItsRecordsAfterBeingStopped() throws InterruptedException {
        reader = new ChainedReader.Builder().addReader(new ChainedReaderTest.CompletingMockReader("r5", ChainedReaderTest.records())).completionMessage("Stopped the r5 reader").build();
        reader.start();
        assertThat(reader.state()).isEqualTo(RUNNING);
        assertThat(reader.poll()).isSameAs(ChainedReaderTest.RL1);
        assertThat(reader.poll()).isSameAs(ChainedReaderTest.RL2);
        // Manually stop this reader, and it will continue returning all of its 5 record lists ...
        reader.stop();
        assertThat(reader.state()).isEqualTo(STOPPING);
        // Read the remaining records ...
        assertThat(reader.poll()).isSameAs(ChainedReaderTest.RL3);
        assertThat(reader.poll()).isSameAs(ChainedReaderTest.RL4);
        assertThat(reader.poll()).isSameAs(ChainedReaderTest.RL5);
        assertThat(reader.poll()).isNull();
        // The reader has no more records, so it should now be stopped ...
        assertThat(reader.state()).isEqualTo(STOPPED);
        assertPollReturnsNoMoreRecords();
    }

    @Test
    public void shouldInitAndDestroyResources() {
        ChainedReaderTest.MockReader r1 = new ChainedReaderTest.MockReader("r1", ChainedReaderTest.records());
        ChainedReaderTest.MockReader r2 = new ChainedReaderTest.MockReader("r2", ChainedReaderTest.records());
        reader = new ChainedReader.Builder().addReader(r1).addReader(r2).build();
        reader.initialize();
        assertThat(r1.mockResource).isNotNull();
        assertThat(r2.mockResource).isNotNull();
        reader.destroy();
        assertThat(r1.mockResource).isNull();
        assertThat(r2.mockResource).isNull();
    }

    /**
     * A {@link Reader} that returns records until manually stopped.
     */
    public static class MockReader implements Reader {
        private final String name;

        private final Supplier<List<SourceRecord>> pollResultsSupplier;

        private final AtomicReference<Runnable> completionHandler = new AtomicReference<>();

        private final AtomicBoolean running = new AtomicBoolean();

        private final AtomicBoolean completed = new AtomicBoolean();

        private Object mockResource;

        public MockReader(String name, Supplier<List<SourceRecord>> pollResultsSupplier) {
            this.name = name;
            this.pollResultsSupplier = pollResultsSupplier;
        }

        @Override
        public State state() {
            if (running.get())
                return State.RUNNING;

            if (completed.get())
                return State.STOPPED;

            return State.STOPPING;
        }

        @Override
        public String name() {
            return name;
        }

        @Override
        public List<SourceRecord> poll() throws InterruptedException {
            List<SourceRecord> record = null;
            if (continueReturningRecordsFromPolling()) {
                record = pollResultsSupplier.get();
            }
            if (record == null) {
                // We're done ...
                Runnable handler = this.completionHandler.get();
                if (handler != null) {
                    handler.run();
                }
                completed.set(true);
                running.set(false);
            }
            return record;
        }

        protected boolean continueReturningRecordsFromPolling() {
            return running.get();
        }

        @Override
        public void start() {
            assertThat(running.get()).isFalse();
            running.set(true);
        }

        @Override
        public void stop() {
            running.set(false);
        }

        @Override
        public void uponCompletion(Runnable handler) {
            completionHandler.set(handler);
        }

        @Override
        public void initialize() {
            mockResource = new String();
        }

        @Override
        public void destroy() {
            mockResource = null;
        }
    }

    /**
     * A {@link MockReader} that always returns all records even after this reader is manually stopped.
     */
    public static class CompletingMockReader extends ChainedReaderTest.MockReader {
        public CompletingMockReader(String name, Supplier<List<SourceRecord>> pollResultsSupplier) {
            super(name, pollResultsSupplier);
        }

        @Override
        protected boolean continueReturningRecordsFromPolling() {
            return true;
        }
    }
}

