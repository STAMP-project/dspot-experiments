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
package org.apache.flink.runtime.state.heap;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import org.apache.flink.runtime.state.InternalPriorityQueueTestBase;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test for {@link HeapPriorityQueue}.
 */
public class HeapPriorityQueueTest extends InternalPriorityQueueTestBase {
    @Test
    public void testClear() {
        HeapPriorityQueue<InternalPriorityQueueTestBase.TestElement> priorityQueueSet = newPriorityQueue(1);
        int count = 10;
        HashSet<InternalPriorityQueueTestBase.TestElement> checkSet = new HashSet<>(count);
        InternalPriorityQueueTestBase.insertRandomElements(priorityQueueSet, checkSet, count);
        Assert.assertEquals(count, priorityQueueSet.size());
        priorityQueueSet.clear();
        Assert.assertEquals(0, priorityQueueSet.size());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testToArray() {
        final int testSize = 10;
        List<InternalPriorityQueueTestBase.TestElement[]> tests = new ArrayList<>(2);
        tests.add(new InternalPriorityQueueTestBase.TestElement[0]);
        tests.add(new InternalPriorityQueueTestBase.TestElement[testSize]);
        tests.add(new InternalPriorityQueueTestBase.TestElement[testSize + 1]);
        for (InternalPriorityQueueTestBase.TestElement[] testArray : tests) {
            Arrays.fill(testArray, new InternalPriorityQueueTestBase.TestElement(42L, 4711L));
            HashSet<InternalPriorityQueueTestBase.TestElement> checkSet = new HashSet<>(testSize);
            HeapPriorityQueue<InternalPriorityQueueTestBase.TestElement> timerPriorityQueue = newPriorityQueue(1);
            Assert.assertEquals(testArray.length, timerPriorityQueue.toArray(testArray).length);
            InternalPriorityQueueTestBase.insertRandomElements(timerPriorityQueue, checkSet, testSize);
            InternalPriorityQueueTestBase.TestElement[] toArray = timerPriorityQueue.toArray(testArray);
            Assert.assertEquals(((testArray.length) >= testSize), (testArray == toArray));
            int count = 0;
            for (InternalPriorityQueueTestBase.TestElement o : toArray) {
                if (o == null) {
                    break;
                }
                Assert.assertTrue(checkSet.remove(o));
                ++count;
            }
            Assert.assertEquals(timerPriorityQueue.size(), count);
            Assert.assertTrue(checkSet.isEmpty());
        }
    }
}

