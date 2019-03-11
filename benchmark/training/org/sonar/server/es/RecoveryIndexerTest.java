/**
 * SonarQube
 * Copyright (C) 2009-2019 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.server.es;


import LoggerLevel.DEBUG;
import com.google.common.collect.ImmutableSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.sonar.api.config.internal.MapSettings;
import org.sonar.api.utils.MessageException;
import org.sonar.api.utils.internal.TestSystem2;
import org.sonar.api.utils.log.LogTester;
import org.sonar.db.DbSession;
import org.sonar.db.DbTester;
import org.sonar.db.es.EsQueueDto;


public class RecoveryIndexerTest {
    private static final long PAST = 1000L;

    private static final IndexType FOO_TYPE = new IndexType("foos", "foo");

    private TestSystem2 system2 = new TestSystem2().setNow(RecoveryIndexerTest.PAST);

    private MapSettings emptySettings = new MapSettings();

    @Rule
    public EsTester es = EsTester.createCustom();

    @Rule
    public DbTester db = DbTester.create(system2);

    @Rule
    public LogTester logTester = new LogTester().setLevel(TRACE);

    @Rule
    public TestRule safeguard = new DisableOnDebug(Timeout.builder().withTimeout(60, TimeUnit.SECONDS).withLookingForStuckThread(true).build());

    private RecoveryIndexer underTest;

    @Test
    public void display_default_configuration_at_startup() {
        underTest = newRecoveryIndexer(emptySettings.asConfig());
        underTest.start();
        underTest.stop();
        assertThat(logTester.logs(DEBUG)).contains("Elasticsearch recovery - sonar.search.recovery.delayInMs=300000", "Elasticsearch recovery - sonar.search.recovery.minAgeInMs=300000");
    }

    @Test
    public void start_triggers_recovery_run_at_fixed_rate() throws Exception {
        MapSettings settings = new MapSettings().setProperty("sonar.search.recovery.initialDelayInMs", "0").setProperty("sonar.search.recovery.delayInMs", "1");
        underTest = Mockito.spy(new RecoveryIndexer(system2, settings.asConfig(), db.getDbClient()));
        AtomicInteger calls = new AtomicInteger(0);
        Mockito.doAnswer(( invocation) -> {
            calls.incrementAndGet();
            return null;
        }).when(underTest).recover();
        underTest.start();
        // wait for 2 runs
        while ((calls.get()) < 2) {
            Thread.sleep(1L);
        } 
    }

    @Test
    public void successfully_recover_indexing_requests() {
        IndexType type1 = new IndexType("foos", "foo");
        EsQueueDto item1a = insertItem(type1, "f1");
        EsQueueDto item1b = insertItem(type1, "f2");
        IndexType type2 = new IndexType("bars", "bar");
        EsQueueDto item2 = insertItem(type2, "b1");
        RecoveryIndexerTest.SuccessfulFakeIndexer indexer1 = new RecoveryIndexerTest.SuccessfulFakeIndexer(type1);
        RecoveryIndexerTest.SuccessfulFakeIndexer indexer2 = new RecoveryIndexerTest.SuccessfulFakeIndexer(type2);
        advanceInTime();
        underTest = newRecoveryIndexer(indexer1, indexer2);
        underTest.recover();
        assertThatQueueHasSize(0);
        assertThatLogsContain(INFO, "Elasticsearch recovery - 3 documents processed [0 failures]");
        assertThat(indexer1.called).hasSize(1);
        assertThat(indexer1.called.get(0)).extracting(EsQueueDto::getUuid).containsExactlyInAnyOrder(item1a.getUuid(), item1b.getUuid());
        assertThatLogsContain(TRACE, "Elasticsearch recovery - processing 2 [foos/foo]");
        assertThat(indexer2.called).hasSize(1);
        assertThat(indexer2.called.get(0)).extracting(EsQueueDto::getUuid).containsExactlyInAnyOrder(item2.getUuid());
        assertThatLogsContain(TRACE, "Elasticsearch recovery - processing 1 [bars/bar]");
    }

    @Test
    public void recent_records_are_not_recovered() {
        EsQueueDto item = insertItem(RecoveryIndexerTest.FOO_TYPE, "f1");
        RecoveryIndexerTest.SuccessfulFakeIndexer indexer = new RecoveryIndexerTest.SuccessfulFakeIndexer(RecoveryIndexerTest.FOO_TYPE);
        // do not advance in time
        underTest = newRecoveryIndexer(indexer);
        underTest.recover();
        assertThatQueueHasSize(1);
        assertThat(indexer.called).isEmpty();
        assertThatLogsDoNotContain(TRACE, "Elasticsearch recovery - processing 2 [foos/foo]");
        assertThatLogsDoNotContain(INFO, "documents processed");
    }

    @Test
    public void do_nothing_if_queue_is_empty() {
        underTest = newRecoveryIndexer();
        underTest.recover();
        assertThatNoLogsFromRecovery(INFO);
        assertThatNoLogsFromRecovery(ERROR);
        assertThatQueueHasSize(0);
    }

    @Test
    public void hard_failures_are_logged_and_do_not_stop_recovery_scheduling() throws Exception {
        insertItem(RecoveryIndexerTest.FOO_TYPE, "f1");
        RecoveryIndexerTest.HardFailingFakeIndexer indexer = new RecoveryIndexerTest.HardFailingFakeIndexer(RecoveryIndexerTest.FOO_TYPE);
        advanceInTime();
        underTest = newRecoveryIndexer(indexer);
        underTest.start();
        // all runs fail, but they are still scheduled
        // -> waiting for 2 runs
        while ((indexer.called.size()) < 2) {
            Thread.sleep(1L);
        } 
        underTest.stop();
        // No rows treated
        assertThatQueueHasSize(1);
        assertThatLogsContain(ERROR, "Elasticsearch recovery - fail to recover documents");
    }

    @Test
    public void soft_failures_are_logged_and_do_not_stop_recovery_scheduling() throws Exception {
        insertItem(RecoveryIndexerTest.FOO_TYPE, "f1");
        RecoveryIndexerTest.SoftFailingFakeIndexer indexer = new RecoveryIndexerTest.SoftFailingFakeIndexer(RecoveryIndexerTest.FOO_TYPE);
        advanceInTime();
        underTest = newRecoveryIndexer(indexer);
        underTest.start();
        // all runs fail, but they are still scheduled
        // -> waiting for 2 runs
        while ((indexer.called.size()) < 2) {
            Thread.sleep(1L);
        } 
        underTest.stop();
        // No rows treated
        assertThatQueueHasSize(1);
        assertThatLogsContain(INFO, "Elasticsearch recovery - 1 documents processed [1 failures]");
    }

    @Test
    public void unsupported_types_are_kept_in_queue_for_manual_fix_operation() {
        insertItem(RecoveryIndexerTest.FOO_TYPE, "f1");
        ResilientIndexer indexer = new RecoveryIndexerTest.SuccessfulFakeIndexer(new IndexType("bars", "bar"));
        advanceInTime();
        underTest = newRecoveryIndexer(indexer);
        underTest.recover();
        assertThatQueueHasSize(1);
        assertThatLogsContain(ERROR, "Elasticsearch recovery - ignore 1 items with unsupported type [foos/foo]");
    }

    @Test
    public void stop_run_if_too_many_failures() {
        IntStream.range(0, 10).forEach(( i) -> insertItem(RecoveryIndexerTest.FOO_TYPE, ("" + i)));
        advanceInTime();
        // 10 docs to process, by groups of 3.
        // The first group successfully recovers only 1 docs --> above 30% of failures --> stop run
        RecoveryIndexerTest.PartiallyFailingIndexer indexer = new RecoveryIndexerTest.PartiallyFailingIndexer(RecoveryIndexerTest.FOO_TYPE, 1);
        MapSettings settings = new MapSettings().setProperty("sonar.search.recovery.loopLimit", "3");
        underTest = newRecoveryIndexer(settings.asConfig(), indexer);
        underTest.recover();
        assertThatLogsContain(ERROR, "Elasticsearch recovery - too many failures [2/3 documents], waiting for next run");
        assertThatQueueHasSize(9);
        // The indexer must have been called once and only once.
        assertThat(indexer.called).hasSize(3);
    }

    @Test
    public void do_not_stop_run_if_success_rate_is_greater_than_circuit_breaker() {
        IntStream.range(0, 10).forEach(( i) -> insertItem(RecoveryIndexerTest.FOO_TYPE, ("" + i)));
        advanceInTime();
        // 10 docs to process, by groups of 5.
        // Each group successfully recovers 4 docs --> below 30% of failures --> continue run
        RecoveryIndexerTest.PartiallyFailingIndexer indexer = new RecoveryIndexerTest.PartiallyFailingIndexer(RecoveryIndexerTest.FOO_TYPE, 4, 4, 2);
        MapSettings settings = new MapSettings().setProperty("sonar.search.recovery.loopLimit", "5");
        underTest = newRecoveryIndexer(settings.asConfig(), indexer);
        underTest.recover();
        assertThatLogsDoNotContain(ERROR, "too many failures");
        assertThatQueueHasSize(0);
        assertThat(indexer.indexed).hasSize(10);
        /* retries */
        assertThat(indexer.called).hasSize((10 + 2));
    }

    @Test
    public void failing_always_on_same_document_does_not_generate_infinite_loop() {
        EsQueueDto buggy = insertItem(RecoveryIndexerTest.FOO_TYPE, "buggy");
        IntStream.range(0, 10).forEach(( i) -> insertItem(RecoveryIndexerTest.FOO_TYPE, ("" + i)));
        advanceInTime();
        RecoveryIndexerTest.FailingAlwaysOnSameElementIndexer indexer = new RecoveryIndexerTest.FailingAlwaysOnSameElementIndexer(RecoveryIndexerTest.FOO_TYPE, buggy);
        underTest = newRecoveryIndexer(indexer);
        underTest.recover();
        assertThatLogsContain(ERROR, "Elasticsearch recovery - too many failures [1/1 documents], waiting for next run");
        assertThatQueueHasSize(1);
    }

    @Test
    public void recover_multiple_times_the_same_document() {
        EsQueueDto item1 = insertItem(RecoveryIndexerTest.FOO_TYPE, "f1");
        EsQueueDto item2 = insertItem(RecoveryIndexerTest.FOO_TYPE, item1.getDocId());
        EsQueueDto item3 = insertItem(RecoveryIndexerTest.FOO_TYPE, item1.getDocId());
        advanceInTime();
        RecoveryIndexerTest.SuccessfulFakeIndexer indexer = new RecoveryIndexerTest.SuccessfulFakeIndexer(RecoveryIndexerTest.FOO_TYPE);
        underTest = newRecoveryIndexer(indexer);
        underTest.recover();
        assertThatQueueHasSize(0);
        assertThat(indexer.called).hasSize(1);
        assertThat(indexer.called.get(0)).extracting(EsQueueDto::getUuid).containsExactlyInAnyOrder(item1.getUuid(), item2.getUuid(), item3.getUuid());
        assertThatLogsContain(TRACE, "Elasticsearch recovery - processing 3 [foos/foo]");
        assertThatLogsContain(INFO, "Elasticsearch recovery - 3 documents processed [0 failures]");
    }

    private class FailingAlwaysOnSameElementIndexer implements ResilientIndexer {
        private final IndexType indexType;

        private final EsQueueDto failing;

        FailingAlwaysOnSameElementIndexer(IndexType indexType, EsQueueDto failing) {
            this.indexType = indexType;
            this.failing = failing;
        }

        @Override
        public IndexingResult index(DbSession dbSession, Collection<EsQueueDto> items) {
            IndexingResult result = new IndexingResult();
            items.forEach(( item) -> {
                result.incrementRequests();
                if (!(item.getUuid().equals(failing.getUuid()))) {
                    result.incrementSuccess();
                    org.sonar.server.es.db.getDbClient().esQueueDao().delete(dbSession, item);
                    dbSession.commit();
                }
            });
            return result;
        }

        @Override
        public void indexOnStartup(Set<IndexType> uninitializedIndexTypes) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Set<IndexType> getIndexTypes() {
            return ImmutableSet.of(indexType);
        }
    }

    private class PartiallyFailingIndexer implements ResilientIndexer {
        private final IndexType indexType;

        private final List<EsQueueDto> called = new ArrayList<>();

        private final List<EsQueueDto> indexed = new ArrayList<>();

        private final Iterator<Integer> successfulReturns;

        PartiallyFailingIndexer(IndexType indexType, int... successfulReturns) {
            this.indexType = indexType;
            this.successfulReturns = IntStream.of(successfulReturns).iterator();
        }

        @Override
        public IndexingResult index(DbSession dbSession, Collection<EsQueueDto> items) {
            called.addAll(items);
            int success = successfulReturns.next();
            IndexingResult result = new IndexingResult();
            items.stream().limit(success).forEach(( i) -> {
                org.sonar.server.es.db.getDbClient().esQueueDao().delete(dbSession, i);
                result.incrementSuccess();
                indexed.add(i);
            });
            IntStream.rangeClosed(1, items.size()).forEach(( i) -> result.incrementRequests());
            dbSession.commit();
            return result;
        }

        @Override
        public void indexOnStartup(Set<IndexType> uninitializedIndexTypes) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Set<IndexType> getIndexTypes() {
            return ImmutableSet.of(indexType);
        }
    }

    private class SuccessfulFakeIndexer implements ResilientIndexer {
        private final Set<IndexType> types;

        private final List<Collection<EsQueueDto>> called = new ArrayList<>();

        private SuccessfulFakeIndexer(IndexType type) {
            this.types = ImmutableSet.of(type);
        }

        @Override
        public void indexOnStartup(Set<IndexType> uninitializedIndexTypes) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Set<IndexType> getIndexTypes() {
            return types;
        }

        @Override
        public IndexingResult index(DbSession dbSession, Collection<EsQueueDto> items) {
            called.add(items);
            IndexingResult result = new IndexingResult();
            items.forEach(( i) -> result.incrementSuccess().incrementRequests());
            db.getDbClient().esQueueDao().delete(dbSession, items);
            dbSession.commit();
            return result;
        }
    }

    private class HardFailingFakeIndexer implements ResilientIndexer {
        private final Set<IndexType> types;

        private final List<Collection<EsQueueDto>> called = new ArrayList<>();

        private HardFailingFakeIndexer(IndexType type) {
            this.types = ImmutableSet.of(type);
        }

        @Override
        public void indexOnStartup(Set<IndexType> uninitializedIndexTypes) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Set<IndexType> getIndexTypes() {
            return types;
        }

        @Override
        public IndexingResult index(DbSession dbSession, Collection<EsQueueDto> items) {
            called.add(items);
            // MessageException is used just to reduce noise in test logs
            throw MessageException.of("BOOM");
        }
    }

    private class SoftFailingFakeIndexer implements ResilientIndexer {
        private final Set<IndexType> types;

        private final List<Collection<EsQueueDto>> called = new ArrayList<>();

        private SoftFailingFakeIndexer(IndexType type) {
            this.types = ImmutableSet.of(type);
        }

        @Override
        public void indexOnStartup(Set<IndexType> uninitializedIndexTypes) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Set<IndexType> getIndexTypes() {
            return types;
        }

        @Override
        public IndexingResult index(DbSession dbSession, Collection<EsQueueDto> items) {
            called.add(items);
            IndexingResult result = new IndexingResult();
            items.forEach(( i) -> result.incrementRequests());
            return result;
        }
    }
}

