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
package com.hazelcast.cp.internal.datastructures.countdownlatch;


import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.internal.HazelcastRaftTestSupport;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.concurrent.ExecutionException;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class RaftCountDownLatchLongAwaitTest extends HazelcastRaftTestSupport {
    private HazelcastInstance[] instances;

    private String objectName = "latch";

    private String proxyName = (objectName) + "@group1";

    private int groupSize = 3;

    private CPGroupId groupId;

    private final long callTimeoutSeconds = 15;

    @Test
    public void when_awaitDurationIsLongerThanOperationTimeout_then_invocationFromLeaderInstanceWaits() throws InterruptedException, ExecutionException {
        testLongAwait(getLeaderInstance(instances, groupId));
    }

    @Test
    public void when_awaitDurationIsLongerThanOperationTimeout_then_invocationFromNonLeaderInstanceWaits() throws InterruptedException, ExecutionException {
        testLongAwait(getRandomFollowerInstance(instances, groupId));
    }
}

