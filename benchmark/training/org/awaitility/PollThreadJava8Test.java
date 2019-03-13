/**
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.awaitility;


import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.awaitility.classes.ExceptionThrowingAsynch;
import org.awaitility.classes.FakeRepository;
import org.awaitility.core.ConditionTimeoutException;
import org.awaitility.core.InternalExecutorServiceFactory;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runners.model.TestTimedOutException;


@SuppressWarnings("Duplicates")
public class PollThreadJava8Test {
    private FakeRepository fakeRepository;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test(timeout = 2000)
    public void canRunConditionEvaluationsInTheSameThreadAsTheTestThread() {
        perform();
        AtomicReference<Thread> threadAtomicReference = new AtomicReference<>();
        Awaitility.with().pollInSameThread().and().conditionEvaluationListener(( __) -> threadAtomicReference.set(Thread.currentThread())).await().atMost(1000, TimeUnit.MILLISECONDS).until(() -> (fakeRepository.getValue()) == 1);
        assertThat(threadAtomicReference.get()).isEqualTo(Thread.currentThread());
    }

    @Test(timeout = 2000)
    public void canRunConditionEvaluationsInTheSameThreadAsTheTestThreadWhenConfiguredStatically() {
        perform();
        AtomicReference<Thread> threadAtomicReference = new AtomicReference<>();
        Awaitility.pollInSameThread();
        Awaitility.with().conditionEvaluationListener(( __) -> threadAtomicReference.set(Thread.currentThread())).await().atMost(1000, TimeUnit.MILLISECONDS).until(() -> (fakeRepository.getValue()) == 1);
        assertThat(threadAtomicReference.get()).isEqualTo(Thread.currentThread());
    }

    @Test(timeout = 700)
    public void uncaughtExceptionsArePropagatedToAwaitingThreadButCannotBreakForeverBlockWhenConditionIsEvaluatedFromTheTestThread() throws Exception {
        exception.expect(TestTimedOutException.class);
        new ExceptionThrowingAsynch(new IllegalStateException("Illegal state!")).perform();
        Awaitility.given().catchUncaughtExceptions().and().pollInSameThread().await().forever().until(() -> (fakeRepository.getValue()) == 1);
    }

    @Test(timeout = 2000)
    public void canTimeoutWhenPollingInSameThreadAsTest() throws Exception {
        exception.expect(ConditionTimeoutException.class);
        perform();
        Awaitility.given().pollInSameThread().await().atMost(300, TimeUnit.MILLISECONDS).until(() -> (fakeRepository.getValue()) == 1);
    }

    @Test(timeout = 2000)
    public void canRunConditionEvaluationsCustomTestWithoutAliasThreadUsingJava8MethodReference() {
        perform();
        AtomicReference<Thread> threadAtomicReference = new AtomicReference<>();
        Awaitility.with().pollThread(Thread::new).and().conditionEvaluationListener(( __) -> threadAtomicReference.set(Thread.currentThread())).await().atMost(1000, TimeUnit.MILLISECONDS).until(() -> (fakeRepository.getValue()) == 1);
        assertThat(threadAtomicReference.get()).isNotEqualTo(Thread.currentThread());
    }

    @Test(timeout = 2000)
    public void pollThreadSupplierIsCalledOncePerTest() {
        perform();
        List<Thread> conditionThreads = new CopyOnWriteArrayList<>();
        Awaitility.with().pollThread(Thread::new).and().conditionEvaluationListener(( __) -> conditionThreads.add(Thread.currentThread())).await().atMost(1000, TimeUnit.MILLISECONDS).until(() -> (fakeRepository.getValue()) == 1);
        assertThat(new HashSet(conditionThreads)).doesNotContain(Thread.currentThread()).hasSize(1);
    }

    @Test(timeout = 2000)
    public void canRunConditionEvaluationsInCustomThread() {
        perform();
        AtomicReference<Thread> expectedThread = new AtomicReference<>();
        AtomicReference<Thread> actualThread = new AtomicReference<>();
        Awaitility.with().pollThread(( r) -> {
            Thread thread = new Thread(r);
            expectedThread.set(thread);
            return thread;
        }).and().conditionEvaluationListener(( __) -> actualThread.set(Thread.currentThread())).await().atMost(1000, TimeUnit.MILLISECONDS).until(() -> (fakeRepository.getValue()) == 1);
        assertThat(actualThread.get()).isNotEqualTo(Thread.currentThread()).isEqualTo(expectedThread.get());
    }

    @Test(timeout = 2000)
    public void canRunConditionEvaluationsInCustomThreadWhenConfiguredStatically() {
        perform();
        AtomicReference<Thread> expectedThread = new AtomicReference<>();
        AtomicReference<Thread> actualThread = new AtomicReference<>();
        Awaitility.pollThread(( r) -> {
            Thread thread = new Thread(r);
            expectedThread.set(thread);
            return thread;
        });
        Awaitility.with().conditionEvaluationListener(( __) -> actualThread.set(Thread.currentThread())).await().atMost(1000, TimeUnit.MILLISECONDS).until(() -> (fakeRepository.getValue()) == 1);
        assertThat(actualThread.get()).isNotEqualTo(Thread.currentThread()).isEqualTo(expectedThread.get());
    }

    @Test(timeout = 2000)
    public void canRunConditionInSpecificExecutorService() throws InterruptedException, ExecutionException {
        ExecutorService executorService = InternalExecutorServiceFactory.create(Thread::new);
        FakeRepository threadLocalRepo = executorService.submit(() -> new FakeRepository() {
            ThreadLocal<Integer> threadLocal = new ThreadLocal<>();

            @Override
            public int getValue() {
                Integer myValue = threadLocal.get();
                return myValue == null ? 0 : myValue;
            }

            @Override
            public void setValue(int value) {
                threadLocal.set(value);
            }
        }).get();
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(600);
                executorService.submit(() -> threadLocalRepo.setValue(1));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        thread.start();
        Awaitility.given().pollExecutorService(executorService).await().atMost(1000, TimeUnit.MILLISECONDS).until(() -> (threadLocalRepo.getValue()) == 1);
    }

    @Test(timeout = 2000)
    public void canRunConditionInSpecificExecutorServiceWhenExecutorServiceIsConfiguredStatically() throws InterruptedException, ExecutionException {
        ExecutorService executorService = InternalExecutorServiceFactory.create(Thread::new);
        Awaitility.pollExecutorService(executorService);
        FakeRepository threadLocalRepo = executorService.submit(() -> new FakeRepository() {
            ThreadLocal<Integer> threadLocal = new ThreadLocal<>();

            @Override
            public int getValue() {
                Integer myValue = threadLocal.get();
                return myValue == null ? 0 : myValue;
            }

            @Override
            public void setValue(int value) {
                threadLocal.set(value);
            }
        }).get();
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(600);
                executorService.submit(() -> threadLocalRepo.setValue(1));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        thread.start();
        Awaitility.await().atMost(1000, TimeUnit.MILLISECONDS).until(() -> (threadLocalRepo.getValue()) == 1);
    }

    @Test(timeout = 2000)
    public void awaitilityPollThreadIsGivenANameEqualToAwaitilityThreadWhenNotUsingAnAlias() {
        perform();
        AtomicReference<Thread> thread = new AtomicReference<>();
        Awaitility.with().conditionEvaluationListener(( __) -> thread.set(Thread.currentThread())).await().atMost(1000, TimeUnit.MILLISECONDS).until(() -> (fakeRepository.getValue()) == 1);
        assertThat(thread.get().getName()).isEqualTo("awaitility-thread");
    }

    @Test(timeout = 2000)
    public void awaitilityPollThreadIsGivenANameIncludingAliasWhenUsingAnAlias() {
        perform();
        AtomicReference<Thread> thread = new AtomicReference<>();
        Awaitility.with().conditionEvaluationListener(( __) -> thread.set(Thread.currentThread())).await("my alias").atMost(1000, TimeUnit.MILLISECONDS).until(() -> (fakeRepository.getValue()) == 1);
        assertThat(thread.get().getName()).isEqualTo("awaitility[my alias]");
    }
}

