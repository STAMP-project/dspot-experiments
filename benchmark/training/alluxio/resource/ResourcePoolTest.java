/**
 * The Alluxio Open Foundation licenses this work under the Apache License, version 2.0
 * (the "License"). You may not use this work except in compliance with the License, which is
 * available at www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied, as more fully set forth in the License.
 *
 * See the NOTICE file distributed with this work for information regarding copyright ownership.
 */
package alluxio.resource;


import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;


/**
 * Unit test for {@code ResourcePool} class.
 */
public final class ResourcePoolTest {
    /**
     * The exception expected to be thrown.
     */
    @Rule
    public ExpectedException mThrown = ExpectedException.none();

    /**
     * Constructor for {@code ResourcePool} class.
     */
    class TestResourcePool extends ResourcePool<Integer> {
        int mPort = 0;

        /**
         * Creates a {@code ResourcePool} instance with a specified capacity.
         *
         * @param maxCapacity
         * 		the maximum of resources in this pool
         */
        public TestResourcePool(int maxCapacity) {
            super(maxCapacity);
        }

        /**
         * Creates a {@code ResourcePool} instance with a specified capacity and blocking queue.
         *
         * @param maxCapacity
         * 		the maximum of resources in this pool
         * @param resources
         * 		blocking queue to use
         */
        public TestResourcePool(int maxCapacity, ConcurrentLinkedQueue<Integer> resources) {
            super(maxCapacity, resources);
        }

        @Override
        public void close() {
            // no ops
        }

        @Override
        protected Integer createNewResource() {
            (mPort)++;
            return mPort;
        }
    }

    /**
     * Tests the normal acquiration of resource pools.
     */
    @Test
    public void resourcePoolNormal() {
        ResourcePoolTest.TestResourcePool testPool = new ResourcePoolTest.TestResourcePool(2);
        int resource1 = testPool.acquire();
        release(resource1);
        int resource2 = testPool.acquire();
        Assert.assertEquals(resource1, resource2);
    }

    /**
     * Tests that an exception is thrown if the resource pool is used more than its size can take.
     */
    @Test
    public void resourcePoolBlocking() throws InterruptedException {
        mThrown.expect(RuntimeException.class);
        final int POOL_SIZE = 2;
        @SuppressWarnings("unchecked")
        ConcurrentLinkedQueue<Integer> queue = Mockito.mock(ConcurrentLinkedQueue.class);
        ResourcePoolTest.TestResourcePool testPool = new ResourcePoolTest.TestResourcePool(POOL_SIZE, queue);
        Mockito.when(queue.isEmpty()).thenReturn(true);
        Mockito.when(queue.poll()).thenThrow(new InterruptedException());
        for (int i = 0; i < (POOL_SIZE + 1); i++) {
            testPool.acquire();
        }
    }

    /**
     * Tests that an exception is thrown if the timestamps overflows and terminate before 5 seconds.
     */
    @Test
    public void resourcePoolTimestampOverflow() {
        Callable<Integer> task = () -> {
            ConcurrentLinkedQueue<Integer> queue = Mockito.mock(ConcurrentLinkedQueue.class);
            ResourcePoolTest.TestResourcePool testPool = new ResourcePoolTest.TestResourcePool(2, queue);
            Integer resource1 = acquire(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
            Integer resource2 = acquire(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
            Assert.assertEquals(new Integer(1), resource1);
            Assert.assertEquals(new Integer(2), resource2);
            Integer resource3 = acquire(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
            Assert.assertNull(resource3);
            return resource3;
        };
        ExecutorService executor = Executors.newFixedThreadPool(1);
        Future<Integer> future = executor.submit(task);
        boolean timeout = false;
        try {
            future.get(5000, TimeUnit.MILLISECONDS);
        } catch (Exception ex) {
            timeout = true;
        }
        Assert.assertTrue(timeout);
        Assert.assertFalse(future.isDone());
        future.cancel(true);
    }
}

