/**
 * Quasar: lightweight threads and actors for the JVM.
 * Copyright (c) 2013-2015, Parallel Universe Software Co. All rights reserved.
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
package co.paralleluniverse.strands.queues;


import co.paralleluniverse.common.test.TestUtil;
import java.util.Iterator;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Simple, single-threaded tests
 *
 * @author pron
 */
@RunWith(Parameterized.class)
public class SingleConsumerPrimitiveQueueTest {
    @Rule
    public TestName name = new TestName();

    @Rule
    public TestRule watchman = TestUtil.WATCHMAN;

    final SingleConsumerQueue<Integer> wordQueue;

    final SingleConsumerQueue<Double> dwordQueue;

    // public SingleConsumerPrimitiveQueueTest() {
    // this.wordQueue = new SingleConsumerLinkedArrayIntQueue();
    // this.dwordQueue = new SingleConsumerLinkedArrayDoubleQueue();
    // }
    public SingleConsumerPrimitiveQueueTest(int queueType) {
        switch (queueType) {
            case 1 :
                this.wordQueue = new SingleConsumerArrayIntQueue(10);
                this.dwordQueue = new SingleConsumerArrayDoubleQueue(10);
                break;
            case 2 :
                this.wordQueue = new SingleConsumerLinkedIntQueue();
                this.dwordQueue = new SingleConsumerLinkedDoubleQueue();
                break;
            case 3 :
                this.wordQueue = new SingleConsumerLinkedArrayIntQueue();
                this.dwordQueue = new SingleConsumerLinkedArrayDoubleQueue();
                break;
            default :
                throw new AssertionError();
        }
    }

    @Test
    public void testEmptyQueue() {
        testEmptyQueue(wordQueue);
        testEmptyQueue(dwordQueue);
    }

    @Test
    public void testOffer() {
        wordQueue.offer(1);
        wordQueue.offer(2);
        wordQueue.offer(3);
        Assert.assertThat(wordQueue.isEmpty(), CoreMatchers.is(false));
        Assert.assertThat(wordQueue.size(), CoreMatchers.is(3));
        Assert.assertThat(wordQueue.peek(), CoreMatchers.is(1));
        Assert.assertThat(SingleConsumerPrimitiveQueueTest.list(wordQueue), CoreMatchers.is(CoreMatchers.equalTo(SingleConsumerPrimitiveQueueTest.list(1, 2, 3))));
        dwordQueue.offer(1.2);
        dwordQueue.offer(2.3);
        dwordQueue.offer(3.4);
        Assert.assertThat(dwordQueue.isEmpty(), CoreMatchers.is(false));
        Assert.assertThat(dwordQueue.size(), CoreMatchers.is(3));
        Assert.assertThat(SingleConsumerPrimitiveQueueTest.list(dwordQueue), CoreMatchers.is(CoreMatchers.equalTo(SingleConsumerPrimitiveQueueTest.list(1.2, 2.3, 3.4))));
    }

    @Test
    public void testPoll() {
        int j = 1;
        int k = 1;
        for (int i = 0; i < 8; i++) {
            wordQueue.offer((j++));
            wordQueue.offer((j++));
            Integer s = wordQueue.poll();
            Assert.assertThat(s, CoreMatchers.equalTo((k++)));
        }
        Assert.assertThat(wordQueue.size(), CoreMatchers.is(8));
        Assert.assertThat(SingleConsumerPrimitiveQueueTest.list(wordQueue), CoreMatchers.is(CoreMatchers.equalTo(SingleConsumerPrimitiveQueueTest.list(9, 10, 11, 12, 13, 14, 15, 16))));
        for (int i = 0; i < 8; i++) {
            Integer s = wordQueue.poll();
            Assert.assertThat(s, CoreMatchers.equalTo((k++)));
        }
        testEmptyQueue(wordQueue);
        j = 1;
        k = 1;
        for (int i = 0; i < 8; i++) {
            dwordQueue.offer((0.1 + (j++)));
            dwordQueue.offer((0.1 + (j++)));
            Double s = dwordQueue.poll();
            Assert.assertThat(s, CoreMatchers.equalTo((0.1 + (k++))));
        }
        Assert.assertThat(dwordQueue.size(), CoreMatchers.is(8));
        Assert.assertThat(SingleConsumerPrimitiveQueueTest.list(dwordQueue), CoreMatchers.is(CoreMatchers.equalTo(SingleConsumerPrimitiveQueueTest.list(9.1, 10.1, 11.1, 12.1, 13.1, 14.1, 15.1, 16.1))));
        for (int i = 0; i < 8; i++) {
            Double s = dwordQueue.poll();
            Assert.assertThat(s, CoreMatchers.equalTo((0.1 + (k++))));
        }
        testEmptyQueue(dwordQueue);
    }

    @Test
    public void testIteratorRemove() {
        int j = 1;
        int k = 1;
        for (int i = 0; i < 9; i++)
            wordQueue.offer((j++));

        for (Iterator<Integer> it = wordQueue.iterator(); it.hasNext();) {
            it.next();
            if (((k++) % 2) == 0)
                it.remove();

        }
        Assert.assertThat(SingleConsumerPrimitiveQueueTest.list(wordQueue), CoreMatchers.is(CoreMatchers.equalTo(SingleConsumerPrimitiveQueueTest.list(1, 3, 5, 7, 9))));
        for (int i = 0; i < 4; i++)
            wordQueue.offer((j++));

        k = 1;
        for (Iterator<Integer> it = wordQueue.iterator(); it.hasNext();) {
            it.next();
            if (((k++) % 2) != 0)
                it.remove();

        }
        Assert.assertThat(SingleConsumerPrimitiveQueueTest.list(wordQueue), CoreMatchers.is(CoreMatchers.equalTo(SingleConsumerPrimitiveQueueTest.list(3, 7, 10, 12))));
        j = 1;
        k = 1;
        for (int i = 0; i < 9; i++)
            dwordQueue.offer((0.1 + (j++)));

        for (Iterator<Double> it = dwordQueue.iterator(); it.hasNext();) {
            it.next();
            if (((k++) % 2) == 0)
                it.remove();

        }
        Assert.assertThat(SingleConsumerPrimitiveQueueTest.list(dwordQueue), CoreMatchers.is(CoreMatchers.equalTo(SingleConsumerPrimitiveQueueTest.list(1.1, 3.1, 5.1, 7.1, 9.1))));
        for (int i = 0; i < 4; i++)
            dwordQueue.offer((0.1 + (j++)));

        k = 1;
        for (Iterator<Double> it = dwordQueue.iterator(); it.hasNext();) {
            it.next();
            if (((k++) % 2) != 0)
                it.remove();

        }
        Assert.assertThat(SingleConsumerPrimitiveQueueTest.list(dwordQueue), CoreMatchers.is(CoreMatchers.equalTo(SingleConsumerPrimitiveQueueTest.list(3.1, 7.1, 10.1, 12.1))));
    }

    @Test
    public void testIteratorRemoveFirst() {
        {
            wordQueue.offer(1);
            wordQueue.offer(2);
            wordQueue.offer(3);
            wordQueue.offer(4);
            Iterator<Integer> it = wordQueue.iterator();
            it.next();
            it.remove();
            it.next();
            it.remove();
            Assert.assertThat(wordQueue.size(), CoreMatchers.is(2));
            Assert.assertThat(SingleConsumerPrimitiveQueueTest.list(wordQueue), CoreMatchers.is(CoreMatchers.equalTo(SingleConsumerPrimitiveQueueTest.list(3, 4))));
            wordQueue.offer(5);
            wordQueue.offer(6);
            it.next();
            it.remove();
            it.next();
            it.remove();
            Assert.assertThat(wordQueue.size(), CoreMatchers.is(2));
            Assert.assertThat(SingleConsumerPrimitiveQueueTest.list(wordQueue), CoreMatchers.is(CoreMatchers.equalTo(SingleConsumerPrimitiveQueueTest.list(5, 6))));
        }
        {
            dwordQueue.offer(1.2);
            dwordQueue.offer(2.3);
            dwordQueue.offer(3.4);
            dwordQueue.offer(4.5);
            Iterator<Double> it = dwordQueue.iterator();
            it.next();
            it.remove();
            it.next();
            it.remove();
            Assert.assertThat(dwordQueue.size(), CoreMatchers.is(2));
            Assert.assertThat(SingleConsumerPrimitiveQueueTest.list(dwordQueue), CoreMatchers.is(CoreMatchers.equalTo(SingleConsumerPrimitiveQueueTest.list(3.4, 4.5))));
            dwordQueue.offer(5.6);
            dwordQueue.offer(6.7);
            it.next();
            it.remove();
            it.next();
            it.remove();
            Assert.assertThat(dwordQueue.size(), CoreMatchers.is(2));
            Assert.assertThat(SingleConsumerPrimitiveQueueTest.list(dwordQueue), CoreMatchers.is(CoreMatchers.equalTo(SingleConsumerPrimitiveQueueTest.list(5.6, 6.7))));
        }
    }

    @Test
    public void testIteratorRemoveLast() {
        {
            wordQueue.offer(1);
            wordQueue.offer(2);
            wordQueue.offer(3);
            wordQueue.offer(4);
            QueueIterator<Integer> it = wordQueue.iterator();
            while (it.hasNext())
                it.next();

            it.remove();
            it.reset();
            while (it.hasNext())
                it.next();

            it.remove();
            Assert.assertThat(wordQueue.size(), CoreMatchers.is(2));
            Assert.assertThat(SingleConsumerPrimitiveQueueTest.list(wordQueue), CoreMatchers.is(CoreMatchers.equalTo(SingleConsumerPrimitiveQueueTest.list(1, 2))));
            wordQueue.offer(5);
            wordQueue.offer(6);
            it.reset();
            while (it.hasNext())
                it.next();

            it.remove();
            it.reset();
            while (it.hasNext())
                it.next();

            it.remove();
            Assert.assertThat(wordQueue.size(), CoreMatchers.is(2));
            Assert.assertThat(SingleConsumerPrimitiveQueueTest.list(wordQueue), CoreMatchers.is(CoreMatchers.equalTo(SingleConsumerPrimitiveQueueTest.list(1, 2))));
        }
        {
            dwordQueue.offer(1.2);
            dwordQueue.offer(2.3);
            dwordQueue.offer(3.4);
            dwordQueue.offer(4.5);
            QueueIterator<Double> it = dwordQueue.iterator();
            while (it.hasNext())
                it.next();

            it.remove();
            it.reset();
            while (it.hasNext())
                it.next();

            it.remove();
            Assert.assertThat(dwordQueue.size(), CoreMatchers.is(2));
            Assert.assertThat(SingleConsumerPrimitiveQueueTest.list(dwordQueue), CoreMatchers.is(CoreMatchers.equalTo(SingleConsumerPrimitiveQueueTest.list(1.2, 2.3))));
            dwordQueue.offer(5.6);
            dwordQueue.offer(6.7);
            it.reset();
            while (it.hasNext())
                it.next();

            it.remove();
            it.reset();
            while (it.hasNext())
                it.next();

            it.remove();
            Assert.assertThat(dwordQueue.size(), CoreMatchers.is(2));
            Assert.assertThat(SingleConsumerPrimitiveQueueTest.list(dwordQueue), CoreMatchers.is(CoreMatchers.equalTo(SingleConsumerPrimitiveQueueTest.list(1.2, 2.3))));
        }
    }

    @Test
    public void testIteratorRemoveOnly() {
        {
            wordQueue.offer(1);
            Iterator<Integer> it = wordQueue.iterator();
            it.next();
            it.remove();
            testEmptyQueue(wordQueue);
            wordQueue.offer(1);
            Assert.assertThat(wordQueue.size(), CoreMatchers.is(1));
            Assert.assertThat(SingleConsumerPrimitiveQueueTest.list(wordQueue), CoreMatchers.is(CoreMatchers.equalTo(SingleConsumerPrimitiveQueueTest.list(1))));
            it = wordQueue.iterator();
            it.next();
            it.remove();
            testEmptyQueue(wordQueue);
            wordQueue.offer(1);
            Assert.assertThat(wordQueue.size(), CoreMatchers.is(1));
            Assert.assertThat(SingleConsumerPrimitiveQueueTest.list(wordQueue), CoreMatchers.is(CoreMatchers.equalTo(SingleConsumerPrimitiveQueueTest.list(1))));
        }
        {
            dwordQueue.offer(1.2);
            Iterator<Double> it = dwordQueue.iterator();
            it.next();
            it.remove();
            testEmptyQueue(dwordQueue);
            dwordQueue.offer(1.2);
            Assert.assertThat(dwordQueue.size(), CoreMatchers.is(1));
            Assert.assertThat(SingleConsumerPrimitiveQueueTest.list(dwordQueue), CoreMatchers.is(CoreMatchers.equalTo(SingleConsumerPrimitiveQueueTest.list(1.2))));
            it = dwordQueue.iterator();
            it.next();
            it.remove();
            testEmptyQueue(dwordQueue);
            dwordQueue.offer(1.2);
            Assert.assertThat(dwordQueue.size(), CoreMatchers.is(1));
            Assert.assertThat(SingleConsumerPrimitiveQueueTest.list(dwordQueue), CoreMatchers.is(CoreMatchers.equalTo(SingleConsumerPrimitiveQueueTest.list(1.2))));
        }
    }
}

