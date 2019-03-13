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
package io.confluent.ksql.rest.server.resources.streaming;


import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import java.io.OutputStream;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.utils.Bytes;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class TopicStreamWriterTest {
    @Mock
    public KafkaConsumer<String, Bytes> kafkaConsumer;

    @Mock
    public SchemaRegistryClient schemaRegistry;

    private TopicStreamWriterTest.ValidatingOutputStream out;

    @Test
    public void testIntervalOneAndLimitTwo() {
        // Given:
        final TopicStreamWriter writer = new TopicStreamWriter(schemaRegistry, kafkaConsumer, "topic", 1, Duration.ZERO, OptionalInt.of(2));
        // When:
        writer.write(out);
        // Then:
        final List<String> expected = ImmutableList.of("Format:STRING", "key0 , value0", "key1 , value1");
        out.assertWrites(expected);
    }

    @Test
    public void testIntervalTwoAndLimitTwo() {
        // Given:
        final TopicStreamWriter writer = new TopicStreamWriter(schemaRegistry, kafkaConsumer, "topic", 2, Duration.ZERO, OptionalInt.of(2));
        // When:
        TopicStreamWriterTest.ValidatingOutputStream out = new TopicStreamWriterTest.ValidatingOutputStream();
        writer.write(out);
        // Then:
        final List<String> expected = ImmutableList.of("Format:STRING", "key0 , value0", "key2 , value2");
        out.assertWrites(expected);
    }

    private static class ValidatingOutputStream extends OutputStream {
        private final List<byte[]> recordedWrites;

        ValidatingOutputStream() {
            this.recordedWrites = new ArrayList<>();
        }

        @Override
        public void write(final int b) {
            /* not called */
        }

        @Override
        public void write(final byte[] b) {
            recordedWrites.add(b);
        }

        void assertWrites(final List<String> expected) {
            MatcherAssert.assertThat(recordedWrites, Matchers.hasSize(expected.size()));
            for (int i = 0; i < (recordedWrites.size()); i++) {
                final byte[] bytes = recordedWrites.get(i);
                MatcherAssert.assertThat(new String(bytes, Charsets.UTF_8), Matchers.containsString(expected.get(i)));
            }
        }
    }
}

