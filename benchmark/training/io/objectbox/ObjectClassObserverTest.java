/**
 * Copyright 2017 ObjectBox Ltd. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.objectbox;


import io.objectbox.reactive.DataObserver;
import io.objectbox.reactive.DataSubscription;
import io.objectbox.reactive.RunWithParam;
import io.objectbox.reactive.Scheduler;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;


public class ObjectClassObserverTest extends AbstractObjectBoxTest {
    CountDownLatch observerLatch = new CountDownLatch(1);

    final List<Class> classesWithChanges = new ArrayList<>();

    DataObserver objectClassObserver = new DataObserver<Class>() {
        @Override
        public void onData(Class objectClass) {
            classesWithChanges.add(objectClass);
            observerLatch.countDown();
        }
    };

    Runnable txRunnable = new Runnable() {
        @Override
        public void run() {
            putTestEntities(3);
            Box<TestEntityMinimal> boxMini = store.boxFor(TestEntityMinimal.class);
            boxMini.put(new TestEntityMinimal(), new TestEntityMinimal());
            Assert.assertEquals(0, classesWithChanges.size());
        }
    };

    @Test
    public void testTwoObjectClassesChanged_catchAllObserver() {
        testTwoObjectClassesChanged_catchAllObserver(false);
    }

    @Test
    public void testTwoObjectClassesChanged_catchAllObserverWeak() {
        testTwoObjectClassesChanged_catchAllObserver(true);
    }

    @Test
    public void testTwoObjectClassesChanged_oneClassObserver() throws InterruptedException {
        testTwoObjectClassesChanged_oneClassObserver(false);
    }

    @Test
    public void testTwoObjectClassesChanged_oneClassObserverWeak() throws InterruptedException {
        testTwoObjectClassesChanged_oneClassObserver(true);
    }

    @Test
    public void testTransform() throws InterruptedException {
        testTransform(null);
    }

    @Test
    public void testScheduler() throws InterruptedException {
        ObjectClassObserverTest.TestScheduler scheduler = new ObjectClassObserverTest.TestScheduler();
        store.subscribe().onlyChanges().on(scheduler).observer(objectClassObserver);
        runTxAndWaitForObservers(2);
        Assert.assertEquals(2, scheduler.counter());
        Assert.assertEquals(2, classesWithChanges.size());
    }

    @Test
    public void testTransformerWithScheduler() throws InterruptedException {
        ObjectClassObserverTest.TestScheduler scheduler = new ObjectClassObserverTest.TestScheduler();
        testTransform(scheduler);
        Assert.assertEquals(2, scheduler.counter());
    }

    private static class TestScheduler implements Scheduler {
        AtomicInteger counter = new AtomicInteger();

        int counter() {
            return counter.intValue();
        }

        @Override
        public <T> void run(RunWithParam runnable, T param) {
            counter.incrementAndGet();
            runnable.run(param);
        }
    }

    @Test
    public void testTransformError() throws InterruptedException {
        testTransformError(null);
    }

    @Test
    public void testTransformErrorWithScheduler() throws InterruptedException {
        ObjectClassObserverTest.TestScheduler scheduler = new ObjectClassObserverTest.TestScheduler();
        testTransformError(scheduler);
        Assert.assertEquals(2, scheduler.counter());
    }

    @Test
    public void testObserverError() throws InterruptedException {
        testObserverError(null);
    }

    @Test
    public void testObserverErrorWithScheduler() throws InterruptedException {
        ObjectClassObserverTest.TestScheduler scheduler = new ObjectClassObserverTest.TestScheduler();
        testObserverError(scheduler);
        Assert.assertEquals((2 + 2), scheduler.counter());// 2 observer + 2 error observer calls

    }

    @Test
    public void testForObserverLeaks() {
        testForObserverLeaks(false, false);
    }

    @Test
    public void testForObserverLeaks_weak() {
        testForObserverLeaks(false, true);
    }

    @Test
    public void testForObserverLeaks_wrapped() {
        testForObserverLeaks(true, false);
    }

    @Test
    public void testForObserverLeaks_wrappedWeak() {
        testForObserverLeaks(true, true);
    }

    @Test
    public void testSingle() {
        testSingle(false, false);
    }

    @Test
    public void testSingle_wrapped() {
        testSingle(false, true);
    }

    @Test
    public void testSingle_weak() {
        testSingle(true, false);
    }

    @Test
    public void testSingle_weakWrapped() {
        testSingle(true, true);
    }

    @Test
    public void testSingleCancelSubscription() throws InterruptedException {
        DataSubscription subscription = store.subscribe().single().transform(new io.objectbox.reactive.DataTransformer<Class, Class>() {
            @Override
            @SuppressWarnings("NullableProblems")
            public Class transform(Class source) throws Exception {
                Thread.sleep(20);
                return source;
            }
        }).observer(objectClassObserver);
        subscription.cancel();
        Thread.sleep(40);
        assertNoStaleObservers();
    }
}

