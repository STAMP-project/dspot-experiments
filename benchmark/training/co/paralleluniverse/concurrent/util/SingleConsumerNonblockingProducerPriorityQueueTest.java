/**
 * Quasar: lightweight threads and actors for the JVM.
 * Copyright (c) 2013-2014, Parallel Universe Software Co. All rights reserved.
 *
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 *
 *   or (per the licensee's choosing)
 *
 * under the terms of the GNU Lesser General Public License version 3.0
 * as published by the Free Software Foundation.
 */
package co.paralleluniverse.concurrent.util;


import co.paralleluniverse.common.test.TestUtil;
import java.util.concurrent.BlockingQueue;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.rules.TestRule;


/**
 *
 *
 * @author pron
 */
public class SingleConsumerNonblockingProducerPriorityQueueTest {
    @Rule
    public TestName name = new TestName();

    @Rule
    public TestRule watchman = TestUtil.WATCHMAN;

    public SingleConsumerNonblockingProducerPriorityQueueTest() {
    }

    BlockingQueue<SingleConsumerNonblockingProducerPriorityQueueTest.Foo> q;

    @Test
    public void simpleProiorityTest() throws Exception {
        int index = 0;
        q.put(new SingleConsumerNonblockingProducerPriorityQueueTest.Foo(5, (index++)));
        q.put(new SingleConsumerNonblockingProducerPriorityQueueTest.Foo(10, (index++)));
        q.put(new SingleConsumerNonblockingProducerPriorityQueueTest.Foo(3, (index++)));
        q.put(new SingleConsumerNonblockingProducerPriorityQueueTest.Foo(7, (index++)));
        q.put(new SingleConsumerNonblockingProducerPriorityQueueTest.Foo(20, (index++)));
        Assert.assertThat(q.size(), CoreMatchers.is(5));
        Assert.assertThat(q.poll().priority, CoreMatchers.is(3));
        Assert.assertThat(q.poll().priority, CoreMatchers.is(5));
        Assert.assertThat(q.poll().priority, CoreMatchers.is(7));
        Assert.assertThat(q.poll().priority, CoreMatchers.is(10));
        Assert.assertThat(q.poll().priority, CoreMatchers.is(20));
        Assert.assertThat(q.isEmpty(), CoreMatchers.is(true));
    }

    static class Foo implements Comparable<SingleConsumerNonblockingProducerPriorityQueueTest.Foo> {
        final int priority;

        final int index;

        public Foo(int priority, int index) {
            this.priority = priority;
            this.index = index;
        }

        @Override
        public int compareTo(SingleConsumerNonblockingProducerPriorityQueueTest.Foo o) {
            return (this.priority) - (o.priority);
        }
    }
}

