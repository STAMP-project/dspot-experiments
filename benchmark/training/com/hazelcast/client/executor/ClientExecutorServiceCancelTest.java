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
package com.hazelcast.client.executor;


import com.hazelcast.client.test.TestHazelcastFactory;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.io.IOException;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


/* This test is failing because of order problem between actual invoke and cancel.
For random and partition, the reason of broken order is also unknown to me (@sancar)
For submit to member, it is because we do not have order guarantee in the first place.
and when there is partition movement, we can not is partition ID since tasks will not move with partitions
 */
@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
@Ignore
public class ClientExecutorServiceCancelTest extends HazelcastTestSupport {
    private static final int SLEEP_TIME = 1000000;

    private final TestHazelcastFactory hazelcastFactory = new TestHazelcastFactory();

    private HazelcastInstance server1;

    private HazelcastInstance server2;

    @Test(expected = CancellationException.class)
    public void testCancel_submitRandom_withSmartRouting() throws IOException, InterruptedException, ExecutionException {
        testCancel_submitRandom(true);
    }

    @Test(expected = CancellationException.class)
    public void testCancel_submitRandom_withDummyRouting() throws IOException, InterruptedException, ExecutionException {
        testCancel_submitRandom(false);
    }

    @Test(expected = CancellationException.class)
    public void testCancel_submitToKeyOwner_withSmartRouting() throws IOException, InterruptedException, ExecutionException {
        testCancel_submitToKeyOwner(true);
    }

    @Test(expected = CancellationException.class)
    public void testCancel_submitToKeyOwner_withDummyRouting() throws IOException, InterruptedException, ExecutionException {
        testCancel_submitToKeyOwner(false);
    }
}

