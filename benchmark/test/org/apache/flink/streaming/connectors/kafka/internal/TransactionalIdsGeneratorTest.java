/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.streaming.connectors.kafka.internal;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link TransactionalIdsGenerator}.
 */
public class TransactionalIdsGeneratorTest {
    private static final int POOL_SIZE = 3;

    private static final int SAFE_SCALE_DOWN_FACTOR = 3;

    private static final int SUBTASKS_COUNT = 5;

    @Test
    public void testGenerateIdsToUse() {
        TransactionalIdsGenerator generator = new TransactionalIdsGenerator("test", 2, TransactionalIdsGeneratorTest.SUBTASKS_COUNT, TransactionalIdsGeneratorTest.POOL_SIZE, TransactionalIdsGeneratorTest.SAFE_SCALE_DOWN_FACTOR);
        Assert.assertEquals(new HashSet(Arrays.asList("test-42", "test-43", "test-44")), generator.generateIdsToUse(36));
    }

    /**
     * Ids to abort and to use should never clash between subtasks.
     */
    @Test
    public void testGeneratedIdsDoNotClash() {
        List<Set<String>> idsToAbort = new ArrayList<>();
        List<Set<String>> idsToUse = new ArrayList<>();
        for (int subtask = 0; subtask < (TransactionalIdsGeneratorTest.SUBTASKS_COUNT); subtask++) {
            TransactionalIdsGenerator generator = new TransactionalIdsGenerator("test", subtask, TransactionalIdsGeneratorTest.SUBTASKS_COUNT, TransactionalIdsGeneratorTest.POOL_SIZE, TransactionalIdsGeneratorTest.SAFE_SCALE_DOWN_FACTOR);
            idsToUse.add(generator.generateIdsToUse(0));
            idsToAbort.add(generator.generateIdsToAbort());
        }
        for (int subtask1 = 0; subtask1 < (TransactionalIdsGeneratorTest.SUBTASKS_COUNT); subtask1++) {
            for (int subtask2 = 0; subtask2 < (TransactionalIdsGeneratorTest.SUBTASKS_COUNT); subtask2++) {
                if (subtask2 == subtask1) {
                    continue;
                }
                assertDisjoint(idsToAbort.get(subtask2), idsToAbort.get(subtask1));
                assertDisjoint(idsToUse.get(subtask2), idsToUse.get(subtask1));
                assertDisjoint(idsToAbort.get(subtask2), idsToUse.get(subtask1));
            }
        }
    }
}

