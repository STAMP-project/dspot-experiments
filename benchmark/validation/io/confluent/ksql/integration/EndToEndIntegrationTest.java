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
package io.confluent.ksql.integration;


import ProducerConfig.INTERCEPTOR_CLASSES_CONFIG;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.confluent.ksql.GenericRow;
import io.confluent.ksql.query.QueryId;
import io.confluent.ksql.util.PageViewDataProvider;
import io.confluent.ksql.util.QueryMetadata;
import io.confluent.ksql.util.QueuedQueryMetadata;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.apache.kafka.clients.consumer.ConsumerInterceptor;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.test.IntegrationTest;
import org.apache.kafka.test.TestUtils;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This test emulates the end to end flow in the quick start guide and ensures that the outputs at each stage
 * are what we expect. This tests a broad set of KSQL functionality and is a good catch-all.
 */
@Category({ IntegrationTest.class })
public class EndToEndIntegrationTest {
    private static final Logger log = LoggerFactory.getLogger(EndToEndIntegrationTest.class);

    private static final String PAGE_VIEW_TOPIC = "pageviews";

    private static final String USERS_TOPIC = "users";

    private static final String PAGE_VIEW_STREAM = "pageviews_original";

    private static final String USER_TABLE = "users_original";

    private static final AtomicInteger CONSUMED_COUNT = new AtomicInteger();

    private static final AtomicInteger PRODUCED_COUNT = new AtomicInteger();

    private static final PageViewDataProvider PAGE_VIEW_DATA_PROVIDER = new PageViewDataProvider();

    @ClassRule
    public static final IntegrationTestHarness TEST_HARNESS = IntegrationTestHarness.build();

    @Rule
    public final TestKsqlContext ksqlContext = EndToEndIntegrationTest.TEST_HARNESS.ksqlContextBuilder().withAdditionalConfig(StreamsConfig.producerPrefix(INTERCEPTOR_CLASSES_CONFIG), EndToEndIntegrationTest.DummyProducerInterceptor.class.getName()).withAdditionalConfig(StreamsConfig.consumerPrefix(ConsumerConfig.INTERCEPTOR_CLASSES_CONFIG), EndToEndIntegrationTest.DummyConsumerInterceptor.class.getName()).build();

    @Rule
    public final Timeout timeout = Timeout.seconds(120);

    private QueryMetadata toClose;

    @Test
    public void shouldSelectAllFromUsers() throws Exception {
        final QueuedQueryMetadata queryMetadata = executeQuery("SELECT * from %s;", EndToEndIntegrationTest.USER_TABLE);
        final Set<String> expectedUsers = ImmutableSet.of("USER_0", "USER_1", "USER_2", "USER_3", "USER_4");
        final List<GenericRow> rows = EndToEndIntegrationTest.verifyAvailableRows(queryMetadata, expectedUsers.size());
        final Set<Object> actualUsers = rows.stream().filter(Objects::nonNull).peek(( row) -> assertThat(row.getColumns(), hasSize(6))).map(( row) -> row.getColumns().get(1)).collect(Collectors.toSet());
        Assert.assertThat(EndToEndIntegrationTest.CONSUMED_COUNT.get(), Matchers.greaterThan(0));
        Assert.assertThat(actualUsers, Matchers.is(expectedUsers));
    }

    @Test
    public void shouldSelectFromPageViewsWithSpecificColumn() throws Exception {
        final QueuedQueryMetadata queryMetadata = executeQuery("SELECT pageid from %s;", EndToEndIntegrationTest.PAGE_VIEW_STREAM);
        final List<String> expectedPages = Arrays.asList("PAGE_1", "PAGE_2", "PAGE_3", "PAGE_4", "PAGE_5", "PAGE_5", "PAGE_5");
        final List<GenericRow> rows = EndToEndIntegrationTest.verifyAvailableRows(queryMetadata, expectedPages.size());
        final List<Object> actualPages = rows.stream().filter(Objects::nonNull).peek(( row) -> assertThat(row.getColumns(), hasSize(1))).map(( row) -> row.getColumns().get(0)).collect(Collectors.toList());
        Assert.assertThat(actualPages.subList(0, expectedPages.size()), Matchers.is(expectedPages));
        Assert.assertThat(EndToEndIntegrationTest.CONSUMED_COUNT.get(), Matchers.greaterThan(0));
    }

    @Test
    public void shouldSelectAllFromDerivedStream() throws Exception {
        executeStatement(("CREATE STREAM pageviews_female" + (((" AS SELECT %s.userid AS userid, pageid, regionid, gender " + " FROM %s ") + " LEFT JOIN %s ON %s.userid = %s.userid") + " WHERE gender = 'FEMALE';")), EndToEndIntegrationTest.USER_TABLE, EndToEndIntegrationTest.PAGE_VIEW_STREAM, EndToEndIntegrationTest.USER_TABLE, EndToEndIntegrationTest.PAGE_VIEW_STREAM, EndToEndIntegrationTest.USER_TABLE);
        final QueuedQueryMetadata queryMetadata = executeQuery("SELECT * from pageviews_female;");
        final List<KeyValue<String, GenericRow>> results = new ArrayList<>();
        final BlockingQueue<KeyValue<String, GenericRow>> rowQueue = queryMetadata.getRowQueue();
        // From the mock data, we expect exactly 3 page views from female users.
        final List<String> expectedPages = ImmutableList.of("PAGE_2", "PAGE_5", "PAGE_5");
        final List<String> expectedUsers = ImmutableList.of("USER_2", "USER_0", "USER_2");
        TestUtils.waitForCondition(() -> {
            try {
                log.debug("polling from pageviews_female");
                final KeyValue<String, GenericRow> nextRow = rowQueue.poll(1, TimeUnit.SECONDS);
                if (nextRow != null) {
                    results.add(nextRow);
                } else {
                    // If we didn't receive any records on the output topic for 8 seconds, it probably means that the join
                    // failed because the table data wasn't populated when the stream data was consumed. We should just
                    // re populate the stream data to try the join again.
                    log.warn("repopulating {} because the join returned no results.", PAGE_VIEW_TOPIC);
                    TEST_HARNESS.produceRows(PAGE_VIEW_TOPIC, PAGE_VIEW_DATA_PROVIDER, JSON, System::currentTimeMillis);
                }
            } catch (final  e) {
                throw new <e>RuntimeException("Got exception when polling from pageviews_female");
            }
            return (expectedPages.size()) <= (results.size());
        }, 30000, (("Could not consume any records from " + (EndToEndIntegrationTest.PAGE_VIEW_TOPIC)) + " for 30 seconds"));
        final List<String> actualPages = new ArrayList<>();
        final List<String> actualUsers = new ArrayList<>();
        for (final KeyValue<String, GenericRow> result : results) {
            final List<Object> columns = result.value.getColumns();
            EndToEndIntegrationTest.log.debug("pageview join: {}", columns);
            Assert.assertThat(columns, Matchers.hasSize(6));
            final String user = ((String) (columns.get(2)));
            actualUsers.add(user);
            final String page = ((String) (columns.get(3)));
            actualPages.add(page);
        }
        Assert.assertThat(EndToEndIntegrationTest.CONSUMED_COUNT.get(), Matchers.greaterThan(0));
        Assert.assertThat(EndToEndIntegrationTest.PRODUCED_COUNT.get(), Matchers.greaterThan(0));
        Assert.assertThat(actualPages, Matchers.is(expectedPages));
        Assert.assertThat(actualUsers, Matchers.is(expectedUsers));
    }

    @Test
    public void shouldCreateStreamUsingLikeClause() throws Exception {
        executeStatement(("CREATE STREAM pageviews_like_p5" + ((" WITH (kafka_topic='pageviews_enriched_r0', value_format='DELIMITED')" + " AS SELECT * FROM %s") + " WHERE pageId LIKE '%%_5';")), EndToEndIntegrationTest.PAGE_VIEW_STREAM);
        final QueuedQueryMetadata queryMetadata = executeQuery("SELECT userid, pageid from pageviews_like_p5;");
        final List<Object> columns = EndToEndIntegrationTest.waitForFirstRow(queryMetadata);
        Assert.assertThat(columns.get(1), Matchers.is("PAGE_5"));
    }

    @Test
    public void shouldRetainSelectedColumnsInPartitionBy() throws Exception {
        executeStatement(("CREATE STREAM pageviews_by_viewtime " + (("AS SELECT viewtime, pageid, userid " + "from %s ") + "partition by viewtime;")), EndToEndIntegrationTest.PAGE_VIEW_STREAM);
        final QueuedQueryMetadata queryMetadata = executeQuery("SELECT * from pageviews_by_viewtime;");
        final List<Object> columns = EndToEndIntegrationTest.waitForFirstRow(queryMetadata);
        Assert.assertThat(EndToEndIntegrationTest.CONSUMED_COUNT.get(), Matchers.greaterThan(0));
        Assert.assertThat(EndToEndIntegrationTest.PRODUCED_COUNT.get(), Matchers.greaterThan(0));
        Assert.assertThat(columns.get(3).toString(), Matchers.startsWith("PAGE_"));
        Assert.assertThat(columns.get(4).toString(), Matchers.startsWith("USER_"));
    }

    @Test
    public void shouldSupportDroppingAndRecreatingJoinQuery() throws Exception {
        final String createStreamStatement = String.format(("create stream cart_event_product as " + ("select pv.pageid, u.gender " + "from %s pv left join %s u on pv.userid=u.userid;")), EndToEndIntegrationTest.PAGE_VIEW_STREAM, EndToEndIntegrationTest.USER_TABLE);
        executeStatement(createStreamStatement);
        ksqlContext.terminateQuery(new QueryId("CSAS_CART_EVENT_PRODUCT_0"));
        executeStatement("DROP STREAM CART_EVENT_PRODUCT;");
        executeStatement(createStreamStatement);
        final QueuedQueryMetadata queryMetadata = executeQuery("SELECT * from cart_event_product;");
        final List<Object> columns = EndToEndIntegrationTest.waitForFirstRow(queryMetadata);
        Assert.assertThat(EndToEndIntegrationTest.CONSUMED_COUNT.get(), Matchers.greaterThan(0));
        Assert.assertThat(EndToEndIntegrationTest.PRODUCED_COUNT.get(), Matchers.greaterThan(0));
        Assert.assertThat(columns.get(1).toString(), Matchers.startsWith("USER_"));
        Assert.assertThat(columns.get(2).toString(), Matchers.startsWith("PAGE_"));
        Assert.assertThat(columns.get(3).toString(), Matchers.either(Matchers.is("FEMALE")).or(Matchers.is("MALE")));
    }

    public static class DummyConsumerInterceptor implements ConsumerInterceptor {
        @SuppressFBWarnings("ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD")
        public ConsumerRecords onConsume(final ConsumerRecords consumerRecords) {
            EndToEndIntegrationTest.CONSUMED_COUNT.addAndGet(consumerRecords.count());
            return consumerRecords;
        }

        public void close() {
        }

        public void onCommit(final Map map) {
        }

        public void configure(final Map<String, ?> map) {
        }
    }

    public static class DummyProducerInterceptor implements ProducerInterceptor {
        public void onAcknowledgement(final RecordMetadata rm, final Exception e) {
        }

        @SuppressFBWarnings("ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD")
        public ProducerRecord onSend(final ProducerRecord producerRecord) {
            EndToEndIntegrationTest.PRODUCED_COUNT.incrementAndGet();
            return producerRecord;
        }

        public void close() {
        }

        public void configure(final Map<String, ?> map) {
        }
    }
}

