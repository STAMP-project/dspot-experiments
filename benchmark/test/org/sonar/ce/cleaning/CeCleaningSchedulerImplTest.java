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
package org.sonar.ce.cleaning;


import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sonar.ce.CeDistributedInformation;
import org.sonar.ce.configuration.CeConfiguration;
import org.sonar.ce.queue.InternalCeQueue;


public class CeCleaningSchedulerImplTest {
    private Lock jobLock = Mockito.mock(Lock.class);

    @Test
    public void startScheduling_does_not_fail_if_cleaning_methods_send_even_an_Exception() {
        InternalCeQueue mockedInternalCeQueue = Mockito.mock(InternalCeQueue.class);
        CeDistributedInformation mockedCeDistributedInformation = mockCeDistributedInformation(jobLock);
        CeCleaningSchedulerImpl underTest = mockCeCleaningSchedulerImpl(mockedInternalCeQueue, mockedCeDistributedInformation);
        Exception exception = new IllegalArgumentException("faking unchecked exception thrown by cancelWornOuts");
        Mockito.doThrow(exception).when(mockedInternalCeQueue).resetTasksWithUnknownWorkerUUIDs(ArgumentMatchers.any());
        underTest.startScheduling();
        Mockito.verify(mockedInternalCeQueue).resetTasksWithUnknownWorkerUUIDs(ArgumentMatchers.any());
    }

    @Test
    public void startScheduling_fails_if_resetTasksWithUnknownWorkerUUIDs_send_an_Error() {
        InternalCeQueue mockedInternalCeQueue = Mockito.mock(InternalCeQueue.class);
        CeDistributedInformation mockedCeDistributedInformation = mockCeDistributedInformation(jobLock);
        CeCleaningSchedulerImpl underTest = mockCeCleaningSchedulerImpl(mockedInternalCeQueue, mockedCeDistributedInformation);
        Error expected = new Error("faking Error thrown by cancelWornOuts");
        Mockito.doThrow(expected).when(mockedInternalCeQueue).resetTasksWithUnknownWorkerUUIDs(ArgumentMatchers.any());
        try {
            underTest.startScheduling();
            fail("the error should have been thrown");
        } catch (Error e) {
            assertThat(e).isSameAs(expected);
        }
        Mockito.verify(mockedInternalCeQueue).resetTasksWithUnknownWorkerUUIDs(ArgumentMatchers.any());
    }

    @Test
    public void startScheduling_must_call_the_lock_methods() {
        InternalCeQueue mockedInternalCeQueue = Mockito.mock(InternalCeQueue.class);
        CeDistributedInformation mockedCeDistributedInformation = mockCeDistributedInformation(jobLock);
        CeCleaningSchedulerImpl underTest = mockCeCleaningSchedulerImpl(mockedInternalCeQueue, mockedCeDistributedInformation);
        underTest.startScheduling();
        Mockito.verify(mockedCeDistributedInformation, Mockito.times(1)).acquireCleanJobLock();
        Mockito.verify(jobLock, Mockito.times(1)).tryLock();
        Mockito.verify(jobLock, Mockito.times(1)).unlock();
    }

    @Test
    public void startScheduling_must_not_execute_method_if_lock_is_already_acquired() {
        InternalCeQueue mockedInternalCeQueue = Mockito.mock(InternalCeQueue.class);
        CeDistributedInformation mockedCeDistributedInformation = mockCeDistributedInformation(jobLock);
        Mockito.when(jobLock.tryLock()).thenReturn(false);
        CeCleaningSchedulerImpl underTest = mockCeCleaningSchedulerImpl(mockedInternalCeQueue, mockedCeDistributedInformation);
        underTest.startScheduling();
        Mockito.verify(mockedCeDistributedInformation, Mockito.times(1)).acquireCleanJobLock();
        Mockito.verify(jobLock, Mockito.times(1)).tryLock();
        // since lock cannot be locked, unlock method is not been called
        Mockito.verify(jobLock, Mockito.times(0)).unlock();
        // since lock cannot be locked, cleaning job methods must not be called
        Mockito.verify(mockedInternalCeQueue, Mockito.times(0)).resetTasksWithUnknownWorkerUUIDs(ArgumentMatchers.any());
    }

    @Test
    public void startScheduling_calls_cleaning_methods_of_internalCeQueue_at_fixed_rate_with_value_from_CeConfiguration() {
        InternalCeQueue mockedInternalCeQueue = Mockito.mock(InternalCeQueue.class);
        long wornOutInitialDelay = 10L;
        long wornOutDelay = 20L;
        long unknownWorkerInitialDelay = 11L;
        long unknownWorkerDelay = 21L;
        CeConfiguration mockedCeConfiguration = mockCeConfiguration(wornOutInitialDelay, wornOutDelay);
        CeCleaningSchedulerImplTest.CeCleaningAdapter executorService = new CeCleaningSchedulerImplTest.CeCleaningAdapter() {
            @Override
            public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initDelay, long period, TimeUnit unit) {
                (schedulerCounter)++;
                switch (schedulerCounter) {
                    case 1 :
                        assertThat(initDelay).isEqualTo(wornOutInitialDelay);
                        assertThat(period).isEqualTo(wornOutDelay);
                        assertThat(unit).isEqualTo(TimeUnit.MINUTES);
                        break;
                    case 2 :
                        assertThat(initDelay).isEqualTo(unknownWorkerInitialDelay);
                        assertThat(period).isEqualTo(unknownWorkerDelay);
                        assertThat(unit).isEqualTo(TimeUnit.MINUTES);
                        break;
                    default :
                        fail("Unknwon call of scheduleWithFixedDelay");
                }
                // synchronously execute command
                command.run();
                return null;
            }
        };
        CeCleaningSchedulerImpl underTest = new CeCleaningSchedulerImpl(executorService, mockedCeConfiguration, mockedInternalCeQueue, mockCeDistributedInformation(jobLock));
        underTest.startScheduling();
        assertThat(executorService.schedulerCounter).isEqualTo(1);
    }

    /**
     * Implementation of {@link CeCleaningExecutorService} which throws {@link UnsupportedOperationException} for every
     * method.
     */
    private static class CeCleaningAdapter implements CeCleaningExecutorService {
        protected int schedulerCounter = 0;

        @Override
        public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
            throw createUnsupportedOperationException();
        }

        @Override
        public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
            throw createUnsupportedOperationException();
        }

        @Override
        public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
            throw createUnsupportedOperationException();
        }

        @Override
        public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
            throw createUnsupportedOperationException();
        }

        @Override
        public void shutdown() {
            throw createUnsupportedOperationException();
        }

        @Override
        public List<Runnable> shutdownNow() {
            throw createUnsupportedOperationException();
        }

        @Override
        public boolean isShutdown() {
            throw createUnsupportedOperationException();
        }

        @Override
        public boolean isTerminated() {
            throw createUnsupportedOperationException();
        }

        @Override
        public boolean awaitTermination(long timeout, TimeUnit unit) {
            throw createUnsupportedOperationException();
        }

        @Override
        public <T> Future<T> submit(Callable<T> task) {
            throw createUnsupportedOperationException();
        }

        @Override
        public <T> Future<T> submit(Runnable task, T result) {
            throw createUnsupportedOperationException();
        }

        @Override
        public Future<?> submit(Runnable task) {
            throw createUnsupportedOperationException();
        }

        @Override
        public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) {
            throw createUnsupportedOperationException();
        }

        @Override
        public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) {
            throw createUnsupportedOperationException();
        }

        @Override
        public <T> T invokeAny(Collection<? extends Callable<T>> tasks) {
            throw createUnsupportedOperationException();
        }

        @Override
        public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) {
            throw createUnsupportedOperationException();
        }

        @Override
        public void execute(Runnable command) {
            throw createUnsupportedOperationException();
        }

        private UnsupportedOperationException createUnsupportedOperationException() {
            return new UnsupportedOperationException("Unexpected call");
        }
    }
}

