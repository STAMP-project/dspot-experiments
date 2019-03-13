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
package com.hazelcast.quorum.set;


import com.hazelcast.quorum.AbstractQuorumTest;
import com.hazelcast.quorum.QuorumException;
import com.hazelcast.quorum.QuorumType;
import com.hazelcast.test.HazelcastSerialParametersRunnerFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Arrays;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(HazelcastSerialParametersRunnerFactory.class)
@Category({ QuickTest.class, ParallelTest.class })
public class SetQuorumWriteTest extends AbstractQuorumTest {
    @Parameterized.Parameter
    public static QuorumType quorumType;

    @Test
    public void addOperation_successful_whenQuorumSize_met() {
        set(0).add("foo");
    }

    @Test(expected = QuorumException.class)
    public void addOperation_successful_whenQuorumSize_notMet() {
        set(3).add("foo");
    }

    @Test
    public void addAllOperation_successful_whenQuorumSize_met() {
        set(0).addAll(Arrays.asList("foo", "bar"));
    }

    @Test(expected = QuorumException.class)
    public void addAllOperation_successful_whenQuorumSize_notMet() {
        set(3).add(Arrays.asList("foo", "bar"));
    }

    @Test
    public void removeOperation_successful_whenQuorumSize_met() {
        set(0).remove("foo");
    }

    @Test(expected = QuorumException.class)
    public void removeOperation_successful_whenQuorumSize_notMet() {
        set(3).remove("foo");
    }

    @Test
    public void compareAndRemoveOperation_removeAll_successful_whenQuorumSize_met() {
        set(0).removeAll(Arrays.asList("foo", "bar"));
    }

    @Test(expected = QuorumException.class)
    public void compareAndRemoveOperation_removeAll_successful_whenQuorumSize_notMet() {
        set(3).removeAll(Arrays.asList("foo", "bar"));
    }

    @Test
    public void compareAndRemoveOperation_retainAll_successful_whenQuorumSize_met() {
        set(0).removeAll(Arrays.asList("foo", "bar"));
    }

    @Test(expected = QuorumException.class)
    public void compareAndRemoveOperation_retainAll_successful_whenQuorumSize_notMet() {
        set(3).removeAll(Arrays.asList("foo", "bar"));
    }

    @Test
    public void clearOperation_successful_whenQuorumSize_met() {
        set(0).clear();
    }

    @Test(expected = QuorumException.class)
    public void clearOperation_successful_whenQuorumSize_notMet() {
        set(3).clear();
    }
}

