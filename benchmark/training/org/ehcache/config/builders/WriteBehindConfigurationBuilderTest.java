/**
 * Copyright Terracotta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ehcache.config.builders;


import java.util.concurrent.TimeUnit;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.junit.Assert;
import org.junit.Test;


public class WriteBehindConfigurationBuilderTest {
    @Test
    public void testDefaultUnBatchedConcurrency() {
        Assert.assertThat(WriteBehindConfigurationBuilder.newUnBatchedWriteBehindConfiguration().build().getConcurrency(), Is.is(1));
    }

    @Test
    public void testDefaultBatchedConcurrency() {
        Assert.assertThat(WriteBehindConfigurationBuilder.newBatchedWriteBehindConfiguration(1, TimeUnit.MINUTES, 10).build().getConcurrency(), Is.is(1));
    }

    @Test
    public void testIllegalNonPositiveConcurrencyWhenUnBatched() {
        try {
            WriteBehindConfigurationBuilder.newUnBatchedWriteBehindConfiguration().concurrencyLevel(0);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void testIllegalNonPositiveConcurrencyWhenBatched() {
        try {
            WriteBehindConfigurationBuilder.newBatchedWriteBehindConfiguration(1, TimeUnit.MINUTES, 10).concurrencyLevel(0);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void testDefaultUnBatchedQueueSize() {
        Assert.assertThat(WriteBehindConfigurationBuilder.newUnBatchedWriteBehindConfiguration().build().getMaxQueueSize(), Is.is(Integer.MAX_VALUE));
    }

    @Test
    public void testDefaultBatchedQueueSize() {
        Assert.assertThat(WriteBehindConfigurationBuilder.newBatchedWriteBehindConfiguration(1, TimeUnit.MINUTES, 10).build().getMaxQueueSize(), Is.is(Integer.MAX_VALUE));
    }

    @Test
    public void testIllegalNonPositiveQueueSizeWhenUnBatched() {
        try {
            WriteBehindConfigurationBuilder.newUnBatchedWriteBehindConfiguration().queueSize(0);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void testIllegalNonPositiveQueueSizeWhenBatched() {
        try {
            WriteBehindConfigurationBuilder.newBatchedWriteBehindConfiguration(1, TimeUnit.MINUTES, 10).queueSize(0);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void testDefaultUnBatchedThreadPoolAlias() {
        Assert.assertThat(WriteBehindConfigurationBuilder.newUnBatchedWriteBehindConfiguration().build().getThreadPoolAlias(), IsNull.nullValue());
    }

    @Test
    public void testDefaultBatchedThreadPoolAlias() {
        Assert.assertThat(WriteBehindConfigurationBuilder.newBatchedWriteBehindConfiguration(1, TimeUnit.MINUTES, 10).build().getThreadPoolAlias(), IsNull.nullValue());
    }

    @Test
    public void testDefaultBatchCoalescing() {
        Assert.assertThat(WriteBehindConfigurationBuilder.newBatchedWriteBehindConfiguration(1, TimeUnit.MINUTES, 10).build().getBatchingConfiguration().isCoalescing(), Is.is(false));
    }

    @Test
    public void testIllegalNonPositiveBatchDelay() {
        try {
            WriteBehindConfigurationBuilder.newBatchedWriteBehindConfiguration(0, TimeUnit.MINUTES, 10);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void testIllegalNonPositiveBatchSize() {
        try {
            WriteBehindConfigurationBuilder.newBatchedWriteBehindConfiguration(1, TimeUnit.MINUTES, 0);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }
}

