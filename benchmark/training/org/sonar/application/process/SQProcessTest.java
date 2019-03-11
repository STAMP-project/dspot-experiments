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
package org.sonar.application.process;


import Lifecycle.State.INIT;
import Lifecycle.State.STARTED;
import Lifecycle.State.STOPPED;
import Lifecycle.State.STOPPING;
import ProcessEventListener.Type.ASK_FOR_RESTART;
import ProcessEventListener.Type.OPERATIONAL;
import java.io.InputStream;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.ExpectedException;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import org.mockito.Mockito;
import org.sonar.process.ProcessId;


public class SQProcessTest {
    private static final ProcessId A_PROCESS_ID = ProcessId.ELASTICSEARCH;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public TestRule safeguardTimeout = new DisableOnDebug(Timeout.seconds(60));

    @Test
    public void initial_state_is_INIT() {
        SQProcess underTest = SQProcess.builder(SQProcessTest.A_PROCESS_ID).build();
        assertThat(underTest.getProcessId()).isEqualTo(SQProcessTest.A_PROCESS_ID);
        assertThat(underTest.getState()).isEqualTo(INIT);
    }

    @Test
    public void start_and_stop_process() {
        ProcessLifecycleListener listener = Mockito.mock(ProcessLifecycleListener.class);
        SQProcess underTest = SQProcess.builder(SQProcessTest.A_PROCESS_ID).addProcessLifecycleListener(listener).build();
        try (SQProcessTest.TestProcess testProcess = new SQProcessTest.TestProcess()) {
            assertThat(underTest.start(() -> testProcess)).isTrue();
            assertThat(underTest.getState()).isEqualTo(STARTED);
            assertThat(testProcess.isAlive()).isTrue();
            assertThat(testProcess.streamsClosed).isFalse();
            Mockito.verify(listener).onProcessState(SQProcessTest.A_PROCESS_ID, STARTED);
            testProcess.close();
            // do not wait next run of watcher threads
            underTest.refreshState();
            assertThat(underTest.getState()).isEqualTo(STOPPED);
            assertThat(testProcess.isAlive()).isFalse();
            assertThat(testProcess.streamsClosed).isTrue();
            Mockito.verify(listener).onProcessState(SQProcessTest.A_PROCESS_ID, STOPPED);
        }
    }

    @Test
    public void start_does_not_nothing_if_already_started_once() {
        SQProcess underTest = SQProcess.builder(SQProcessTest.A_PROCESS_ID).build();
        try (SQProcessTest.TestProcess testProcess = new SQProcessTest.TestProcess()) {
            assertThat(underTest.start(() -> testProcess)).isTrue();
            assertThat(underTest.getState()).isEqualTo(STARTED);
            assertThat(underTest.start(() -> {
                throw new IllegalStateException();
            })).isFalse();
            assertThat(underTest.getState()).isEqualTo(STARTED);
        }
    }

    @Test
    public void start_throws_exception_and_move_to_state_STOPPED_if_execution_of_command_fails() {
        SQProcess underTest = SQProcess.builder(SQProcessTest.A_PROCESS_ID).build();
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("error");
        underTest.start(() -> {
            throw new IllegalStateException("error");
        });
        assertThat(underTest.getState()).isEqualTo(STOPPED);
    }

    @Test
    public void send_event_when_process_is_operational() {
        ProcessEventListener listener = Mockito.mock(ProcessEventListener.class);
        SQProcess underTest = SQProcess.builder(SQProcessTest.A_PROCESS_ID).addEventListener(listener).build();
        try (SQProcessTest.TestProcess testProcess = new SQProcessTest.TestProcess()) {
            underTest.start(() -> testProcess);
            testProcess.operational = true;
            underTest.refreshState();
            Mockito.verify(listener).onProcessEvent(SQProcessTest.A_PROCESS_ID, OPERATIONAL);
        }
        Mockito.verifyNoMoreInteractions(listener);
    }

    @Test
    public void operational_event_is_sent_once() {
        ProcessEventListener listener = Mockito.mock(ProcessEventListener.class);
        SQProcess underTest = SQProcess.builder(SQProcessTest.A_PROCESS_ID).addEventListener(listener).build();
        try (SQProcessTest.TestProcess testProcess = new SQProcessTest.TestProcess()) {
            underTest.start(() -> testProcess);
            testProcess.operational = true;
            underTest.refreshState();
            Mockito.verify(listener).onProcessEvent(SQProcessTest.A_PROCESS_ID, OPERATIONAL);
            // second run
            underTest.refreshState();
            Mockito.verifyNoMoreInteractions(listener);
        }
    }

    @Test
    public void send_event_when_process_requests_for_restart() {
        ProcessEventListener listener = Mockito.mock(ProcessEventListener.class);
        SQProcess underTest = SQProcess.builder(SQProcessTest.A_PROCESS_ID).addEventListener(listener).setWatcherDelayMs(1L).build();
        try (SQProcessTest.TestProcess testProcess = new SQProcessTest.TestProcess()) {
            underTest.start(() -> testProcess);
            testProcess.askedForRestart = true;
            Mockito.verify(listener, Mockito.timeout(10000)).onProcessEvent(SQProcessTest.A_PROCESS_ID, ASK_FOR_RESTART);
            // flag is reset so that next run does not trigger again the event
            underTest.refreshState();
            Mockito.verifyNoMoreInteractions(listener);
            assertThat(testProcess.askedForRestart).isFalse();
        }
    }

    @Test
    public void stopForcibly_stops_the_process_without_graceful_request_for_stop() {
        SQProcess underTest = SQProcess.builder(SQProcessTest.A_PROCESS_ID).build();
        try (SQProcessTest.TestProcess testProcess = new SQProcessTest.TestProcess()) {
            underTest.start(() -> testProcess);
            underTest.stopForcibly();
            assertThat(underTest.getState()).isEqualTo(STOPPED);
            assertThat(testProcess.askedForStop).isFalse();
            assertThat(testProcess.destroyedForcibly).isTrue();
            // second execution of stopForcibly does nothing. It's still stopped.
            underTest.stopForcibly();
            assertThat(underTest.getState()).isEqualTo(STOPPED);
        }
    }

    @Test
    public void process_stops_after_graceful_request_for_stop() throws Exception {
        ProcessLifecycleListener listener = Mockito.mock(ProcessLifecycleListener.class);
        SQProcess underTest = SQProcess.builder(SQProcessTest.A_PROCESS_ID).addProcessLifecycleListener(listener).build();
        try (SQProcessTest.TestProcess testProcess = new SQProcessTest.TestProcess()) {
            underTest.start(() -> testProcess);
            Thread stopperThread = new Thread(() -> underTest.stop(1, TimeUnit.HOURS));
            stopperThread.start();
            // thread is blocked until process stopped
            assertThat(stopperThread.isAlive()).isTrue();
            // wait for the stopper thread to ask graceful stop
            while (!(testProcess.askedForStop)) {
                Thread.sleep(1L);
            } 
            assertThat(underTest.getState()).isEqualTo(STOPPING);
            Mockito.verify(listener).onProcessState(SQProcessTest.A_PROCESS_ID, STOPPING);
            // process stopped
            testProcess.close();
            // waiting for stopper thread to detect and handle the stop
            stopperThread.join();
            assertThat(underTest.getState()).isEqualTo(STOPPED);
            Mockito.verify(listener).onProcessState(SQProcessTest.A_PROCESS_ID, STOPPED);
        }
    }

    @Test
    public void process_is_stopped_forcibly_if_graceful_stop_is_too_long() throws Exception {
        ProcessLifecycleListener listener = Mockito.mock(ProcessLifecycleListener.class);
        SQProcess underTest = SQProcess.builder(SQProcessTest.A_PROCESS_ID).addProcessLifecycleListener(listener).build();
        try (SQProcessTest.TestProcess testProcess = new SQProcessTest.TestProcess()) {
            underTest.start(() -> testProcess);
            underTest.stop(1L, TimeUnit.MILLISECONDS);
            testProcess.waitFor();
            assertThat(testProcess.askedForStop).isTrue();
            assertThat(testProcess.destroyedForcibly).isTrue();
            assertThat(testProcess.isAlive()).isFalse();
            assertThat(underTest.getState()).isEqualTo(STOPPED);
            Mockito.verify(listener).onProcessState(SQProcessTest.A_PROCESS_ID, STOPPED);
        }
    }

    @Test
    public void process_requests_are_listened_on_regular_basis() {
        ProcessEventListener listener = Mockito.mock(ProcessEventListener.class);
        SQProcess underTest = SQProcess.builder(SQProcessTest.A_PROCESS_ID).addEventListener(listener).setWatcherDelayMs(1L).build();
        try (SQProcessTest.TestProcess testProcess = new SQProcessTest.TestProcess()) {
            underTest.start(() -> testProcess);
            testProcess.operational = true;
            Mockito.verify(listener, Mockito.timeout(1000L)).onProcessEvent(SQProcessTest.A_PROCESS_ID, OPERATIONAL);
        }
    }

    @Test
    public void test_toString() {
        SQProcess underTest = SQProcess.builder(SQProcessTest.A_PROCESS_ID).build();
        assertThat(underTest.toString()).isEqualTo((("Process[" + (SQProcessTest.A_PROCESS_ID.getKey())) + "]"));
    }

    private static class TestProcess implements AutoCloseable , ProcessMonitor {
        private final CountDownLatch alive = new CountDownLatch(1);

        private final InputStream inputStream = Mockito.mock(InputStream.class, Mockito.RETURNS_MOCKS);

        private final InputStream errorStream = Mockito.mock(InputStream.class, Mockito.RETURNS_MOCKS);

        private boolean streamsClosed = false;

        private boolean operational = false;

        private boolean askedForRestart = false;

        private boolean askedForStop = false;

        private boolean destroyedForcibly = false;

        @Override
        public InputStream getInputStream() {
            return inputStream;
        }

        @Override
        public InputStream getErrorStream() {
            return errorStream;
        }

        @Override
        public void closeStreams() {
            streamsClosed = true;
        }

        @Override
        public boolean isAlive() {
            return (alive.getCount()) == 1;
        }

        @Override
        public void askForStop() {
            askedForStop = true;
            // do not stop, just asking
        }

        @Override
        public void destroyForcibly() {
            destroyedForcibly = true;
            alive.countDown();
        }

        @Override
        public void waitFor() throws InterruptedException {
            alive.await();
        }

        @Override
        public void waitFor(long timeout, TimeUnit timeoutUnit) throws InterruptedException {
            alive.await(timeout, timeoutUnit);
        }

        @Override
        public boolean isOperational() {
            return operational;
        }

        @Override
        public boolean askedForRestart() {
            return askedForRestart;
        }

        @Override
        public void acknowledgeAskForRestart() {
            this.askedForRestart = false;
        }

        @Override
        public void close() {
            alive.countDown();
        }
    }
}

