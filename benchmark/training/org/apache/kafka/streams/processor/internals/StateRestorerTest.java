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


import java.util.Collections;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.streams.state.internals.RecordConverters;
import org.apache.kafka.test.MockRestoreCallback;
import org.apache.kafka.test.MockStateRestoreListener;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;


public class StateRestorerTest {
    private static final long OFFSET_LIMIT = 50;

    private final MockRestoreCallback callback = new MockRestoreCallback();

    private final MockStateRestoreListener reportingListener = new MockStateRestoreListener();

    private final CompositeRestoreListener compositeRestoreListener = new CompositeRestoreListener(callback);

    private final StateRestorer restorer = new StateRestorer(new TopicPartition("topic", 1), compositeRestoreListener, null, StateRestorerTest.OFFSET_LIMIT, true, "storeName", RecordConverters.identity());

    @Test
    public void shouldCallRestoreOnRestoreCallback() {
        restorer.restore(Collections.singletonList(new org.apache.kafka.clients.consumer.ConsumerRecord("", 0, 0L, new byte[0], new byte[0])));
        MatcherAssert.assertThat(callback.restored.size(), CoreMatchers.equalTo(1));
    }

    @Test
    public void shouldBeCompletedIfRecordOffsetGreaterThanEndOffset() {
        Assert.assertTrue(restorer.hasCompleted(11, 10));
    }

    @Test
    public void shouldBeCompletedIfRecordOffsetGreaterThanOffsetLimit() {
        Assert.assertTrue(restorer.hasCompleted(51, 100));
    }

    @Test
    public void shouldBeCompletedIfEndOffsetAndRecordOffsetAreZero() {
        Assert.assertTrue(restorer.hasCompleted(0, 0));
    }

    @Test
    public void shouldBeCompletedIfOffsetAndOffsetLimitAreZero() {
        final StateRestorer restorer = new StateRestorer(new TopicPartition("topic", 1), compositeRestoreListener, null, 0, true, "storeName", RecordConverters.identity());
        Assert.assertTrue(restorer.hasCompleted(0, 10));
    }

    @Test
    public void shouldSetRestoredOffsetToMinOfLimitAndOffset() {
        restorer.setRestoredOffset(20);
        MatcherAssert.assertThat(restorer.restoredOffset(), CoreMatchers.equalTo(20L));
        restorer.setRestoredOffset(100);
        MatcherAssert.assertThat(restorer.restoredOffset(), CoreMatchers.equalTo(StateRestorerTest.OFFSET_LIMIT));
    }

    @Test
    public void shouldSetStartingOffsetToMinOfLimitAndOffset() {
        restorer.setStartingOffset(20);
        MatcherAssert.assertThat(restorer.startingOffset(), CoreMatchers.equalTo(20L));
        restorer.setRestoredOffset(100);
        MatcherAssert.assertThat(restorer.restoredOffset(), CoreMatchers.equalTo(StateRestorerTest.OFFSET_LIMIT));
    }

    @Test
    public void shouldReturnCorrectNumRestoredRecords() {
        restorer.setStartingOffset(20);
        restorer.setRestoredOffset(40);
        MatcherAssert.assertThat(restorer.restoredNumRecords(), CoreMatchers.equalTo(20L));
        restorer.setRestoredOffset(100);
        MatcherAssert.assertThat(restorer.restoredNumRecords(), CoreMatchers.equalTo(((StateRestorerTest.OFFSET_LIMIT) - 20)));
    }
}

