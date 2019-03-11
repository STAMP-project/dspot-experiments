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
package org.apache.kafka.streams.processor.internals.assignment;


import SubscriptionInfo.LATEST_SUPPORTED_VERSION;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import org.apache.kafka.streams.processor.TaskId;
import org.junit.Assert;
import org.junit.Test;

import static SubscriptionInfo.LATEST_SUPPORTED_VERSION;
import static SubscriptionInfo.UNKNOWN;


public class SubscriptionInfoTest {
    private final UUID processId = UUID.randomUUID();

    private final Set<TaskId> activeTasks = new java.util.HashSet(Arrays.asList(new TaskId(0, 0), new TaskId(0, 1), new TaskId(1, 0)));

    private final Set<TaskId> standbyTasks = new java.util.HashSet(Arrays.asList(new TaskId(1, 1), new TaskId(2, 0)));

    private static final String IGNORED_USER_ENDPOINT = "ignoredUserEndpoint:80";

    @Test
    public void shouldUseLatestSupportedVersionByDefault() {
        final SubscriptionInfo info = new SubscriptionInfo(processId, activeTasks, standbyTasks, "localhost:80");
        Assert.assertEquals(LATEST_SUPPORTED_VERSION, info.version());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowForUnknownVersion1() {
        new SubscriptionInfo(0, processId, activeTasks, standbyTasks, "localhost:80");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowForUnknownVersion2() {
        new SubscriptionInfo(((LATEST_SUPPORTED_VERSION) + 1), processId, activeTasks, standbyTasks, "localhost:80");
    }

    @Test
    public void shouldEncodeAndDecodeVersion1() {
        final SubscriptionInfo info = new SubscriptionInfo(1, processId, activeTasks, standbyTasks, SubscriptionInfoTest.IGNORED_USER_ENDPOINT);
        final SubscriptionInfo expectedInfo = new SubscriptionInfo(1, UNKNOWN, processId, activeTasks, standbyTasks, null);
        Assert.assertEquals(expectedInfo, SubscriptionInfo.decode(info.encode()));
    }

    @Test
    public void shouldEncodeAndDecodeVersion2() {
        final SubscriptionInfo info = new SubscriptionInfo(2, processId, activeTasks, standbyTasks, "localhost:80");
        final SubscriptionInfo expectedInfo = new SubscriptionInfo(2, UNKNOWN, processId, activeTasks, standbyTasks, "localhost:80");
        Assert.assertEquals(expectedInfo, SubscriptionInfo.decode(info.encode()));
    }

    @Test
    public void shouldEncodeAndDecodeVersion3() {
        final SubscriptionInfo info = new SubscriptionInfo(3, processId, activeTasks, standbyTasks, "localhost:80");
        final SubscriptionInfo expectedInfo = new SubscriptionInfo(3, LATEST_SUPPORTED_VERSION, processId, activeTasks, standbyTasks, "localhost:80");
        Assert.assertEquals(expectedInfo, SubscriptionInfo.decode(info.encode()));
    }

    @Test
    public void shouldEncodeAndDecodeVersion4() {
        final SubscriptionInfo info = new SubscriptionInfo(4, processId, activeTasks, standbyTasks, "localhost:80");
        final SubscriptionInfo expectedInfo = new SubscriptionInfo(4, LATEST_SUPPORTED_VERSION, processId, activeTasks, standbyTasks, "localhost:80");
        Assert.assertEquals(expectedInfo, SubscriptionInfo.decode(info.encode()));
    }

    @Test
    public void shouldAllowToDecodeFutureSupportedVersion() {
        final SubscriptionInfo info = SubscriptionInfo.decode(encodeFutureVersion());
        Assert.assertEquals(((LATEST_SUPPORTED_VERSION) + 1), info.version());
        Assert.assertEquals(((LATEST_SUPPORTED_VERSION) + 1), info.latestSupportedVersion());
    }
}

