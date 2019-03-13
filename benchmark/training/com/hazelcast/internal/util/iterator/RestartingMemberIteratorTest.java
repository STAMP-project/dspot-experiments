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
package com.hazelcast.internal.util.iterator;


import com.hazelcast.core.HazelcastException;
import com.hazelcast.core.Member;
import com.hazelcast.internal.cluster.ClusterService;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.NoSuchElementException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class RestartingMemberIteratorTest extends HazelcastTestSupport {
    private ClusterService mockClusterService;

    @Test
    public void testIteratorOverSingleEntry() {
        int maxRetries = 0;
        Member mockMember = addClusterMember();
        RestartingMemberIterator iterator = new RestartingMemberIterator(mockClusterService, maxRetries);
        Assert.assertTrue(iterator.hasNext());
        Member member = iterator.next();
        Assert.assertSame(mockMember, member);
        Assert.assertFalse(iterator.hasNext());
    }

    @Test
    public void testRestart() {
        int maxRetries = 1;
        Member mockMember = addClusterMember();
        RestartingMemberIterator iterator = new RestartingMemberIterator(mockClusterService, maxRetries);
        Assert.assertTrue(iterator.hasNext());
        Member member = iterator.next();
        Assert.assertSame(mockMember, member);
        // topologoy change -> this should restart iteration
        Member anotherMockMember = addClusterMember();
        member = iterator.next();
        Assert.assertSame(mockMember, member);
        member = iterator.next();
        Assert.assertSame(anotherMockMember, member);
        Assert.assertFalse(iterator.hasNext());
    }

    @Test(expected = HazelcastException.class)
    public void testRestart_withMaxRetriesExhausted_nextThrowsException() {
        int maxRetries = 0;
        addClusterMember();
        RestartingMemberIterator iterator = new RestartingMemberIterator(mockClusterService, maxRetries);
        // start iterating
        iterator.next();
        // topologoy change -> this should restart iteration, but there are no retry attempt left
        addClusterMember();
        // this has to throw the Exception
        iterator.next();
    }

    @Test(expected = HazelcastException.class)
    public void testRestart_withMaxRetriesExhausted_hasNextThrowsException() {
        int maxRetries = 0;
        addClusterMember();
        RestartingMemberIterator iterator = new RestartingMemberIterator(mockClusterService, maxRetries);
        iterator.next();
        addClusterMember();
        iterator.hasNext();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void removeThrowUOE() {
        int maxRetries = 0;
        addClusterMember();
        RestartingMemberIterator iterator = new RestartingMemberIterator(mockClusterService, maxRetries);
        iterator.next();
        iterator.remove();
    }

    @Test(expected = NoSuchElementException.class)
    public void give() {
        int maxRetries = 0;
        addClusterMember();
        RestartingMemberIterator iterator = new RestartingMemberIterator(mockClusterService, maxRetries);
        iterator.next();
        // this should throw NoSuchElementException
        iterator.next();
    }
}

