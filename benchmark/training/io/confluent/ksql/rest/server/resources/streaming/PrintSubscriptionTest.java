package io.confluent.ksql.rest.server.resources.streaming;


import com.google.common.collect.Lists;
import com.google.common.util.concurrent.MoreExecutors;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.ksql.rest.server.resources.streaming.PrintPublisher.PrintSubscription;
import java.util.Collection;
import java.util.concurrent.Executors;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.utils.Bytes;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class PrintSubscriptionTest {
    @Mock
    public KafkaConsumer<String, Bytes> kafkaConsumer;

    @Mock
    public SchemaRegistryClient schemaRegistry;

    @Test
    public void testPrintPublisher() {
        // Given:
        StreamingTestUtils.TestSubscriber<Collection<String>> subscriber = new StreamingTestUtils.TestSubscriber<>();
        PrintSubscription subscription = new PrintSubscription(MoreExecutors.listeningDecorator(Executors.newScheduledThreadPool(1)), StreamingTestUtils.printTopic("topic", true, null, null), subscriber, kafkaConsumer, new io.confluent.ksql.rest.server.resources.streaming.TopicStream.RecordFormatter(schemaRegistry, "topic"));
        // When:
        Collection<String> results = subscription.poll();
        // Then:
        MatcherAssert.assertThat(results, contains(Lists.newArrayList(Matchers.containsString("key0 , value0"), Matchers.containsString("key1 , value1"), Matchers.containsString("key2 , value2"))));
    }

    @Test
    public void testPrintPublisherLimit() {
        // Given:
        StreamingTestUtils.TestSubscriber<Collection<String>> subscriber = new StreamingTestUtils.TestSubscriber<>();
        PrintSubscription subscription = new PrintSubscription(MoreExecutors.listeningDecorator(Executors.newScheduledThreadPool(1)), StreamingTestUtils.printTopic("topic", true, null, 2), subscriber, kafkaConsumer, new io.confluent.ksql.rest.server.resources.streaming.TopicStream.RecordFormatter(schemaRegistry, "topic"));
        // When:
        Collection<String> results = subscription.poll();
        Collection<String> results2 = subscription.poll();
        // Then:
        MatcherAssert.assertThat(results, contains(Lists.newArrayList(Matchers.containsString("key0 , value0"), Matchers.containsString("key1 , value1"))));
        MatcherAssert.assertThat(results2, Matchers.empty());
    }

    @Test
    public void testPrintPublisherLimitTwoBatches() {
        // Given:
        StreamingTestUtils.TestSubscriber<Collection<String>> subscriber = new StreamingTestUtils.TestSubscriber<>();
        PrintSubscription subscription = new PrintSubscription(MoreExecutors.listeningDecorator(Executors.newScheduledThreadPool(1)), StreamingTestUtils.printTopic("topic", true, null, 5), subscriber, kafkaConsumer, new io.confluent.ksql.rest.server.resources.streaming.TopicStream.RecordFormatter(schemaRegistry, "topic"));
        // When:
        Collection<String> results = subscription.poll();
        Collection<String> results2 = subscription.poll();
        // Then:
        MatcherAssert.assertThat(results, contains(Lists.newArrayList(Matchers.containsString("key0 , value0"), Matchers.containsString("key1 , value1"), Matchers.containsString("key2 , value2"))));
        MatcherAssert.assertThat(results2, contains(Lists.newArrayList(Matchers.containsString("key3 , value3"), Matchers.containsString("key4 , value4"))));
    }

    @Test
    public void testPrintPublisherIntervalNoLimit() {
        // Given:
        StreamingTestUtils.TestSubscriber<Collection<String>> subscriber = new StreamingTestUtils.TestSubscriber<>();
        PrintSubscription subscription = new PrintSubscription(MoreExecutors.listeningDecorator(Executors.newScheduledThreadPool(1)), StreamingTestUtils.printTopic("topic", true, 2, null), subscriber, kafkaConsumer, new io.confluent.ksql.rest.server.resources.streaming.TopicStream.RecordFormatter(schemaRegistry, "topic"));
        // When:
        Collection<String> results = subscription.poll();
        Collection<String> results2 = subscription.poll();
        // Then:
        MatcherAssert.assertThat(results, contains(Lists.newArrayList(Matchers.containsString("key0 , value0"), Matchers.containsString("key2 , value2"))));
        MatcherAssert.assertThat(results2, contains(Matchers.containsString("key4 , value4")));
    }

    @Test
    public void testPrintPublisherIntervalAndLimit() {
        // Given:
        StreamingTestUtils.TestSubscriber<Collection<String>> subscriber = new StreamingTestUtils.TestSubscriber<>();
        PrintSubscription subscription = new PrintSubscription(MoreExecutors.listeningDecorator(Executors.newScheduledThreadPool(1)), StreamingTestUtils.printTopic("topic", true, 2, 4), subscriber, kafkaConsumer, new io.confluent.ksql.rest.server.resources.streaming.TopicStream.RecordFormatter(schemaRegistry, "topic"));
        // When:
        Collection<String> results = subscription.poll();
        Collection<String> results2 = subscription.poll();
        Collection<String> results3 = subscription.poll();
        Collection<String> results4 = subscription.poll();
        // Then:
        MatcherAssert.assertThat(results, contains(Lists.newArrayList(Matchers.containsString("key0 , value0"), Matchers.containsString("key2 , value2"))));
        MatcherAssert.assertThat(results2, contains(Matchers.containsString("key4 , value4")));
        MatcherAssert.assertThat(results3, contains(Matchers.containsString("key6 , value6")));
        MatcherAssert.assertThat(results4, Matchers.empty());
    }
}

