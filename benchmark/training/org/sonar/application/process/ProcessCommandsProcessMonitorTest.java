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


import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.process.ProcessId;
import org.sonar.process.sharedmemoryfile.ProcessCommands;


public class ProcessCommandsProcessMonitorTest {
    @Test
    public void ProcessMonitorImpl_is_a_proxy_of_Process() throws Exception {
        Process process = Mockito.mock(Process.class, Mockito.RETURNS_DEEP_STUBS);
        ProcessCommands commands = Mockito.mock(ProcessCommands.class, Mockito.RETURNS_DEEP_STUBS);
        ProcessCommandsProcessMonitor underTest = new ProcessCommandsProcessMonitor(process, ProcessId.WEB_SERVER, commands);
        underTest.waitFor();
        Mockito.verify(process).waitFor();
        underTest.closeStreams();
        Mockito.verify(process.getErrorStream()).close();
        Mockito.verify(process.getInputStream()).close();
        Mockito.verify(process.getOutputStream()).close();
        underTest.destroyForcibly();
        Mockito.verify(process).destroyForcibly();
        assertThat(underTest.getInputStream()).isNotNull();
        underTest.isAlive();
        Mockito.verify(process).isAlive();
        underTest.waitFor(123, TimeUnit.MILLISECONDS);
        Mockito.verify(process).waitFor(123, TimeUnit.MILLISECONDS);
    }

    @Test
    public void ProcessMonitorImpl_is_a_proxy_of_Commands() {
        Process process = Mockito.mock(Process.class, Mockito.RETURNS_DEEP_STUBS);
        ProcessCommands commands = Mockito.mock(ProcessCommands.class, Mockito.RETURNS_DEEP_STUBS);
        ProcessCommandsProcessMonitor underTest = new ProcessCommandsProcessMonitor(process, null, commands);
        underTest.askForStop();
        Mockito.verify(commands).askForStop();
        underTest.acknowledgeAskForRestart();
        Mockito.verify(commands).acknowledgeAskForRestart();
        underTest.askedForRestart();
        Mockito.verify(commands).askedForRestart();
        underTest.isOperational();
        Mockito.verify(commands).isOperational();
    }

    @Test
    public void closeStreams_ignores_null_stream() {
        ProcessCommands commands = Mockito.mock(ProcessCommands.class);
        Process process = Mockito.mock(Process.class);
        Mockito.when(process.getInputStream()).thenReturn(null);
        ProcessCommandsProcessMonitor underTest = new ProcessCommandsProcessMonitor(process, null, commands);
        // no failures
        underTest.closeStreams();
    }

    @Test
    public void closeStreams_ignores_failure_if_stream_fails_to_be_closed() throws Exception {
        InputStream stream = Mockito.mock(InputStream.class);
        Mockito.doThrow(new IOException("error")).when(stream).close();
        Process process = Mockito.mock(Process.class);
        Mockito.when(process.getInputStream()).thenReturn(stream);
        ProcessCommandsProcessMonitor underTest = new ProcessCommandsProcessMonitor(process, null, Mockito.mock(ProcessCommands.class, Mockito.RETURNS_MOCKS));
        // no failures
        underTest.closeStreams();
    }
}

