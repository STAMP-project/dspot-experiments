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
package org.sonar.process;


import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import org.mockito.Mockito;
import org.sonar.process.sharedmemoryfile.ProcessCommands;


public class StopWatcherTest {
    @Rule
    public TestRule safeguardTimeout = new DisableOnDebug(Timeout.seconds(60));

    @Test
    public void stop_if_receive_command() throws Exception {
        ProcessCommands commands = Mockito.mock(ProcessCommands.class);
        Mockito.when(commands.askedForStop()).thenReturn(false, true);
        Stoppable stoppable = Mockito.mock(Stoppable.class);
        StopWatcher underTest = new StopWatcher(commands, stoppable, 1L);
        underTest.start();
        while (underTest.isAlive()) {
            Thread.sleep(1L);
        } 
        Mockito.verify(stoppable).stopAsync();
    }

    @Test
    public void stop_watching_on_interruption() throws Exception {
        ProcessCommands commands = Mockito.mock(ProcessCommands.class);
        Mockito.when(commands.askedForStop()).thenReturn(false);
        Stoppable stoppable = Mockito.mock(Stoppable.class);
        StopWatcher underTest = new StopWatcher(commands, stoppable, 1L);
        underTest.start();
        underTest.interrupt();
        while (underTest.isAlive()) {
            Thread.sleep(1L);
        } 
        Mockito.verify(stoppable, Mockito.never()).stopAsync();
    }
}

