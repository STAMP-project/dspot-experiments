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
import com.hazelcast.cp.internal.raft.impl.RaftUtil;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class CandidateStateTest {
    private CandidateState state;

    private int majority;

    @Test
    public void test_initialState() {
        Assert.assertEquals(majority, state.majority());
        Assert.assertEquals(0, state.voteCount());
        Assert.assertFalse(state.isMajorityGranted());
    }

    @Test
    public void test_grantVote_withoutMajority() {
        Endpoint endpoint = RaftUtil.newRaftMember(1000);
        Assert.assertTrue(state.grantVote(endpoint));
        Assert.assertFalse(state.grantVote(endpoint));
        Assert.assertEquals(1, state.voteCount());
        Assert.assertFalse(state.isMajorityGranted());
    }

    @Test
    public void test_grantVote_withMajority() {
        for (int i = 0; i < (majority); i++) {
            Endpoint endpoint = RaftUtil.newRaftMember((1000 + i));
            Assert.assertTrue(state.grantVote(endpoint));
        }
        Assert.assertEquals(majority, state.voteCount());
        Assert.assertTrue(state.isMajorityGranted());
    }
}

