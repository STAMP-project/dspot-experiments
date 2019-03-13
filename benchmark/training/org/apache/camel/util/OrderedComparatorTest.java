/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.util;


import Ordered.HIGHEST;
import Ordered.LOWEST;
import java.util.ArrayList;
import java.util.List;
import org.apache.camel.Ordered;
import org.apache.camel.support.OrderedComparator;
import org.junit.Assert;
import org.junit.Test;


public class OrderedComparatorTest extends Assert {
    @Test
    public void testOrderedComparatorGet() throws Exception {
        List<Ordered> answer = new ArrayList<>();
        answer.add(new OrderedComparatorTest.MyOrder(0));
        answer.add(new OrderedComparatorTest.MyOrder(2));
        answer.add(new OrderedComparatorTest.MyOrder(1));
        answer.add(new OrderedComparatorTest.MyOrder(5));
        answer.add(new OrderedComparatorTest.MyOrder(4));
        answer.sort(OrderedComparator.get());
        Assert.assertEquals(0, answer.get(0).getOrder());
        Assert.assertEquals(1, answer.get(1).getOrder());
        Assert.assertEquals(2, answer.get(2).getOrder());
        Assert.assertEquals(4, answer.get(3).getOrder());
        Assert.assertEquals(5, answer.get(4).getOrder());
    }

    @Test
    public void testOrderedComparator() throws Exception {
        List<Ordered> answer = new ArrayList<>();
        answer.add(new OrderedComparatorTest.MyOrder(0));
        answer.add(new OrderedComparatorTest.MyOrder(2));
        answer.add(new OrderedComparatorTest.MyOrder(1));
        answer.add(new OrderedComparatorTest.MyOrder(5));
        answer.add(new OrderedComparatorTest.MyOrder(4));
        answer.sort(new OrderedComparator());
        Assert.assertEquals(0, answer.get(0).getOrder());
        Assert.assertEquals(1, answer.get(1).getOrder());
        Assert.assertEquals(2, answer.get(2).getOrder());
        Assert.assertEquals(4, answer.get(3).getOrder());
        Assert.assertEquals(5, answer.get(4).getOrder());
    }

    @Test
    public void testOrderedComparatorGetReverse() throws Exception {
        List<Ordered> answer = new ArrayList<>();
        answer.add(new OrderedComparatorTest.MyOrder(0));
        answer.add(new OrderedComparatorTest.MyOrder(2));
        answer.add(new OrderedComparatorTest.MyOrder(1));
        answer.add(new OrderedComparatorTest.MyOrder(5));
        answer.add(new OrderedComparatorTest.MyOrder(4));
        answer.sort(OrderedComparator.getReverse());
        Assert.assertEquals(5, answer.get(0).getOrder());
        Assert.assertEquals(4, answer.get(1).getOrder());
        Assert.assertEquals(2, answer.get(2).getOrder());
        Assert.assertEquals(1, answer.get(3).getOrder());
        Assert.assertEquals(0, answer.get(4).getOrder());
    }

    @Test
    public void testOrderedComparatorReverse() throws Exception {
        List<Ordered> answer = new ArrayList<>();
        answer.add(new OrderedComparatorTest.MyOrder(0));
        answer.add(new OrderedComparatorTest.MyOrder(2));
        answer.add(new OrderedComparatorTest.MyOrder(1));
        answer.add(new OrderedComparatorTest.MyOrder(5));
        answer.add(new OrderedComparatorTest.MyOrder(4));
        answer.sort(new OrderedComparator(true));
        Assert.assertEquals(5, answer.get(0).getOrder());
        Assert.assertEquals(4, answer.get(1).getOrder());
        Assert.assertEquals(2, answer.get(2).getOrder());
        Assert.assertEquals(1, answer.get(3).getOrder());
        Assert.assertEquals(0, answer.get(4).getOrder());
    }

    @Test
    public void testOrderedComparatorHigh() throws Exception {
        List<Ordered> answer = new ArrayList<>();
        answer.add(new OrderedComparatorTest.MyOrder(0));
        answer.add(new OrderedComparatorTest.MyOrder(2));
        answer.add(new OrderedComparatorTest.MyOrder(200));
        answer.add(new OrderedComparatorTest.MyOrder(50));
        answer.add(new OrderedComparatorTest.MyOrder(Ordered.HIGHEST));
        answer.add(new OrderedComparatorTest.MyOrder(4));
        answer.sort(new OrderedComparator());
        Assert.assertEquals(HIGHEST, answer.get(0).getOrder());
        Assert.assertEquals(0, answer.get(1).getOrder());
        Assert.assertEquals(2, answer.get(2).getOrder());
        Assert.assertEquals(4, answer.get(3).getOrder());
        Assert.assertEquals(50, answer.get(4).getOrder());
        Assert.assertEquals(200, answer.get(5).getOrder());
    }

    @Test
    public void testOrderedComparatorHighReverse() throws Exception {
        List<Ordered> answer = new ArrayList<>();
        answer.add(new OrderedComparatorTest.MyOrder(0));
        answer.add(new OrderedComparatorTest.MyOrder(2));
        answer.add(new OrderedComparatorTest.MyOrder(200));
        answer.add(new OrderedComparatorTest.MyOrder(50));
        answer.add(new OrderedComparatorTest.MyOrder(Ordered.HIGHEST));
        answer.add(new OrderedComparatorTest.MyOrder(4));
        answer.sort(new OrderedComparator(true));
        Assert.assertEquals(200, answer.get(0).getOrder());
        Assert.assertEquals(50, answer.get(1).getOrder());
        Assert.assertEquals(4, answer.get(2).getOrder());
        Assert.assertEquals(2, answer.get(3).getOrder());
        Assert.assertEquals(0, answer.get(4).getOrder());
        Assert.assertEquals(HIGHEST, answer.get(5).getOrder());
    }

    @Test
    public void testOrderedComparatorLow() throws Exception {
        List<Ordered> answer = new ArrayList<>();
        answer.add(new OrderedComparatorTest.MyOrder(0));
        answer.add(new OrderedComparatorTest.MyOrder((-2)));
        answer.add(new OrderedComparatorTest.MyOrder(200));
        answer.add(new OrderedComparatorTest.MyOrder(50));
        answer.add(new OrderedComparatorTest.MyOrder(Ordered.LOWEST));
        answer.add(new OrderedComparatorTest.MyOrder((-4)));
        answer.sort(new OrderedComparator());
        Assert.assertEquals((-4), answer.get(0).getOrder());
        Assert.assertEquals((-2), answer.get(1).getOrder());
        Assert.assertEquals(0, answer.get(2).getOrder());
        Assert.assertEquals(50, answer.get(3).getOrder());
        Assert.assertEquals(200, answer.get(4).getOrder());
        Assert.assertEquals(LOWEST, answer.get(5).getOrder());
    }

    @Test
    public void testOrderedComparatorLowReverse() throws Exception {
        List<Ordered> answer = new ArrayList<>();
        answer.add(new OrderedComparatorTest.MyOrder(0));
        answer.add(new OrderedComparatorTest.MyOrder((-2)));
        answer.add(new OrderedComparatorTest.MyOrder(200));
        answer.add(new OrderedComparatorTest.MyOrder(50));
        answer.add(new OrderedComparatorTest.MyOrder(Ordered.LOWEST));
        answer.add(new OrderedComparatorTest.MyOrder((-4)));
        answer.sort(new OrderedComparator(true));
        Assert.assertEquals(LOWEST, answer.get(0).getOrder());
        Assert.assertEquals(200, answer.get(1).getOrder());
        Assert.assertEquals(50, answer.get(2).getOrder());
        Assert.assertEquals(0, answer.get(3).getOrder());
        Assert.assertEquals((-2), answer.get(4).getOrder());
        Assert.assertEquals((-4), answer.get(5).getOrder());
    }

    private static final class MyOrder implements Ordered {
        private final int order;

        private MyOrder(int order) {
            this.order = order;
        }

        public int getOrder() {
            return order;
        }

        @Override
        public String toString() {
            return "" + (order);
        }
    }
}

