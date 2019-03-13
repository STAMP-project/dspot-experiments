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
package com.hazelcast.internal.util.futures;


import ChainingFuture.ExceptionHandler;
import com.hazelcast.core.HazelcastException;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.core.MemberLeftException;
import com.hazelcast.core.OperationTimeoutException;
import com.hazelcast.internal.cluster.ClusterService;
import com.hazelcast.internal.util.iterator.RestartingMemberIterator;
import com.hazelcast.logging.ILogger;
import com.hazelcast.spi.exception.TargetNotMemberException;
import com.hazelcast.spi.impl.AbstractCompletableFuture;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ChainingFutureTest extends HazelcastTestSupport {
    private Executor executor = new ChainingFutureTest.LocalExecutor();

    private ILogger logger = Mockito.mock(ILogger.class);

    private ClusterService clusterService = Mockito.mock(ClusterService.class);

    private RestartingMemberIterator repairingIterator;

    @Test
    public void testCompletesOnlyWhenTheLastFutureCompletes() {
        ChainingFutureTest.MyCompletableFuture<Object> future1 = newFuture();
        ChainingFutureTest.MyCompletableFuture<Object> future2 = newFuture();
        ChainingFutureTest.MyCompletableFuture<Object> future3 = newFuture();
        ChainingFutureTest.CountingIterator<ICompletableFuture<Object>> iterator = toIterator(future1, future2, future3);
        ChainingFuture.ExceptionHandler handler = repairingIterator;
        ChainingFuture<Object> future = new ChainingFuture<Object>(iterator, executor, handler, logger);
        Assert.assertEquals(1, iterator.getHasNextCounter());
        Assert.assertEquals(1, iterator.getNextCounter());
        Assert.assertFalse(future.isDone());
        future1.complete("foo");
        Assert.assertEquals(2, iterator.getHasNextCounter());
        Assert.assertEquals(2, iterator.getNextCounter());
        Assert.assertFalse(future.isDone());
        future2.complete("foo");
        Assert.assertFalse(future.isDone());
        Assert.assertEquals(3, iterator.getHasNextCounter());
        Assert.assertEquals(3, iterator.getNextCounter());
        future3.complete("foo");
        Assert.assertTrue(future.isDone());
        Assert.assertEquals(4, iterator.getHasNextCounter());
        Assert.assertEquals(3, iterator.getNextCounter());
    }

    @Test
    public void testTopologyChangesExceptionsAreIgnored() {
        ChainingFutureTest.MyCompletableFuture<Object> future1 = newFuture();
        ChainingFutureTest.MyCompletableFuture<Object> future2 = newFuture();
        ChainingFutureTest.MyCompletableFuture<Object> future3 = newFuture();
        ChainingFutureTest.CountingIterator<ICompletableFuture<Object>> iterator = toIterator(future1, future2, future3);
        ChainingFuture.ExceptionHandler handler = repairingIterator;
        ChainingFuture<Object> future = new ChainingFuture<Object>(iterator, executor, handler, logger);
        Assert.assertEquals(1, iterator.getHasNextCounter());
        Assert.assertEquals(1, iterator.getNextCounter());
        Assert.assertFalse(future.isDone());
        future1.complete(new MemberLeftException("this should be ignored"));
        Assert.assertEquals(2, iterator.getHasNextCounter());
        Assert.assertEquals(2, iterator.getNextCounter());
        Assert.assertFalse(future.isDone());
        future2.complete(new TargetNotMemberException("this should be ignored"));
        Assert.assertEquals(3, iterator.getHasNextCounter());
        Assert.assertEquals(3, iterator.getNextCounter());
        Assert.assertFalse(future.isDone());
        future3.complete("foo");
        Assert.assertTrue(future.isDone());
        Assert.assertEquals(4, iterator.getHasNextCounter());
        Assert.assertEquals(3, iterator.getNextCounter());
    }

    @Test(expected = OperationTimeoutException.class)
    public void testNonTopologyRelatedExceptionArePropagated() throws InterruptedException, ExecutionException {
        ChainingFutureTest.MyCompletableFuture<Object> future1 = newFuture();
        ChainingFutureTest.MyCompletableFuture<Object> future2 = newFuture();
        ChainingFutureTest.MyCompletableFuture<Object> future3 = newFuture();
        ChainingFutureTest.CountingIterator<ICompletableFuture<Object>> iterator = toIterator(future1, future2, future3);
        ChainingFuture.ExceptionHandler handler = repairingIterator;
        ChainingFuture<Object> future = new ChainingFuture<Object>(iterator, executor, handler, logger);
        Assert.assertEquals(1, iterator.getHasNextCounter());
        Assert.assertEquals(1, iterator.getNextCounter());
        Assert.assertFalse(future.isDone());
        future1.complete(new OperationTimeoutException());
        Assert.assertTrue(future.isDone());
        future.get();
    }

    @Test(expected = HazelcastException.class)
    public void testIteratingExceptionArePropagated() throws InterruptedException, ExecutionException {
        ChainingFutureTest.MyCompletableFuture<Object> future1 = newFuture();
        ChainingFutureTest.MyCompletableFuture<Object> future2 = newFuture();
        ChainingFutureTest.MyCompletableFuture<Object> future3 = newFuture();
        ChainingFutureTest.CountingIterator<ICompletableFuture<Object>> iterator = toIterator(future1, future2, future3);
        ChainingFuture.ExceptionHandler handler = repairingIterator;
        ChainingFuture<Object> future = new ChainingFuture<Object>(iterator, executor, handler, logger);
        Assert.assertEquals(1, iterator.getHasNextCounter());
        Assert.assertEquals(1, iterator.getNextCounter());
        Assert.assertFalse(future.isDone());
        iterator.exceptionToThrow = new HazelcastException("iterating exception");
        future1.complete("foo");
        Assert.assertTrue(future.isDone());
        future.get();
    }

    @Test
    public void testEmptyIterator() {
        ChainingFutureTest.CountingIterator<ICompletableFuture<Object>> iterator = toIterator();
        ChainingFuture.ExceptionHandler handler = repairingIterator;
        ChainingFuture<Object> future = new ChainingFuture<Object>(iterator, executor, handler, logger);
        Assert.assertTrue(future.isDone());
    }

    private static class CountingIterator<T> implements Iterator<T> {
        private final Iterator<T> innerIterator;

        private AtomicInteger hasNextCounter = new AtomicInteger();

        private AtomicInteger nextCounter = new AtomicInteger();

        private volatile RuntimeException exceptionToThrow;

        private CountingIterator(Iterator<T> innerIterator) {
            this.innerIterator = innerIterator;
        }

        @Override
        public boolean hasNext() {
            hasNextCounter.incrementAndGet();
            throwExceptionIfSet();
            return innerIterator.hasNext();
        }

        private void throwExceptionIfSet() {
            if ((exceptionToThrow) != null) {
                throw exceptionToThrow;
            }
        }

        @Override
        public T next() {
            nextCounter.incrementAndGet();
            throwExceptionIfSet();
            return innerIterator.next();
        }

        @Override
        public void remove() {
            innerIterator.remove();
        }

        public int getNextCounter() {
            return nextCounter.get();
        }

        public int getHasNextCounter() {
            return hasNextCounter.get();
        }
    }

    private static class LocalExecutor implements Executor {
        @Override
        public void execute(Runnable command) {
            command.run();
        }
    }

    private static class MyCompletableFuture<T> extends AbstractCompletableFuture<T> {
        protected MyCompletableFuture(Executor defaultExecutor, ILogger logger) {
            super(defaultExecutor, logger);
        }

        public void complete(Object o) {
            ChainingFutureTest.MyCompletableFuture.setResult(o);
        }
    }
}

