/**
 * Copyright (c) 2016-present, RxJava Contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See
 * the License for the specific language governing permissions and limitations under the License.
 */
/**
 * The code was inspired by the similarly named JCTools class:
 * https://github.com/JCTools/JCTools/blob/master/jctools-core/src/main/java/org/jctools/queues/atomic
 */
package io.reactivex.internal.queue;


import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;
import org.junit.Assert;
import org.junit.Test;


public class SimpleQueueTest {
    @Test(expected = NullPointerException.class)
    public void spscArrayQueueNull() {
        SpscArrayQueue<Object> q = new SpscArrayQueue<Object>(16);
        q.offer(null);
    }

    @Test(expected = NullPointerException.class)
    public void spscLinkedArrayQueueNull() {
        SpscLinkedArrayQueue<Object> q = new SpscLinkedArrayQueue<Object>(16);
        q.offer(null);
    }

    @Test(expected = NullPointerException.class)
    public void mpscLinkedQueueNull() {
        MpscLinkedQueue<Object> q = new MpscLinkedQueue<Object>();
        q.offer(null);
    }

    @Test
    public void spscArrayQueueBiOffer() {
        SpscArrayQueue<Object> q = new SpscArrayQueue<Object>(16);
        q.offer(1, 2);
        Assert.assertEquals(1, q.poll());
        Assert.assertEquals(2, q.poll());
        Assert.assertNull(q.poll());
    }

    @Test
    public void spscLinkedArrayQueueBiOffer() {
        SpscLinkedArrayQueue<Object> q = new SpscLinkedArrayQueue<Object>(16);
        q.offer(1, 2);
        Assert.assertEquals(1, q.poll());
        Assert.assertEquals(2, q.poll());
        Assert.assertNull(q.poll());
    }

    @Test
    public void mpscLinkedQueueBiOffer() {
        MpscLinkedQueue<Object> q = new MpscLinkedQueue<Object>();
        q.offer(1, 2);
        Assert.assertEquals(1, q.poll());
        Assert.assertEquals(2, q.poll());
        Assert.assertNull(q.poll());
    }

    @Test
    public void spscBiOfferCapacity() {
        SpscArrayQueue<Integer> q = new SpscArrayQueue<Integer>(8);
        Assert.assertTrue(q.offer(1, 2));
        Assert.assertTrue(q.offer(3, 4));
        Assert.assertTrue(q.offer(5, 6));
        Assert.assertTrue(q.offer(7));
        Assert.assertFalse(q.offer(8, 9));
        Assert.assertFalse(q.offer(9, 10));
    }

    @Test
    public void spscLinkedNewBufferPeek() {
        SpscLinkedArrayQueue<Integer> q = new SpscLinkedArrayQueue<Integer>(8);
        Assert.assertTrue(q.offer(1, 2));
        Assert.assertTrue(q.offer(3, 4));
        Assert.assertTrue(q.offer(5, 6));
        Assert.assertTrue(q.offer(7, 8));// this should trigger a new buffer

        for (int i = 0; i < 8; i++) {
            Assert.assertEquals((i + 1), q.peek().intValue());
            Assert.assertEquals((i + 1), q.poll().intValue());
        }
        Assert.assertNull(q.peek());
        Assert.assertNull(q.poll());
    }

    @Test
    public void mpscOfferPollRace() throws Exception {
        final MpscLinkedQueue<Integer> q = new MpscLinkedQueue<Integer>();
        final AtomicInteger c = new AtomicInteger(3);
        Thread t1 = new Thread(new Runnable() {
            int i;

            @Override
            public void run() {
                c.decrementAndGet();
                while ((c.get()) != 0) {
                } 
                while (((i)++) < 10000) {
                    q.offer(i);
                } 
            }
        });
        t1.start();
        Thread t2 = new Thread(new Runnable() {
            int i = 10000;

            @Override
            public void run() {
                c.decrementAndGet();
                while ((c.get()) != 0) {
                } 
                while (((i)++) < 10000) {
                    q.offer(i);
                } 
            }
        });
        t2.start();
        Runnable r3 = new Runnable() {
            int i = 20000;

            @Override
            public void run() {
                c.decrementAndGet();
                while ((c.get()) != 0) {
                } 
                while ((--(i)) > 0) {
                    q.poll();
                } 
            }
        };
        r3.run();
        t1.join();
        t2.join();
    }

    @Test
    public void spscLinkedArrayQueueNoNepotism() {
        SpscLinkedArrayQueue<Integer> q = new SpscLinkedArrayQueue<Integer>(16);
        AtomicReferenceArray<Object> ara = q.producerBuffer;
        for (int i = 0; i < 20; i++) {
            q.offer(i);
        }
        Assert.assertNotNull(ara.get(16));
        for (int i = 0; i < 20; i++) {
            Assert.assertEquals(i, q.poll().intValue());
        }
        Assert.assertNull(ara.get(16));
    }
}

