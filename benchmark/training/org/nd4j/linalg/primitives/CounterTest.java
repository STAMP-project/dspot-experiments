/**
 * *****************************************************************************
 * Copyright (c) 2015-2018 Skymind, Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * ****************************************************************************
 */
package org.nd4j.linalg.primitives;


import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for Counter
 *
 * @author raver119@gmail.com
 */
@Slf4j
public class CounterTest {
    @Test
    public void testCounterIncrementAll1() {
        Counter<String> counterA = new Counter();
        counterA.incrementCount("A", 1);
        counterA.incrementCount("A", 1);
        counterA.incrementCount("A", 1);
        Counter<String> counterB = new Counter();
        counterB.incrementCount("B", 2);
        counterB.incrementCount("B", 2);
        Assert.assertEquals(3.0, counterA.getCount("A"), 1.0E-5);
        Assert.assertEquals(4.0, counterB.getCount("B"), 1.0E-5);
        counterA.incrementAll(counterB);
        Assert.assertEquals(3.0, counterA.getCount("A"), 1.0E-5);
        Assert.assertEquals(4.0, counterA.getCount("B"), 1.0E-5);
        counterA.setCount("B", 234);
        Assert.assertEquals(234.0, counterA.getCount("B"), 1.0E-5);
    }

    @Test
    public void testCounterTopN1() {
        Counter<String> counterA = new Counter();
        counterA.incrementCount("A", 1);
        counterA.incrementCount("B", 2);
        counterA.incrementCount("C", 3);
        counterA.incrementCount("D", 4);
        counterA.incrementCount("E", 5);
        counterA.keepTopNElements(4);
        Assert.assertEquals(4, counterA.size());
        // we expect element A to be gone
        Assert.assertEquals(0.0, counterA.getCount("A"), 1.0E-5);
        Assert.assertEquals(2.0, counterA.getCount("B"), 1.0E-5);
        Assert.assertEquals(3.0, counterA.getCount("C"), 1.0E-5);
        Assert.assertEquals(4.0, counterA.getCount("D"), 1.0E-5);
        Assert.assertEquals(5.0, counterA.getCount("E"), 1.0E-5);
    }

    @Test
    public void testKeysSorted1() throws Exception {
        Counter<String> counterA = new Counter();
        counterA.incrementCount("A", 1);
        counterA.incrementCount("B", 2);
        counterA.incrementCount("C", 3);
        counterA.incrementCount("D", 4);
        counterA.incrementCount("E", 5);
        Assert.assertEquals("E", counterA.argMax());
        List<String> list = counterA.keySetSorted();
        Assert.assertEquals(5, list.size());
        Assert.assertEquals("E", list.get(0));
        Assert.assertEquals("D", list.get(1));
        Assert.assertEquals("C", list.get(2));
        Assert.assertEquals("B", list.get(3));
        Assert.assertEquals("A", list.get(4));
    }

    @Test
    public void testCounterTotal() {
        Counter<String> counter = new Counter();
        counter.incrementCount("A", 1);
        counter.incrementCount("B", 1);
        counter.incrementCount("C", 1);
        Assert.assertEquals(3.0, counter.totalCount(), 1.0E-5);
        counter.setCount("B", 234);
        Assert.assertEquals(236.0, counter.totalCount(), 1.0E-5);
        counter.setCount("D", 1);
        Assert.assertEquals(237.0, counter.totalCount(), 1.0E-5);
        counter.removeKey("B");
        Assert.assertEquals(3.0, counter.totalCount(), 1.0E-5);
    }
}

