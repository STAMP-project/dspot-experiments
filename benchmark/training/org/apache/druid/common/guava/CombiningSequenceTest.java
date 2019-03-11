/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.common.guava;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.druid.java.util.common.Pair;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class CombiningSequenceTest {
    private final int yieldEvery;

    public CombiningSequenceTest(int yieldEvery) {
        this.yieldEvery = yieldEvery;
    }

    @Test
    public void testMerge() throws Exception {
        List<Pair<Integer, Integer>> pairs = Arrays.asList(Pair.of(0, 1), Pair.of(0, 2), Pair.of(0, 3), Pair.of(1, 1), Pair.of(2, 1), Pair.of(5, 1), Pair.of(5, 10), Pair.of(6, 1), Pair.of(5, 1));
        List<Pair<Integer, Integer>> expected = Arrays.asList(Pair.of(0, 6), Pair.of(1, 1), Pair.of(2, 1), Pair.of(5, 11), Pair.of(6, 1), Pair.of(5, 1));
        testCombining(pairs, expected);
    }

    @Test
    public void testNoMergeOne() throws Exception {
        List<Pair<Integer, Integer>> pairs = Collections.singletonList(Pair.of(0, 1));
        List<Pair<Integer, Integer>> expected = Collections.singletonList(Pair.of(0, 1));
        testCombining(pairs, expected);
    }

    @Test
    public void testMergeMany() throws Exception {
        List<Pair<Integer, Integer>> pairs = Arrays.asList(Pair.of(0, 6), Pair.of(1, 1), Pair.of(2, 1), Pair.of(5, 11), Pair.of(6, 1), Pair.of(5, 1));
        List<Pair<Integer, Integer>> expected = Arrays.asList(Pair.of(0, 6), Pair.of(1, 1), Pair.of(2, 1), Pair.of(5, 11), Pair.of(6, 1), Pair.of(5, 1));
        testCombining(pairs, expected);
    }

    @Test
    public void testNoMergeTwo() throws Exception {
        List<Pair<Integer, Integer>> pairs = Arrays.asList(Pair.of(0, 1), Pair.of(1, 1));
        List<Pair<Integer, Integer>> expected = Arrays.asList(Pair.of(0, 1), Pair.of(1, 1));
        testCombining(pairs, expected);
    }

    @Test
    public void testMergeTwo() throws Exception {
        List<Pair<Integer, Integer>> pairs = Arrays.asList(Pair.of(0, 1), Pair.of(0, 1));
        List<Pair<Integer, Integer>> expected = Collections.singletonList(Pair.of(0, 2));
        testCombining(pairs, expected);
    }

    @Test
    public void testMergeSomeThingsMergedAtEnd() throws Exception {
        List<Pair<Integer, Integer>> pairs = Arrays.asList(Pair.of(0, 1), Pair.of(0, 2), Pair.of(0, 3), Pair.of(1, 1), Pair.of(2, 1), Pair.of(5, 1), Pair.of(5, 10), Pair.of(6, 1), Pair.of(5, 1), Pair.of(5, 2), Pair.of(5, 2), Pair.of(5, 2), Pair.of(5, 2), Pair.of(5, 2));
        List<Pair<Integer, Integer>> expected = Arrays.asList(Pair.of(0, 6), Pair.of(1, 1), Pair.of(2, 1), Pair.of(5, 11), Pair.of(6, 1), Pair.of(5, 11));
        testCombining(pairs, expected);
    }

    @Test
    public void testNothing() throws Exception {
        testCombining(Collections.emptyList(), Collections.emptyList());
    }
}

