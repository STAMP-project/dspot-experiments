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
package com.hazelcast.replicatedmap;


import InMemoryFormat.BINARY;
import InMemoryFormat.OBJECT;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ReplicatedMapHitsAndLastAccessTimeTest extends ReplicatedMapAbstractTest {
    @Test
    public void test_hitsAndLastAccessTimeSetToAnyValueAfterStartTime_object() throws Exception {
        testHitsAndLastAccessTimeIsSetToAnyValueAfterStartTime(buildConfig(OBJECT));
    }

    @Test
    public void test_hitsAndLastAccessTimeSetToAnyValueAfterStartTime_Binary() throws Exception {
        testHitsAndLastAccessTimeIsSetToAnyValueAfterStartTime(buildConfig(BINARY));
    }

    @Test
    public void test_hitsAreZeroInitially_withSingleNode_object() throws Exception {
        testHitsAreZeroInitiallyWithSingleNode(buildConfig(OBJECT));
    }

    @Test
    public void test_hitsAreZeroInitially_withSingleNode_Binary() throws Exception {
        testHitsAreZeroInitiallyWithSingleNode(buildConfig(BINARY));
    }

    @Test
    public void test_hitsAndLastAccessTimeAreSet_withSingleNode_object() throws Exception {
        testHitsAndLastAccessTimeAreSetWithSingleNode(buildConfig(OBJECT));
    }

    @Test
    public void test_hitsAndLastAccessTimeAreSet_withSingleNode_Binary() throws Exception {
        testHitsAndLastAccessTimeAreSetWithSingleNode(buildConfig(BINARY));
    }

    @Test
    public void test_hitsAndLastAccessTimeAreSet_with2Nodes_object() throws Exception {
        testHitsAndLastAccessTimeAreSetFor1Of2Nodes(buildConfig(OBJECT));
    }

    @Test
    public void test_hitsAndLastAccessTimeAreSet_with2Nodes_Binary() throws Exception {
        testHitsAndLastAccessTimeAreSetFor1Of2Nodes(buildConfig(BINARY));
    }

    @Test
    public void test_hitsAreIncrementedOnPuts_withSingleNode_object() throws Exception {
        testHitsAreIncrementedOnPutsWithSingleNode(buildConfig(OBJECT));
    }

    @Test
    public void test_hitsAreIncrementedOnPuts_withSingleNode_Binary() throws Exception {
        testHitsAreIncrementedOnPutsWithSingleNode(buildConfig(BINARY));
    }

    @Test
    public void test_hitsAreIncrementedOnPuts_with2Nodes_object() {
        testHitsAreIncrementedOnPutsFor1Of2Nodes(buildConfig(OBJECT));
    }

    @Test
    public void test_hitsAreIncrementedOnPuts_with2Nodes_Binary() {
        testHitsAreIncrementedOnPutsFor1Of2Nodes(buildConfig(BINARY));
    }
}

