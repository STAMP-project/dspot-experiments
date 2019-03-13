/**
 * Copyright 2018 Confluent Inc.
 *
 * Licensed under the Confluent Community License (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 *
 * http://www.confluent.io/confluent-community-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package io.confluent.ksql.metastore;


import com.google.common.collect.ImmutableSet;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.confluent.ksql.function.FunctionRegistry;
import io.confluent.ksql.util.KsqlException;
import io.confluent.ksql.util.KsqlReferentialIntegrityException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_NO_SIDE_EFFECT")
@RunWith(MockitoJUnitRunner.class)
public class MetaStoreImplTest {
    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Mock
    private FunctionRegistry functionRegistry;

    @Mock
    private KsqlTopic topic;

    @Mock
    private StructuredDataSource dataSource;

    @Mock
    private StructuredDataSource dataSource1;

    private MetaStoreImpl metaStore;

    private ExecutorService executor;

    @Test
    public void shouldDeepCopyTopicsOnCopy() {
        // Given:
        metaStore.putTopic(topic);
        // When:
        final MetaStore copy = metaStore.copy();
        metaStore.deleteTopic(topic.getName());
        // Then:
        MatcherAssert.assertThat(copy.getAllKsqlTopics().keySet(), Matchers.contains(topic.getName()));
        MatcherAssert.assertThat(metaStore.getAllKsqlTopics().keySet(), Matchers.is(Matchers.empty()));
    }

    @Test
    public void shouldDeepCopySourcesOnCopy() {
        // Given:
        metaStore.putSource(dataSource);
        // When:
        final MetaStore copy = metaStore.copy();
        metaStore.deleteSource(dataSource.getName());
        // Then:
        MatcherAssert.assertThat(copy.getAllStructuredDataSources().keySet(), Matchers.contains(dataSource.getName()));
        MatcherAssert.assertThat(metaStore.getAllStructuredDataSources().keySet(), Matchers.is(Matchers.empty()));
    }

    @Test
    public void shouldDeepCopySourceReferentialIntegrityDataOnCopy() {
        // Given:
        metaStore.putSource(dataSource);
        metaStore.updateForPersistentQuery("source query", ImmutableSet.of(dataSource.getName()), ImmutableSet.of());
        metaStore.updateForPersistentQuery("sink query", ImmutableSet.of(), ImmutableSet.of(dataSource.getName()));
        // When:
        final MetaStore copy = metaStore.copy();
        metaStore.removePersistentQuery("source query");
        metaStore.removePersistentQuery("sink query");
        // Then:
        MatcherAssert.assertThat(copy.getQueriesWithSource(dataSource.getName()), Matchers.contains("source query"));
        MatcherAssert.assertThat(metaStore.getQueriesWithSource(dataSource.getName()), Matchers.is(Matchers.empty()));
        MatcherAssert.assertThat(copy.getQueriesWithSink(dataSource.getName()), Matchers.contains("sink query"));
        MatcherAssert.assertThat(metaStore.getQueriesWithSink(dataSource.getName()), Matchers.is(Matchers.empty()));
    }

    @Test
    public void shouldNotAllowModificationViaGetAllStructuredDataSources() {
        // Given:
        metaStore.putSource(dataSource);
        final Map<String, StructuredDataSource> dataSources = metaStore.getAllStructuredDataSources();
        // When
        dataSources.keySet().clear();
        // Then:
        MatcherAssert.assertThat(metaStore.getAllStructuredDataSources().keySet(), Matchers.contains(dataSource.getName()));
    }

    @Test
    public void shouldNotAllowModificationViaGetAllKsqlTopics() {
        // Given:
        metaStore.putTopic(topic);
        final Map<String, KsqlTopic> topics = metaStore.getAllKsqlTopics();
        // Expect:
        expectedException.expect(UnsupportedOperationException.class);
        // When
        topics.keySet().clear();
    }

    @Test
    public void shouldThrowOnDuplicateTopic() {
        // Given:
        metaStore.putTopic(topic);
        // Then:
        expectedException.expect(KsqlException.class);
        expectedException.expectMessage("Another topic with the same name already exists");
        // When:
        metaStore.putTopic(topic);
    }

    @Test
    public void shouldThrowOnRemoveUnknownTopic() {
        // Then:
        expectedException.expect(KsqlException.class);
        expectedException.expectMessage("No topic with name bob was registered");
        // When:
        metaStore.deleteTopic("bob");
    }

    @Test
    public void shouldThrowOnDuplicateSource() {
        // Given:
        metaStore.putSource(dataSource);
        // Then:
        expectedException.expect(KsqlException.class);
        expectedException.expectMessage("Another data source with the same name already exists");
        // When:
        metaStore.putSource(dataSource);
    }

    @Test
    public void shouldThrowOnRemoveUnknownSource() {
        // Then:
        expectedException.expect(KsqlException.class);
        expectedException.expectMessage("No data source with name bob exists");
        // When:
        metaStore.deleteSource("bob");
    }

    @Test
    public void shouldThrowOnDropSourceIfUsedAsSourceOfQueries() {
        // Given:
        metaStore.putSource(dataSource);
        metaStore.updateForPersistentQuery("source query", ImmutableSet.of(dataSource.getName()), ImmutableSet.of());
        // Then:
        expectedException.expect(KsqlReferentialIntegrityException.class);
        expectedException.expectMessage("The following queries read from this source: [source query]");
        // When:
        metaStore.deleteSource(dataSource.getName());
    }

    @Test
    public void shouldThrowOnUpdateForPersistentQueryOnUnknownSource() {
        // Then:
        expectedException.expect(KsqlException.class);
        expectedException.expectMessage("Unknown source: unknown");
        // When:
        metaStore.updateForPersistentQuery("source query", ImmutableSet.of("unknown"), ImmutableSet.of());
    }

    @Test
    public void shouldThrowOnUpdateForPersistentQueryOnUnknownSink() {
        // Then:
        expectedException.expect(KsqlException.class);
        expectedException.expectMessage("Unknown source: unknown");
        // When:
        metaStore.updateForPersistentQuery("sink query", ImmutableSet.of(), ImmutableSet.of("unknown"));
    }

    @Test
    public void shouldThrowOnDropSourceIfUsedAsSinkOfQueries() {
        // Given:
        metaStore.putSource(dataSource);
        metaStore.updateForPersistentQuery("sink query", ImmutableSet.of(), ImmutableSet.of(dataSource.getName()));
        // Then:
        expectedException.expect(KsqlReferentialIntegrityException.class);
        expectedException.expectMessage("The following queries write into this source: [sink query]");
        // When:
        metaStore.deleteSource(dataSource.getName());
    }

    @Test
    public void shouldFailToUpdateForPersistentQueryAtomicallyForUnknownSource() {
        // When:
        try {
            metaStore.updateForPersistentQuery("some query", ImmutableSet.of(dataSource.getName(), "unknown"), ImmutableSet.of(dataSource.getName()));
        } catch (final KsqlException e) {
            // Expected
        }
        // Then:
        MatcherAssert.assertThat(metaStore.getQueriesWithSource(dataSource.getName()), Matchers.is(Matchers.empty()));
        MatcherAssert.assertThat(metaStore.getQueriesWithSink(dataSource.getName()), Matchers.is(Matchers.empty()));
    }

    @Test
    public void shouldFailToUpdateForPersistentQueryAtomicallyForUnknownSink() {
        // When:
        try {
            metaStore.updateForPersistentQuery("some query", ImmutableSet.of(dataSource.getName()), ImmutableSet.of(dataSource.getName(), "unknown"));
        } catch (final KsqlException e) {
            // Expected
        }
        // Then:
        MatcherAssert.assertThat(metaStore.getQueriesWithSource(dataSource.getName()), Matchers.is(Matchers.empty()));
        MatcherAssert.assertThat(metaStore.getQueriesWithSink(dataSource.getName()), Matchers.is(Matchers.empty()));
    }

    @Test
    public void shouldDefaultToEmptySetOfQueriesForUnknownSource() {
        MatcherAssert.assertThat(metaStore.getQueriesWithSource("unknown"), Matchers.is(Matchers.empty()));
    }

    @Test
    public void shouldDefaultToEmptySetOfQueriesForUnknownSink() {
        MatcherAssert.assertThat(metaStore.getQueriesWithSink("unknown"), Matchers.is(Matchers.empty()));
    }

    @Test
    public void shouldRegisterQuerySources() {
        // Given:
        metaStore.putSource(dataSource);
        // When:
        metaStore.updateForPersistentQuery("some query", ImmutableSet.of(dataSource.getName()), ImmutableSet.of());
        // Then:
        MatcherAssert.assertThat(metaStore.getQueriesWithSource(dataSource.getName()), Matchers.contains("some query"));
    }

    @Test
    public void shouldRegisterQuerySinks() {
        // Given:
        metaStore.putSource(dataSource);
        // When:
        metaStore.updateForPersistentQuery("some query", ImmutableSet.of(), ImmutableSet.of(dataSource.getName()));
        // Then:
        MatcherAssert.assertThat(metaStore.getQueriesWithSink(dataSource.getName()), Matchers.contains("some query"));
    }

    @Test
    public void shouldBeThreadSafe() {
        IntStream.range(0, 1000).parallel().forEach(( idx) -> {
            final KsqlTopic topic = Mockito.mock(KsqlTopic.class);
            Mockito.when(topic.getName()).thenReturn(("topic" + idx));
            metaStore.putTopic(topic);
            metaStore.getTopic(topic.getName());
            final StructuredDataSource source = Mockito.mock(StructuredDataSource.class);
            Mockito.when(source.getName()).thenReturn(("source" + idx));
            metaStore.putSource(source);
            metaStore.getSource(source.getName());
            metaStore.getAllStructuredDataSources();
            final String queryId = "query" + idx;
            metaStore.updateForPersistentQuery(queryId, ImmutableSet.of(source.getName()), ImmutableSet.of(source.getName()));
            metaStore.getQueriesWithSource(source.getName());
            metaStore.getQueriesWithSink(source.getName());
            metaStore.copy();
            metaStore.removePersistentQuery(queryId);
            metaStore.deleteTopic(topic.getName());
            metaStore.deleteSource(source.getName());
        });
        MatcherAssert.assertThat(metaStore.getAllKsqlTopics().keySet(), Matchers.is(Matchers.empty()));
        MatcherAssert.assertThat(metaStore.getAllStructuredDataSources().keySet(), Matchers.is(Matchers.empty()));
    }

    @Test(timeout = 10000)
    public void shouldBeThreadSafeAroundRefIntegrity() throws Exception {
        // Given:
        final int iterations = 1000;
        final AtomicInteger remaining = new AtomicInteger(iterations);
        final Set<String> sources = ImmutableSet.of(dataSource1.getName(), dataSource.getName());
        metaStore.putSource(dataSource1);
        final Future<?> mainThread = executor.submit(() -> {
            while ((remaining.get()) > 0) {
                metaStore.putSource(dataSource);
                while (true) {
                    try {
                        metaStore.deleteSource(dataSource.getName());
                        break;
                    } catch (final KsqlReferentialIntegrityException e) {
                        // Expected
                    }
                } 
            } 
        });
        // When:
        IntStream.range(0, iterations).parallel().forEach(( idx) -> {
            try {
                final String queryId = "query" + idx;
                while (true) {
                    try {
                        metaStore.updateForPersistentQuery(queryId, sources, sources);
                        break;
                    } catch (final KsqlException e) {
                        // Expected
                    }
                } 
                MatcherAssert.assertThat(metaStore.getQueriesWithSource(dataSource.getName()), Matchers.hasItem(queryId));
                MatcherAssert.assertThat(metaStore.getQueriesWithSink(dataSource.getName()), Matchers.hasItem(queryId));
                metaStore.removePersistentQuery(queryId);
                remaining.decrementAndGet();
            } catch (final Throwable t) {
                remaining.set(0);
                throw t;
            }
        });
        // Then:
        MatcherAssert.assertThat(metaStore.getQueriesWithSource(dataSource1.getName()), Matchers.is(Matchers.empty()));
        MatcherAssert.assertThat(metaStore.getQueriesWithSink(dataSource1.getName()), Matchers.is(Matchers.empty()));
        mainThread.get(1, TimeUnit.MINUTES);
    }
}

