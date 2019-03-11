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
package com.thoughtworks.go.agent;


import com.thoughtworks.go.agent.service.AgentUpgradeService;
import com.thoughtworks.go.agent.service.SslInfrastructureService;
import com.thoughtworks.go.config.AgentRegistry;
import com.thoughtworks.go.plugin.access.artifact.ArtifactExtension;
import com.thoughtworks.go.plugin.access.packagematerial.PackageRepositoryExtension;
import com.thoughtworks.go.plugin.access.pluggabletask.TaskExtension;
import com.thoughtworks.go.plugin.access.scm.SCMExtension;
import com.thoughtworks.go.plugin.infra.PluginManager;
import com.thoughtworks.go.plugin.infra.PluginManagerReference;
import com.thoughtworks.go.plugin.infra.monitor.PluginJarLocationMonitor;
import com.thoughtworks.go.publishers.GoArtifactsManipulator;
import com.thoughtworks.go.remote.AgentIdentifier;
import com.thoughtworks.go.remote.BuildRepositoryRemote;
import com.thoughtworks.go.remote.work.AgentWorkContext;
import com.thoughtworks.go.remote.work.Work;
import com.thoughtworks.go.server.service.AgentRuntimeInfo;
import com.thoughtworks.go.util.SubprocessLogger;
import com.thoughtworks.go.util.SystemEnvironment;
import com.thoughtworks.go.util.command.EnvironmentVariableContext;
import java.io.IOException;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class AgentHTTPClientControllerTest {
    @Rule
    public final TemporaryFolder folder = new TemporaryFolder();

    @Mock
    private BuildRepositoryRemote loopServer;

    @Mock
    private GoArtifactsManipulator artifactsManipulator;

    @Mock
    private SslInfrastructureService sslInfrastructureService;

    @Mock
    private Work work;

    @Mock
    private AgentRegistry agentRegistry;

    @Mock
    private SubprocessLogger subprocessLogger;

    @Mock
    private SystemEnvironment systemEnvironment;

    @Mock
    private AgentUpgradeService agentUpgradeService;

    @Mock
    private PluginManager pluginManager;

    @Mock
    private PackageRepositoryExtension packageRepositoryExtension;

    @Mock
    private SCMExtension scmExtension;

    @Mock
    private TaskExtension taskExtension;

    @Mock
    private ArtifactExtension artifactExtension;

    @Mock
    private PluginJarLocationMonitor pluginJarLocationMonitor;

    private String agentUuid = "uuid";

    private AgentIdentifier agentIdentifier;

    private AgentHTTPClientController agentController;

    @Test
    public void shouldSetPluginManagerReference() throws Exception {
        agentController = createAgentController();
        Assert.assertThat(PluginManagerReference.reference().getPluginManager(), Matchers.is(pluginManager));
    }

    @Test
    public void shouldRetrieveWorkFromServerAndDoIt() throws Exception {
        Mockito.when(loopServer.getWork(ArgumentMatchers.any(AgentRuntimeInfo.class))).thenReturn(work);
        Mockito.when(agentRegistry.uuid()).thenReturn(agentUuid);
        Mockito.when(pluginJarLocationMonitor.hasRunAtLeastOnce()).thenReturn(true);
        agentController = createAgentController();
        agentController.init();
        agentController.ping();
        agentController.work();
        Mockito.verify(work).doWork(ArgumentMatchers.any(EnvironmentVariableContext.class), ArgumentMatchers.any(AgentWorkContext.class));
        Mockito.verify(sslInfrastructureService).createSslInfrastructure();
    }

    @Test
    public void shouldNotRetrieveWorkIfPluginMonitorHasNotRun() throws IOException {
        Mockito.when(agentRegistry.uuid()).thenReturn(agentUuid);
        Mockito.when(pluginJarLocationMonitor.hasRunAtLeastOnce()).thenReturn(false);
        agentController = createAgentController();
        agentController.init();
        agentController.ping();
        agentController.work();
        Mockito.verifyZeroInteractions(work);
    }

    @Test
    public void shouldRetrieveCookieIfNotPresent() throws Exception {
        agentController = createAgentController();
        agentController.init();
        Mockito.when(loopServer.getCookie(ArgumentMatchers.any(AgentIdentifier.class), ArgumentMatchers.eq(agentController.getAgentRuntimeInfo().getLocation()))).thenReturn("cookie");
        Mockito.when(sslInfrastructureService.isRegistered()).thenReturn(true);
        Mockito.when(loopServer.getWork(agentController.getAgentRuntimeInfo())).thenReturn(work);
        Mockito.when(agentRegistry.uuid()).thenReturn(agentUuid);
        Mockito.when(pluginJarLocationMonitor.hasRunAtLeastOnce()).thenReturn(true);
        agentController.loop();
        Mockito.verify(work).doWork(ArgumentMatchers.any(EnvironmentVariableContext.class), ArgumentMatchers.any(AgentWorkContext.class));
    }

    @Test
    public void shouldNotTellServerWorkIsCompletedWhenThereIsNoWork() throws Exception {
        Mockito.when(loopServer.getWork(ArgumentMatchers.any(AgentRuntimeInfo.class))).thenReturn(work);
        Mockito.when(agentRegistry.uuid()).thenReturn(agentUuid);
        agentController = createAgentController();
        agentController.init();
        agentController.retrieveWork();
        Mockito.verify(work).doWork(ArgumentMatchers.any(EnvironmentVariableContext.class), ArgumentMatchers.any(AgentWorkContext.class));
        Mockito.verify(sslInfrastructureService).createSslInfrastructure();
    }

    @Test
    public void shouldRegisterSubprocessLoggerAtExit() throws Exception {
        SslInfrastructureService sslInfrastructureService = Mockito.mock(SslInfrastructureService.class);
        AgentRegistry agentRegistry = Mockito.mock(AgentRegistry.class);
        agentController = new AgentHTTPClientController(loopServer, artifactsManipulator, sslInfrastructureService, agentRegistry, agentUpgradeService, subprocessLogger, systemEnvironment, pluginManager, packageRepositoryExtension, scmExtension, taskExtension, artifactExtension, null, null, pluginJarLocationMonitor);
        agentController.init();
        Mockito.verify(subprocessLogger).registerAsExitHook("Following processes were alive at shutdown: ");
    }

    @Test
    public void shouldNotPingIfNotRegisteredYet() throws Exception {
        Mockito.when(agentRegistry.uuid()).thenReturn(agentUuid);
        Mockito.when(sslInfrastructureService.isRegistered()).thenReturn(false);
        agentController = createAgentController();
        agentController.init();
        agentController.ping();
        Mockito.verify(sslInfrastructureService).createSslInfrastructure();
    }

    @Test
    public void shouldPingIfAfterRegistered() throws Exception {
        Mockito.when(agentRegistry.uuid()).thenReturn(agentUuid);
        Mockito.when(sslInfrastructureService.isRegistered()).thenReturn(true);
        agentController = createAgentController();
        agentController.init();
        agentController.ping();
        Mockito.verify(sslInfrastructureService).createSslInfrastructure();
        Mockito.verify(loopServer).ping(ArgumentMatchers.any(AgentRuntimeInfo.class));
    }
}

