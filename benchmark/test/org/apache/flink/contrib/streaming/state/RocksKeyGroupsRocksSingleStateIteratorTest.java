/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.contrib.streaming.state;


import java.util.Collections;
import org.apache.flink.contrib.streaming.state.iterator.RocksStatesPerKeyGroupMergeIterator;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * Tests for the RocksStatesPerKeyGroupMergeIterator.
 */
public class RocksKeyGroupsRocksSingleStateIteratorTest {
    private static final int NUM_KEY_VAL_STATES = 50;

    private static final int MAX_NUM_KEYS = 20;

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void testEmptyMergeIterator() throws Exception {
        RocksStatesPerKeyGroupMergeIterator emptyIterator = new RocksStatesPerKeyGroupMergeIterator(Collections.emptyList(), 2);
        Assert.assertFalse(emptyIterator.isValid());
    }

    @Test
    public void testMergeIteratorByte() throws Exception {
        Assert.assertTrue(((RocksKeyGroupsRocksSingleStateIteratorTest.MAX_NUM_KEYS) <= (Byte.MAX_VALUE)));
        testMergeIterator(Byte.MAX_VALUE);
    }

    @Test
    public void testMergeIteratorShort() throws Exception {
        Assert.assertTrue(((RocksKeyGroupsRocksSingleStateIteratorTest.MAX_NUM_KEYS) <= (Byte.MAX_VALUE)));
        testMergeIterator(Short.MAX_VALUE);
    }
}

