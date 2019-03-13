/**
 * Copyright 2017 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.thoughtworks.go.util;


import com.thoughtworks.go.util.command.EnvironmentVariableContext;
import com.thoughtworks.go.util.command.ProcessOutputStreamConsumer;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Predicate;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class ProcessManagerTest {
    private ProcessManager processManager;

    private Process processOne;

    private Process processTwo;

    private ProcessWrapper wrapperForProcessOne;

    private ProcessWrapper wrapperForProcessTwo;

    private Process processStartedByManager;

    @Test
    public void shouldAddToProcessListWhenNewProcessCreated() {
        processManager.createProcess(new String[]{ "echo", "message" }, "echo 'message'", null, new HashMap(), new EnvironmentVariableContext(), ProcessOutputStreamConsumer.inMemoryConsumer(), "test-tag", "utf-8", "ERROR: ");
        Assert.assertThat(processManager.getProcessMap().size(), Matchers.is(3));
    }

    @Test
    public void shouldRemoveKilledProcessFromList() {
        processManager.processKilled(processTwo);
        Assert.assertThat(processManager.getProcessMap().size(), Matchers.is(1));
        Assert.assertThat(processManager.getProcessMap().containsKey(processOne), Matchers.is(true));
    }

    @Test
    public void shouldGetIdleTimeForGivenProcess() {
        processManager = new ProcessManager();
        ProcessWrapper processWrapperOne = Mockito.mock(ProcessWrapper.class);
        Process processOne = Mockito.mock(Process.class);
        ProcessWrapper processWrapperTwo = Mockito.mock(ProcessWrapper.class);
        Process processTwo = Mockito.mock(Process.class);
        ConcurrentMap<Process, ProcessWrapper> processMap = processManager.getProcessMap();
        processMap.put(processOne, processWrapperOne);
        processMap.put(processTwo, processWrapperTwo);
        Mockito.when(processWrapperOne.getProcessTag()).thenReturn("tag1");
        Mockito.when(processWrapperOne.getIdleTime()).thenReturn(200L);
        Mockito.when(processWrapperTwo.getProcessTag()).thenReturn("tag2");
        Mockito.when(processWrapperTwo.getIdleTime()).thenReturn(100L);
        long timeout = processManager.getIdleTimeFor("tag2");
        Assert.assertThat(timeout, Matchers.is(100L));
    }

    @Test
    public void processListForDisplayShouldBeSameAsTheCurrentProcessList() throws Exception {
        processManager = new ProcessManager();
        ProcessWrapper processWrapperOne = Mockito.mock(ProcessWrapper.class);
        Process processOne = Mockito.mock(Process.class);
        ProcessWrapper processWrapperTwo = Mockito.mock(ProcessWrapper.class);
        Process processTwo = Mockito.mock(Process.class);
        ConcurrentMap<Process, ProcessWrapper> processMap = processManager.getProcessMap();
        processMap.put(processOne, processWrapperOne);
        processMap.put(processTwo, processWrapperTwo);
        Collection<ProcessWrapper> processWrappersForDisplay = processManager.currentProcessListForDisplay();
        Assert.assertThat(processWrappersForDisplay, Matchers.is(processMap.values()));
    }

    @Test
    public void canGetProcessLevelEnvironmentVariableNames() {
        processManager.environmentVariableNames().stream().filter(new Predicate<String>() {
            @Override
            public boolean test(String item) {
                return item.equalsIgnoreCase("path");
            }
        }).findFirst().orElse(null);
    }
}

