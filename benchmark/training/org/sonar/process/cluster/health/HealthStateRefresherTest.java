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
package org.sonar.process.cluster.health;


import com.hazelcast.core.HazelcastInstanceNotActiveException;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.slf4j.event.Level;
import org.sonar.process.LoggingRule;


public class HealthStateRefresherTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public LoggingRule logging = new LoggingRule(HealthStateRefresher.class);

    private Random random = new Random();

    private NodeDetailsTestSupport testSupport = new NodeDetailsTestSupport(random);

    private HealthStateRefresherExecutorService executorService = Mockito.mock(HealthStateRefresherExecutorService.class);

    private NodeHealthProvider nodeHealthProvider = Mockito.mock(NodeHealthProvider.class);

    private SharedHealthState sharedHealthState = Mockito.mock(SharedHealthState.class);

    private HealthStateRefresher underTest = new HealthStateRefresher(executorService, nodeHealthProvider, sharedHealthState);

    @Test
    public void start_adds_runnable_with_10_second_delay_and_initial_delay_putting_NodeHealth_from_provider_into_SharedHealthState() {
        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        NodeHealth[] nodeHealths = new NodeHealth[]{ testSupport.randomNodeHealth(), testSupport.randomNodeHealth(), testSupport.randomNodeHealth() };
        Error expected = new Error("Simulating exception raised by NodeHealthProvider");
        Mockito.when(nodeHealthProvider.get()).thenReturn(nodeHealths[0]).thenReturn(nodeHealths[1]).thenReturn(nodeHealths[2]).thenThrow(expected);
        underTest.start();
        Mockito.verify(executorService).scheduleWithFixedDelay(runnableCaptor.capture(), ArgumentMatchers.eq(1L), ArgumentMatchers.eq(10L), ArgumentMatchers.eq(TimeUnit.SECONDS));
        Runnable runnable = runnableCaptor.getValue();
        runnable.run();
        runnable.run();
        runnable.run();
        Mockito.verify(sharedHealthState).writeMine(nodeHealths[0]);
        Mockito.verify(sharedHealthState).writeMine(nodeHealths[1]);
        Mockito.verify(sharedHealthState).writeMine(nodeHealths[2]);
        try {
            runnable.run();
        } catch (IllegalStateException e) {
            fail("Runnable should catch any Throwable");
        }
    }

    @Test
    public void stop_has_no_effect() {
        underTest.stop();
        Mockito.verify(sharedHealthState).clearMine();
        Mockito.verifyZeroInteractions(executorService, nodeHealthProvider);
    }

    @Test
    public void do_not_log_errors_when_hazelcast_is_not_active() {
        logging.setLevel(Level.DEBUG);
        Mockito.doThrow(new HazelcastInstanceNotActiveException()).when(sharedHealthState).writeMine(ArgumentMatchers.any());
        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        underTest.start();
        Mockito.verify(executorService).scheduleWithFixedDelay(runnableCaptor.capture(), ArgumentMatchers.eq(1L), ArgumentMatchers.eq(10L), ArgumentMatchers.eq(TimeUnit.SECONDS));
        Runnable runnable = runnableCaptor.getValue();
        runnable.run();
        assertThat(logging.getLogs(Level.ERROR)).isEmpty();
        assertThat(logging.hasLog(Level.DEBUG, "Hazelcast is no more active")).isTrue();
    }
}

