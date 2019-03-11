/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.streams.processor.internals;


import StreamsConfig.ROCKSDB_CONFIG_SETTER_CLASS_CONFIG;
import java.time.Duration;
import java.util.Properties;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.metrics.Metrics;
import org.apache.kafka.common.utils.LogContext;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.processor.Cancellable;
import org.apache.kafka.streams.processor.PunctuationType;
import org.apache.kafka.streams.processor.Punctuator;
import org.apache.kafka.streams.processor.StateStore;
import org.apache.kafka.streams.processor.TaskId;
import org.apache.kafka.streams.processor.To;
import org.apache.kafka.streams.state.RocksDBConfigSetter;
import org.apache.kafka.test.MockKeyValueStore;
import org.apache.kafka.test.StreamsTestUtils;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;

import static AbstractProcessorContext.NONEXIST_TOPIC;


public class AbstractProcessorContextTest {
    private final MockStreamsMetrics metrics = new MockStreamsMetrics(new Metrics());

    private final AbstractProcessorContext context = new AbstractProcessorContextTest.TestProcessorContext(metrics);

    private final MockKeyValueStore stateStore = new MockKeyValueStore("store", false);

    private final Headers headers = new org.apache.kafka.common.header.internals.RecordHeaders(new Header[]{ new RecordHeader("key", "value".getBytes()) });

    private final ProcessorRecordContext recordContext = new ProcessorRecordContext(10, System.currentTimeMillis(), 1, "foo", headers);

    @Test
    public void shouldThrowIllegalStateExceptionOnRegisterWhenContextIsInitialized() {
        context.initialize();
        try {
            context.register(stateStore, null);
            Assert.fail("should throw illegal state exception when context already initialized");
        } catch (final IllegalStateException e) {
            // pass
        }
    }

    @Test
    public void shouldNotThrowIllegalStateExceptionOnRegisterWhenContextIsNotInitialized() {
        context.register(stateStore, null);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerOnRegisterIfStateStoreIsNull() {
        context.register(null, null);
    }

    @Test
    public void shouldThrowIllegalStateExceptionOnTopicIfNoRecordContext() {
        context.setRecordContext(null);
        try {
            context.topic();
            Assert.fail("should throw illegal state exception when record context is null");
        } catch (final IllegalStateException e) {
            // pass
        }
    }

    @Test
    public void shouldReturnTopicFromRecordContext() {
        MatcherAssert.assertThat(context.topic(), CoreMatchers.equalTo(recordContext.topic()));
    }

    @Test
    public void shouldReturnNullIfTopicEqualsNonExistTopic() {
        context.setRecordContext(new ProcessorRecordContext(0, 0, 0, NONEXIST_TOPIC));
        MatcherAssert.assertThat(context.topic(), CoreMatchers.nullValue());
    }

    @Test
    public void shouldThrowIllegalStateExceptionOnPartitionIfNoRecordContext() {
        context.setRecordContext(null);
        try {
            context.partition();
            Assert.fail("should throw illegal state exception when record context is null");
        } catch (final IllegalStateException e) {
            // pass
        }
    }

    @Test
    public void shouldReturnPartitionFromRecordContext() {
        MatcherAssert.assertThat(context.partition(), CoreMatchers.equalTo(recordContext.partition()));
    }

    @Test
    public void shouldThrowIllegalStateExceptionOnOffsetIfNoRecordContext() {
        context.setRecordContext(null);
        try {
            context.offset();
        } catch (final IllegalStateException e) {
            // pass
        }
    }

    @Test
    public void shouldReturnOffsetFromRecordContext() {
        MatcherAssert.assertThat(context.offset(), CoreMatchers.equalTo(recordContext.offset()));
    }

    @Test
    public void shouldThrowIllegalStateExceptionOnTimestampIfNoRecordContext() {
        context.setRecordContext(null);
        try {
            context.timestamp();
            Assert.fail("should throw illegal state exception when record context is null");
        } catch (final IllegalStateException e) {
            // pass
        }
    }

    @Test
    public void shouldReturnTimestampFromRecordContext() {
        MatcherAssert.assertThat(context.timestamp(), CoreMatchers.equalTo(recordContext.timestamp()));
    }

    @Test
    public void shouldReturnHeadersFromRecordContext() {
        MatcherAssert.assertThat(context.headers(), CoreMatchers.equalTo(recordContext.headers()));
    }

    @Test
    public void shouldReturnNullIfHeadersAreNotSet() {
        context.setRecordContext(new ProcessorRecordContext(0, 0, 0, NONEXIST_TOPIC));
        MatcherAssert.assertThat(context.headers(), CoreMatchers.nullValue());
    }

    @Test
    public void shouldThrowIllegalStateExceptionOnHeadersIfNoRecordContext() {
        context.setRecordContext(null);
        try {
            context.headers();
        } catch (final IllegalStateException e) {
            // pass
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void appConfigsShouldReturnParsedValues() {
        MatcherAssert.assertThat(((Class<RocksDBConfigSetter>) (context.appConfigs().get(ROCKSDB_CONFIG_SETTER_CLASS_CONFIG))), CoreMatchers.equalTo(RocksDBConfigSetter.class));
    }

    @Test
    public void appConfigsShouldReturnUnrecognizedValues() {
        MatcherAssert.assertThat(((String) (context.appConfigs().get("user.supplied.config"))), CoreMatchers.equalTo("user-suppplied-value"));
    }

    private static class TestProcessorContext extends AbstractProcessorContext {
        static Properties config;

        static {
            AbstractProcessorContextTest.TestProcessorContext.config = StreamsTestUtils.getStreamsConfig();
            // Value must be a string to test className -> class conversion
            AbstractProcessorContextTest.TestProcessorContext.config.put(ROCKSDB_CONFIG_SETTER_CLASS_CONFIG, RocksDBConfigSetter.class.getName());
            AbstractProcessorContextTest.TestProcessorContext.config.put("user.supplied.config", "user-suppplied-value");
        }

        TestProcessorContext(final MockStreamsMetrics metrics) {
            super(new TaskId(0, 0), new StreamsConfig(AbstractProcessorContextTest.TestProcessorContext.config), metrics, new StateManagerStub(), new org.apache.kafka.streams.state.internals.ThreadCache(new LogContext("name "), 0, metrics));
        }

        @Override
        public StateStore getStateStore(final String name) {
            return null;
        }

        @SuppressWarnings("deprecation")
        @Override
        public Cancellable schedule(final long interval, final PunctuationType type, final Punctuator callback) {
            return null;
        }

        @Override
        public Cancellable schedule(final Duration interval, final PunctuationType type, final Punctuator callback) throws IllegalArgumentException {
            return null;
        }

        @Override
        public <K, V> void forward(final K key, final V value) {
        }

        @Override
        public <K, V> void forward(final K key, final V value, final To to) {
        }

        @SuppressWarnings("deprecation")
        @Override
        public <K, V> void forward(final K key, final V value, final int childIndex) {
        }

        @SuppressWarnings("deprecation")
        @Override
        public <K, V> void forward(final K key, final V value, final String childName) {
        }

        @Override
        public void commit() {
        }
    }
}

