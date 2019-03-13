/**
 * Copyright 2014 Netflix, Inc.
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
package com.netflix.hystrix;


import HystrixCommandGroupKey.Factory;
import HystrixEventType.COLLAPSED;
import HystrixEventType.EMIT;
import HystrixEventType.SUCCESS;
import HystrixObservableCollapser.Scope.REQUEST;
import HystrixObservableCommand.Setter;
import com.hystrix.junit.HystrixRequestContextRule;
import com.netflix.hystrix.HystrixCollapser.CollapsedRequest;
import com.netflix.hystrix.collapser.CollapserTimer;
import com.netflix.hystrix.collapser.RealCollapserTimer;
import com.netflix.hystrix.strategy.concurrency.HystrixContextRunnable;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;


public class HystrixObservableCollapserTest {
    private static Action1<CollapsedRequest<String, String>> onMissingError = new Action1<CollapsedRequest<String, String>>() {
        @Override
        public void call(CollapsedRequest<String, String> collapsedReq) {
            collapsedReq.setException(new IllegalStateException("must have a value"));
        }
    };

    private static Action1<CollapsedRequest<String, String>> onMissingThrow = new Action1<CollapsedRequest<String, String>>() {
        @Override
        public void call(CollapsedRequest<String, String> collapsedReq) {
            throw new RuntimeException("synchronous error in onMissingResponse handler");
        }
    };

    private static Action1<CollapsedRequest<String, String>> onMissingComplete = new Action1<CollapsedRequest<String, String>>() {
        @Override
        public void call(CollapsedRequest<String, String> collapsedReq) {
            collapsedReq.setComplete();
        }
    };

    private static Action1<CollapsedRequest<String, String>> onMissingIgnore = new Action1<CollapsedRequest<String, String>>() {
        @Override
        public void call(CollapsedRequest<String, String> collapsedReq) {
            // do nothing
        }
    };

    private static Action1<CollapsedRequest<String, String>> onMissingFillIn = new Action1<CollapsedRequest<String, String>>() {
        @Override
        public void call(CollapsedRequest<String, String> collapsedReq) {
            collapsedReq.setResponse("fillin");
        }
    };

    private static Func1<String, String> prefixMapper = new Func1<String, String>() {
        @Override
        public String call(String s) {
            return s.substring(0, s.indexOf(":"));
        }
    };

    private static Func1<String, String> map1To3And2To2 = new Func1<String, String>() {
        @Override
        public String call(String s) {
            String prefix = s.substring(0, s.indexOf(":"));
            if (prefix.equals("2")) {
                return "2";
            } else {
                return "3";
            }
        }
    };

    private static Func1<String, String> mapWithErrorOn1 = new Func1<String, String>() {
        @Override
        public String call(String s) {
            String prefix = s.substring(0, s.indexOf(":"));
            if (prefix.equals("1")) {
                throw new RuntimeException("poorly implemented demultiplexer");
            } else {
                return "2";
            }
        }
    };

    @Rule
    public HystrixRequestContextRule ctx = new HystrixRequestContextRule();

    private static ExecutorService threadPool = new ThreadPoolExecutor(100, 100, 10, TimeUnit.MINUTES, new SynchronousQueue<Runnable>());

    @Test
    public void testTwoRequests() throws Exception {
        HystrixCollapserTest.TestCollapserTimer timer = new HystrixCollapserTest.TestCollapserTimer();
        HystrixObservableCollapser<String, String, String, String> collapser1 = new HystrixObservableCollapserTest.TestRequestCollapser(timer, 1);
        HystrixObservableCollapser<String, String, String, String> collapser2 = new HystrixObservableCollapserTest.TestRequestCollapser(timer, 2);
        Future<String> response1 = collapser1.observe().toBlocking().toFuture();
        Future<String> response2 = collapser2.observe().toBlocking().toFuture();
        timer.incrementTime(10);// let time pass that equals the default delay/period

        Assert.assertEquals("1", response1.get());
        Assert.assertEquals("2", response2.get());
        Assert.assertEquals(1, HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().size());
        Iterator<HystrixInvokableInfo<?>> cmdIterator = HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().iterator();
        Assert.assertEquals(2, cmdIterator.next().getNumberCollapsed());
    }

    @Test
    public void stressTestRequestCollapser() throws Exception {
        for (int i = 0; i < 10; i++) {
            init();
            testTwoRequests();
            ctx.reset();
        }
    }

    @Test
    public void testTwoRequestsWhichShouldEachEmitTwice() throws Exception {
        // TestCollapserTimer timer = new TestCollapserTimer();
        CollapserTimer timer = new RealCollapserTimer();
        HystrixObservableCollapser<String, String, String, String> collapser1 = new HystrixObservableCollapserTest.TestCollapserWithMultipleResponses(timer, 1, 3, false, false, HystrixObservableCollapserTest.prefixMapper, HystrixObservableCollapserTest.onMissingComplete);
        HystrixObservableCollapser<String, String, String, String> collapser2 = new HystrixObservableCollapserTest.TestCollapserWithMultipleResponses(timer, 2, 3, false, false, HystrixObservableCollapserTest.prefixMapper, HystrixObservableCollapserTest.onMissingComplete);
        TestSubscriber<String> testSubscriber1 = new TestSubscriber<String>();
        TestSubscriber<String> testSubscriber2 = new TestSubscriber<String>();
        System.out.println(((System.currentTimeMillis()) + "Starting to observe collapser1"));
        collapser1.observe().subscribe(testSubscriber1);
        collapser2.observe().subscribe(testSubscriber2);
        System.out.println(((System.currentTimeMillis()) + "Done with collapser observe()s"));
        // Note that removing these awaits breaks the unit test.  That implies that the above subscribe does not wait for a terminal event
        testSubscriber1.awaitTerminalEvent();
        testSubscriber2.awaitTerminalEvent();
        testSubscriber1.assertCompleted();
        testSubscriber1.assertNoErrors();
        testSubscriber1.assertValues("1:1", "1:2", "1:3");
        testSubscriber2.assertCompleted();
        testSubscriber2.assertNoErrors();
        testSubscriber2.assertValues("2:2", "2:4", "2:6");
    }

    @Test
    public void testTwoRequestsWithErrorProducingBatchCommand() {
        HystrixCollapserTest.TestCollapserTimer timer = new HystrixCollapserTest.TestCollapserTimer();
        HystrixObservableCollapser<String, String, String, String> collapser1 = new HystrixObservableCollapserTest.TestCollapserWithMultipleResponses(timer, 1, 3, true);
        HystrixObservableCollapser<String, String, String, String> collapser2 = new HystrixObservableCollapserTest.TestCollapserWithMultipleResponses(timer, 2, 3, true);
        System.out.println("Starting to observe collapser1");
        Observable<String> result1 = collapser1.observe();
        Observable<String> result2 = collapser2.observe();
        timer.incrementTime(10);// let time pass that equals the default delay/period

        TestSubscriber<String> testSubscriber1 = new TestSubscriber<String>();
        result1.subscribe(testSubscriber1);
        TestSubscriber<String> testSubscriber2 = new TestSubscriber<String>();
        result2.subscribe(testSubscriber2);
        testSubscriber1.awaitTerminalEvent();
        testSubscriber2.awaitTerminalEvent();
        testSubscriber1.assertError(RuntimeException.class);
        testSubscriber1.assertNoValues();
        testSubscriber2.assertError(RuntimeException.class);
        testSubscriber2.assertNoValues();
    }

    @Test
    public void testTwoRequestsWithErrorInDemultiplex() {
        HystrixCollapserTest.TestCollapserTimer timer = new HystrixCollapserTest.TestCollapserTimer();
        HystrixObservableCollapser<String, String, String, String> collapser1 = new HystrixObservableCollapserTest.TestCollapserWithMultipleResponses(timer, 1, 3, false, false, HystrixObservableCollapserTest.mapWithErrorOn1, HystrixObservableCollapserTest.onMissingError);
        HystrixObservableCollapser<String, String, String, String> collapser2 = new HystrixObservableCollapserTest.TestCollapserWithMultipleResponses(timer, 2, 3, false, false, HystrixObservableCollapserTest.mapWithErrorOn1, HystrixObservableCollapserTest.onMissingError);
        System.out.println("Starting to observe collapser1");
        Observable<String> result1 = collapser1.observe();
        Observable<String> result2 = collapser2.observe();
        timer.incrementTime(10);// let time pass that equals the default delay/period

        TestSubscriber<String> testSubscriber1 = new TestSubscriber<String>();
        result1.subscribe(testSubscriber1);
        TestSubscriber<String> testSubscriber2 = new TestSubscriber<String>();
        result2.subscribe(testSubscriber2);
        testSubscriber1.awaitTerminalEvent();
        testSubscriber2.awaitTerminalEvent();
        testSubscriber1.assertError(RuntimeException.class);
        testSubscriber1.assertNoValues();
        testSubscriber2.assertCompleted();
        testSubscriber2.assertNoErrors();
        testSubscriber2.assertValues("2:2", "2:4", "2:6");
    }

    @Test
    public void testTwoRequestsWithEmptyResponseAndOnMissingError() {
        HystrixCollapserTest.TestCollapserTimer timer = new HystrixCollapserTest.TestCollapserTimer();
        HystrixObservableCollapser<String, String, String, String> collapser1 = new HystrixObservableCollapserTest.TestCollapserWithMultipleResponses(timer, 1, 0, HystrixObservableCollapserTest.onMissingError);
        HystrixObservableCollapser<String, String, String, String> collapser2 = new HystrixObservableCollapserTest.TestCollapserWithMultipleResponses(timer, 2, 0, HystrixObservableCollapserTest.onMissingError);
        System.out.println("Starting to observe collapser1");
        Observable<String> result1 = collapser1.observe();
        Observable<String> result2 = collapser2.observe();
        timer.incrementTime(10);// let time pass that equals the default delay/period

        TestSubscriber<String> testSubscriber1 = new TestSubscriber<String>();
        result1.subscribe(testSubscriber1);
        TestSubscriber<String> testSubscriber2 = new TestSubscriber<String>();
        result2.subscribe(testSubscriber2);
        testSubscriber1.awaitTerminalEvent();
        testSubscriber2.awaitTerminalEvent();
        testSubscriber1.assertError(IllegalStateException.class);
        testSubscriber1.assertNoValues();
        testSubscriber2.assertError(IllegalStateException.class);
        testSubscriber2.assertNoValues();
    }

    @Test
    public void testTwoRequestsWithEmptyResponseAndOnMissingThrow() {
        HystrixCollapserTest.TestCollapserTimer timer = new HystrixCollapserTest.TestCollapserTimer();
        HystrixObservableCollapser<String, String, String, String> collapser1 = new HystrixObservableCollapserTest.TestCollapserWithMultipleResponses(timer, 1, 0, HystrixObservableCollapserTest.onMissingThrow);
        HystrixObservableCollapser<String, String, String, String> collapser2 = new HystrixObservableCollapserTest.TestCollapserWithMultipleResponses(timer, 2, 0, HystrixObservableCollapserTest.onMissingThrow);
        System.out.println("Starting to observe collapser1");
        Observable<String> result1 = collapser1.observe();
        Observable<String> result2 = collapser2.observe();
        timer.incrementTime(10);// let time pass that equals the default delay/period

        TestSubscriber<String> testSubscriber1 = new TestSubscriber<String>();
        result1.subscribe(testSubscriber1);
        TestSubscriber<String> testSubscriber2 = new TestSubscriber<String>();
        result2.subscribe(testSubscriber2);
        testSubscriber1.awaitTerminalEvent();
        testSubscriber2.awaitTerminalEvent();
        testSubscriber1.assertError(RuntimeException.class);
        testSubscriber1.assertNoValues();
        testSubscriber2.assertError(RuntimeException.class);
        testSubscriber2.assertNoValues();
    }

    @Test
    public void testTwoRequestsWithEmptyResponseAndOnMissingComplete() {
        HystrixCollapserTest.TestCollapserTimer timer = new HystrixCollapserTest.TestCollapserTimer();
        HystrixObservableCollapser<String, String, String, String> collapser1 = new HystrixObservableCollapserTest.TestCollapserWithMultipleResponses(timer, 1, 0, HystrixObservableCollapserTest.onMissingComplete);
        HystrixObservableCollapser<String, String, String, String> collapser2 = new HystrixObservableCollapserTest.TestCollapserWithMultipleResponses(timer, 2, 0, HystrixObservableCollapserTest.onMissingComplete);
        System.out.println("Starting to observe collapser1");
        Observable<String> result1 = collapser1.observe();
        Observable<String> result2 = collapser2.observe();
        timer.incrementTime(10);// let time pass that equals the default delay/period

        TestSubscriber<String> testSubscriber1 = new TestSubscriber<String>();
        result1.subscribe(testSubscriber1);
        TestSubscriber<String> testSubscriber2 = new TestSubscriber<String>();
        result2.subscribe(testSubscriber2);
        testSubscriber1.awaitTerminalEvent();
        testSubscriber2.awaitTerminalEvent();
        testSubscriber1.assertCompleted();
        testSubscriber1.assertNoErrors();
        testSubscriber1.assertNoValues();
        testSubscriber2.assertCompleted();
        testSubscriber2.assertNoErrors();
        testSubscriber2.assertNoValues();
    }

    @Test
    public void testTwoRequestsWithEmptyResponseAndOnMissingIgnore() {
        HystrixCollapserTest.TestCollapserTimer timer = new HystrixCollapserTest.TestCollapserTimer();
        HystrixObservableCollapser<String, String, String, String> collapser1 = new HystrixObservableCollapserTest.TestCollapserWithMultipleResponses(timer, 1, 0, HystrixObservableCollapserTest.onMissingIgnore);
        HystrixObservableCollapser<String, String, String, String> collapser2 = new HystrixObservableCollapserTest.TestCollapserWithMultipleResponses(timer, 2, 0, HystrixObservableCollapserTest.onMissingIgnore);
        System.out.println("Starting to observe collapser1");
        Observable<String> result1 = collapser1.observe();
        Observable<String> result2 = collapser2.observe();
        timer.incrementTime(10);// let time pass that equals the default delay/period

        TestSubscriber<String> testSubscriber1 = new TestSubscriber<String>();
        result1.subscribe(testSubscriber1);
        TestSubscriber<String> testSubscriber2 = new TestSubscriber<String>();
        result2.subscribe(testSubscriber2);
        testSubscriber1.awaitTerminalEvent();
        testSubscriber2.awaitTerminalEvent();
        testSubscriber1.assertCompleted();
        testSubscriber1.assertNoErrors();
        testSubscriber1.assertNoValues();
        testSubscriber2.assertCompleted();
        testSubscriber2.assertNoErrors();
        testSubscriber2.assertNoValues();
    }

    @Test
    public void testTwoRequestsWithEmptyResponseAndOnMissingFillInStaticValue() {
        HystrixCollapserTest.TestCollapserTimer timer = new HystrixCollapserTest.TestCollapserTimer();
        HystrixObservableCollapser<String, String, String, String> collapser1 = new HystrixObservableCollapserTest.TestCollapserWithMultipleResponses(timer, 1, 0, HystrixObservableCollapserTest.onMissingFillIn);
        HystrixObservableCollapser<String, String, String, String> collapser2 = new HystrixObservableCollapserTest.TestCollapserWithMultipleResponses(timer, 2, 0, HystrixObservableCollapserTest.onMissingFillIn);
        System.out.println("Starting to observe collapser1");
        Observable<String> result1 = collapser1.observe();
        Observable<String> result2 = collapser2.observe();
        timer.incrementTime(10);// let time pass that equals the default delay/period

        TestSubscriber<String> testSubscriber1 = new TestSubscriber<String>();
        result1.subscribe(testSubscriber1);
        TestSubscriber<String> testSubscriber2 = new TestSubscriber<String>();
        result2.subscribe(testSubscriber2);
        testSubscriber1.awaitTerminalEvent();
        testSubscriber2.awaitTerminalEvent();
        testSubscriber1.assertCompleted();
        testSubscriber1.assertNoErrors();
        testSubscriber1.assertValues("fillin");
        testSubscriber2.assertCompleted();
        testSubscriber2.assertNoErrors();
        testSubscriber2.assertValues("fillin");
    }

    @Test
    public void testTwoRequestsWithValuesForOneArgOnlyAndOnMissingComplete() {
        HystrixCollapserTest.TestCollapserTimer timer = new HystrixCollapserTest.TestCollapserTimer();
        HystrixObservableCollapser<String, String, String, String> collapser1 = new HystrixObservableCollapserTest.TestCollapserWithMultipleResponses(timer, 1, 0, HystrixObservableCollapserTest.onMissingComplete);
        HystrixObservableCollapser<String, String, String, String> collapser2 = new HystrixObservableCollapserTest.TestCollapserWithMultipleResponses(timer, 2, 5, HystrixObservableCollapserTest.onMissingComplete);
        System.out.println("Starting to observe collapser1");
        Observable<String> result1 = collapser1.observe();
        Observable<String> result2 = collapser2.observe();
        timer.incrementTime(10);// let time pass that equals the default delay/period

        TestSubscriber<String> testSubscriber1 = new TestSubscriber<String>();
        result1.subscribe(testSubscriber1);
        TestSubscriber<String> testSubscriber2 = new TestSubscriber<String>();
        result2.subscribe(testSubscriber2);
        testSubscriber1.awaitTerminalEvent();
        testSubscriber2.awaitTerminalEvent();
        testSubscriber1.assertCompleted();
        testSubscriber1.assertNoErrors();
        testSubscriber1.assertNoValues();
        testSubscriber2.assertCompleted();
        testSubscriber2.assertNoErrors();
        testSubscriber2.assertValues("2:2", "2:4", "2:6", "2:8", "2:10");
    }

    @Test
    public void testTwoRequestsWithValuesForOneArgOnlyAndOnMissingFillInStaticValue() {
        HystrixCollapserTest.TestCollapserTimer timer = new HystrixCollapserTest.TestCollapserTimer();
        HystrixObservableCollapser<String, String, String, String> collapser1 = new HystrixObservableCollapserTest.TestCollapserWithMultipleResponses(timer, 1, 0, HystrixObservableCollapserTest.onMissingFillIn);
        HystrixObservableCollapser<String, String, String, String> collapser2 = new HystrixObservableCollapserTest.TestCollapserWithMultipleResponses(timer, 2, 5, HystrixObservableCollapserTest.onMissingFillIn);
        System.out.println("Starting to observe collapser1");
        Observable<String> result1 = collapser1.observe();
        Observable<String> result2 = collapser2.observe();
        timer.incrementTime(10);// let time pass that equals the default delay/period

        TestSubscriber<String> testSubscriber1 = new TestSubscriber<String>();
        result1.subscribe(testSubscriber1);
        TestSubscriber<String> testSubscriber2 = new TestSubscriber<String>();
        result2.subscribe(testSubscriber2);
        testSubscriber1.awaitTerminalEvent();
        testSubscriber2.awaitTerminalEvent();
        testSubscriber1.assertCompleted();
        testSubscriber1.assertNoErrors();
        testSubscriber1.assertValues("fillin");
        testSubscriber2.assertCompleted();
        testSubscriber2.assertNoErrors();
        testSubscriber2.assertValues("2:2", "2:4", "2:6", "2:8", "2:10");
    }

    @Test
    public void testTwoRequestsWithValuesForOneArgOnlyAndOnMissingIgnore() {
        HystrixCollapserTest.TestCollapserTimer timer = new HystrixCollapserTest.TestCollapserTimer();
        HystrixObservableCollapser<String, String, String, String> collapser1 = new HystrixObservableCollapserTest.TestCollapserWithMultipleResponses(timer, 1, 0, HystrixObservableCollapserTest.onMissingIgnore);
        HystrixObservableCollapser<String, String, String, String> collapser2 = new HystrixObservableCollapserTest.TestCollapserWithMultipleResponses(timer, 2, 5, HystrixObservableCollapserTest.onMissingIgnore);
        System.out.println("Starting to observe collapser1");
        Observable<String> result1 = collapser1.observe();
        Observable<String> result2 = collapser2.observe();
        timer.incrementTime(10);// let time pass that equals the default delay/period

        TestSubscriber<String> testSubscriber1 = new TestSubscriber<String>();
        result1.subscribe(testSubscriber1);
        TestSubscriber<String> testSubscriber2 = new TestSubscriber<String>();
        result2.subscribe(testSubscriber2);
        testSubscriber1.awaitTerminalEvent();
        testSubscriber2.awaitTerminalEvent();
        testSubscriber1.assertCompleted();
        testSubscriber1.assertNoErrors();
        testSubscriber1.assertNoValues();
        testSubscriber2.assertCompleted();
        testSubscriber2.assertNoErrors();
        testSubscriber2.assertValues("2:2", "2:4", "2:6", "2:8", "2:10");
    }

    @Test
    public void testTwoRequestsWithValuesForOneArgOnlyAndOnMissingError() {
        HystrixCollapserTest.TestCollapserTimer timer = new HystrixCollapserTest.TestCollapserTimer();
        HystrixObservableCollapser<String, String, String, String> collapser1 = new HystrixObservableCollapserTest.TestCollapserWithMultipleResponses(timer, 1, 0, HystrixObservableCollapserTest.onMissingError);
        HystrixObservableCollapser<String, String, String, String> collapser2 = new HystrixObservableCollapserTest.TestCollapserWithMultipleResponses(timer, 2, 5, HystrixObservableCollapserTest.onMissingError);
        System.out.println("Starting to observe collapser1");
        Observable<String> result1 = collapser1.observe();
        Observable<String> result2 = collapser2.observe();
        timer.incrementTime(10);// let time pass that equals the default delay/period

        TestSubscriber<String> testSubscriber1 = new TestSubscriber<String>();
        result1.subscribe(testSubscriber1);
        TestSubscriber<String> testSubscriber2 = new TestSubscriber<String>();
        result2.subscribe(testSubscriber2);
        testSubscriber1.awaitTerminalEvent();
        testSubscriber2.awaitTerminalEvent();
        testSubscriber1.assertError(IllegalStateException.class);
        testSubscriber1.assertNoValues();
        testSubscriber2.assertCompleted();
        testSubscriber2.assertNoErrors();
        testSubscriber2.assertValues("2:2", "2:4", "2:6", "2:8", "2:10");
    }

    @Test
    public void testTwoRequestsWithValuesForOneArgOnlyAndOnMissingThrow() {
        HystrixCollapserTest.TestCollapserTimer timer = new HystrixCollapserTest.TestCollapserTimer();
        HystrixObservableCollapser<String, String, String, String> collapser1 = new HystrixObservableCollapserTest.TestCollapserWithMultipleResponses(timer, 1, 0, HystrixObservableCollapserTest.onMissingThrow);
        HystrixObservableCollapser<String, String, String, String> collapser2 = new HystrixObservableCollapserTest.TestCollapserWithMultipleResponses(timer, 2, 5, HystrixObservableCollapserTest.onMissingThrow);
        System.out.println("Starting to observe collapser1");
        Observable<String> result1 = collapser1.observe();
        Observable<String> result2 = collapser2.observe();
        timer.incrementTime(10);// let time pass that equals the default delay/period

        TestSubscriber<String> testSubscriber1 = new TestSubscriber<String>();
        result1.subscribe(testSubscriber1);
        TestSubscriber<String> testSubscriber2 = new TestSubscriber<String>();
        result2.subscribe(testSubscriber2);
        testSubscriber1.awaitTerminalEvent();
        testSubscriber2.awaitTerminalEvent();
        testSubscriber1.assertError(RuntimeException.class);
        testSubscriber1.assertNoValues();
        testSubscriber2.assertCompleted();
        testSubscriber2.assertNoErrors();
        testSubscriber2.assertValues("2:2", "2:4", "2:6", "2:8", "2:10");
    }

    @Test
    public void testTwoRequestsWithValuesForWrongArgs() {
        HystrixCollapserTest.TestCollapserTimer timer = new HystrixCollapserTest.TestCollapserTimer();
        HystrixObservableCollapser<String, String, String, String> collapser1 = new HystrixObservableCollapserTest.TestCollapserWithMultipleResponses(timer, 1, 3, false, false, HystrixObservableCollapserTest.map1To3And2To2, HystrixObservableCollapserTest.onMissingError);
        HystrixObservableCollapser<String, String, String, String> collapser2 = new HystrixObservableCollapserTest.TestCollapserWithMultipleResponses(timer, 2, 3, false, false, HystrixObservableCollapserTest.map1To3And2To2, HystrixObservableCollapserTest.onMissingError);
        System.out.println("Starting to observe collapser1");
        Observable<String> result1 = collapser1.observe();
        Observable<String> result2 = collapser2.observe();
        timer.incrementTime(10);// let time pass that equals the default delay/period

        TestSubscriber<String> testSubscriber1 = new TestSubscriber<String>();
        result1.subscribe(testSubscriber1);
        TestSubscriber<String> testSubscriber2 = new TestSubscriber<String>();
        result2.subscribe(testSubscriber2);
        testSubscriber1.awaitTerminalEvent();
        testSubscriber2.awaitTerminalEvent();
        testSubscriber1.assertError(RuntimeException.class);
        testSubscriber1.assertNoValues();
        testSubscriber2.assertCompleted();
        testSubscriber2.assertNoErrors();
        testSubscriber2.assertValues("2:2", "2:4", "2:6");
    }

    @Test
    public void testTwoRequestsWhenBatchCommandFails() {
        HystrixCollapserTest.TestCollapserTimer timer = new HystrixCollapserTest.TestCollapserTimer();
        HystrixObservableCollapser<String, String, String, String> collapser1 = new HystrixObservableCollapserTest.TestCollapserWithMultipleResponses(timer, 1, 3, false, true, HystrixObservableCollapserTest.map1To3And2To2, HystrixObservableCollapserTest.onMissingError);
        HystrixObservableCollapser<String, String, String, String> collapser2 = new HystrixObservableCollapserTest.TestCollapserWithMultipleResponses(timer, 2, 3, false, true, HystrixObservableCollapserTest.map1To3And2To2, HystrixObservableCollapserTest.onMissingError);
        System.out.println("Starting to observe collapser1");
        Observable<String> result1 = collapser1.observe();
        Observable<String> result2 = collapser2.observe();
        timer.incrementTime(10);// let time pass that equals the default delay/period

        TestSubscriber<String> testSubscriber1 = new TestSubscriber<String>();
        result1.subscribe(testSubscriber1);
        TestSubscriber<String> testSubscriber2 = new TestSubscriber<String>();
        result2.subscribe(testSubscriber2);
        testSubscriber1.awaitTerminalEvent();
        testSubscriber2.awaitTerminalEvent();
        testSubscriber1.assertError(RuntimeException.class);
        testSubscriber1.getOnErrorEvents().get(0).printStackTrace();
        testSubscriber1.assertNoValues();
        testSubscriber2.assertError(RuntimeException.class);
        testSubscriber2.assertNoValues();
    }

    @Test
    public void testCollapserUnderConcurrency() throws InterruptedException {
        final CollapserTimer timer = new RealCollapserTimer();
        final int NUM_THREADS_SUBMITTING_WORK = 8;
        final int NUM_REQUESTS_PER_THREAD = 8;
        final CountDownLatch latch = new CountDownLatch(NUM_THREADS_SUBMITTING_WORK);
        List<Runnable> runnables = new ArrayList<Runnable>();
        final ConcurrentLinkedQueue<TestSubscriber<String>> subscribers = new ConcurrentLinkedQueue<TestSubscriber<String>>();
        HystrixRequestContext context = HystrixRequestContext.initializeContext();
        final AtomicInteger uniqueInt = new AtomicInteger(0);
        for (int i = 0; i < NUM_THREADS_SUBMITTING_WORK; i++) {
            runnables.add(new Runnable() {
                @Override
                public void run() {
                    try {
                        // System.out.println("Runnable starting on thread : " + Thread.currentThread().getName());
                        for (int j = 0; j < NUM_REQUESTS_PER_THREAD; j++) {
                            HystrixObservableCollapser<String, String, String, String> collapser = new HystrixObservableCollapserTest.TestCollapserWithMultipleResponses(timer, uniqueInt.getAndIncrement(), 3, false);
                            Observable<String> o = collapser.toObservable();
                            TestSubscriber<String> subscriber = new TestSubscriber<String>();
                            o.subscribe(subscriber);
                            subscribers.offer(subscriber);
                        }
                        // System.out.println("Runnable done on thread : " + Thread.currentThread().getName());
                    } finally {
                        latch.countDown();
                    }
                }
            });
        }
        for (Runnable r : runnables) {
            HystrixObservableCollapserTest.threadPool.submit(new HystrixContextRunnable(r));
        }
        Assert.assertTrue(latch.await(1000, TimeUnit.MILLISECONDS));
        for (TestSubscriber<String> subscriber : subscribers) {
            subscriber.awaitTerminalEvent();
            if ((subscriber.getOnErrorEvents().size()) > 0) {
                System.out.println(("ERROR : " + (subscriber.getOnErrorEvents())));
                for (Throwable ex : subscriber.getOnErrorEvents()) {
                    ex.printStackTrace();
                }
            }
            subscriber.assertCompleted();
            subscriber.assertNoErrors();
            System.out.println(("Received : " + (subscriber.getOnNextEvents())));
            subscriber.assertValueCount(3);
        }
        context.shutdown();
    }

    @Test
    public void testConcurrencyInLoop() throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            System.out.println(("TRIAL : " + i));
            testCollapserUnderConcurrency();
        }
    }

    @Test
    public void testEarlyUnsubscribeExecutedViaToObservable() throws Exception {
        HystrixCollapserTest.TestCollapserTimer timer = new HystrixCollapserTest.TestCollapserTimer();
        HystrixObservableCollapser<String, String, String, String> collapser1 = new HystrixObservableCollapserTest.TestRequestCollapser(timer, 1);
        Observable<String> response1 = collapser1.toObservable();
        HystrixObservableCollapser<String, String, String, String> collapser2 = new HystrixObservableCollapserTest.TestRequestCollapser(timer, 2);
        Observable<String> response2 = collapser2.toObservable();
        final CountDownLatch latch1 = new CountDownLatch(1);
        final CountDownLatch latch2 = new CountDownLatch(1);
        final AtomicReference<String> value1 = new AtomicReference<String>(null);
        final AtomicReference<String> value2 = new AtomicReference<String>(null);
        Subscription s1 = response1.doOnUnsubscribe(new Action0() {
            @Override
            public void call() {
                System.out.println(((System.currentTimeMillis()) + " : s1 Unsubscribed!"));
                latch1.countDown();
            }
        }).subscribe(new rx.Subscriber<String>() {
            @Override
            public void onCompleted() {
                System.out.println(((System.currentTimeMillis()) + " : s1 OnCompleted"));
                latch1.countDown();
            }

            @Override
            public void onError(Throwable e) {
                System.out.println((((System.currentTimeMillis()) + " : s1 OnError : ") + e));
                latch1.countDown();
            }

            @Override
            public void onNext(String s) {
                System.out.println((((System.currentTimeMillis()) + " : s1 OnNext : ") + s));
                value1.set(s);
            }
        });
        Subscription s2 = response2.doOnUnsubscribe(new Action0() {
            @Override
            public void call() {
                System.out.println(((System.currentTimeMillis()) + " : s2 Unsubscribed!"));
                latch2.countDown();
            }
        }).subscribe(new rx.Subscriber<String>() {
            @Override
            public void onCompleted() {
                System.out.println(((System.currentTimeMillis()) + " : s2 OnCompleted"));
                latch2.countDown();
            }

            @Override
            public void onError(Throwable e) {
                System.out.println((((System.currentTimeMillis()) + " : s2 OnError : ") + e));
                latch2.countDown();
            }

            @Override
            public void onNext(String s) {
                System.out.println((((System.currentTimeMillis()) + " : s2 OnNext : ") + s));
                value2.set(s);
            }
        });
        s1.unsubscribe();
        timer.incrementTime(10);// let time pass that equals the default delay/period

        Assert.assertTrue(latch1.await(1000, TimeUnit.MILLISECONDS));
        Assert.assertTrue(latch2.await(1000, TimeUnit.MILLISECONDS));
        Assert.assertNull(value1.get());
        Assert.assertEquals("2", value2.get());
        System.out.println(("ReqLog : " + (HystrixRequestLog.getCurrentRequest().getExecutedCommandsAsString())));
        Assert.assertEquals(1, HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().size());
        HystrixCollapserMetrics metrics = collapser1.getMetrics();
        Assert.assertSame(metrics, collapser2.getMetrics());
        HystrixInvokableInfo<?> command = HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().iterator().next();
        Assert.assertEquals(1, command.getNumberCollapsed());// 1 should have been removed from batch

    }

    @Test
    public void testEarlyUnsubscribeExecutedViaObserve() throws Exception {
        HystrixCollapserTest.TestCollapserTimer timer = new HystrixCollapserTest.TestCollapserTimer();
        HystrixObservableCollapser<String, String, String, String> collapser1 = new HystrixObservableCollapserTest.TestRequestCollapser(timer, 1);
        Observable<String> response1 = collapser1.observe();
        HystrixObservableCollapser<String, String, String, String> collapser2 = new HystrixObservableCollapserTest.TestRequestCollapser(timer, 2);
        Observable<String> response2 = collapser2.observe();
        final CountDownLatch latch1 = new CountDownLatch(1);
        final CountDownLatch latch2 = new CountDownLatch(1);
        final AtomicReference<String> value1 = new AtomicReference<String>(null);
        final AtomicReference<String> value2 = new AtomicReference<String>(null);
        Subscription s1 = response1.doOnUnsubscribe(new Action0() {
            @Override
            public void call() {
                System.out.println(((System.currentTimeMillis()) + " : s1 Unsubscribed!"));
                latch1.countDown();
            }
        }).subscribe(new rx.Subscriber<String>() {
            @Override
            public void onCompleted() {
                System.out.println(((System.currentTimeMillis()) + " : s1 OnCompleted"));
                latch1.countDown();
            }

            @Override
            public void onError(Throwable e) {
                System.out.println((((System.currentTimeMillis()) + " : s1 OnError : ") + e));
                latch1.countDown();
            }

            @Override
            public void onNext(String s) {
                System.out.println((((System.currentTimeMillis()) + " : s1 OnNext : ") + s));
                value1.set(s);
            }
        });
        Subscription s2 = response2.doOnUnsubscribe(new Action0() {
            @Override
            public void call() {
                System.out.println(((System.currentTimeMillis()) + " : s2 Unsubscribed!"));
                latch2.countDown();
            }
        }).subscribe(new rx.Subscriber<String>() {
            @Override
            public void onCompleted() {
                System.out.println(((System.currentTimeMillis()) + " : s2 OnCompleted"));
                latch2.countDown();
            }

            @Override
            public void onError(Throwable e) {
                System.out.println((((System.currentTimeMillis()) + " : s2 OnError : ") + e));
                latch2.countDown();
            }

            @Override
            public void onNext(String s) {
                System.out.println((((System.currentTimeMillis()) + " : s2 OnNext : ") + s));
                value2.set(s);
            }
        });
        s1.unsubscribe();
        timer.incrementTime(10);// let time pass that equals the default delay/period

        Assert.assertTrue(latch1.await(1000, TimeUnit.MILLISECONDS));
        Assert.assertTrue(latch2.await(1000, TimeUnit.MILLISECONDS));
        Assert.assertNull(value1.get());
        Assert.assertEquals("2", value2.get());
        System.out.println(("ReqLog : " + (HystrixRequestLog.getCurrentRequest().getExecutedCommandsAsString())));
        Assert.assertEquals(1, HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().size());
        HystrixCollapserMetrics metrics = collapser1.getMetrics();
        Assert.assertSame(metrics, collapser2.getMetrics());
        HystrixInvokableInfo<?> command = HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().iterator().next();
        Assert.assertEquals(1, command.getNumberCollapsed());// 1 should have been removed from batch

    }

    @Test
    public void testEarlyUnsubscribeFromAllCancelsBatch() throws Exception {
        HystrixCollapserTest.TestCollapserTimer timer = new HystrixCollapserTest.TestCollapserTimer();
        HystrixObservableCollapser<String, String, String, String> collapser1 = new HystrixObservableCollapserTest.TestRequestCollapser(timer, 1);
        Observable<String> response1 = collapser1.observe();
        HystrixObservableCollapser<String, String, String, String> collapser2 = new HystrixObservableCollapserTest.TestRequestCollapser(timer, 2);
        Observable<String> response2 = collapser2.observe();
        final CountDownLatch latch1 = new CountDownLatch(1);
        final CountDownLatch latch2 = new CountDownLatch(1);
        final AtomicReference<String> value1 = new AtomicReference<String>(null);
        final AtomicReference<String> value2 = new AtomicReference<String>(null);
        Subscription s1 = response1.doOnUnsubscribe(new Action0() {
            @Override
            public void call() {
                System.out.println(((System.currentTimeMillis()) + " : s1 Unsubscribed!"));
                latch1.countDown();
            }
        }).subscribe(new rx.Subscriber<String>() {
            @Override
            public void onCompleted() {
                System.out.println(((System.currentTimeMillis()) + " : s1 OnCompleted"));
                latch1.countDown();
            }

            @Override
            public void onError(Throwable e) {
                System.out.println((((System.currentTimeMillis()) + " : s1 OnError : ") + e));
                latch1.countDown();
            }

            @Override
            public void onNext(String s) {
                System.out.println((((System.currentTimeMillis()) + " : s1 OnNext : ") + s));
                value1.set(s);
            }
        });
        Subscription s2 = response2.doOnUnsubscribe(new Action0() {
            @Override
            public void call() {
                System.out.println(((System.currentTimeMillis()) + " : s2 Unsubscribed!"));
                latch2.countDown();
            }
        }).subscribe(new rx.Subscriber<String>() {
            @Override
            public void onCompleted() {
                System.out.println(((System.currentTimeMillis()) + " : s2 OnCompleted"));
                latch2.countDown();
            }

            @Override
            public void onError(Throwable e) {
                System.out.println((((System.currentTimeMillis()) + " : s2 OnError : ") + e));
                latch2.countDown();
            }

            @Override
            public void onNext(String s) {
                System.out.println((((System.currentTimeMillis()) + " : s2 OnNext : ") + s));
                value2.set(s);
            }
        });
        s1.unsubscribe();
        s2.unsubscribe();
        timer.incrementTime(10);// let time pass that equals the default delay/period

        Assert.assertTrue(latch1.await(1000, TimeUnit.MILLISECONDS));
        Assert.assertTrue(latch2.await(1000, TimeUnit.MILLISECONDS));
        Assert.assertNull(value1.get());
        Assert.assertNull(value2.get());
        System.out.println(("ReqLog : " + (HystrixRequestLog.getCurrentRequest().getExecutedCommandsAsString())));
        Assert.assertEquals(0, HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().size());
    }

    @Test
    public void testRequestThenCacheHitAndCacheHitUnsubscribed() throws Exception {
        HystrixCollapserTest.TestCollapserTimer timer = new HystrixCollapserTest.TestCollapserTimer();
        HystrixObservableCollapser<String, String, String, String> collapser1 = new HystrixObservableCollapserTest.SuccessfulCacheableCollapsedCommand(timer, "foo", true);
        Observable<String> response1 = collapser1.observe();
        HystrixObservableCollapser<String, String, String, String> collapser2 = new HystrixObservableCollapserTest.SuccessfulCacheableCollapsedCommand(timer, "foo", true);
        Observable<String> response2 = collapser2.observe();
        final CountDownLatch latch1 = new CountDownLatch(1);
        final CountDownLatch latch2 = new CountDownLatch(1);
        final AtomicReference<String> value1 = new AtomicReference<String>(null);
        final AtomicReference<String> value2 = new AtomicReference<String>(null);
        Subscription s1 = response1.doOnUnsubscribe(new Action0() {
            @Override
            public void call() {
                System.out.println(((System.currentTimeMillis()) + " : s1 Unsubscribed!"));
                latch1.countDown();
            }
        }).subscribe(new rx.Subscriber<String>() {
            @Override
            public void onCompleted() {
                System.out.println(((System.currentTimeMillis()) + " : s1 OnCompleted"));
                latch1.countDown();
            }

            @Override
            public void onError(Throwable e) {
                System.out.println((((System.currentTimeMillis()) + " : s1 OnError : ") + e));
                latch1.countDown();
            }

            @Override
            public void onNext(String s) {
                System.out.println((((System.currentTimeMillis()) + " : s1 OnNext : ") + s));
                value1.set(s);
            }
        });
        Subscription s2 = response2.doOnUnsubscribe(new Action0() {
            @Override
            public void call() {
                System.out.println(((System.currentTimeMillis()) + " : s2 Unsubscribed!"));
                latch2.countDown();
            }
        }).subscribe(new rx.Subscriber<String>() {
            @Override
            public void onCompleted() {
                System.out.println(((System.currentTimeMillis()) + " : s2 OnCompleted"));
                latch2.countDown();
            }

            @Override
            public void onError(Throwable e) {
                System.out.println((((System.currentTimeMillis()) + " : s2 OnError : ") + e));
                latch2.countDown();
            }

            @Override
            public void onNext(String s) {
                System.out.println((((System.currentTimeMillis()) + " : s2 OnNext : ") + s));
                value2.set(s);
            }
        });
        s2.unsubscribe();
        timer.incrementTime(10);// let time pass that equals the default delay/period

        Assert.assertTrue(latch1.await(1000, TimeUnit.MILLISECONDS));
        Assert.assertTrue(latch2.await(1000, TimeUnit.MILLISECONDS));
        Assert.assertEquals("foo", value1.get());
        Assert.assertNull(value2.get());
        System.out.println(("ReqLog : " + (HystrixRequestLog.getCurrentRequest().getExecutedCommandsAsString())));
        Assert.assertEquals(1, HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().size());
        HystrixInvokableInfo<?> command = HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().iterator().next();
        assertCommandExecutionEvents(command, EMIT, SUCCESS, COLLAPSED);
        Assert.assertEquals(1, command.getNumberCollapsed());// should only be 1 collapsed - other came from cache, then was cancelled

    }

    @Test
    public void testRequestThenCacheHitAndOriginalUnsubscribed() throws Exception {
        HystrixCollapserTest.TestCollapserTimer timer = new HystrixCollapserTest.TestCollapserTimer();
        HystrixObservableCollapser<String, String, String, String> collapser1 = new HystrixObservableCollapserTest.SuccessfulCacheableCollapsedCommand(timer, "foo", true);
        Observable<String> response1 = collapser1.observe();
        HystrixObservableCollapser<String, String, String, String> collapser2 = new HystrixObservableCollapserTest.SuccessfulCacheableCollapsedCommand(timer, "foo", true);
        Observable<String> response2 = collapser2.observe();
        final CountDownLatch latch1 = new CountDownLatch(1);
        final CountDownLatch latch2 = new CountDownLatch(1);
        final AtomicReference<String> value1 = new AtomicReference<String>(null);
        final AtomicReference<String> value2 = new AtomicReference<String>(null);
        Subscription s1 = response1.doOnUnsubscribe(new Action0() {
            @Override
            public void call() {
                System.out.println(((System.currentTimeMillis()) + " : s1 Unsubscribed!"));
                latch1.countDown();
            }
        }).subscribe(new rx.Subscriber<String>() {
            @Override
            public void onCompleted() {
                System.out.println(((System.currentTimeMillis()) + " : s1 OnCompleted"));
                latch1.countDown();
            }

            @Override
            public void onError(Throwable e) {
                System.out.println((((System.currentTimeMillis()) + " : s1 OnError : ") + e));
                latch1.countDown();
            }

            @Override
            public void onNext(String s) {
                System.out.println((((System.currentTimeMillis()) + " : s1 OnNext : ") + s));
                value1.set(s);
            }
        });
        Subscription s2 = response2.doOnUnsubscribe(new Action0() {
            @Override
            public void call() {
                System.out.println(((System.currentTimeMillis()) + " : s2 Unsubscribed!"));
                latch2.countDown();
            }
        }).subscribe(new rx.Subscriber<String>() {
            @Override
            public void onCompleted() {
                System.out.println(((System.currentTimeMillis()) + " : s2 OnCompleted"));
                latch2.countDown();
            }

            @Override
            public void onError(Throwable e) {
                System.out.println((((System.currentTimeMillis()) + " : s2 OnError : ") + e));
                latch2.countDown();
            }

            @Override
            public void onNext(String s) {
                System.out.println((((System.currentTimeMillis()) + " : s2 OnNext : ") + s));
                value2.set(s);
            }
        });
        s1.unsubscribe();
        timer.incrementTime(10);// let time pass that equals the default delay/period

        Assert.assertTrue(latch1.await(1000, TimeUnit.MILLISECONDS));
        Assert.assertTrue(latch2.await(1000, TimeUnit.MILLISECONDS));
        Assert.assertNull(value1.get());
        Assert.assertEquals("foo", value2.get());
        System.out.println(("ReqLog : " + (HystrixRequestLog.getCurrentRequest().getExecutedCommandsAsString())));
        Assert.assertEquals(1, HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().size());
        HystrixInvokableInfo<?> command = HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().iterator().next();
        assertCommandExecutionEvents(command, EMIT, SUCCESS, COLLAPSED);
        Assert.assertEquals(1, command.getNumberCollapsed());// should only be 1 collapsed - other came from cache, then was cancelled

    }

    @Test
    public void testRequestThenTwoCacheHitsOriginalAndOneCacheHitUnsubscribed() throws Exception {
        HystrixCollapserTest.TestCollapserTimer timer = new HystrixCollapserTest.TestCollapserTimer();
        HystrixObservableCollapser<String, String, String, String> collapser1 = new HystrixObservableCollapserTest.SuccessfulCacheableCollapsedCommand(timer, "foo", true);
        Observable<String> response1 = collapser1.observe();
        HystrixObservableCollapser<String, String, String, String> collapser2 = new HystrixObservableCollapserTest.SuccessfulCacheableCollapsedCommand(timer, "foo", true);
        Observable<String> response2 = collapser2.observe();
        HystrixObservableCollapser<String, String, String, String> collapser3 = new HystrixObservableCollapserTest.SuccessfulCacheableCollapsedCommand(timer, "foo", true);
        Observable<String> response3 = collapser3.observe();
        final CountDownLatch latch1 = new CountDownLatch(1);
        final CountDownLatch latch2 = new CountDownLatch(1);
        final CountDownLatch latch3 = new CountDownLatch(1);
        final AtomicReference<String> value1 = new AtomicReference<String>(null);
        final AtomicReference<String> value2 = new AtomicReference<String>(null);
        final AtomicReference<String> value3 = new AtomicReference<String>(null);
        Subscription s1 = response1.doOnUnsubscribe(new Action0() {
            @Override
            public void call() {
                System.out.println(((System.currentTimeMillis()) + " : s1 Unsubscribed!"));
                latch1.countDown();
            }
        }).subscribe(new rx.Subscriber<String>() {
            @Override
            public void onCompleted() {
                System.out.println(((System.currentTimeMillis()) + " : s1 OnCompleted"));
                latch1.countDown();
            }

            @Override
            public void onError(Throwable e) {
                System.out.println((((System.currentTimeMillis()) + " : s1 OnError : ") + e));
                latch1.countDown();
            }

            @Override
            public void onNext(String s) {
                System.out.println((((System.currentTimeMillis()) + " : s1 OnNext : ") + s));
                value1.set(s);
            }
        });
        Subscription s2 = response2.doOnUnsubscribe(new Action0() {
            @Override
            public void call() {
                System.out.println(((System.currentTimeMillis()) + " : s2 Unsubscribed!"));
                latch2.countDown();
            }
        }).subscribe(new rx.Subscriber<String>() {
            @Override
            public void onCompleted() {
                System.out.println(((System.currentTimeMillis()) + " : s2 OnCompleted"));
                latch2.countDown();
            }

            @Override
            public void onError(Throwable e) {
                System.out.println((((System.currentTimeMillis()) + " : s2 OnError : ") + e));
                latch2.countDown();
            }

            @Override
            public void onNext(String s) {
                System.out.println((((System.currentTimeMillis()) + " : s2 OnNext : ") + s));
                value2.set(s);
            }
        });
        Subscription s3 = response3.doOnUnsubscribe(new Action0() {
            @Override
            public void call() {
                System.out.println(((System.currentTimeMillis()) + " : s3 Unsubscribed!"));
                latch3.countDown();
            }
        }).subscribe(new rx.Subscriber<String>() {
            @Override
            public void onCompleted() {
                System.out.println(((System.currentTimeMillis()) + " : s3 OnCompleted"));
                latch3.countDown();
            }

            @Override
            public void onError(Throwable e) {
                System.out.println((((System.currentTimeMillis()) + " : s3 OnError : ") + e));
                latch3.countDown();
            }

            @Override
            public void onNext(String s) {
                System.out.println((((System.currentTimeMillis()) + " : s3 OnNext : ") + s));
                value3.set(s);
            }
        });
        s1.unsubscribe();
        s3.unsubscribe();
        timer.incrementTime(10);// let time pass that equals the default delay/period

        Assert.assertTrue(latch1.await(1000, TimeUnit.MILLISECONDS));
        Assert.assertTrue(latch2.await(1000, TimeUnit.MILLISECONDS));
        Assert.assertNull(value1.get());
        Assert.assertEquals("foo", value2.get());
        Assert.assertNull(value3.get());
        System.out.println(("ReqLog : " + (HystrixRequestLog.getCurrentRequest().getExecutedCommandsAsString())));
        Assert.assertEquals(1, HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().size());
        HystrixInvokableInfo<?> command = HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().iterator().next();
        assertCommandExecutionEvents(command, EMIT, SUCCESS, COLLAPSED);
        Assert.assertEquals(1, command.getNumberCollapsed());// should only be 1 collapsed - other came from cache, then was cancelled

    }

    @Test
    public void testRequestThenTwoCacheHitsAllUnsubscribed() throws Exception {
        HystrixCollapserTest.TestCollapserTimer timer = new HystrixCollapserTest.TestCollapserTimer();
        HystrixObservableCollapser<String, String, String, String> collapser1 = new HystrixObservableCollapserTest.SuccessfulCacheableCollapsedCommand(timer, "foo", true);
        Observable<String> response1 = collapser1.observe();
        HystrixObservableCollapser<String, String, String, String> collapser2 = new HystrixObservableCollapserTest.SuccessfulCacheableCollapsedCommand(timer, "foo", true);
        Observable<String> response2 = collapser2.observe();
        HystrixObservableCollapser<String, String, String, String> collapser3 = new HystrixObservableCollapserTest.SuccessfulCacheableCollapsedCommand(timer, "foo", true);
        Observable<String> response3 = collapser3.observe();
        final CountDownLatch latch1 = new CountDownLatch(1);
        final CountDownLatch latch2 = new CountDownLatch(1);
        final CountDownLatch latch3 = new CountDownLatch(1);
        final AtomicReference<String> value1 = new AtomicReference<String>(null);
        final AtomicReference<String> value2 = new AtomicReference<String>(null);
        final AtomicReference<String> value3 = new AtomicReference<String>(null);
        Subscription s1 = response1.doOnUnsubscribe(new Action0() {
            @Override
            public void call() {
                System.out.println(((System.currentTimeMillis()) + " : s1 Unsubscribed!"));
                latch1.countDown();
            }
        }).subscribe(new rx.Subscriber<String>() {
            @Override
            public void onCompleted() {
                System.out.println(((System.currentTimeMillis()) + " : s1 OnCompleted"));
                latch1.countDown();
            }

            @Override
            public void onError(Throwable e) {
                System.out.println((((System.currentTimeMillis()) + " : s1 OnError : ") + e));
                latch1.countDown();
            }

            @Override
            public void onNext(String s) {
                System.out.println((((System.currentTimeMillis()) + " : s1 OnNext : ") + s));
                value1.set(s);
            }
        });
        Subscription s2 = response2.doOnUnsubscribe(new Action0() {
            @Override
            public void call() {
                System.out.println(((System.currentTimeMillis()) + " : s2 Unsubscribed!"));
                latch2.countDown();
            }
        }).subscribe(new rx.Subscriber<String>() {
            @Override
            public void onCompleted() {
                System.out.println(((System.currentTimeMillis()) + " : s2 OnCompleted"));
                latch2.countDown();
            }

            @Override
            public void onError(Throwable e) {
                System.out.println((((System.currentTimeMillis()) + " : s2 OnError : ") + e));
                latch2.countDown();
            }

            @Override
            public void onNext(String s) {
                System.out.println((((System.currentTimeMillis()) + " : s2 OnNext : ") + s));
                value2.set(s);
            }
        });
        Subscription s3 = response3.doOnUnsubscribe(new Action0() {
            @Override
            public void call() {
                System.out.println(((System.currentTimeMillis()) + " : s3 Unsubscribed!"));
                latch3.countDown();
            }
        }).subscribe(new rx.Subscriber<String>() {
            @Override
            public void onCompleted() {
                System.out.println(((System.currentTimeMillis()) + " : s3 OnCompleted"));
                latch3.countDown();
            }

            @Override
            public void onError(Throwable e) {
                System.out.println((((System.currentTimeMillis()) + " : s3 OnError : ") + e));
                latch3.countDown();
            }

            @Override
            public void onNext(String s) {
                System.out.println((((System.currentTimeMillis()) + " : s3 OnNext : ") + s));
                value3.set(s);
            }
        });
        s1.unsubscribe();
        s2.unsubscribe();
        s3.unsubscribe();
        timer.incrementTime(10);// let time pass that equals the default delay/period

        Assert.assertTrue(latch1.await(1000, TimeUnit.MILLISECONDS));
        Assert.assertTrue(latch2.await(1000, TimeUnit.MILLISECONDS));
        Assert.assertNull(value1.get());
        Assert.assertNull(value2.get());
        Assert.assertNull(value3.get());
        System.out.println(("ReqLog : " + (HystrixRequestLog.getCurrentRequest().getExecutedCommandsAsString())));
        Assert.assertEquals(0, HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().size());
    }

    class Pair<A, B> {
        final A a;

        final B b;

        Pair(A a, B b) {
            this.a = a;
            this.b = b;
        }
    }

    class MyCommand extends HystrixObservableCommand<HystrixObservableCollapserTest.Pair<String, Integer>> {
        private final List<String> args;

        public MyCommand(List<String> args) {
            super(Setter.withGroupKey(Factory.asKey("BATCH")));
            this.args = args;
        }

        @Override
        protected Observable<HystrixObservableCollapserTest.Pair<String, Integer>> construct() {
            return Observable.from(args).map(new Func1<String, HystrixObservableCollapserTest.Pair<String, Integer>>() {
                @Override
                public HystrixObservableCollapserTest.Pair<String, Integer> call(String s) {
                    return new HystrixObservableCollapserTest.Pair<String, Integer>(s, Integer.parseInt(s));
                }
            });
        }
    }

    class MyCollapser extends HystrixObservableCollapser<String, HystrixObservableCollapserTest.Pair<String, Integer>, Integer, String> {
        private final String arg;

        public MyCollapser(String arg, boolean requestCachingOn) {
            super(HystrixCollapserKey.Factory.asKey("UNITTEST"), REQUEST, new RealCollapserTimer(), HystrixCollapserProperties.Setter().withRequestCacheEnabled(requestCachingOn), HystrixCollapserMetrics.getInstance(HystrixCollapserKey.Factory.asKey("UNITTEST"), new com.netflix.hystrix.strategy.properties.HystrixPropertiesCollapserDefault(HystrixCollapserKey.Factory.asKey("UNITTEST"), HystrixCollapserProperties.Setter())));
            this.arg = arg;
        }

        @Override
        public String getRequestArgument() {
            return arg;
        }

        @Override
        protected HystrixObservableCommand<HystrixObservableCollapserTest.Pair<String, Integer>> createCommand(Collection<CollapsedRequest<Integer, String>> collapsedRequests) {
            List<String> args = new ArrayList<String>();
            for (CollapsedRequest<Integer, String> req : collapsedRequests) {
                args.add(req.getArgument());
            }
            return new HystrixObservableCollapserTest.MyCommand(args);
        }

        @Override
        protected Func1<HystrixObservableCollapserTest.Pair<String, Integer>, String> getBatchReturnTypeKeySelector() {
            return new Func1<HystrixObservableCollapserTest.Pair<String, Integer>, String>() {
                @Override
                public String call(HystrixObservableCollapserTest.Pair<String, Integer> pair) {
                    return pair.a;
                }
            };
        }

        @Override
        protected Func1<String, String> getRequestArgumentKeySelector() {
            return new Func1<String, String>() {
                @Override
                public String call(String s) {
                    return s;
                }
            };
        }

        @Override
        protected void onMissingResponse(CollapsedRequest<Integer, String> r) {
            r.setException(new RuntimeException("missing"));
        }

        @Override
        protected Func1<HystrixObservableCollapserTest.Pair<String, Integer>, Integer> getBatchReturnTypeToResponseTypeMapper() {
            return new Func1<HystrixObservableCollapserTest.Pair<String, Integer>, Integer>() {
                @Override
                public Integer call(HystrixObservableCollapserTest.Pair<String, Integer> pair) {
                    return pair.b;
                }
            };
        }
    }

    @Test
    public void testDuplicateArgumentsWithRequestCachingOn() throws Exception {
        final int NUM = 10;
        List<Observable<Integer>> observables = new ArrayList<Observable<Integer>>();
        for (int i = 0; i < NUM; i++) {
            HystrixObservableCollapserTest.MyCollapser c = new HystrixObservableCollapserTest.MyCollapser("5", true);
            observables.add(toObservable());
        }
        List<TestSubscriber<Integer>> subscribers = new ArrayList<TestSubscriber<Integer>>();
        for (final Observable<Integer> o : observables) {
            final TestSubscriber<Integer> sub = new TestSubscriber<Integer>();
            subscribers.add(sub);
            o.subscribe(sub);
        }
        Thread.sleep(100);
        // all subscribers should receive the same value
        for (TestSubscriber<Integer> sub : subscribers) {
            sub.awaitTerminalEvent(1000, TimeUnit.MILLISECONDS);
            System.out.println(("Subscriber received : " + (sub.getOnNextEvents())));
            sub.assertCompleted();
            sub.assertNoErrors();
            sub.assertValues(5);
        }
    }

    @Test
    public void testDuplicateArgumentsWithRequestCachingOff() throws Exception {
        final int NUM = 10;
        List<Observable<Integer>> observables = new ArrayList<Observable<Integer>>();
        for (int i = 0; i < NUM; i++) {
            HystrixObservableCollapserTest.MyCollapser c = new HystrixObservableCollapserTest.MyCollapser("5", false);
            observables.add(toObservable());
        }
        List<TestSubscriber<Integer>> subscribers = new ArrayList<TestSubscriber<Integer>>();
        for (final Observable<Integer> o : observables) {
            final TestSubscriber<Integer> sub = new TestSubscriber<Integer>();
            subscribers.add(sub);
            o.subscribe(sub);
        }
        Thread.sleep(100);
        AtomicInteger numErrors = new AtomicInteger(0);
        AtomicInteger numValues = new AtomicInteger(0);
        // only the first subscriber should receive the value.
        // the others should get an error that the batch contains duplicates
        for (TestSubscriber<Integer> sub : subscribers) {
            sub.awaitTerminalEvent(1000, TimeUnit.MILLISECONDS);
            if (sub.getOnCompletedEvents().isEmpty()) {
                System.out.println((((Thread.currentThread().getName()) + " Error : ") + (sub.getOnErrorEvents())));
                sub.assertError(IllegalArgumentException.class);
                sub.assertNoValues();
                numErrors.getAndIncrement();
            } else {
                System.out.println((((Thread.currentThread().getName()) + " OnNext : ") + (sub.getOnNextEvents())));
                sub.assertValues(5);
                sub.assertCompleted();
                sub.assertNoErrors();
                numValues.getAndIncrement();
            }
        }
        Assert.assertEquals(1, numValues.get());
        Assert.assertEquals((NUM - 1), numErrors.get());
    }

    private static class TestRequestCollapser extends HystrixObservableCollapser<String, String, String, String> {
        private final String value;

        private ConcurrentLinkedQueue<HystrixObservableCommand<String>> commandsExecuted;

        public TestRequestCollapser(HystrixCollapserTest.TestCollapserTimer timer, int value) {
            this(timer, String.valueOf(value));
        }

        public TestRequestCollapser(HystrixCollapserTest.TestCollapserTimer timer, String value) {
            this(timer, value, 10000, 10);
        }

        public TestRequestCollapser(HystrixCollapserTest.TestCollapserTimer timer, String value, ConcurrentLinkedQueue<HystrixObservableCommand<String>> executionLog) {
            this(timer, value, 10000, 10, executionLog);
        }

        public TestRequestCollapser(HystrixCollapserTest.TestCollapserTimer timer, int value, int defaultMaxRequestsInBatch, int defaultTimerDelayInMilliseconds) {
            this(timer, String.valueOf(value), defaultMaxRequestsInBatch, defaultTimerDelayInMilliseconds);
        }

        public TestRequestCollapser(HystrixCollapserTest.TestCollapserTimer timer, String value, int defaultMaxRequestsInBatch, int defaultTimerDelayInMilliseconds) {
            this(timer, value, defaultMaxRequestsInBatch, defaultTimerDelayInMilliseconds, null);
        }

        public TestRequestCollapser(Scope scope, HystrixCollapserTest.TestCollapserTimer timer, String value, int defaultMaxRequestsInBatch, int defaultTimerDelayInMilliseconds) {
            this(scope, timer, value, defaultMaxRequestsInBatch, defaultTimerDelayInMilliseconds, null);
        }

        public TestRequestCollapser(HystrixCollapserTest.TestCollapserTimer timer, String value, int defaultMaxRequestsInBatch, int defaultTimerDelayInMilliseconds, ConcurrentLinkedQueue<HystrixObservableCommand<String>> executionLog) {
            this(Scope.REQUEST, timer, value, defaultMaxRequestsInBatch, defaultTimerDelayInMilliseconds, executionLog);
        }

        private static HystrixCollapserMetrics createMetrics() {
            HystrixCollapserKey key = HystrixCollapserKey.Factory.asKey("COLLAPSER_ONE");
            return HystrixCollapserMetrics.getInstance(key, new com.netflix.hystrix.strategy.properties.HystrixPropertiesCollapserDefault(key, HystrixCollapserProperties.Setter()));
        }

        public TestRequestCollapser(Scope scope, HystrixCollapserTest.TestCollapserTimer timer, String value, int defaultMaxRequestsInBatch, int defaultTimerDelayInMilliseconds, ConcurrentLinkedQueue<HystrixObservableCommand<String>> executionLog) {
            // use a CollapserKey based on the CollapserTimer object reference so it's unique for each timer as we don't want caching
            // of properties to occur and we're using the default HystrixProperty which typically does caching
            super(HystrixObservableCollapserTest.collapserKeyFromString(timer), scope, timer, HystrixCollapserProperties.Setter().withMaxRequestsInBatch(defaultMaxRequestsInBatch).withTimerDelayInMilliseconds(defaultTimerDelayInMilliseconds), HystrixObservableCollapserTest.TestRequestCollapser.createMetrics());
            this.value = value;
            this.commandsExecuted = executionLog;
        }

        @Override
        public String getRequestArgument() {
            return value;
        }

        @Override
        public HystrixObservableCommand<String> createCommand(final Collection<CollapsedRequest<String, String>> requests) {
            /* return a mocked command */
            HystrixObservableCommand<String> command = new HystrixObservableCollapserTest.TestCollapserCommand(requests);
            if ((commandsExecuted) != null) {
                commandsExecuted.add(command);
            }
            return command;
        }

        @Override
        protected Func1<String, String> getBatchReturnTypeToResponseTypeMapper() {
            return new Func1<String, String>() {
                @Override
                public String call(String s) {
                    return s;
                }
            };
        }

        @Override
        protected Func1<String, String> getBatchReturnTypeKeySelector() {
            return new Func1<String, String>() {
                @Override
                public String call(String s) {
                    return s;
                }
            };
        }

        @Override
        protected Func1<String, String> getRequestArgumentKeySelector() {
            return new Func1<String, String>() {
                @Override
                public String call(String s) {
                    return s;
                }
            };
        }

        @Override
        protected void onMissingResponse(CollapsedRequest<String, String> r) {
            r.setException(new RuntimeException("missing value!"));
        }
    }

    private static class TestCollapserCommand extends TestHystrixObservableCommand<String> {
        private final Collection<CollapsedRequest<String, String>> requests;

        TestCollapserCommand(Collection<CollapsedRequest<String, String>> requests) {
            super(TestHystrixObservableCommand.testPropsBuilder().setCommandPropertiesDefaults(HystrixCommandPropertiesTest.getUnitTestPropertiesSetter().withExecutionTimeoutInMilliseconds(1000)));
            this.requests = requests;
        }

        @Override
        protected Observable<String> construct() {
            return Observable.create(new rx.Observable.OnSubscribe<String>() {
                @Override
                public void call(rx.Subscriber<? super String> s) {
                    System.out.println((">>> TestCollapserCommand run() ... batch size: " + (requests.size())));
                    // simulate a batch request
                    for (CollapsedRequest<String, String> request : requests) {
                        if ((request.getArgument()) == null) {
                            throw new NullPointerException("Simulated Error");
                        }
                        if (request.getArgument().equals("TIMEOUT")) {
                            try {
                                Thread.sleep(200);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        s.onNext(request.getArgument());
                    }
                    s.onCompleted();
                }
            }).subscribeOn(Schedulers.computation());
        }
    }

    private static class TestCollapserWithMultipleResponses extends HystrixObservableCollapser<String, String, String, String> {
        private final String arg;

        private static final ConcurrentMap<String, Integer> emitsPerArg;

        private final boolean commandConstructionFails;

        private final boolean commandExecutionFails;

        private final Func1<String, String> keyMapper;

        private final Action1<CollapsedRequest<String, String>> onMissingResponseHandler;

        private static final HystrixCollapserKey key = HystrixCollapserKey.Factory.asKey("COLLAPSER_MULTI");

        private static final HystrixCollapserProperties.Setter propsSetter = HystrixCollapserProperties.Setter().withMaxRequestsInBatch(10).withTimerDelayInMilliseconds(10);

        private static final HystrixCollapserMetrics metrics = HystrixCollapserMetrics.getInstance(HystrixObservableCollapserTest.TestCollapserWithMultipleResponses.key, new com.netflix.hystrix.strategy.properties.HystrixPropertiesCollapserDefault(HystrixObservableCollapserTest.TestCollapserWithMultipleResponses.key, HystrixCollapserProperties.Setter()));

        static {
            emitsPerArg = new ConcurrentHashMap<String, Integer>();
        }

        public TestCollapserWithMultipleResponses(CollapserTimer timer, int arg, int numEmits, boolean commandConstructionFails) {
            this(timer, arg, numEmits, commandConstructionFails, false, HystrixObservableCollapserTest.prefixMapper, HystrixObservableCollapserTest.onMissingComplete);
        }

        public TestCollapserWithMultipleResponses(CollapserTimer timer, int arg, int numEmits, Action1<CollapsedRequest<String, String>> onMissingHandler) {
            this(timer, arg, numEmits, false, false, HystrixObservableCollapserTest.prefixMapper, onMissingHandler);
        }

        public TestCollapserWithMultipleResponses(CollapserTimer timer, int arg, int numEmits, Func1<String, String> keyMapper) {
            this(timer, arg, numEmits, false, false, keyMapper, HystrixObservableCollapserTest.onMissingComplete);
        }

        public TestCollapserWithMultipleResponses(CollapserTimer timer, int arg, int numEmits, boolean commandConstructionFails, boolean commandExecutionFails, Func1<String, String> keyMapper, Action1<CollapsedRequest<String, String>> onMissingResponseHandler) {
            super(HystrixObservableCollapserTest.collapserKeyFromString(timer), Scope.REQUEST, timer, HystrixObservableCollapserTest.TestCollapserWithMultipleResponses.propsSetter, HystrixObservableCollapserTest.TestCollapserWithMultipleResponses.metrics);
            this.arg = arg + "";
            HystrixObservableCollapserTest.TestCollapserWithMultipleResponses.emitsPerArg.put(this.arg, numEmits);
            this.commandConstructionFails = commandConstructionFails;
            this.commandExecutionFails = commandExecutionFails;
            this.keyMapper = keyMapper;
            this.onMissingResponseHandler = onMissingResponseHandler;
        }

        @Override
        public String getRequestArgument() {
            return arg;
        }

        @Override
        protected HystrixObservableCommand<String> createCommand(Collection<CollapsedRequest<String, String>> collapsedRequests) {
            Assert.assertNotNull("command creation should have HystrixRequestContext", HystrixRequestContext.getContextForCurrentThread());
            if (commandConstructionFails) {
                throw new RuntimeException("Exception thrown in command construction");
            } else {
                List<Integer> args = new ArrayList<Integer>();
                for (CollapsedRequest<String, String> collapsedRequest : collapsedRequests) {
                    String stringArg = collapsedRequest.getArgument();
                    int intArg = Integer.parseInt(stringArg);
                    args.add(intArg);
                }
                return new HystrixObservableCollapserTest.TestCollapserCommandWithMultipleResponsePerArgument(args, HystrixObservableCollapserTest.TestCollapserWithMultipleResponses.emitsPerArg, commandExecutionFails);
            }
        }

        // Data comes back in the form: 1:1, 1:2, 1:3, 2:2, 2:4, 2:6.
        // This method should use the first half of that string as the request arg
        @Override
        protected Func1<String, String> getBatchReturnTypeKeySelector() {
            return keyMapper;
        }

        @Override
        protected Func1<String, String> getRequestArgumentKeySelector() {
            return new Func1<String, String>() {
                @Override
                public String call(String s) {
                    return s;
                }
            };
        }

        @Override
        protected void onMissingResponse(CollapsedRequest<String, String> r) {
            onMissingResponseHandler.call(r);
        }

        @Override
        protected Func1<String, String> getBatchReturnTypeToResponseTypeMapper() {
            return new Func1<String, String>() {
                @Override
                public String call(String s) {
                    return s;
                }
            };
        }
    }

    private static class TestCollapserCommandWithMultipleResponsePerArgument extends TestHystrixObservableCommand<String> {
        private final List<Integer> args;

        private final Map<String, Integer> emitsPerArg;

        private final boolean commandExecutionFails;

        private static InspectableBuilder.TestCommandBuilder setter = TestHystrixObservableCommand.testPropsBuilder();

        TestCollapserCommandWithMultipleResponsePerArgument(List<Integer> args, Map<String, Integer> emitsPerArg, boolean commandExecutionFails) {
            super(HystrixObservableCollapserTest.TestCollapserCommandWithMultipleResponsePerArgument.setter);
            this.args = args;
            this.emitsPerArg = emitsPerArg;
            this.commandExecutionFails = commandExecutionFails;
        }

        @Override
        protected Observable<String> construct() {
            Assert.assertNotNull("Wiring the Batch command into the Observable chain should have a HystrixRequestContext", HystrixRequestContext.getContextForCurrentThread());
            if (commandExecutionFails) {
                return Observable.error(new RuntimeException("Synthetic error while running batch command"));
            } else {
                return Observable.create(new rx.Observable.OnSubscribe<String>() {
                    @Override
                    public void call(rx.Subscriber<? super String> subscriber) {
                        try {
                            Assert.assertNotNull("Executing the Batch command should have a HystrixRequestContext", HystrixRequestContext.getContextForCurrentThread());
                            Thread.sleep(1);
                            for (Integer arg : args) {
                                int numEmits = emitsPerArg.get(arg.toString());
                                for (int j = 1; j < (numEmits + 1); j++) {
                                    subscriber.onNext(((arg + ":") + (arg * j)));
                                    Thread.sleep(1);
                                }
                                Thread.sleep(1);
                            }
                            subscriber.onCompleted();
                        } catch (Throwable ex) {
                            ex.printStackTrace();
                            subscriber.onError(ex);
                        }
                    }
                });
            }
        }
    }

    /**
     * A Command implementation that supports caching.
     */
    private static class SuccessfulCacheableCollapsedCommand extends HystrixObservableCollapserTest.TestRequestCollapser {
        private final boolean cacheEnabled;

        public SuccessfulCacheableCollapsedCommand(HystrixCollapserTest.TestCollapserTimer timer, String value, boolean cacheEnabled) {
            super(timer, value);
            this.cacheEnabled = cacheEnabled;
        }

        @Override
        public String getCacheKey() {
            if (cacheEnabled)
                return "aCacheKey_" + (super.value);
            else
                return null;

        }
    }
}

