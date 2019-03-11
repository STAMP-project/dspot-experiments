/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.yarn.server.nodemanager.containermanager.launcher;


import ContainerExecutor.Signal.TERM;
import java.io.IOException;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.SignalContainerCommand;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.server.nodemanager.ContainerExecutor;
import org.apache.hadoop.yarn.server.nodemanager.executor.ContainerSignalContext;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Tests for {@link ContainerCleanup}.
 */
public class TestContainerCleanup {
    private YarnConfiguration conf;

    private ContainerId containerId;

    private ContainerExecutor executor;

    private ContainerLaunch launch;

    private ContainerCleanup cleanup;

    @Test
    public void testNoCleanupWhenContainerNotLaunched() throws IOException {
        cleanup.run();
        Mockito.verify(launch, Mockito.times(0)).signalContainer(Mockito.any(SignalContainerCommand.class));
    }

    @Test
    public void testCleanup() throws Exception {
        launch.containerAlreadyLaunched.set(true);
        cleanup.run();
        ArgumentCaptor<ContainerSignalContext> captor = ArgumentCaptor.forClass(ContainerSignalContext.class);
        Mockito.verify(executor, Mockito.times(1)).signalContainer(captor.capture());
        Assert.assertEquals("signal", TERM, captor.getValue().getSignal());
    }
}

