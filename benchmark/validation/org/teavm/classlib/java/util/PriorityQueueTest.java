/**
 * Copyright 2014 Alexey Andreev.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.teavm.classlib.java.util;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.teavm.junit.TeaVMTestRunner;


@RunWith(TeaVMTestRunner.class)
public class PriorityQueueTest {
    @Test
    public void receivesElements() {
        PriorityQueue<Integer> queue = fillQueue();
        Assert.assertEquals(8, queue.size());
        List<Integer> list = new ArrayList<>();
        while (!(queue.isEmpty())) {
            list.add(queue.poll());
        } 
        Assert.assertNull(queue.poll());
        Assert.assertArrayEquals(new Integer[]{ 0, 1, 2, 3, 4, 5, 6, 7 }, list.toArray(new Integer[0]));
    }

    @Test
    public void removeElements() {
        for (int i = 1; i < 7; ++i) {
            PriorityQueue<Integer> queue = fillQueue();
            List<Integer> expectedList = new ArrayList<>();
            for (int j = 0; j < (queue.size()); ++j) {
                expectedList.add(j);
            }
            Iterator<Integer> iter = queue.iterator();
            int indexToRemove = -1;
            for (int j = 0; j < i; ++j) {
                indexToRemove = iter.next();
            }
            iter.remove();
            List<Integer> list = new ArrayList<>();
            while (!(queue.isEmpty())) {
                list.add(queue.poll());
            } 
            expectedList.remove(indexToRemove);
            Assert.assertArrayEquals(expectedList.toArray(), list.toArray());
        }
    }
}

