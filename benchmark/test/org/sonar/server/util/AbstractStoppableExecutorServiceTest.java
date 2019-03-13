/**
 * SonarQube
 * Copyright (C) 2009-2019 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.server.util;


import com.google.common.collect.ImmutableList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;


public class AbstractStoppableExecutorServiceTest {
    private static final Callable<String> SOME_CALLABLE = new Callable<String>() {
        @Override
        public String call() {
            return null;
        }
    };

    private static final Runnable SOME_RUNNABLE = new Runnable() {
        @Override
        public void run() {
        }
    };

    private static final String SOME_STRING = "some string";

    private static final long SOME_LONG = 100L;

    private static final int TIMEOUT = 5;

    private static final TimeUnit TIMEOUT_UNIT = TimeUnit.SECONDS;

    private ExecutorService executorService = Mockito.mock(ExecutorService.class);

    private InOrder inOrder = Mockito.inOrder(executorService);

    private AbstractStoppableExecutorService underTest = new AbstractStoppableExecutorService(executorService) {};

    public static final ImmutableList<Callable<String>> CALLABLES = ImmutableList.of(AbstractStoppableExecutorServiceTest.SOME_CALLABLE);

    @Test
    public void stop_calls_shutdown_and_verify_termination() throws InterruptedException {
        Mockito.when(executorService.awaitTermination(AbstractStoppableExecutorServiceTest.TIMEOUT, AbstractStoppableExecutorServiceTest.TIMEOUT_UNIT)).thenReturn(true);
        underTest.stop();
        inOrder.verify(executorService).shutdown();
        inOrder.verify(executorService).awaitTermination(AbstractStoppableExecutorServiceTest.TIMEOUT, AbstractStoppableExecutorServiceTest.TIMEOUT_UNIT);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void stop_calls_shutdown_then_shutdownNow_if_not_terminated_and_check_termination_again() throws InterruptedException {
        Mockito.when(executorService.awaitTermination(AbstractStoppableExecutorServiceTest.TIMEOUT, AbstractStoppableExecutorServiceTest.TIMEOUT_UNIT)).thenReturn(false).thenReturn(true);
        underTest.stop();
        inOrder.verify(executorService).shutdown();
        inOrder.verify(executorService).awaitTermination(AbstractStoppableExecutorServiceTest.TIMEOUT, AbstractStoppableExecutorServiceTest.TIMEOUT_UNIT);
        inOrder.verify(executorService).shutdownNow();
        inOrder.verify(executorService).awaitTermination(AbstractStoppableExecutorServiceTest.TIMEOUT, AbstractStoppableExecutorServiceTest.TIMEOUT_UNIT);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void stop_calls_shutdownnow_if_interrupted_exception_is_raised() throws InterruptedException {
        Mockito.when(executorService.awaitTermination(AbstractStoppableExecutorServiceTest.TIMEOUT, AbstractStoppableExecutorServiceTest.TIMEOUT_UNIT)).thenThrow(new InterruptedException());
        underTest.stop();
        inOrder.verify(executorService).shutdown();
        inOrder.verify(executorService).awaitTermination(AbstractStoppableExecutorServiceTest.TIMEOUT, AbstractStoppableExecutorServiceTest.TIMEOUT_UNIT);
        inOrder.verify(executorService).shutdownNow();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void shutdown_delegates_to_executorService() {
        underTest.shutdown();
        inOrder.verify(executorService).shutdown();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void shutdownNow_delegates_to_executorService() {
        underTest.shutdownNow();
        inOrder.verify(executorService).shutdownNow();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void isShutdown_delegates_to_executorService() {
        underTest.isShutdown();
        inOrder.verify(executorService).isShutdown();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void isTerminated_delegates_to_executorService() {
        underTest.isTerminated();
        inOrder.verify(executorService).isTerminated();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void awaitTermination_delegates_to_executorService() throws InterruptedException {
        underTest.awaitTermination(AbstractStoppableExecutorServiceTest.SOME_LONG, TimeUnit.SECONDS);
        inOrder.verify(executorService).awaitTermination(AbstractStoppableExecutorServiceTest.SOME_LONG, TimeUnit.SECONDS);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void submit_callable_delegates_to_executorService() {
        underTest.submit(AbstractStoppableExecutorServiceTest.SOME_CALLABLE);
        inOrder.verify(executorService).submit(AbstractStoppableExecutorServiceTest.SOME_CALLABLE);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void submit_runnable_delegates_to_executorService() {
        underTest.submit(AbstractStoppableExecutorServiceTest.SOME_RUNNABLE);
        inOrder.verify(executorService).submit(AbstractStoppableExecutorServiceTest.SOME_RUNNABLE);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void submit_runnable_with_result_delegates_to_executorService() {
        underTest.submit(AbstractStoppableExecutorServiceTest.SOME_RUNNABLE, AbstractStoppableExecutorServiceTest.SOME_STRING);
        inOrder.verify(executorService).submit(AbstractStoppableExecutorServiceTest.SOME_RUNNABLE, AbstractStoppableExecutorServiceTest.SOME_STRING);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void invokeAll_delegates_to_executorService() throws InterruptedException {
        underTest.invokeAll(AbstractStoppableExecutorServiceTest.CALLABLES);
        inOrder.verify(executorService).invokeAll(AbstractStoppableExecutorServiceTest.CALLABLES);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void invokeAll1_delegates_to_executorService() throws InterruptedException {
        underTest.invokeAll(AbstractStoppableExecutorServiceTest.CALLABLES, AbstractStoppableExecutorServiceTest.SOME_LONG, TimeUnit.SECONDS);
        inOrder.verify(executorService).invokeAll(AbstractStoppableExecutorServiceTest.CALLABLES, AbstractStoppableExecutorServiceTest.SOME_LONG, TimeUnit.SECONDS);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void invokeAny_delegates_to_executorService() throws InterruptedException, ExecutionException {
        underTest.invokeAny(AbstractStoppableExecutorServiceTest.CALLABLES);
        inOrder.verify(executorService).invokeAny(AbstractStoppableExecutorServiceTest.CALLABLES);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void invokeAny1_delegates_to_executorService() throws InterruptedException, ExecutionException, TimeoutException {
        underTest.invokeAny(AbstractStoppableExecutorServiceTest.CALLABLES, AbstractStoppableExecutorServiceTest.SOME_LONG, TimeUnit.SECONDS);
        inOrder.verify(executorService).invokeAny(AbstractStoppableExecutorServiceTest.CALLABLES, AbstractStoppableExecutorServiceTest.SOME_LONG, TimeUnit.SECONDS);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void execute_delegates_to_executorService() {
        underTest.execute(AbstractStoppableExecutorServiceTest.SOME_RUNNABLE);
        inOrder.verify(executorService).execute(AbstractStoppableExecutorServiceTest.SOME_RUNNABLE);
        inOrder.verifyNoMoreInteractions();
    }
}

