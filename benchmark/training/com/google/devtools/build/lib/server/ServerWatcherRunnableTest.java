/**
 * Copyright 2018 The Bazel Authors. All rights reserved.
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
package com.google.devtools.build.lib.server;


import com.google.devtools.build.lib.testutil.ManualClock;
import com.google.devtools.build.lib.testutil.TestUtils;
import io.grpc.Server;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


/**
 * Tests for {@link ServerWatcherRunnable}.
 */
@RunWith(JUnit4.class)
public class ServerWatcherRunnableTest {
    private ManualClock clock;

    private Server mockServer;

    @Test
    public void testBasicIdleCheck() throws Exception {
        CommandManager mockCommands = Mockito.mock(CommandManager.class);
        ServerWatcherRunnable underTest = /* maxIdleSeconds= */
        /* shutdownOnLowSysMem= */
        new ServerWatcherRunnable(mockServer, 10, false, mockCommands);
        Thread thread = new Thread(underTest);
        Mockito.when(mockCommands.isEmpty()).thenReturn(true);
        AtomicInteger checkIdleCounter = new AtomicInteger();
        Mockito.doAnswer(( invocation) -> {
            checkIdleCounter.incrementAndGet();
            Mockito.verify(mockServer, Mockito.never()).shutdown();
            clock.advanceMillis(Duration.ofSeconds(5).toMillis());
            return null;
        }).when(mockCommands).waitForChange(ArgumentMatchers.anyLong());
        thread.start();
        thread.join(TestUtils.WAIT_TIMEOUT_MILLISECONDS);
        Mockito.verify(mockServer).shutdown();
        assertThat(checkIdleCounter.get()).isEqualTo(2);
    }

    @Test
    public void runLowAbsoluteHighPercentageMemoryCheck() throws Exception {
        if (!(usingLinux())) {
            return;
        }
        assertThat(/* freeRamKb= */
        /* totalRamKb= */
        doesIdleLowMemoryCheckShutdown(5000, 10000)).isFalse();
    }

    @Test
    public void runHighAbsoluteLowPercentageMemoryCheck() throws Exception {
        if (!(usingLinux())) {
            return;
        }
        assertThat(/* freeRamKb= */
        /* totalRamKb= */
        doesIdleLowMemoryCheckShutdown((1L << 21), (1L << 30))).isFalse();
    }

    @Test
    public void runLowAsboluteLowPercentageMemoryCheck() throws Exception {
        if (!(usingLinux())) {
            return;
        }
        assertThat(/* freeRamKb= */
        /* totalRamKb= */
        doesIdleLowMemoryCheckShutdown(5000, 1000000)).isTrue();
    }

    @Test
    public void testshutdownOnLowSysMemDisabled() throws Exception {
        if (!(usingLinux())) {
            return;
        }
        assertThat(/* freeRamKb= */
        /* totalRamKb= */
        /* shutdownOnLowSysMem= */
        doesIdleLowMemoryCheckShutdown(5000, 1000000, false)).isFalse();
    }
}

