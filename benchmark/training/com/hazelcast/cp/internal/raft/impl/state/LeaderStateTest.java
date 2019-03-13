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
package com.hazelcast.cp.internal.raft.impl.state;


import com.hazelcast.core.Endpoint;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.util.RandomPicker;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class LeaderStateTest {
    private LeaderState state;

    private Set<Endpoint> remoteEndpoints;

    private int lastLogIndex;

    @Test
    public void test_initialState() {
        for (Endpoint endpoint : remoteEndpoints) {
            FollowerState followerState = state.getFollowerState(endpoint);
            Assert.assertEquals(0, followerState.matchIndex());
            Assert.assertEquals(((lastLogIndex) + 1), followerState.nextIndex());
        }
        long[] matchIndices = state.matchIndices();
        Assert.assertEquals(((remoteEndpoints.size()) + 1), matchIndices.length);
        for (long index : matchIndices) {
            Assert.assertEquals(0, index);
        }
    }

    @Test
    public void test_nextIndex() {
        Map<Endpoint, Integer> indices = new HashMap<Endpoint, Integer>();
        for (Endpoint endpoint : remoteEndpoints) {
            int index = 1 + (RandomPicker.getInt(100));
            state.getFollowerState(endpoint).nextIndex(index);
            indices.put(endpoint, index);
        }
        for (Endpoint endpoint : remoteEndpoints) {
            int index = indices.get(endpoint);
            Assert.assertEquals(index, state.getFollowerState(endpoint).nextIndex());
        }
    }

    @Test
    public void test_matchIndex() {
        Map<Endpoint, Long> indices = new HashMap<Endpoint, Long>();
        for (Endpoint endpoint : remoteEndpoints) {
            long index = 1 + (RandomPicker.getInt(100));
            state.getFollowerState(endpoint).matchIndex(index);
            indices.put(endpoint, index);
        }
        for (Endpoint endpoint : remoteEndpoints) {
            long index = indices.get(endpoint);
            Assert.assertEquals(index, state.getFollowerState(endpoint).matchIndex());
        }
        long[] matchIndices = state.matchIndices();
        Assert.assertEquals(((indices.size()) + 1), matchIndices.length);
        for (int i = 0; i < ((matchIndices.length) - 1); i++) {
            long index = matchIndices[i];
            Assert.assertTrue(indices.containsValue(index));
        }
    }
}

