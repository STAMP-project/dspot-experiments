/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.client.ringbuffer;


import com.hazelcast.client.test.TestHazelcastFactory;
import com.hazelcast.client.test.ringbuffer.filter.StartsWithStringFilter;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.ringbuffer.OverflowPolicy;
import com.hazelcast.ringbuffer.ReadResultSet;
import com.hazelcast.ringbuffer.Ringbuffer;
import com.hazelcast.ringbuffer.StaleSequenceException;
import com.hazelcast.ringbuffer.impl.client.PortableReadResultSet;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.util.RootCauseMatcher;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category(QuickTest.class)
public class RingbufferTest extends HazelcastTestSupport {
    public static final int CAPACITY = 10;

    private final TestHazelcastFactory hazelcastFactory = new TestHazelcastFactory();

    private HazelcastInstance client;

    private HazelcastInstance server;

    private Ringbuffer<String> clientRingbuffer;

    private Ringbuffer<String> serverRingbuffer;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void readManyAsync_whenHitsStale_shouldNotBeBlocked() throws Exception {
        ICompletableFuture<ReadResultSet<String>> f = clientRingbuffer.readManyAsync(0, 1, 10, null);
        serverRingbuffer.addAllAsync(Arrays.asList("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"), OverflowPolicy.OVERWRITE);
        expectedException.expect(new RootCauseMatcher(StaleSequenceException.class));
        f.get();
    }

    @Test
    public void readOne_whenHitsStale_shouldNotBeBlocked() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        Thread consumer = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    clientRingbuffer.readOne(0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (StaleSequenceException e) {
                    latch.countDown();
                }
            }
        });
        consumer.start();
        serverRingbuffer.addAllAsync(Arrays.asList("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"), OverflowPolicy.OVERWRITE);
        assertOpenEventually(latch);
    }

    @Test
    public void headSequence() {
        for (int k = 0; k < (2 * (RingbufferTest.CAPACITY)); k++) {
            serverRingbuffer.add("foo");
        }
        Assert.assertEquals(serverRingbuffer.headSequence(), clientRingbuffer.headSequence());
    }

    @Test
    public void tailSequence() {
        for (int k = 0; k < (2 * (RingbufferTest.CAPACITY)); k++) {
            serverRingbuffer.add("foo");
        }
        Assert.assertEquals(serverRingbuffer.tailSequence(), clientRingbuffer.tailSequence());
    }

    @Test
    public void size() {
        serverRingbuffer.add("foo");
        Assert.assertEquals(serverRingbuffer.size(), clientRingbuffer.size());
    }

    @Test
    public void capacity() {
        Assert.assertEquals(serverRingbuffer.capacity(), clientRingbuffer.capacity());
    }

    @Test
    public void remainingCapacity() {
        serverRingbuffer.add("foo");
        Assert.assertEquals(serverRingbuffer.remainingCapacity(), clientRingbuffer.remainingCapacity());
    }

    @Test
    public void add() throws Exception {
        clientRingbuffer.add("foo");
        Assert.assertEquals("foo", serverRingbuffer.readOne(0));
    }

    @Test
    public void addAsync() throws Exception {
        Future<Long> f = clientRingbuffer.addAsync("foo", OverflowPolicy.OVERWRITE);
        Long result = f.get();
        Assert.assertEquals(new Long(serverRingbuffer.headSequence()), result);
        Assert.assertEquals("foo", serverRingbuffer.readOne(0));
        Assert.assertEquals(0, serverRingbuffer.headSequence());
        Assert.assertEquals(0, serverRingbuffer.tailSequence());
    }

    @Test
    public void addAll() throws Exception {
        Future<Long> f = clientRingbuffer.addAllAsync(Arrays.asList("foo", "bar"), OverflowPolicy.OVERWRITE);
        Long result = f.get();
        Assert.assertEquals(new Long(serverRingbuffer.tailSequence()), result);
        Assert.assertEquals("foo", serverRingbuffer.readOne(0));
        Assert.assertEquals("bar", serverRingbuffer.readOne(1));
        Assert.assertEquals(0, serverRingbuffer.headSequence());
        Assert.assertEquals(1, serverRingbuffer.tailSequence());
    }

    @Test
    public void readOne() throws Exception {
        serverRingbuffer.add("foo");
        Assert.assertEquals("foo", clientRingbuffer.readOne(0));
    }

    @Test
    public void readManyAsync_noFilter() throws Exception {
        serverRingbuffer.add("1");
        serverRingbuffer.add("2");
        serverRingbuffer.add("3");
        ICompletableFuture<ReadResultSet<String>> f = clientRingbuffer.readManyAsync(0, 3, 3, null);
        ReadResultSet rs = f.get();
        assertInstanceOf(PortableReadResultSet.class, rs);
        Assert.assertEquals(3, rs.readCount());
        Assert.assertEquals("1", rs.get(0));
        Assert.assertEquals("2", rs.get(1));
        Assert.assertEquals("3", rs.get(2));
    }

    // checks if the max count works. So if more results are available than needed, the surplus results should not be read.
    @Test
    public void readManyAsync_maxCount() throws Exception {
        serverRingbuffer.add("1");
        serverRingbuffer.add("2");
        serverRingbuffer.add("3");
        serverRingbuffer.add("4");
        serverRingbuffer.add("5");
        serverRingbuffer.add("6");
        ICompletableFuture<ReadResultSet<String>> f = clientRingbuffer.readManyAsync(0, 3, 3, null);
        ReadResultSet rs = f.get();
        assertInstanceOf(PortableReadResultSet.class, rs);
        Assert.assertEquals(3, rs.readCount());
        Assert.assertEquals("1", rs.get(0));
        Assert.assertEquals("2", rs.get(1));
        Assert.assertEquals("3", rs.get(2));
    }

    @Test
    public void readManyAsync_withFilter() throws Exception {
        serverRingbuffer.add("good1");
        serverRingbuffer.add("bad1");
        serverRingbuffer.add("good2");
        serverRingbuffer.add("bad2");
        serverRingbuffer.add("good3");
        serverRingbuffer.add("bad3");
        ICompletableFuture<ReadResultSet<String>> f = clientRingbuffer.readManyAsync(0, 3, 3, new StartsWithStringFilter("good"));
        ReadResultSet rs = f.get();
        assertInstanceOf(PortableReadResultSet.class, rs);
        Assert.assertEquals(5, rs.readCount());
        Assert.assertEquals("good1", rs.get(0));
        Assert.assertEquals("good2", rs.get(1));
        Assert.assertEquals("good3", rs.get(2));
    }

    // checks if the max count works in combination with a filter.
    // So if more results are available than needed, the surplus results should not be read.
    @Test
    public void readManyAsync_withFilter_andMaxCount() throws Exception {
        serverRingbuffer.add("good1");
        serverRingbuffer.add("bad1");
        serverRingbuffer.add("good2");
        serverRingbuffer.add("bad2");
        serverRingbuffer.add("good3");
        serverRingbuffer.add("bad3");
        serverRingbuffer.add("good4");
        serverRingbuffer.add("bad4");
        ICompletableFuture<ReadResultSet<String>> f = clientRingbuffer.readManyAsync(0, 3, 3, new StartsWithStringFilter("good"));
        ReadResultSet rs = f.get();
        assertInstanceOf(PortableReadResultSet.class, rs);
        Assert.assertEquals(5, rs.readCount());
        Assert.assertEquals("good1", rs.get(0));
        Assert.assertEquals("good2", rs.get(1));
        Assert.assertEquals("good3", rs.get(2));
    }
}

