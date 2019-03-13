/**
 * Copyright 2018 the original author or authors.
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


import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import org.awaitility.core.Function;
import org.junit.Test;


/**
 * Fixes issue <a href="https://github.com/awaitility/awaitility/issues/101">101</a>
 */
public class AwaitilityCustomThreadMultipleWaitTest {
    @Test
    public void custom_poll_thread_function_is_called_for_each_await_statement_when_using_static_configuration() {
        CopyOnWriteArrayList<Thread> threads = new CopyOnWriteArrayList<>();
        Awaitility.pollThread(( runnable) -> {
            Thread thread = new Thread(Thread.currentThread().getThreadGroup(), runnable, "mythread");
            threads.add(thread);
            return thread;
        });
        AtomicReference<Thread> firstCalled = new AtomicReference<>();
        AtomicReference<Thread> secondCalled = new AtomicReference<>();
        Awaitility.await("first").until(() -> {
            firstCalled.set(Thread.currentThread());
            return true;
        });
        Awaitility.await("second").until(() -> {
            secondCalled.set(Thread.currentThread());
            return true;
        });
        assertThat(firstCalled).hasValue(threads.get(0));
        assertThat(secondCalled).hasValue(threads.get(1));
        assertThat(firstCalled).doesNotHaveValue(secondCalled.get());
    }

    @Test
    public void custom_poll_thread_function_is_called_for_each_await_statement_when_using_instance_configuration() {
        CopyOnWriteArrayList<Thread> threads = new CopyOnWriteArrayList<>();
        Function<Runnable, Thread> threadFn = ( runnable) -> {
            Thread thread = new Thread(Thread.currentThread().getThreadGroup(), runnable, "mythread");
            threads.add(thread);
            return thread;
        };
        AtomicReference<Thread> firstCalled = new AtomicReference<>();
        AtomicReference<Thread> secondCalled = new AtomicReference<>();
        Awaitility.with().pollThread(threadFn).await("first").until(() -> {
            firstCalled.set(Thread.currentThread());
            return true;
        });
        Awaitility.with().pollThread(threadFn).await("second").until(() -> {
            secondCalled.set(Thread.currentThread());
            return true;
        });
        assertThat(firstCalled).hasValue(threads.get(0));
        assertThat(secondCalled).hasValue(threads.get(1));
        assertThat(firstCalled).doesNotHaveValue(secondCalled.get());
    }

    @Test
    public void custom_executor_service_is_not_shutdown_between_tests_when_using_static_configuration() throws InterruptedException, ExecutionException {
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        AtomicReference<String> createdExecutorServiceThreadGroupName = new AtomicReference<>();
        executorService.submit(() -> createdExecutorServiceThreadGroupName.set(Thread.currentThread().getThreadGroup().getName())).get();
        Awaitility.pollExecutorService(executorService);
        AtomicReference<String> firstCalled = new AtomicReference<>();
        AtomicReference<String> secondCalled = new AtomicReference<>();
        Awaitility.await("first").until(() -> {
            firstCalled.set(Thread.currentThread().getThreadGroup().getName());
            return true;
        });
        Awaitility.await("second").until(() -> {
            secondCalled.set(Thread.currentThread().getThreadGroup().getName());
            return true;
        });
        assertThat(firstCalled).hasValue(createdExecutorServiceThreadGroupName.get()).hasValue(secondCalled.get());
    }

    @Test
    public void custom_executor_service_is_not_shutdown_between_tests_when_using_instance_configuration() throws InterruptedException, ExecutionException {
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        AtomicReference<String> createdExecutorServiceThreadGroupName = new AtomicReference<>();
        executorService.submit(() -> createdExecutorServiceThreadGroupName.set(Thread.currentThread().getThreadGroup().getName())).get();
        AtomicReference<String> firstCalled = new AtomicReference<>();
        AtomicReference<String> secondCalled = new AtomicReference<>();
        Awaitility.with().pollExecutorService(executorService).await("first").until(() -> {
            firstCalled.set(Thread.currentThread().getThreadGroup().getName());
            return true;
        });
        Awaitility.with().pollExecutorService(executorService).await("second").until(() -> {
            secondCalled.set(Thread.currentThread().getThreadGroup().getName());
            return true;
        });
        assertThat(firstCalled).hasValue(createdExecutorServiceThreadGroupName.get()).hasValue(secondCalled.get());
    }
}

