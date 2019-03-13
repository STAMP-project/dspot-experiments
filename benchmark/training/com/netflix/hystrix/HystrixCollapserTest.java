/**
 * Copyright 2015 Netflix, Inc.
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
import HystrixEventType.FAILURE;
import HystrixEventType.SUCCESS;
import HystrixEventType.TIMEOUT;
import Scope.GLOBAL;
import Scope.REQUEST;
import com.hystrix.junit.HystrixRequestContextRule;
import com.netflix.hystrix.HystrixCollapser.CollapsedRequest;
import com.netflix.hystrix.collapser.CollapserTimer;
import com.netflix.hystrix.collapser.RealCollapserTimer;
import com.netflix.hystrix.collapser.RequestCollapser;
import com.netflix.hystrix.collapser.RequestCollapserFactory;
import com.netflix.hystrix.strategy.HystrixPlugins;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestVariableHolder;
import com.netflix.hystrix.util.HystrixTimer.TimerListener;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action0;
import rx.observers.TestSubscriber;


public class HystrixCollapserTest {
    @Rule
    public HystrixRequestContextRule context = new HystrixRequestContextRule();

    @Test
    public void testTwoRequests() throws Exception {
        HystrixCollapserTest.TestCollapserTimer timer = new HystrixCollapserTest.TestCollapserTimer();
        HystrixCollapser<List<String>, String, String> collapser1 = new HystrixCollapserTest.TestRequestCollapser(timer, 1);
        Future<String> response1 = collapser1.queue();
        HystrixCollapser<List<String>, String, String> collapser2 = new HystrixCollapserTest.TestRequestCollapser(timer, 2);
        Future<String> response2 = collapser2.queue();
        timer.incrementTime(10);// let time pass that equals the default delay/period

        Assert.assertEquals("1", response1.get());
        Assert.assertEquals("2", response2.get(1000, TimeUnit.MILLISECONDS));
        Assert.assertEquals(1, HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().size());
        HystrixCollapserMetrics metrics = collapser1.getMetrics();
        Assert.assertSame(metrics, collapser2.getMetrics());
        HystrixInvokableInfo<?> command = HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().iterator().next();
        Assert.assertEquals(2, command.getNumberCollapsed());
    }

    @Test
    public void testMultipleBatches() throws Exception {
        HystrixCollapserTest.TestCollapserTimer timer = new HystrixCollapserTest.TestCollapserTimer();
        HystrixCollapser<List<String>, String, String> collapser1 = new HystrixCollapserTest.TestRequestCollapser(timer, 1);
        Future<String> response1 = collapser1.queue();
        Future<String> response2 = queue();
        timer.incrementTime(10);// let time pass that equals the default delay/period

        Assert.assertEquals("1", response1.get(1000, TimeUnit.MILLISECONDS));
        Assert.assertEquals("2", response2.get(1000, TimeUnit.MILLISECONDS));
        // now request more
        Future<String> response3 = queue();
        timer.incrementTime(10);// let time pass that equals the default delay/period

        Assert.assertEquals("3", response3.get(1000, TimeUnit.MILLISECONDS));
        // we should have had it execute twice now
        Assert.assertEquals(2, HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().size());
        Iterator<HystrixInvokableInfo<?>> cmdIterator = HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().iterator();
        Assert.assertEquals(2, cmdIterator.next().getNumberCollapsed());
        Assert.assertEquals(1, cmdIterator.next().getNumberCollapsed());
    }

    @Test
    public void testMaxRequestsInBatch() throws Exception {
        HystrixCollapserTest.TestCollapserTimer timer = new HystrixCollapserTest.TestCollapserTimer();
        HystrixCollapser<List<String>, String, String> collapser1 = new HystrixCollapserTest.TestRequestCollapser(timer, 1, 2, 10);
        HystrixCollapser<List<String>, String, String> collapser2 = new HystrixCollapserTest.TestRequestCollapser(timer, 2, 2, 10);
        HystrixCollapser<List<String>, String, String> collapser3 = new HystrixCollapserTest.TestRequestCollapser(timer, 3, 2, 10);
        System.out.println((((("*** " + (System.currentTimeMillis())) + " : ") + (Thread.currentThread().getName())) + " Constructed the collapsers"));
        Future<String> response1 = collapser1.queue();
        Future<String> response2 = collapser2.queue();
        Future<String> response3 = collapser3.queue();
        System.out.println((((("*** " + (System.currentTimeMillis())) + " : ") + (Thread.currentThread().getName())) + " queued the collapsers"));
        timer.incrementTime(10);// let time pass that equals the default delay/period

        System.out.println((((("*** " + (System.currentTimeMillis())) + " : ") + (Thread.currentThread().getName())) + " incremented the virtual timer"));
        Assert.assertEquals("1", response1.get(1000, TimeUnit.MILLISECONDS));
        Assert.assertEquals("2", response2.get(1000, TimeUnit.MILLISECONDS));
        Assert.assertEquals("3", response3.get(1000, TimeUnit.MILLISECONDS));
        // we should have had it execute twice because the batch size was 2
        System.out.println(("ReqLog : " + (HystrixRequestLog.getCurrentRequest().getExecutedCommandsAsString())));
        Assert.assertEquals(2, HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().size());
        Iterator<HystrixInvokableInfo<?>> cmdIterator = HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().iterator();
        Assert.assertEquals(2, cmdIterator.next().getNumberCollapsed());
        Assert.assertEquals(1, cmdIterator.next().getNumberCollapsed());
    }

    @Test
    public void testRequestsOverTime() throws Exception {
        HystrixCollapserTest.TestCollapserTimer timer = new HystrixCollapserTest.TestCollapserTimer();
        HystrixCollapser<List<String>, String, String> collapser1 = new HystrixCollapserTest.TestRequestCollapser(timer, 1);
        Future<String> response1 = collapser1.queue();
        timer.incrementTime(5);
        Future<String> response2 = queue();
        timer.incrementTime(8);
        // should execute here
        Future<String> response3 = queue();
        timer.incrementTime(6);
        Future<String> response4 = queue();
        timer.incrementTime(8);
        // should execute here
        Future<String> response5 = queue();
        timer.incrementTime(10);
        // should execute here
        // wait for all tasks to complete
        Assert.assertEquals("1", response1.get(1000, TimeUnit.MILLISECONDS));
        Assert.assertEquals("2", response2.get(1000, TimeUnit.MILLISECONDS));
        Assert.assertEquals("3", response3.get(1000, TimeUnit.MILLISECONDS));
        Assert.assertEquals("4", response4.get(1000, TimeUnit.MILLISECONDS));
        Assert.assertEquals("5", response5.get(1000, TimeUnit.MILLISECONDS));
        Assert.assertEquals(3, HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().size());
        Iterator<HystrixInvokableInfo<?>> cmdIterator = HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().iterator();
        Assert.assertEquals(2, cmdIterator.next().getNumberCollapsed());
        Assert.assertEquals(2, cmdIterator.next().getNumberCollapsed());
        Assert.assertEquals(1, cmdIterator.next().getNumberCollapsed());
    }

    class Pair<A, B> {
        final A a;

        final B b;

        Pair(A a, B b) {
            this.a = a;
            this.b = b;
        }
    }

    class MyCommand extends HystrixCommand<List<HystrixCollapserTest.Pair<String, Integer>>> {
        private final List<String> args;

        public MyCommand(List<String> args) {
            super(Setter.withGroupKey(Factory.asKey("BATCH")));
            this.args = args;
        }

        @Override
        protected List<HystrixCollapserTest.Pair<String, Integer>> run() throws Exception {
            System.out.println(((("Executing batch command on : " + (Thread.currentThread().getName())) + " with args : ") + (args)));
            List<HystrixCollapserTest.Pair<String, Integer>> results = new ArrayList<HystrixCollapserTest.Pair<String, Integer>>();
            for (String arg : args) {
                results.add(new HystrixCollapserTest.Pair<String, Integer>(arg, Integer.parseInt(arg)));
            }
            return results;
        }
    }

    class MyCollapser extends HystrixCollapser<List<HystrixCollapserTest.Pair<String, Integer>>, Integer, String> {
        private final String arg;

        MyCollapser(String arg, boolean reqCacheEnabled) {
            super(HystrixCollapserKey.Factory.asKey("UNITTEST"), REQUEST, new RealCollapserTimer(), HystrixCollapserProperties.Setter().withRequestCacheEnabled(reqCacheEnabled), HystrixCollapserMetrics.getInstance(HystrixCollapserKey.Factory.asKey("UNITTEST"), new com.netflix.hystrix.strategy.properties.HystrixPropertiesCollapserDefault(HystrixCollapserKey.Factory.asKey("UNITTEST"), HystrixCollapserProperties.Setter())));
            this.arg = arg;
        }

        @Override
        public String getRequestArgument() {
            return arg;
        }

        @Override
        protected HystrixCommand<List<HystrixCollapserTest.Pair<String, Integer>>> createCommand(Collection<CollapsedRequest<Integer, String>> collapsedRequests) {
            List<String> args = new ArrayList<String>(collapsedRequests.size());
            for (CollapsedRequest<Integer, String> req : collapsedRequests) {
                args.add(req.getArgument());
            }
            return new HystrixCollapserTest.MyCommand(args);
        }

        @Override
        protected void mapResponseToRequests(List<HystrixCollapserTest.Pair<String, Integer>> batchResponse, Collection<CollapsedRequest<Integer, String>> collapsedRequests) {
            for (HystrixCollapserTest.Pair<String, Integer> pair : batchResponse) {
                for (CollapsedRequest<Integer, String> collapsedReq : collapsedRequests) {
                    if (collapsedReq.getArgument().equals(pair.a)) {
                        collapsedReq.setResponse(pair.b);
                    }
                }
            }
        }

        @Override
        protected String getCacheKey() {
            return arg;
        }
    }

    @Test
    public void testDuplicateArgumentsWithRequestCachingOn() throws Exception {
        final int NUM = 10;
        List<Observable<Integer>> observables = new ArrayList<Observable<Integer>>();
        for (int i = 0; i < NUM; i++) {
            HystrixCollapserTest.MyCollapser c = new HystrixCollapserTest.MyCollapser("5", true);
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
            sub.assertNoErrors();
            sub.assertValues(5);
        }
    }

    @Test
    public void testDuplicateArgumentsWithRequestCachingOff() throws Exception {
        final int NUM = 10;
        List<Observable<Integer>> observables = new ArrayList<Observable<Integer>>();
        for (int i = 0; i < NUM; i++) {
            HystrixCollapserTest.MyCollapser c = new HystrixCollapserTest.MyCollapser("5", false);
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

    @Test
    public void testUnsubscribeFromSomeDuplicateArgsDoesNotRemoveFromBatch() throws Exception {
        final int NUM = 10;
        List<Observable<Integer>> observables = new ArrayList<Observable<Integer>>();
        for (int i = 0; i < NUM; i++) {
            HystrixCollapserTest.MyCollapser c = new HystrixCollapserTest.MyCollapser("5", true);
            observables.add(toObservable());
        }
        List<TestSubscriber<Integer>> subscribers = new ArrayList<TestSubscriber<Integer>>();
        List<Subscription> subscriptions = new ArrayList<Subscription>();
        for (final Observable<Integer> o : observables) {
            final TestSubscriber<Integer> sub = new TestSubscriber<Integer>();
            subscribers.add(sub);
            Subscription s = o.subscribe(sub);
            subscriptions.add(s);
        }
        // unsubscribe from all but 1
        for (int i = 0; i < (NUM - 1); i++) {
            Subscription s = subscriptions.get(i);
            s.unsubscribe();
        }
        Thread.sleep(100);
        // all subscribers with an active subscription should receive the same value
        for (TestSubscriber<Integer> sub : subscribers) {
            if (!(sub.isUnsubscribed())) {
                sub.awaitTerminalEvent(1000, TimeUnit.MILLISECONDS);
                System.out.println(("Subscriber received : " + (sub.getOnNextEvents())));
                sub.assertNoErrors();
                sub.assertValues(5);
            } else {
                System.out.println("Subscriber is unsubscribed");
            }
        }
    }

    @Test
    public void testUnsubscribeOnOneDoesntKillBatch() throws Exception {
        HystrixCollapserTest.TestCollapserTimer timer = new HystrixCollapserTest.TestCollapserTimer();
        HystrixCollapser<List<String>, String, String> collapser1 = new HystrixCollapserTest.TestRequestCollapser(timer, 1);
        Future<String> response1 = collapser1.queue();
        Future<String> response2 = queue();
        // kill the first
        response1.cancel(true);
        timer.incrementTime(10);// let time pass that equals the default delay/period

        // the first is cancelled so should return null
        try {
            response1.get(1000, TimeUnit.MILLISECONDS);
            Assert.fail("expect CancellationException after cancelling");
        } catch (CancellationException e) {
            // expected
        }
        // we should still get a response on the second
        Assert.assertEquals("2", response2.get(1000, TimeUnit.MILLISECONDS));
        Assert.assertEquals(1, HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().size());
        Iterator<HystrixInvokableInfo<?>> cmdIterator = HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().iterator();
        Assert.assertEquals(1, cmdIterator.next().getNumberCollapsed());
    }

    @Test
    public void testShardedRequests() throws Exception {
        HystrixCollapserTest.TestCollapserTimer timer = new HystrixCollapserTest.TestCollapserTimer();
        HystrixCollapser<List<String>, String, String> collapser1 = new HystrixCollapserTest.TestShardedRequestCollapser(timer, "1a");
        Future<String> response1 = collapser1.queue();
        Future<String> response2 = queue();
        Future<String> response3 = queue();
        Future<String> response4 = queue();
        timer.incrementTime(10);// let time pass that equals the default delay/period

        Assert.assertEquals("1a", response1.get(1000, TimeUnit.MILLISECONDS));
        Assert.assertEquals("2b", response2.get(1000, TimeUnit.MILLISECONDS));
        Assert.assertEquals("3b", response3.get(1000, TimeUnit.MILLISECONDS));
        Assert.assertEquals("4a", response4.get(1000, TimeUnit.MILLISECONDS));
        /* we should get 2 batches since it gets sharded */
        Assert.assertEquals(2, HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().size());
        Iterator<HystrixInvokableInfo<?>> cmdIterator = HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().iterator();
        Assert.assertEquals(2, cmdIterator.next().getNumberCollapsed());
        Assert.assertEquals(2, cmdIterator.next().getNumberCollapsed());
    }

    @Test
    public void testRequestScope() throws Exception {
        HystrixCollapserTest.TestCollapserTimer timer = new HystrixCollapserTest.TestCollapserTimer();
        HystrixCollapser<List<String>, String, String> collapser1 = new HystrixCollapserTest.TestRequestCollapser(timer, "1");
        Future<String> response1 = collapser1.queue();
        Future<String> response2 = queue();
        // simulate a new request
        RequestCollapserFactory.resetRequest();
        Future<String> response3 = queue();
        Future<String> response4 = queue();
        timer.incrementTime(10);// let time pass that equals the default delay/period

        Assert.assertEquals("1", response1.get(1000, TimeUnit.MILLISECONDS));
        Assert.assertEquals("2", response2.get(1000, TimeUnit.MILLISECONDS));
        Assert.assertEquals("3", response3.get(1000, TimeUnit.MILLISECONDS));
        Assert.assertEquals("4", response4.get(1000, TimeUnit.MILLISECONDS));
        // 2 different batches should execute, 1 per request
        Assert.assertEquals(2, HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().size());
        Iterator<HystrixInvokableInfo<?>> cmdIterator = HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().iterator();
        Assert.assertEquals(2, cmdIterator.next().getNumberCollapsed());
        Assert.assertEquals(2, cmdIterator.next().getNumberCollapsed());
    }

    @Test
    public void testGlobalScope() throws Exception {
        HystrixCollapserTest.TestCollapserTimer timer = new HystrixCollapserTest.TestCollapserTimer();
        HystrixCollapser<List<String>, String, String> collapser1 = new HystrixCollapserTest.TestGloballyScopedRequestCollapser(timer, "1");
        Future<String> response1 = collapser1.queue();
        Future<String> response2 = queue();
        // simulate a new request
        RequestCollapserFactory.resetRequest();
        Future<String> response3 = queue();
        Future<String> response4 = queue();
        timer.incrementTime(10);// let time pass that equals the default delay/period

        Assert.assertEquals("1", response1.get(1000, TimeUnit.MILLISECONDS));
        Assert.assertEquals("2", response2.get(1000, TimeUnit.MILLISECONDS));
        Assert.assertEquals("3", response3.get(1000, TimeUnit.MILLISECONDS));
        Assert.assertEquals("4", response4.get(1000, TimeUnit.MILLISECONDS));
        // despite having cleared the cache in between we should have a single execution because this is on the global not request cache
        Assert.assertEquals(1, HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().size());
        Iterator<HystrixInvokableInfo<?>> cmdIterator = HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().iterator();
        Assert.assertEquals(4, cmdIterator.next().getNumberCollapsed());
    }

    @Test
    public void testErrorHandlingViaFutureException() throws Exception {
        HystrixCollapserTest.TestCollapserTimer timer = new HystrixCollapserTest.TestCollapserTimer();
        HystrixCollapser<List<String>, String, String> collapser1 = new HystrixCollapserTest.TestRequestCollapserWithFaultyCreateCommand(timer, "1");
        Future<String> response1 = collapser1.queue();
        Future<String> response2 = queue();
        timer.incrementTime(10);// let time pass that equals the default delay/period

        try {
            response1.get(1000, TimeUnit.MILLISECONDS);
            Assert.fail("we should have received an exception");
        } catch (ExecutionException e) {
            // what we expect
        }
        try {
            response2.get(1000, TimeUnit.MILLISECONDS);
            Assert.fail("we should have received an exception");
        } catch (ExecutionException e) {
            // what we expect
        }
        Assert.assertEquals(0, HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().size());
    }

    @Test
    public void testErrorHandlingWhenMapToResponseFails() throws Exception {
        HystrixCollapserTest.TestCollapserTimer timer = new HystrixCollapserTest.TestCollapserTimer();
        HystrixCollapser<List<String>, String, String> collapser1 = new HystrixCollapserTest.TestRequestCollapserWithFaultyMapToResponse(timer, "1");
        Future<String> response1 = collapser1.queue();
        Future<String> response2 = queue();
        timer.incrementTime(10);// let time pass that equals the default delay/period

        try {
            response1.get(1000, TimeUnit.MILLISECONDS);
            Assert.fail("we should have received an exception");
        } catch (ExecutionException e) {
            // what we expect
        }
        try {
            response2.get(1000, TimeUnit.MILLISECONDS);
            Assert.fail("we should have received an exception");
        } catch (ExecutionException e) {
            // what we expect
        }
        // the batch failed so no executions
        // but it still executed the command once
        Assert.assertEquals(1, HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().size());
        Iterator<HystrixInvokableInfo<?>> cmdIterator = HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().iterator();
        Assert.assertEquals(2, cmdIterator.next().getNumberCollapsed());
    }

    @Test
    public void testRequestVariableLifecycle1() throws Exception {
        HystrixRequestContext reqContext = HystrixRequestContext.initializeContext();
        // do actual work
        HystrixCollapserTest.TestCollapserTimer timer = new HystrixCollapserTest.TestCollapserTimer();
        HystrixCollapser<List<String>, String, String> collapser1 = new HystrixCollapserTest.TestRequestCollapser(timer, 1);
        Future<String> response1 = collapser1.queue();
        timer.incrementTime(5);
        Future<String> response2 = queue();
        timer.incrementTime(8);
        // should execute here
        Future<String> response3 = queue();
        timer.incrementTime(6);
        Future<String> response4 = queue();
        timer.incrementTime(8);
        // should execute here
        Future<String> response5 = queue();
        timer.incrementTime(10);
        // should execute here
        // wait for all tasks to complete
        Assert.assertEquals("1", response1.get(1000, TimeUnit.MILLISECONDS));
        Assert.assertEquals("2", response2.get(1000, TimeUnit.MILLISECONDS));
        Assert.assertEquals("3", response3.get(1000, TimeUnit.MILLISECONDS));
        Assert.assertEquals("4", response4.get(1000, TimeUnit.MILLISECONDS));
        Assert.assertEquals("5", response5.get(1000, TimeUnit.MILLISECONDS));
        // each task should have been executed 3 times
        for (HystrixCollapserTest.TestCollapserTimer.ATask t : timer.tasks) {
            Assert.assertEquals(3, t.task.count.get());
        }
        System.out.println(("timer.tasks.size() A: " + (timer.tasks.size())));
        System.out.println(("tasks in test: " + (timer.tasks)));
        Iterator<HystrixInvokableInfo<?>> cmdIterator = HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().iterator();
        Assert.assertEquals(2, cmdIterator.next().getNumberCollapsed());
        Assert.assertEquals(2, cmdIterator.next().getNumberCollapsed());
        Assert.assertEquals(1, cmdIterator.next().getNumberCollapsed());
        System.out.println(("timer.tasks.size() B: " + (timer.tasks.size())));
        HystrixRequestVariableHolder<RequestCollapser<?, ?, ?>> rv = RequestCollapserFactory.getRequestVariable(getCollapserKey().name());
        reqContext.close();
        Assert.assertNotNull(rv);
        // they should have all been removed as part of ThreadContext.remove()
        Assert.assertEquals(0, timer.tasks.size());
    }

    @Test
    public void testRequestVariableLifecycle2() throws Exception {
        final HystrixRequestContext reqContext = HystrixRequestContext.initializeContext();
        final HystrixCollapserTest.TestCollapserTimer timer = new HystrixCollapserTest.TestCollapserTimer();
        final ConcurrentLinkedQueue<Future<String>> responses = new ConcurrentLinkedQueue<Future<String>>();
        ConcurrentLinkedQueue<Thread> threads = new ConcurrentLinkedQueue<Thread>();
        // kick off work (simulating a single request with multiple threads)
        for (int t = 0; t < 5; t++) {
            final int outerLoop = t;
            Thread th = new Thread(new com.netflix.hystrix.strategy.concurrency.HystrixContextRunnable(HystrixPlugins.getInstance().getConcurrencyStrategy(), new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 100; i++) {
                        int uniqueInt = (outerLoop * 100) + i;
                        responses.add(new HystrixCollapserTest.TestRequestCollapser(timer, uniqueInt).queue());
                    }
                }
            }));
            threads.add(th);
            th.start();
        }
        for (Thread th : threads) {
            // wait for each thread to finish
            th.join();
        }
        // we expect 5 threads * 100 responses each
        Assert.assertEquals(500, responses.size());
        for (Future<String> f : responses) {
            // they should not be done yet because the counter hasn't incremented
            Assert.assertFalse(f.isDone());
        }
        timer.incrementTime(5);
        HystrixCollapser<List<String>, String, String> collapser1 = new HystrixCollapserTest.TestRequestCollapser(timer, 2);
        Future<String> response2 = collapser1.queue();
        timer.incrementTime(8);
        // should execute here
        Future<String> response3 = queue();
        timer.incrementTime(6);
        Future<String> response4 = queue();
        timer.incrementTime(8);
        // should execute here
        Future<String> response5 = queue();
        timer.incrementTime(10);
        // should execute here
        // wait for all tasks to complete
        for (Future<String> f : responses) {
            f.get(1000, TimeUnit.MILLISECONDS);
        }
        Assert.assertEquals("2", response2.get(1000, TimeUnit.MILLISECONDS));
        Assert.assertEquals("3", response3.get(1000, TimeUnit.MILLISECONDS));
        Assert.assertEquals("4", response4.get(1000, TimeUnit.MILLISECONDS));
        Assert.assertEquals("5", response5.get(1000, TimeUnit.MILLISECONDS));
        // each task should have been executed 3 times
        for (HystrixCollapserTest.TestCollapserTimer.ATask t : timer.tasks) {
            Assert.assertEquals(3, t.task.count.get());
        }
        Iterator<HystrixInvokableInfo<?>> cmdIterator = HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().iterator();
        Assert.assertEquals(500, cmdIterator.next().getNumberCollapsed());
        Assert.assertEquals(2, cmdIterator.next().getNumberCollapsed());
        Assert.assertEquals(1, cmdIterator.next().getNumberCollapsed());
        HystrixRequestVariableHolder<RequestCollapser<?, ?, ?>> rv = RequestCollapserFactory.getRequestVariable(getCollapserKey().name());
        reqContext.close();
        Assert.assertNotNull(rv);
        // they should have all been removed as part of ThreadContext.remove()
        Assert.assertEquals(0, timer.tasks.size());
    }

    /**
     * Test Request scoped caching of commands so that a 2nd duplicate call doesn't execute but returns the previous Future
     */
    @Test
    public void testRequestCache1() {
        final HystrixCollapserTest.TestCollapserTimer timer = new HystrixCollapserTest.TestCollapserTimer();
        HystrixCollapserTest.SuccessfulCacheableCollapsedCommand command1 = new HystrixCollapserTest.SuccessfulCacheableCollapsedCommand(timer, "A", true);
        HystrixCollapserTest.SuccessfulCacheableCollapsedCommand command2 = new HystrixCollapserTest.SuccessfulCacheableCollapsedCommand(timer, "A", true);
        Future<String> f1 = queue();
        Future<String> f2 = queue();
        // increment past batch time so it executes
        timer.incrementTime(15);
        try {
            Assert.assertEquals("A", f1.get(1000, TimeUnit.MILLISECONDS));
            Assert.assertEquals("A", f2.get(1000, TimeUnit.MILLISECONDS));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Future<String> f3 = queue();
        // increment past batch time so it executes
        timer.incrementTime(15);
        try {
            Assert.assertEquals("A", f3.get(1000, TimeUnit.MILLISECONDS));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // we should still have executed only one command
        Assert.assertEquals(1, HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().size());
        HystrixInvokableInfo<?> command = HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().toArray(new HystrixInvokableInfo<?>[1])[0];
        System.out.println(("command.getExecutionEvents(): " + (command.getExecutionEvents())));
        Assert.assertEquals(2, command.getExecutionEvents().size());
        Assert.assertTrue(command.getExecutionEvents().contains(SUCCESS));
        Assert.assertTrue(command.getExecutionEvents().contains(COLLAPSED));
        Iterator<HystrixInvokableInfo<?>> cmdIterator = HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().iterator();
        Assert.assertEquals(1, cmdIterator.next().getNumberCollapsed());
    }

    /**
     * Test Request scoped caching doesn't prevent different ones from executing
     */
    @Test
    public void testRequestCache2() {
        final HystrixCollapserTest.TestCollapserTimer timer = new HystrixCollapserTest.TestCollapserTimer();
        HystrixCollapserTest.SuccessfulCacheableCollapsedCommand command1 = new HystrixCollapserTest.SuccessfulCacheableCollapsedCommand(timer, "A", true);
        HystrixCollapserTest.SuccessfulCacheableCollapsedCommand command2 = new HystrixCollapserTest.SuccessfulCacheableCollapsedCommand(timer, "B", true);
        Future<String> f1 = queue();
        Future<String> f2 = queue();
        // increment past batch time so it executes
        timer.incrementTime(15);
        try {
            Assert.assertEquals("A", f1.get(1000, TimeUnit.MILLISECONDS));
            Assert.assertEquals("B", f2.get(1000, TimeUnit.MILLISECONDS));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Future<String> f3 = queue();
        Future<String> f4 = queue();
        // increment past batch time so it executes
        timer.incrementTime(15);
        try {
            Assert.assertEquals("A", f3.get(1000, TimeUnit.MILLISECONDS));
            Assert.assertEquals("B", f4.get(1000, TimeUnit.MILLISECONDS));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // we should still have executed only one command
        Assert.assertEquals(1, HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().size());
        HystrixInvokableInfo<?> command = HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().toArray(new HystrixInvokableInfo<?>[1])[0];
        Assert.assertEquals(2, command.getExecutionEvents().size());
        Assert.assertTrue(command.getExecutionEvents().contains(SUCCESS));
        Assert.assertTrue(command.getExecutionEvents().contains(COLLAPSED));
        Iterator<HystrixInvokableInfo<?>> cmdIterator = HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().iterator();
        Assert.assertEquals(2, cmdIterator.next().getNumberCollapsed());
    }

    /**
     * Test Request scoped caching with a mixture of commands
     */
    @Test
    public void testRequestCache3() {
        final HystrixCollapserTest.TestCollapserTimer timer = new HystrixCollapserTest.TestCollapserTimer();
        HystrixCollapserTest.SuccessfulCacheableCollapsedCommand command1 = new HystrixCollapserTest.SuccessfulCacheableCollapsedCommand(timer, "A", true);
        HystrixCollapserTest.SuccessfulCacheableCollapsedCommand command2 = new HystrixCollapserTest.SuccessfulCacheableCollapsedCommand(timer, "B", true);
        HystrixCollapserTest.SuccessfulCacheableCollapsedCommand command3 = new HystrixCollapserTest.SuccessfulCacheableCollapsedCommand(timer, "B", true);
        Future<String> f1 = queue();
        Future<String> f2 = queue();
        Future<String> f3 = queue();
        // increment past batch time so it executes
        timer.incrementTime(15);
        try {
            Assert.assertEquals("A", f1.get(1000, TimeUnit.MILLISECONDS));
            Assert.assertEquals("B", f2.get(1000, TimeUnit.MILLISECONDS));
            Assert.assertEquals("B", f3.get(1000, TimeUnit.MILLISECONDS));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Future<String> f4 = queue();
        Future<String> f5 = queue();
        Future<String> f6 = queue();
        // increment past batch time so it executes
        timer.incrementTime(15);
        try {
            Assert.assertEquals("A", f4.get(1000, TimeUnit.MILLISECONDS));
            Assert.assertEquals("B", f5.get(1000, TimeUnit.MILLISECONDS));
            Assert.assertEquals("B", f6.get(1000, TimeUnit.MILLISECONDS));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // we should still have executed only one command
        Assert.assertEquals(1, HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().size());
        HystrixInvokableInfo<?> command = HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().toArray(new HystrixInvokableInfo<?>[1])[0];
        Assert.assertEquals(2, command.getExecutionEvents().size());
        Assert.assertTrue(command.getExecutionEvents().contains(SUCCESS));
        Assert.assertTrue(command.getExecutionEvents().contains(COLLAPSED));
        Iterator<HystrixInvokableInfo<?>> cmdIterator = HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().iterator();
        Assert.assertEquals(2, cmdIterator.next().getNumberCollapsed());
    }

    /**
     * Test Request scoped caching with a mixture of commands
     */
    @Test
    public void testNoRequestCache3() {
        final HystrixCollapserTest.TestCollapserTimer timer = new HystrixCollapserTest.TestCollapserTimer();
        HystrixCollapserTest.SuccessfulCacheableCollapsedCommand command1 = new HystrixCollapserTest.SuccessfulCacheableCollapsedCommand(timer, "A", false);
        HystrixCollapserTest.SuccessfulCacheableCollapsedCommand command2 = new HystrixCollapserTest.SuccessfulCacheableCollapsedCommand(timer, "B", false);
        HystrixCollapserTest.SuccessfulCacheableCollapsedCommand command3 = new HystrixCollapserTest.SuccessfulCacheableCollapsedCommand(timer, "B", false);
        Future<String> f1 = queue();
        Future<String> f2 = queue();
        Future<String> f3 = queue();
        // increment past batch time so it executes
        timer.incrementTime(15);
        try {
            Assert.assertEquals("A", f1.get(1000, TimeUnit.MILLISECONDS));
            Assert.assertEquals("B", f2.get(1000, TimeUnit.MILLISECONDS));
            Assert.assertEquals("B", f3.get(1000, TimeUnit.MILLISECONDS));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Future<String> f4 = queue();
        Future<String> f5 = queue();
        Future<String> f6 = queue();
        // increment past batch time so it executes
        timer.incrementTime(15);
        try {
            Assert.assertEquals("A", f4.get(1000, TimeUnit.MILLISECONDS));
            Assert.assertEquals("B", f5.get(1000, TimeUnit.MILLISECONDS));
            Assert.assertEquals("B", f6.get(1000, TimeUnit.MILLISECONDS));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // request caching is turned off on this so we expect 2 command executions
        Assert.assertEquals(2, HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().size());
        // we expect to see it with SUCCESS and COLLAPSED and both
        HystrixInvokableInfo<?> commandA = HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().toArray(new HystrixInvokableInfo<?>[2])[0];
        Assert.assertEquals(2, commandA.getExecutionEvents().size());
        Assert.assertTrue(commandA.getExecutionEvents().contains(SUCCESS));
        Assert.assertTrue(commandA.getExecutionEvents().contains(COLLAPSED));
        // we expect to see it with SUCCESS and COLLAPSED and both
        HystrixInvokableInfo<?> commandB = HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().toArray(new HystrixInvokableInfo<?>[2])[1];
        Assert.assertEquals(2, commandB.getExecutionEvents().size());
        Assert.assertTrue(commandB.getExecutionEvents().contains(SUCCESS));
        Assert.assertTrue(commandB.getExecutionEvents().contains(COLLAPSED));
        Iterator<HystrixInvokableInfo<?>> cmdIterator = HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().iterator();
        Assert.assertEquals(2, cmdIterator.next().getNumberCollapsed());// 1 for A, 1 for B.  Batch contains only unique arguments (no duplicates)

        Assert.assertEquals(2, cmdIterator.next().getNumberCollapsed());// 1 for A, 1 for B.  Batch contains only unique arguments (no duplicates)

    }

    /**
     * Test command that uses a null request argument
     */
    @Test
    public void testRequestCacheWithNullRequestArgument() throws Exception {
        ConcurrentLinkedQueue<HystrixCommand<List<String>>> commands = new ConcurrentLinkedQueue<HystrixCommand<List<String>>>();
        final HystrixCollapserTest.TestCollapserTimer timer = new HystrixCollapserTest.TestCollapserTimer();
        HystrixCollapserTest.SuccessfulCacheableCollapsedCommand command1 = new HystrixCollapserTest.SuccessfulCacheableCollapsedCommand(timer, null, true, commands);
        HystrixCollapserTest.SuccessfulCacheableCollapsedCommand command2 = new HystrixCollapserTest.SuccessfulCacheableCollapsedCommand(timer, null, true, commands);
        Future<String> f1 = queue();
        Future<String> f2 = queue();
        // increment past batch time so it executes
        timer.incrementTime(15);
        Assert.assertEquals("NULL", f1.get(1000, TimeUnit.MILLISECONDS));
        Assert.assertEquals("NULL", f2.get(1000, TimeUnit.MILLISECONDS));
        // it should have executed 1 command
        Assert.assertEquals(1, commands.size());
        Assert.assertTrue(commands.peek().getExecutionEvents().contains(SUCCESS));
        Assert.assertTrue(commands.peek().getExecutionEvents().contains(COLLAPSED));
        Future<String> f3 = queue();
        // increment past batch time so it executes
        timer.incrementTime(15);
        Assert.assertEquals("NULL", f3.get(1000, TimeUnit.MILLISECONDS));
        // it should still be 1 ... no new executions
        Assert.assertEquals(1, commands.size());
        Assert.assertEquals(1, HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().size());
        Iterator<HystrixInvokableInfo<?>> cmdIterator = HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().iterator();
        Assert.assertEquals(1, cmdIterator.next().getNumberCollapsed());
    }

    @Test
    public void testRequestCacheWithCommandError() {
        ConcurrentLinkedQueue<HystrixCommand<List<String>>> commands = new ConcurrentLinkedQueue<HystrixCommand<List<String>>>();
        final HystrixCollapserTest.TestCollapserTimer timer = new HystrixCollapserTest.TestCollapserTimer();
        HystrixCollapserTest.SuccessfulCacheableCollapsedCommand command1 = new HystrixCollapserTest.SuccessfulCacheableCollapsedCommand(timer, "FAILURE", true, commands);
        HystrixCollapserTest.SuccessfulCacheableCollapsedCommand command2 = new HystrixCollapserTest.SuccessfulCacheableCollapsedCommand(timer, "FAILURE", true, commands);
        Future<String> f1 = queue();
        Future<String> f2 = queue();
        // increment past batch time so it executes
        timer.incrementTime(15);
        try {
            Assert.assertEquals("A", f1.get(1000, TimeUnit.MILLISECONDS));
            Assert.assertEquals("A", f2.get(1000, TimeUnit.MILLISECONDS));
            Assert.fail("exception should have been thrown");
        } catch (Exception e) {
            // expected
        }
        // it should have executed 1 command
        Assert.assertEquals(1, commands.size());
        Assert.assertTrue(commands.peek().getExecutionEvents().contains(FAILURE));
        Assert.assertTrue(commands.peek().getExecutionEvents().contains(COLLAPSED));
        Future<String> f3 = queue();
        // increment past batch time so it executes
        timer.incrementTime(15);
        try {
            Assert.assertEquals("A", f3.get(1000, TimeUnit.MILLISECONDS));
            Assert.fail("exception should have been thrown");
        } catch (Exception e) {
            // expected
        }
        // it should still be 1 ... no new executions
        Assert.assertEquals(1, commands.size());
        Assert.assertEquals(1, HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().size());
        Iterator<HystrixInvokableInfo<?>> cmdIterator = HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().iterator();
        Assert.assertEquals(1, cmdIterator.next().getNumberCollapsed());
    }

    /**
     * Test that a command that times out will still be cached and when retrieved will re-throw the exception.
     */
    @Test
    public void testRequestCacheWithCommandTimeout() {
        ConcurrentLinkedQueue<HystrixCommand<List<String>>> commands = new ConcurrentLinkedQueue<HystrixCommand<List<String>>>();
        final HystrixCollapserTest.TestCollapserTimer timer = new HystrixCollapserTest.TestCollapserTimer();
        HystrixCollapserTest.SuccessfulCacheableCollapsedCommand command1 = new HystrixCollapserTest.SuccessfulCacheableCollapsedCommand(timer, "TIMEOUT", true, commands);
        HystrixCollapserTest.SuccessfulCacheableCollapsedCommand command2 = new HystrixCollapserTest.SuccessfulCacheableCollapsedCommand(timer, "TIMEOUT", true, commands);
        Future<String> f1 = queue();
        Future<String> f2 = queue();
        // increment past batch time so it executes
        timer.incrementTime(15);
        try {
            Assert.assertEquals("A", f1.get(1000, TimeUnit.MILLISECONDS));
            Assert.assertEquals("A", f2.get(1000, TimeUnit.MILLISECONDS));
            Assert.fail("exception should have been thrown");
        } catch (Exception e) {
            // expected
        }
        // it should have executed 1 command
        Assert.assertEquals(1, commands.size());
        Assert.assertTrue(commands.peek().getExecutionEvents().contains(TIMEOUT));
        Assert.assertTrue(commands.peek().getExecutionEvents().contains(COLLAPSED));
        Future<String> f3 = queue();
        // increment past batch time so it executes
        timer.incrementTime(15);
        try {
            Assert.assertEquals("A", f3.get(1000, TimeUnit.MILLISECONDS));
            Assert.fail("exception should have been thrown");
        } catch (Exception e) {
            // expected
        }
        // it should still be 1 ... no new executions
        Assert.assertEquals(1, commands.size());
        Assert.assertEquals(1, HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().size());
        Iterator<HystrixInvokableInfo<?>> cmdIterator = HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().iterator();
        Assert.assertEquals(1, cmdIterator.next().getNumberCollapsed());
    }

    /**
     * Test how the collapser behaves when the circuit is short-circuited
     */
    @Test
    public void testRequestWithCommandShortCircuited() throws Exception {
        HystrixCollapserTest.TestCollapserTimer timer = new HystrixCollapserTest.TestCollapserTimer();
        HystrixCollapser<List<String>, String, String> collapser1 = new HystrixCollapserTest.TestRequestCollapserWithShortCircuitedCommand(timer, "1");
        Observable<String> response1 = collapser1.observe();
        Observable<String> response2 = observe();
        timer.incrementTime(10);// let time pass that equals the default delay/period

        try {
            response1.toBlocking().first();
            Assert.fail("we should have received an exception");
        } catch (Exception e) {
            e.printStackTrace();
            // what we expect
        }
        try {
            response2.toBlocking().first();
            Assert.fail("we should have received an exception");
        } catch (Exception e) {
            e.printStackTrace();
            // what we expect
        }
        // it will execute once (short-circuited)
        Assert.assertEquals(1, HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().size());
        Iterator<HystrixInvokableInfo<?>> cmdIterator = HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().iterator();
        Assert.assertEquals(2, cmdIterator.next().getNumberCollapsed());
    }

    /**
     * Test a Void response type - null being set as response.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testVoidResponseTypeFireAndForgetCollapsing1() throws Exception {
        HystrixCollapserTest.TestCollapserTimer timer = new HystrixCollapserTest.TestCollapserTimer();
        HystrixCollapserTest.TestCollapserWithVoidResponseType collapser1 = new HystrixCollapserTest.TestCollapserWithVoidResponseType(timer, 1);
        Future<Void> response1 = queue();
        Future<Void> response2 = queue();
        timer.incrementTime(100);// let time pass that equals the default delay/period

        // normally someone wouldn't wait on these, but we need to make sure they do in fact return
        // and not block indefinitely in case someone does call get()
        Assert.assertEquals(null, response1.get(1000, TimeUnit.MILLISECONDS));
        Assert.assertEquals(null, response2.get(1000, TimeUnit.MILLISECONDS));
        Assert.assertEquals(1, HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().size());
        Iterator<HystrixInvokableInfo<?>> cmdIterator = HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().iterator();
        Assert.assertEquals(2, cmdIterator.next().getNumberCollapsed());
    }

    /**
     * Test a Void response type - response never being set in mapResponseToRequest
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testVoidResponseTypeFireAndForgetCollapsing2() throws Exception {
        HystrixCollapserTest.TestCollapserTimer timer = new HystrixCollapserTest.TestCollapserTimer();
        HystrixCollapserTest.TestCollapserWithVoidResponseTypeAndMissingMapResponseToRequests collapser1 = new HystrixCollapserTest.TestCollapserWithVoidResponseTypeAndMissingMapResponseToRequests(timer, 1);
        Future<Void> response1 = queue();
        new HystrixCollapserTest.TestCollapserWithVoidResponseTypeAndMissingMapResponseToRequests(timer, 2).queue();
        timer.incrementTime(100);// let time pass that equals the default delay/period

        // we will fetch one of these just so we wait for completion ... but expect an error
        try {
            Assert.assertEquals(null, response1.get(1000, TimeUnit.MILLISECONDS));
            Assert.fail("expected an error as mapResponseToRequests did not set responses");
        } catch (ExecutionException e) {
            Assert.assertTrue(((e.getCause()) instanceof IllegalStateException));
            Assert.assertTrue(e.getCause().getMessage().startsWith("No response set by"));
        }
        Assert.assertEquals(1, HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().size());
        Iterator<HystrixInvokableInfo<?>> cmdIterator = HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().iterator();
        Assert.assertEquals(2, cmdIterator.next().getNumberCollapsed());
    }

    /**
     * Test a Void response type with execute - response being set in mapResponseToRequest to null
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testVoidResponseTypeFireAndForgetCollapsing3() throws Exception {
        CollapserTimer timer = new RealCollapserTimer();
        HystrixCollapserTest.TestCollapserWithVoidResponseType collapser1 = new HystrixCollapserTest.TestCollapserWithVoidResponseType(timer, 1);
        Assert.assertNull(execute());
        Assert.assertEquals(1, HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().size());
        Iterator<HystrixInvokableInfo<?>> cmdIterator = HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().iterator();
        Assert.assertEquals(1, cmdIterator.next().getNumberCollapsed());
    }

    @Test
    public void testEarlyUnsubscribeExecutedViaToObservable() throws Exception {
        HystrixCollapserTest.TestCollapserTimer timer = new HystrixCollapserTest.TestCollapserTimer();
        HystrixCollapser<List<String>, String, String> collapser1 = new HystrixCollapserTest.TestRequestCollapser(timer, 1);
        Observable<String> response1 = collapser1.toObservable();
        HystrixCollapser<List<String>, String, String> collapser2 = new HystrixCollapserTest.TestRequestCollapser(timer, 2);
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
        HystrixCollapser<List<String>, String, String> collapser1 = new HystrixCollapserTest.TestRequestCollapser(timer, 1);
        Observable<String> response1 = collapser1.observe();
        HystrixCollapser<List<String>, String, String> collapser2 = new HystrixCollapserTest.TestRequestCollapser(timer, 2);
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
        HystrixCollapser<List<String>, String, String> collapser1 = new HystrixCollapserTest.TestRequestCollapser(timer, 1);
        Observable<String> response1 = collapser1.observe();
        HystrixCollapser<List<String>, String, String> collapser2 = new HystrixCollapserTest.TestRequestCollapser(timer, 2);
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
        HystrixCollapser<List<String>, String, String> collapser1 = new HystrixCollapserTest.SuccessfulCacheableCollapsedCommand(timer, "foo", true);
        Observable<String> response1 = collapser1.observe();
        HystrixCollapser<List<String>, String, String> collapser2 = new HystrixCollapserTest.SuccessfulCacheableCollapsedCommand(timer, "foo", true);
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
        assertCommandExecutionEvents(command, SUCCESS, COLLAPSED);
        Assert.assertEquals(1, command.getNumberCollapsed());// should only be 1 collapsed - other came from cache, then was cancelled

    }

    @Test
    public void testRequestThenCacheHitAndOriginalUnsubscribed() throws Exception {
        HystrixCollapserTest.TestCollapserTimer timer = new HystrixCollapserTest.TestCollapserTimer();
        HystrixCollapser<List<String>, String, String> collapser1 = new HystrixCollapserTest.SuccessfulCacheableCollapsedCommand(timer, "foo", true);
        Observable<String> response1 = collapser1.observe();
        HystrixCollapser<List<String>, String, String> collapser2 = new HystrixCollapserTest.SuccessfulCacheableCollapsedCommand(timer, "foo", true);
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
        assertCommandExecutionEvents(command, SUCCESS, COLLAPSED);
        Assert.assertEquals(1, command.getNumberCollapsed());// should only be 1 collapsed - other came from cache, then was cancelled

    }

    @Test
    public void testRequestThenTwoCacheHitsOriginalAndOneCacheHitUnsubscribed() throws Exception {
        HystrixCollapserTest.TestCollapserTimer timer = new HystrixCollapserTest.TestCollapserTimer();
        HystrixCollapser<List<String>, String, String> collapser1 = new HystrixCollapserTest.SuccessfulCacheableCollapsedCommand(timer, "foo", true);
        Observable<String> response1 = collapser1.observe();
        HystrixCollapser<List<String>, String, String> collapser2 = new HystrixCollapserTest.SuccessfulCacheableCollapsedCommand(timer, "foo", true);
        Observable<String> response2 = collapser2.observe();
        HystrixCollapser<List<String>, String, String> collapser3 = new HystrixCollapserTest.SuccessfulCacheableCollapsedCommand(timer, "foo", true);
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
        assertCommandExecutionEvents(command, SUCCESS, COLLAPSED);
        Assert.assertEquals(1, command.getNumberCollapsed());// should only be 1 collapsed - other came from cache, then was cancelled

    }

    @Test
    public void testRequestThenTwoCacheHitsAllUnsubscribed() throws Exception {
        HystrixCollapserTest.TestCollapserTimer timer = new HystrixCollapserTest.TestCollapserTimer();
        HystrixCollapser<List<String>, String, String> collapser1 = new HystrixCollapserTest.SuccessfulCacheableCollapsedCommand(timer, "foo", true);
        Observable<String> response1 = collapser1.observe();
        HystrixCollapser<List<String>, String, String> collapser2 = new HystrixCollapserTest.SuccessfulCacheableCollapsedCommand(timer, "foo", true);
        Observable<String> response2 = collapser2.observe();
        HystrixCollapser<List<String>, String, String> collapser3 = new HystrixCollapserTest.SuccessfulCacheableCollapsedCommand(timer, "foo", true);
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

    private static class TestRequestCollapser extends HystrixCollapser<List<String>, String, String> {
        private final String value;

        private ConcurrentLinkedQueue<HystrixCommand<List<String>>> commandsExecuted;

        public TestRequestCollapser(HystrixCollapserTest.TestCollapserTimer timer, int value) {
            this(timer, String.valueOf(value));
        }

        public TestRequestCollapser(HystrixCollapserTest.TestCollapserTimer timer, String value) {
            this(timer, value, 10000, 10);
        }

        public TestRequestCollapser(Scope scope, HystrixCollapserTest.TestCollapserTimer timer, String value) {
            this(scope, timer, value, 10000, 10);
        }

        public TestRequestCollapser(HystrixCollapserTest.TestCollapserTimer timer, String value, ConcurrentLinkedQueue<HystrixCommand<List<String>>> executionLog) {
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

        public TestRequestCollapser(HystrixCollapserTest.TestCollapserTimer timer, String value, int defaultMaxRequestsInBatch, int defaultTimerDelayInMilliseconds, ConcurrentLinkedQueue<HystrixCommand<List<String>>> executionLog) {
            this(REQUEST, timer, value, defaultMaxRequestsInBatch, defaultTimerDelayInMilliseconds, executionLog);
        }

        private static HystrixCollapserMetrics createMetrics() {
            HystrixCollapserKey key = HystrixCollapserKey.Factory.asKey("COLLAPSER_ONE");
            return HystrixCollapserMetrics.getInstance(key, new com.netflix.hystrix.strategy.properties.HystrixPropertiesCollapserDefault(key, HystrixCollapserProperties.Setter()));
        }

        public TestRequestCollapser(Scope scope, HystrixCollapserTest.TestCollapserTimer timer, String value, int defaultMaxRequestsInBatch, int defaultTimerDelayInMilliseconds, ConcurrentLinkedQueue<HystrixCommand<List<String>>> executionLog) {
            // use a CollapserKey based on the CollapserTimer object reference so it's unique for each timer as we don't want caching
            // of properties to occur and we're using the default HystrixProperty which typically does caching
            super(HystrixCollapserTest.collapserKeyFromString(timer), scope, timer, HystrixCollapserProperties.Setter().withMaxRequestsInBatch(defaultMaxRequestsInBatch).withTimerDelayInMilliseconds(defaultTimerDelayInMilliseconds), HystrixCollapserTest.TestRequestCollapser.createMetrics());
            this.value = value;
            this.commandsExecuted = executionLog;
        }

        @Override
        public String getRequestArgument() {
            return value;
        }

        @Override
        public HystrixCommand<List<String>> createCommand(final Collection<CollapsedRequest<String, String>> requests) {
            /* return a mocked command */
            HystrixCommand<List<String>> command = new HystrixCollapserTest.TestCollapserCommand(requests);
            if ((commandsExecuted) != null) {
                commandsExecuted.add(command);
            }
            return command;
        }

        @Override
        public void mapResponseToRequests(List<String> batchResponse, Collection<CollapsedRequest<String, String>> requests) {
            // for simplicity I'll assume it's a 1:1 mapping between lists ... in real implementations they often need to index to maps
            // to allow random access as the response size does not match the request size
            if ((batchResponse.size()) != (requests.size())) {
                throw new RuntimeException(((("lists don't match in size => " + (batchResponse.size())) + " : ") + (requests.size())));
            }
            int i = 0;
            for (CollapsedRequest<String, String> request : requests) {
                request.setResponse(batchResponse.get((i++)));
            }
        }
    }

    /**
     * Shard on the artificially provided 'type' variable.
     */
    private static class TestShardedRequestCollapser extends HystrixCollapserTest.TestRequestCollapser {
        public TestShardedRequestCollapser(HystrixCollapserTest.TestCollapserTimer timer, String value) {
            super(timer, value);
        }

        @Override
        protected Collection<Collection<CollapsedRequest<String, String>>> shardRequests(Collection<CollapsedRequest<String, String>> requests) {
            Collection<CollapsedRequest<String, String>> typeA = new ArrayList<CollapsedRequest<String, String>>();
            Collection<CollapsedRequest<String, String>> typeB = new ArrayList<CollapsedRequest<String, String>>();
            for (CollapsedRequest<String, String> request : requests) {
                if (request.getArgument().endsWith("a")) {
                    typeA.add(request);
                } else
                    if (request.getArgument().endsWith("b")) {
                        typeB.add(request);
                    }

            }
            ArrayList<Collection<CollapsedRequest<String, String>>> shards = new ArrayList<Collection<CollapsedRequest<String, String>>>();
            shards.add(typeA);
            shards.add(typeB);
            return shards;
        }
    }

    /**
     * Test the global scope
     */
    private static class TestGloballyScopedRequestCollapser extends HystrixCollapserTest.TestRequestCollapser {
        public TestGloballyScopedRequestCollapser(HystrixCollapserTest.TestCollapserTimer timer, String value) {
            super(GLOBAL, timer, value);
        }
    }

    /**
     * Throw an exception when creating a command.
     */
    private static class TestRequestCollapserWithFaultyCreateCommand extends HystrixCollapserTest.TestRequestCollapser {
        public TestRequestCollapserWithFaultyCreateCommand(HystrixCollapserTest.TestCollapserTimer timer, String value) {
            super(timer, value);
        }

        @Override
        public HystrixCommand<List<String>> createCommand(Collection<CollapsedRequest<String, String>> requests) {
            throw new RuntimeException("some failure");
        }
    }

    /**
     * Throw an exception when creating a command.
     */
    private static class TestRequestCollapserWithShortCircuitedCommand extends HystrixCollapserTest.TestRequestCollapser {
        public TestRequestCollapserWithShortCircuitedCommand(HystrixCollapserTest.TestCollapserTimer timer, String value) {
            super(timer, value);
        }

        @Override
        public HystrixCommand<List<String>> createCommand(Collection<CollapsedRequest<String, String>> requests) {
            // args don't matter as it's short-circuited
            return new HystrixCollapserTest.ShortCircuitedCommand();
        }
    }

    /**
     * Throw an exception when mapToResponse is invoked
     */
    private static class TestRequestCollapserWithFaultyMapToResponse extends HystrixCollapserTest.TestRequestCollapser {
        public TestRequestCollapserWithFaultyMapToResponse(HystrixCollapserTest.TestCollapserTimer timer, String value) {
            super(timer, value);
        }

        @Override
        public void mapResponseToRequests(List<String> batchResponse, Collection<CollapsedRequest<String, String>> requests) {
            // pretend we blow up with an NPE
            throw new NullPointerException("batchResponse was null and we blew up");
        }
    }

    private static class TestCollapserCommand extends TestHystrixCommand<List<String>> {
        private final Collection<CollapsedRequest<String, String>> requests;

        TestCollapserCommand(Collection<CollapsedRequest<String, String>> requests) {
            super(TestHystrixCommand.testPropsBuilder().setCommandPropertiesDefaults(HystrixCommandPropertiesTest.getUnitTestPropertiesSetter().withExecutionTimeoutInMilliseconds(500)));
            this.requests = requests;
        }

        @Override
        protected List<String> run() {
            System.out.println((">>> TestCollapserCommand run() ... batch size: " + (requests.size())));
            // simulate a batch request
            ArrayList<String> response = new ArrayList<String>();
            for (CollapsedRequest<String, String> request : requests) {
                if ((request.getArgument()) == null) {
                    response.add("NULL");
                } else {
                    if (request.getArgument().equals("FAILURE")) {
                        throw new NullPointerException("Simulated Error");
                    }
                    if (request.getArgument().equals("TIMEOUT")) {
                        try {
                            Thread.sleep(800);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    response.add(request.getArgument());
                }
            }
            return response;
        }
    }

    /**
     * A Command implementation that supports caching.
     */
    private static class SuccessfulCacheableCollapsedCommand extends HystrixCollapserTest.TestRequestCollapser {
        private final boolean cacheEnabled;

        public SuccessfulCacheableCollapsedCommand(HystrixCollapserTest.TestCollapserTimer timer, String value, boolean cacheEnabled) {
            super(timer, value);
            this.cacheEnabled = cacheEnabled;
        }

        public SuccessfulCacheableCollapsedCommand(HystrixCollapserTest.TestCollapserTimer timer, String value, boolean cacheEnabled, ConcurrentLinkedQueue<HystrixCommand<List<String>>> executionLog) {
            super(timer, value, executionLog);
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

    private static class ShortCircuitedCommand extends HystrixCommand<List<String>> {
        protected ShortCircuitedCommand() {
            super(HystrixCommand.Setter.withGroupKey(Factory.asKey("shortCircuitedCommand")).andCommandPropertiesDefaults(HystrixCommandPropertiesTest.getUnitTestPropertiesSetter().withCircuitBreakerForceOpen(true)));
        }

        @Override
        protected List<String> run() throws Exception {
            System.out.println("*** execution (this shouldn't happen)");
            // this won't ever get called as we're forcing short-circuiting
            ArrayList<String> values = new ArrayList<String>();
            values.add("hello");
            return values;
        }
    }

    private static class FireAndForgetCommand extends HystrixCommand<Void> {
        protected FireAndForgetCommand(List<Integer> values) {
            super(HystrixCommand.Setter.withGroupKey(Factory.asKey("fireAndForgetCommand")).andCommandPropertiesDefaults(HystrixCommandPropertiesTest.getUnitTestPropertiesSetter()));
        }

        @Override
        protected Void run() throws Exception {
            System.out.println(("*** FireAndForgetCommand execution: " + (Thread.currentThread())));
            return null;
        }
    }

    /* package */
    static class TestCollapserTimer implements CollapserTimer {
        private final ConcurrentLinkedQueue<HystrixCollapserTest.TestCollapserTimer.ATask> tasks = new ConcurrentLinkedQueue<HystrixCollapserTest.TestCollapserTimer.ATask>();

        @Override
        public Reference<TimerListener> addListener(final TimerListener collapseTask) {
            tasks.add(new HystrixCollapserTest.TestCollapserTimer.ATask(new HystrixCollapserTest.TestTimerListener(collapseTask)));
            /**
             * This is a hack that overrides 'clear' of a WeakReference to match the required API
             * but then removes the strong-reference we have inside 'tasks'.
             * <p>
             * We do this so our unit tests know if the WeakReference is cleared correctly, and if so then the ATack is removed from 'tasks'
             */
            return new SoftReference<TimerListener>(collapseTask) {
                @Override
                public void clear() {
                    // super.clear();
                    for (HystrixCollapserTest.TestCollapserTimer.ATask t : tasks) {
                        if (t.task.actualListener.equals(collapseTask)) {
                            tasks.remove(t);
                        }
                    }
                }
            };
        }

        /**
         * Increment time by X. Note that incrementing by multiples of delay or period time will NOT execute multiple times.
         * <p>
         * You must call incrementTime multiple times each increment being larger than 'period' on subsequent calls to cause multiple executions.
         * <p>
         * This is because executing multiple times in a tight-loop would not achieve the correct behavior, such as batching, since it will all execute "now" not after intervals of time.
         *
         * @param timeInMilliseconds
         * 		amount of time to increment
         */
        public synchronized void incrementTime(int timeInMilliseconds) {
            for (HystrixCollapserTest.TestCollapserTimer.ATask t : tasks) {
                t.incrementTime(timeInMilliseconds);
            }
        }

        private static class ATask {
            final HystrixCollapserTest.TestTimerListener task;

            final int delay = 10;

            // our relative time that we'll use
            volatile int time = 0;

            volatile int executionCount = 0;

            private ATask(HystrixCollapserTest.TestTimerListener task) {
                this.task = task;
            }

            public synchronized void incrementTime(int timeInMilliseconds) {
                time += timeInMilliseconds;
                if ((task) != null) {
                    if ((executionCount) == 0) {
                        System.out.println(((("ExecutionCount 0 => Time: " + (time)) + " Delay: ") + (delay)));
                        if ((time) >= (delay)) {
                            // first execution, we're past the delay time
                            executeTask();
                        }
                    } else {
                        System.out.println(((("ExecutionCount 1+ => Time: " + (time)) + " Delay: ") + (delay)));
                        if ((time) >= (delay)) {
                            // subsequent executions, we're past the interval time
                            executeTask();
                        }
                    }
                }
            }

            private synchronized void executeTask() {
                System.out.println("Executing task ...");
                task.tick();
                this.time = 0;// we reset time after each execution

                (this.executionCount)++;
                System.out.println(("executionCount: " + (executionCount)));
            }
        }
    }

    private static class TestTimerListener implements TimerListener {
        private final TimerListener actualListener;

        private final AtomicInteger count = new AtomicInteger();

        public TestTimerListener(TimerListener actual) {
            this.actualListener = actual;
        }

        @Override
        public void tick() {
            count.incrementAndGet();
            actualListener.tick();
        }

        @Override
        public int getIntervalTimeInMilliseconds() {
            return 10;
        }
    }

    private static class TestCollapserWithVoidResponseType extends HystrixCollapser<Void, Void, Integer> {
        private final Integer value;

        public TestCollapserWithVoidResponseType(CollapserTimer timer, int value) {
            super(HystrixCollapserTest.collapserKeyFromString(timer), REQUEST, timer, HystrixCollapserProperties.Setter().withMaxRequestsInBatch(1000).withTimerDelayInMilliseconds(50));
            this.value = value;
        }

        @Override
        public Integer getRequestArgument() {
            return value;
        }

        @Override
        protected HystrixCommand<Void> createCommand(Collection<CollapsedRequest<Void, Integer>> requests) {
            ArrayList<Integer> args = new ArrayList<Integer>();
            for (CollapsedRequest<Void, Integer> request : requests) {
                args.add(request.getArgument());
            }
            return new HystrixCollapserTest.FireAndForgetCommand(args);
        }

        @Override
        protected void mapResponseToRequests(Void batchResponse, Collection<CollapsedRequest<Void, Integer>> requests) {
            for (CollapsedRequest<Void, Integer> r : requests) {
                r.setResponse(null);
            }
        }
    }

    private static class TestCollapserWithVoidResponseTypeAndMissingMapResponseToRequests extends HystrixCollapser<Void, Void, Integer> {
        private final Integer value;

        public TestCollapserWithVoidResponseTypeAndMissingMapResponseToRequests(CollapserTimer timer, int value) {
            super(HystrixCollapserTest.collapserKeyFromString(timer), REQUEST, timer, HystrixCollapserProperties.Setter().withMaxRequestsInBatch(1000).withTimerDelayInMilliseconds(50));
            this.value = value;
        }

        @Override
        public Integer getRequestArgument() {
            return value;
        }

        @Override
        protected HystrixCommand<Void> createCommand(Collection<CollapsedRequest<Void, Integer>> requests) {
            ArrayList<Integer> args = new ArrayList<Integer>();
            for (CollapsedRequest<Void, Integer> request : requests) {
                args.add(request.getArgument());
            }
            return new HystrixCollapserTest.FireAndForgetCommand(args);
        }

        @Override
        protected void mapResponseToRequests(Void batchResponse, Collection<CollapsedRequest<Void, Integer>> requests) {
        }
    }
}

