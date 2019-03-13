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
import java.util.NoSuchElementException;
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
public class SingleConsumerQueueTest {
    @Rule
    public TestName name = new TestName();

    @Rule
    public TestRule watchman = TestUtil.WATCHMAN;

    final SingleConsumerQueue<String> queue;

    // public SingleConsumerQueueTest() {
    // this.queue = new SingleConsumerLinkedArrayObjectQueue<String>();
    // }
    public SingleConsumerQueueTest(int queueType) {
        switch (queueType) {
            case 1 :
                this.queue = new SingleConsumerArrayObjectQueue<String>(10);
                break;
            case 2 :
                this.queue = new SingleConsumerLinkedObjectQueue<String>();
                break;
            case 3 :
                this.queue = new SingleConsumerLinkedArrayObjectQueue<String>();
                break;
            default :
                throw new AssertionError();
        }
    }

    @Test
    public void testEmptyQueue() {
        Assert.assertThat(queue.size(), CoreMatchers.is(0));
        Assert.assertTrue(queue.isEmpty());
        Assert.assertThat(queue.peek(), CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertThat(queue.poll(), CoreMatchers.is(CoreMatchers.nullValue()));
        try {
            queue.element();
            Assert.fail();
        } catch (NoSuchElementException e) {
        }
        try {
            queue.remove();
            Assert.fail();
        } catch (NoSuchElementException e) {
        }
    }

    @Test
    public void testOffer() {
        queue.offer("one");
        queue.offer("two");
        queue.offer("three");
        Assert.assertThat(queue.isEmpty(), CoreMatchers.is(false));
        Assert.assertThat(queue.size(), CoreMatchers.is(3));
        Assert.assertThat(SingleConsumerQueueTest.list(queue), CoreMatchers.is(CoreMatchers.equalTo(SingleConsumerQueueTest.list("one", "two", "three"))));
    }

    @Test
    public void testPoll() {
        int j = 1;
        int k = 1;
        for (int i = 0; i < 8; i++) {
            queue.offer(("x" + (j++)));
            queue.offer(("x" + (j++)));
            String s = queue.poll();
            Assert.assertThat(s, CoreMatchers.equalTo(("x" + (k++))));
        }
        Assert.assertThat(queue.size(), CoreMatchers.is(8));
        Assert.assertThat(SingleConsumerQueueTest.list(queue), CoreMatchers.is(CoreMatchers.equalTo(SingleConsumerQueueTest.list("x9", "x10", "x11", "x12", "x13", "x14", "x15", "x16"))));
        for (int i = 0; i < 8; i++) {
            String s = queue.poll();
            Assert.assertThat(s, CoreMatchers.equalTo(("x" + (k++))));
        }
        testEmptyQueue();
    }

    @Test
    public void testIteratorRemove() {
        int j = 1;
        int k = 1;
        for (int i = 0; i < 9; i++)
            queue.offer(("x" + (j++)));

        for (Iterator<String> it = queue.iterator(); it.hasNext();) {
            it.next();
            if (((k++) % 2) == 0)
                it.remove();

        }
        Assert.assertThat(SingleConsumerQueueTest.list(queue), CoreMatchers.is(CoreMatchers.equalTo(SingleConsumerQueueTest.list("x1", "x3", "x5", "x7", "x9"))));
        for (int i = 0; i < 4; i++)
            queue.offer(("x" + (j++)));

        k = 1;
        for (Iterator<String> it = queue.iterator(); it.hasNext();) {
            it.next();
            if (((k++) % 2) != 0)
                it.remove();

        }
        Assert.assertThat(SingleConsumerQueueTest.list(queue), CoreMatchers.is(CoreMatchers.equalTo(SingleConsumerQueueTest.list("x3", "x7", "x10", "x12"))));
    }

    @Test
    public void testIteratorRemoveFirst() {
        queue.offer("one");
        queue.offer("two");
        queue.offer("three");
        queue.offer("four");
        Iterator<String> it = queue.iterator();
        it.next();
        it.remove();
        it.next();
        it.remove();
        Assert.assertThat(queue.size(), CoreMatchers.is(2));
        Assert.assertThat(SingleConsumerQueueTest.list(queue), CoreMatchers.is(CoreMatchers.equalTo(SingleConsumerQueueTest.list("three", "four"))));
        queue.offer("five");
        queue.offer("six");
        it.next();
        it.remove();
        it.next();
        it.remove();
        Assert.assertThat(queue.size(), CoreMatchers.is(2));
        Assert.assertThat(SingleConsumerQueueTest.list(queue), CoreMatchers.is(CoreMatchers.equalTo(SingleConsumerQueueTest.list("five", "six"))));
    }

    @Test
    public void testIteratorRemoveLast() {
        queue.offer("one");
        queue.offer("two");
        queue.offer("three");
        queue.offer("four");
        QueueIterator<String> it = queue.iterator();
        while (it.hasNext())
            it.next();

        it.remove();
        it.reset();
        while (it.hasNext())
            it.next();

        it.remove();
        Assert.assertThat(queue.size(), CoreMatchers.is(2));
        Assert.assertThat(SingleConsumerQueueTest.list(queue), CoreMatchers.is(CoreMatchers.equalTo(SingleConsumerQueueTest.list("one", "two"))));
        queue.offer("five");
        queue.offer("six");
        it.reset();
        while (it.hasNext())
            it.next();

        it.remove();
        it.reset();
        while (it.hasNext())
            it.next();

        it.remove();
        Assert.assertThat(queue.size(), CoreMatchers.is(2));
        Assert.assertThat(SingleConsumerQueueTest.list(queue), CoreMatchers.is(CoreMatchers.equalTo(SingleConsumerQueueTest.list("one", "two"))));
    }

    @Test
    public void testIteratorRemoveOnly() {
        queue.offer("one");
        Iterator<String> it = queue.iterator();
        it.next();
        it.remove();
        testEmptyQueue();
        queue.offer("one");
        Assert.assertThat(queue.size(), CoreMatchers.is(1));
        Assert.assertThat(SingleConsumerQueueTest.list(queue), CoreMatchers.is(CoreMatchers.equalTo(SingleConsumerQueueTest.list("one"))));
        it = queue.iterator();
        it.next();
        it.remove();
        testEmptyQueue();
        queue.offer("one");
        Assert.assertThat(queue.size(), CoreMatchers.is(1));
        Assert.assertThat(SingleConsumerQueueTest.list(queue), CoreMatchers.is(CoreMatchers.equalTo(SingleConsumerQueueTest.list("one"))));
    }
}

