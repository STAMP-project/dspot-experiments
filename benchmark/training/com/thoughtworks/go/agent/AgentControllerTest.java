/**
 * Copyright 2019 ThoughtWorks, Inc.
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
import com.thoughtworks.go.agent.statusapi.AgentHealthHolder;
import com.thoughtworks.go.config.AgentRegistry;
import com.thoughtworks.go.plugin.access.packagematerial.PackageRepositoryExtension;
import com.thoughtworks.go.plugin.access.pluggabletask.TaskExtension;
import com.thoughtworks.go.plugin.access.scm.SCMExtension;
import com.thoughtworks.go.plugin.infra.PluginManager;
import com.thoughtworks.go.publishers.GoArtifactsManipulator;
import com.thoughtworks.go.remote.AgentIdentifier;
import com.thoughtworks.go.remote.BuildRepositoryRemote;
import com.thoughtworks.go.remote.work.Work;
import com.thoughtworks.go.util.HttpService;
import com.thoughtworks.go.util.SubprocessLogger;
import com.thoughtworks.go.util.SystemEnvironment;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Date;
import org.apache.http.client.HttpClient;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class AgentControllerTest {
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
    private HttpService httpService;

    @Mock
    private HttpClient httpClient;

    private AgentController agentController;

    private String agentUuid = "uuid";

    @Mock
    private AgentRegistry agentRegistry;

    private TestingClock clock = new TestingClock();

    private final int pingInterval = 5000;

    private AgentHealthHolder agentHealthHolder = new AgentHealthHolder(clock, pingInterval);

    private AgentIdentifier agentIdentifier;

    @Test
    public void shouldReturnTrueIfCausedBySecurity() throws Exception {
        Exception exception = new Exception(new RuntimeException(new GeneralSecurityException()));
        agentController = createAgentController();
        Assert.assertTrue(agentController.isCausedBySecurity(exception));
    }

    @Test
    public void shouldReturnFalseIfNotCausedBySecurity() throws Exception {
        Exception exception = new Exception(new IOException());
        agentController = createAgentController();
        Assert.assertFalse(agentController.isCausedBySecurity(exception));
    }

    @Test
    public void shouldUpgradeAgentBeforeAgentRegistration() throws Exception {
        agentController = createAgentController();
        InOrder inOrder = Mockito.inOrder(agentUpgradeService, sslInfrastructureService);
        agentController.loop();
        inOrder.verify(agentUpgradeService).checkForUpgradeAndExtraProperties();
        inOrder.verify(sslInfrastructureService).registerIfNecessary(agentController.getAgentAutoRegistrationProperties());
    }

    @Test
    public void remembersLastPingTime() throws Exception {
        // initial time
        Date now = new Date(42);
        clock.setTime(now);
        agentController = createAgentController();
        agentController.pingSuccess();
        Assert.assertFalse(agentHealthHolder.hasLostContact());
        clock.addMillis(pingInterval);
        Assert.assertFalse(agentHealthHolder.hasLostContact());
        clock.addMillis(pingInterval);
        Assert.assertTrue(agentHealthHolder.hasLostContact());
    }
}

