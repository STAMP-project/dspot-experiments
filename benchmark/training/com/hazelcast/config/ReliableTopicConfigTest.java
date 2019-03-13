/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.config;


import TopicOverloadPolicy.ERROR;
import Warning.NONFINAL_FIELDS;
import Warning.NULL_FIELDS;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.topic.TopicOverloadPolicy;
import java.util.Arrays;
import java.util.concurrent.Executor;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ReliableTopicConfigTest {
    @Test
    public void testDefaultSettings() {
        ReliableTopicConfig config = new ReliableTopicConfig("foo");
        Assert.assertNull(config.getExecutor());
        Assert.assertEquals(ReliableTopicConfig.DEFAULT_READ_BATCH_SIZE, config.getReadBatchSize());
        Assert.assertEquals("foo", config.getName());
        Assert.assertEquals(ReliableTopicConfig.DEFAULT_TOPIC_OVERLOAD_POLICY, config.getTopicOverloadPolicy());
        Assert.assertEquals(ReliableTopicConfig.DEFAULT_STATISTICS_ENABLED, config.isStatisticsEnabled());
    }

    @Test
    public void testCopyConstructorWithName() {
        ReliableTopicConfig original = new ReliableTopicConfig("original").setTopicOverloadPolicy(ERROR).setExecutor(Mockito.mock(Executor.class)).setReadBatchSize(1).setStatisticsEnabled((!(ReliableTopicConfig.DEFAULT_STATISTICS_ENABLED)));
        ReliableTopicConfig copy = new ReliableTopicConfig(original, "copy");
        Assert.assertEquals("copy", copy.getName());
        Assert.assertSame(original.getExecutor(), copy.getExecutor());
        Assert.assertEquals(original.getReadBatchSize(), copy.getReadBatchSize());
        Assert.assertEquals(original.isStatisticsEnabled(), copy.isStatisticsEnabled());
        Assert.assertEquals(original.getTopicOverloadPolicy(), copy.getTopicOverloadPolicy());
    }

    @Test
    public void testCopyConstructor() {
        ReliableTopicConfig original = new ReliableTopicConfig("original").setTopicOverloadPolicy(ERROR).setExecutor(Mockito.mock(Executor.class)).setReadBatchSize(1).setStatisticsEnabled((!(ReliableTopicConfig.DEFAULT_STATISTICS_ENABLED)));
        ReliableTopicConfig copy = new ReliableTopicConfig(original);
        Assert.assertEquals(original.getName(), copy.getName());
        Assert.assertSame(original.getExecutor(), copy.getExecutor());
        Assert.assertEquals(original.getReadBatchSize(), copy.getReadBatchSize());
        Assert.assertEquals(original.isStatisticsEnabled(), copy.isStatisticsEnabled());
        Assert.assertEquals(original.getTopicOverloadPolicy(), copy.getTopicOverloadPolicy());
    }

    // ==================== setTopicOverflowPolicy =============================
    @Test(expected = NullPointerException.class)
    public void setTopicOverloadPolicy_whenNull() {
        ReliableTopicConfig config = new ReliableTopicConfig("foo");
        config.setTopicOverloadPolicy(null);
    }

    @Test
    public void setTopicOverloadPolicy() {
        ReliableTopicConfig config = new ReliableTopicConfig("foo");
        config.setTopicOverloadPolicy(TopicOverloadPolicy.DISCARD_NEWEST);
        Assert.assertSame(TopicOverloadPolicy.DISCARD_NEWEST, config.getTopicOverloadPolicy());
    }

    // ==================== setReadBatchSize =============================\
    @Test
    public void setReadBatchSize() {
        ReliableTopicConfig config = new ReliableTopicConfig("foo");
        config.setReadBatchSize(200);
        Assert.assertEquals(200, config.getReadBatchSize());
    }

    @Test(expected = IllegalArgumentException.class)
    public void setReadBatchSize_whenNegative() {
        ReliableTopicConfig config = new ReliableTopicConfig("foo");
        config.setReadBatchSize((-1));
    }

    // ==================== setStatisticsEnabled =============================\
    @Test
    public void setStatisticsEnabled() {
        ReliableTopicConfig config = new ReliableTopicConfig("foo");
        boolean newValue = !(ReliableTopicConfig.DEFAULT_STATISTICS_ENABLED);
        config.setStatisticsEnabled(newValue);
        Assert.assertEquals(newValue, config.isStatisticsEnabled());
    }

    // ===================== listener ===================
    @Test(expected = NullPointerException.class)
    public void addMessageListenerConfig_whenNull() {
        ReliableTopicConfig config = new ReliableTopicConfig("foo");
        config.addMessageListenerConfig(null);
    }

    @Test
    public void addMessageListenerConfig() {
        ReliableTopicConfig config = new ReliableTopicConfig("foo");
        ListenerConfig listenerConfig = new ListenerConfig("foobar");
        config.addMessageListenerConfig(listenerConfig);
        Assert.assertEquals(Arrays.asList(listenerConfig), config.getMessageListenerConfigs());
    }

    // ==================== setExecutor =============================
    @Test
    public void setExecutor() {
        ReliableTopicConfig config = new ReliableTopicConfig("foo");
        Executor executor = Mockito.mock(Executor.class);
        config.setExecutor(executor);
        Assert.assertSame(executor, config.getExecutor());
        config.setExecutor(null);
        Assert.assertNull(config.getExecutor());
    }

    @Test
    public void testReadonly() {
        Executor executor = Mockito.mock(Executor.class);
        ReliableTopicConfig config = new ReliableTopicConfig("foo").setReadBatchSize(201).setExecutor(executor).setTopicOverloadPolicy(ERROR).addMessageListenerConfig(new ListenerConfig("Foobar"));
        ReliableTopicConfig readOnly = config.getAsReadOnly();
        Assert.assertEquals(config.getName(), readOnly.getName());
        Assert.assertSame(config.getExecutor(), readOnly.getExecutor());
        Assert.assertEquals(config.isStatisticsEnabled(), readOnly.isStatisticsEnabled());
        Assert.assertEquals(config.getReadBatchSize(), readOnly.getReadBatchSize());
        Assert.assertEquals(config.getTopicOverloadPolicy(), readOnly.getTopicOverloadPolicy());
        Assert.assertEquals(config.getMessageListenerConfigs(), readOnly.getMessageListenerConfigs());
        try {
            readOnly.setExecutor(null);
            Assert.fail();
        } catch (UnsupportedOperationException e) {
        }
        try {
            readOnly.setReadBatchSize(3);
            Assert.fail();
        } catch (UnsupportedOperationException e) {
        }
        try {
            readOnly.setStatisticsEnabled(true);
            Assert.fail();
        } catch (UnsupportedOperationException e) {
        }
        try {
            readOnly.addMessageListenerConfig(new ListenerConfig("foobar"));
            Assert.fail();
        } catch (UnsupportedOperationException e) {
        }
        try {
            readOnly.setTopicOverloadPolicy(null);
            Assert.fail();
        } catch (UnsupportedOperationException e) {
        }
    }

    @Test
    public void test_toString() {
        ReliableTopicConfig config = new ReliableTopicConfig("foo");
        String s = config.toString();
        Assert.assertEquals(("ReliableTopicConfig{name='foo', topicOverloadPolicy=BLOCK, executor=null," + " readBatchSize=10, statisticsEnabled=true, listenerConfigs=[]}"), s);
    }

    @Test
    public void testEqualsAndHashCode() {
        HazelcastTestSupport.assumeDifferentHashCodes();
        EqualsVerifier.forClass(ReliableTopicConfig.class).allFieldsShouldBeUsed().suppress(NULL_FIELDS, NONFINAL_FIELDS).verify();
    }
}

