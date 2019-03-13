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


import Action.consoleOut;
import Action.ping;
import Action.reportCompleted;
import Action.reportCurrentStatus;
import AgentRuntimeStatus.Idle;
import JobState.Building;
import SystemEnvironment.CONSOLE_LOGS_THROUGH_WEBSOCKET_ENABLED;
import SystemEnvironment.WEBSOCKET_ENABLED;
import com.thoughtworks.go.agent.service.AgentUpgradeService;
import com.thoughtworks.go.agent.service.SslInfrastructureService;
import com.thoughtworks.go.buildsession.BuildSessionBasedTestCase;
import com.thoughtworks.go.config.AgentRegistry;
import com.thoughtworks.go.matchers.RegexMatcher;
import com.thoughtworks.go.plugin.access.artifact.ArtifactExtension;
import com.thoughtworks.go.plugin.access.packagematerial.PackageRepositoryExtension;
import com.thoughtworks.go.plugin.access.pluggabletask.TaskExtension;
import com.thoughtworks.go.plugin.access.scm.SCMExtension;
import com.thoughtworks.go.plugin.infra.PluginManager;
import com.thoughtworks.go.publishers.GoArtifactsManipulator;
import com.thoughtworks.go.remote.BuildRepositoryRemote;
import com.thoughtworks.go.remote.work.Work;
import com.thoughtworks.go.server.service.AgentBuildingInfo;
import com.thoughtworks.go.server.service.AgentRuntimeInfo;
import com.thoughtworks.go.util.HttpService;
import com.thoughtworks.go.util.SubprocessLogger;
import com.thoughtworks.go.util.SystemEnvironment;
import com.thoughtworks.go.work.SleepWork;
import java.io.IOException;
import java.security.GeneralSecurityException;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static Action.assignWork;
import static Action.build;
import static Action.cancelBuild;
import static Action.reregister;
import static Action.setCookie;
import static JobResult.Cancelled;
import static JobResult.Passed;
import static JobState.Building;


@RunWith(MockitoJUnitRunner.class)
public class AgentWebSocketClientControllerTest {
    private static final int MAX_WAIT_IN_TEST = 10000;

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
    private HttpService httpService;

    private AgentWebSocketClientController agentController;

    @Mock
    private WebSocketClientHandler webSocketClientHandler;

    @Mock
    private WebSocketSessionHandler webSocketSessionHandler;

    private String agentUuid = "uuid";

    @Test
    public void shouldSendAgentRuntimeInfoWhenWorkIsCalled() throws Exception {
        Mockito.when(sslInfrastructureService.isRegistered()).thenReturn(true);
        Mockito.when(webSocketSessionHandler.isNotRunning()).thenReturn(false);
        ArgumentCaptor<Message> argumentCaptor = ArgumentCaptor.forClass(Message.class);
        agentController = createAgentController();
        agentController.init();
        agentController.work();
        Mockito.verify(webSocketSessionHandler).sendAndWaitForAcknowledgement(argumentCaptor.capture());
        Message message = argumentCaptor.getValue();
        Assert.assertThat(message.getAcknowledgementId(), Matchers.notNullValue());
        Assert.assertThat(message.getAction(), Matchers.is(ping));
        Assert.assertThat(message.getData(), Matchers.is(MessageEncoding.encodeData(agentController.getAgentRuntimeInfo())));
    }

    @Test
    public void shouldHandleSecurityErrorWhenOpeningWebSocketFails() throws Exception {
        Mockito.when(sslInfrastructureService.isRegistered()).thenReturn(true);
        agentController = createAgentController();
        Mockito.when(webSocketSessionHandler.isNotRunning()).thenReturn(true);
        Mockito.doThrow(new GeneralSecurityException()).when(webSocketClientHandler).connect(agentController);
        agentController.init();
        agentController.loop();
        Mockito.verify(agentUpgradeService).checkForUpgradeAndExtraProperties();
        Mockito.verify(sslInfrastructureService).registerIfNecessary(agentController.getAgentAutoRegistrationProperties());
        Mockito.verify(sslInfrastructureService).invalidateAgentCertificate();
    }

    @Test
    public void processSetCookieAction() throws IOException, InterruptedException {
        agentController = createAgentController();
        agentController.init();
        agentController.process(new Message(setCookie, MessageEncoding.encodeData("cookie")));
        Assert.assertThat(agentController.getAgentRuntimeInfo().getCookie(), Matchers.is("cookie"));
    }

    @Test
    public void processAssignWorkAction() throws IOException, InterruptedException {
        ArgumentCaptor<Message> argumentCaptor = ArgumentCaptor.forClass(Message.class);
        agentController = createAgentController();
        agentController.init();
        agentController.process(new Message(assignWork, MessageEncoding.encodeWork(new SleepWork("work1", 0))));
        Assert.assertThat(agentController.getAgentRuntimeInfo().getRuntimeStatus(), Matchers.is(Idle));
        Mockito.verify(webSocketSessionHandler, Mockito.times(1)).sendAndWaitForAcknowledgement(argumentCaptor.capture());
        Mockito.verify(artifactsManipulator).setProperty(null, new Property("work1_result", "done"));
        Message message = argumentCaptor.getAllValues().get(0);
        Assert.assertThat(message.getAcknowledgementId(), Matchers.notNullValue());
        Assert.assertThat(message.getAction(), Matchers.is(ping));
        Assert.assertThat(message.getData(), Matchers.is(MessageEncoding.encodeData(agentController.getAgentRuntimeInfo())));
    }

    @Test
    public void processAssignWorkActionWithConsoleLogsThroughWebSockets() throws IOException, InterruptedException {
        SystemEnvironment env = new SystemEnvironment();
        env.set(WEBSOCKET_ENABLED, true);
        env.set(CONSOLE_LOGS_THROUGH_WEBSOCKET_ENABLED, true);
        ArgumentCaptor<Message> argumentCaptor = ArgumentCaptor.forClass(Message.class);
        agentController = createAgentController();
        agentController.init();
        agentController.process(new Message(assignWork, MessageEncoding.encodeWork(new SleepWork("work1", 0))));
        Assert.assertThat(agentController.getAgentRuntimeInfo().getRuntimeStatus(), Matchers.is(Idle));
        Mockito.verify(webSocketSessionHandler, Mockito.times(2)).sendAndWaitForAcknowledgement(argumentCaptor.capture());
        Mockito.verify(artifactsManipulator).setProperty(null, new Property("work1_result", "done"));
        Message message = argumentCaptor.getAllValues().get(1);
        Assert.assertThat(message.getAcknowledgementId(), Matchers.notNullValue());
        Assert.assertThat(message.getAction(), Matchers.is(ping));
        Assert.assertThat(message.getData(), Matchers.is(MessageEncoding.encodeData(agentController.getAgentRuntimeInfo())));
        Message message2 = argumentCaptor.getAllValues().get(0);
        Assert.assertThat(message2.getAcknowledgementId(), Matchers.notNullValue());
        Assert.assertThat(message2.getAction(), Matchers.is(consoleOut));
        ConsoleTransmission ct = MessageEncoding.decodeData(message2.getData(), ConsoleTransmission.class);
        Assert.assertThat(ct.getLine(), RegexMatcher.matches("Sleeping for 0 milliseconds"));
        env.set(WEBSOCKET_ENABLED, false);
        env.set(CONSOLE_LOGS_THROUGH_WEBSOCKET_ENABLED, false);
    }

    @Test
    public void processBuildCommandWithConsoleLogsThroughWebSockets() throws Exception {
        ArgumentCaptor<Message> currentStatusMessageCaptor = ArgumentCaptor.forClass(Message.class);
        Mockito.when(systemEnvironment.isConsoleLogsThroughWebsocketEnabled()).thenReturn(true);
        Mockito.when(agentRegistry.uuid()).thenReturn(agentUuid);
        agentController = createAgentController();
        agentController.init();
        BuildSettings build = new BuildSettings();
        build.setBuildId("b001");
        build.setConsoleUrl("http://foo.bar/console");
        build.setArtifactUploadBaseUrl("http://foo.bar/artifacts");
        build.setPropertyBaseUrl("http://foo.bar/properties");
        build.setBuildLocator("build1");
        build.setBuildLocatorForDisplay("build1ForDisplay");
        build.setBuildCommand(BuildCommand.compose(BuildCommand.echo("building"), BuildCommand.reportCurrentStatus(Building)));
        agentController.process(new Message(build, MessageEncoding.encodeData(build)));
        Assert.assertThat(agentController.getAgentRuntimeInfo().getRuntimeStatus(), Matchers.is(Idle));
        AgentRuntimeInfo agentRuntimeInfo = cloneAgentRuntimeInfo(agentController.getAgentRuntimeInfo());
        agentRuntimeInfo.busy(new AgentBuildingInfo("build1ForDisplay", "build1"));
        Mockito.verify(webSocketSessionHandler, Mockito.times(3)).sendAndWaitForAcknowledgement(currentStatusMessageCaptor.capture());
        Message consoleOutMsg = currentStatusMessageCaptor.getAllValues().get(0);
        Assert.assertThat(consoleOutMsg.getAcknowledgementId(), Matchers.notNullValue());
        Assert.assertThat(consoleOutMsg.getAction(), Matchers.is(consoleOut));
        ConsoleTransmission ct = MessageEncoding.decodeData(consoleOutMsg.getData(), ConsoleTransmission.class);
        Assert.assertThat(ct.getLine(), RegexMatcher.matches("building"));
        Assert.assertEquals(ct.getBuildId(), "b001");
        Message message = currentStatusMessageCaptor.getAllValues().get(1);
        Assert.assertThat(message.getAcknowledgementId(), Matchers.notNullValue());
        Assert.assertThat(message.getAction(), Matchers.is(reportCurrentStatus));
        Assert.assertThat(message.getData(), Matchers.is(MessageEncoding.encodeData(new Report(agentRuntimeInfo, "b001", Building, null))));
        Message jobCompletedMessage = currentStatusMessageCaptor.getAllValues().get(2);
        Assert.assertThat(jobCompletedMessage.getAcknowledgementId(), Matchers.notNullValue());
        Assert.assertThat(jobCompletedMessage.getAction(), Matchers.is(reportCompleted));
        Assert.assertThat(jobCompletedMessage.getData(), Matchers.is(MessageEncoding.encodeData(new Report(agentRuntimeInfo, "b001", null, Passed))));
    }

    @Test
    public void processBuildCommand() throws Exception {
        ArgumentCaptor<Message> currentStatusMessageCaptor = ArgumentCaptor.forClass(Message.class);
        Mockito.when(agentRegistry.uuid()).thenReturn(agentUuid);
        CloseableHttpResponse httpResponse = Mockito.mock(CloseableHttpResponse.class);
        Mockito.when(httpResponse.getStatusLine()).thenReturn(new BasicStatusLine(HttpVersion.HTTP_1_1, 200, "OK"));
        Mockito.when(httpService.execute(ArgumentMatchers.any())).thenReturn(httpResponse);
        agentController = createAgentController();
        agentController.init();
        BuildSettings build = new BuildSettings();
        build.setBuildId("b001");
        build.setConsoleUrl("http://foo.bar/console");
        build.setArtifactUploadBaseUrl("http://foo.bar/artifacts");
        build.setPropertyBaseUrl("http://foo.bar/properties");
        build.setBuildLocator("build1");
        build.setBuildLocatorForDisplay("build1ForDisplay");
        build.setBuildCommand(BuildCommand.compose(BuildCommand.echo("building"), BuildCommand.reportCurrentStatus(Building)));
        agentController.process(new Message(build, MessageEncoding.encodeData(build)));
        Assert.assertThat(agentController.getAgentRuntimeInfo().getRuntimeStatus(), Matchers.is(Idle));
        AgentRuntimeInfo agentRuntimeInfo = cloneAgentRuntimeInfo(agentController.getAgentRuntimeInfo());
        agentRuntimeInfo.busy(new AgentBuildingInfo("build1ForDisplay", "build1"));
        Mockito.verify(webSocketSessionHandler, Mockito.times(2)).sendAndWaitForAcknowledgement(currentStatusMessageCaptor.capture());
        Message message = currentStatusMessageCaptor.getAllValues().get(0);
        Assert.assertThat(message.getAcknowledgementId(), Matchers.notNullValue());
        Assert.assertThat(message.getAction(), Matchers.is(reportCurrentStatus));
        Assert.assertThat(message.getData(), Matchers.is(MessageEncoding.encodeData(new Report(agentRuntimeInfo, "b001", Building, null))));
        Message jobCompletedMessage = currentStatusMessageCaptor.getAllValues().get(1);
        Assert.assertThat(jobCompletedMessage.getAcknowledgementId(), Matchers.notNullValue());
        Assert.assertThat(jobCompletedMessage.getAction(), Matchers.is(reportCompleted));
        Assert.assertThat(jobCompletedMessage.getData(), Matchers.is(MessageEncoding.encodeData(new Report(agentRuntimeInfo, "b001", null, Passed))));
    }

    @Test
    public void processCancelBuildCommandBuild() throws IOException, InterruptedException {
        ArgumentCaptor<Message> argumentCaptor = ArgumentCaptor.forClass(Message.class);
        agentController = createAgentController();
        agentController.init();
        agentController.getAgentRuntimeInfo().setSupportsBuildCommandProtocol(true);
        final BuildSettings build = new BuildSettings();
        build.setBuildId("b001");
        build.setConsoleUrl("http://foo.bar/console");
        build.setArtifactUploadBaseUrl("http://foo.bar/artifacts");
        build.setPropertyBaseUrl("http://foo.bar/properties");
        build.setBuildLocator("build1");
        build.setBuildLocatorForDisplay("build1ForDisplay");
        build.setConsoleLogCharset("utf-8");
        build.setBuildCommand(BuildCommand.compose(BuildSessionBasedTestCase.execSleepScript(((AgentWebSocketClientControllerTest.MAX_WAIT_IN_TEST) / 1000)), BuildCommand.reportCurrentStatus(Building)));
        Thread buildingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    agentController.process(new Message(Action.build, MessageEncoding.encodeData(build)));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        buildingThread.start();
        waitForAgentRuntimeState(agentController.getAgentRuntimeInfo(), AgentRuntimeStatus.Building);
        agentController.process(new Message(cancelBuild));
        buildingThread.join(AgentWebSocketClientControllerTest.MAX_WAIT_IN_TEST);
        AgentRuntimeInfo agentRuntimeInfo = cloneAgentRuntimeInfo(agentController.getAgentRuntimeInfo());
        agentRuntimeInfo.busy(new AgentBuildingInfo("build1ForDisplay", "build1"));
        agentRuntimeInfo.cancel();
        Mockito.verify(webSocketSessionHandler).sendAndWaitForAcknowledgement(argumentCaptor.capture());
        Assert.assertThat(agentController.getAgentRuntimeInfo().getRuntimeStatus(), Matchers.is(Idle));
        Message message = argumentCaptor.getValue();
        Assert.assertThat(message.getAcknowledgementId(), Matchers.notNullValue());
        Assert.assertThat(message.getAction(), Matchers.is(reportCompleted));
        Assert.assertThat(message.getData(), Matchers.is(MessageEncoding.encodeData(new Report(agentRuntimeInfo, "b001", null, Cancelled))));
    }

    @Test
    public void processCancelJobAction() throws IOException, InterruptedException {
        agentController = createAgentController();
        agentController.init();
        final SleepWork sleep1secWork = new SleepWork("work1", AgentWebSocketClientControllerTest.MAX_WAIT_IN_TEST);
        Thread buildingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    agentController.process(new Message(Action.assignWork, MessageEncoding.encodeWork(sleep1secWork)));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        buildingThread.start();
        waitForAgentRuntimeState(agentController.getAgentRuntimeInfo(), AgentRuntimeStatus.Building);
        agentController.process(new Message(cancelBuild));
        buildingThread.join(AgentWebSocketClientControllerTest.MAX_WAIT_IN_TEST);
        Assert.assertThat(agentController.getAgentRuntimeInfo().getRuntimeStatus(), Matchers.is(Idle));
        Mockito.verify(artifactsManipulator).setProperty(null, new Property("work1_result", "done_canceled"));
    }

    @Test
    public void processReregisterAction() throws IOException, InterruptedException {
        Mockito.when(agentRegistry.uuid()).thenReturn(agentUuid);
        agentController = createAgentController();
        agentController.init();
        agentController.process(new Message(reregister));
        Mockito.verify(sslInfrastructureService).invalidateAgentCertificate();
        Mockito.verify(webSocketSessionHandler).stop();
    }

    @Test
    public void shouldCancelPreviousRunningJobIfANewAssignWorkMessageIsReceived() throws IOException, InterruptedException {
        agentController = createAgentController();
        agentController.init();
        final SleepWork work1 = new SleepWork("work1", AgentWebSocketClientControllerTest.MAX_WAIT_IN_TEST);
        Thread work1Thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    agentController.process(new Message(Action.assignWork, MessageEncoding.encodeWork(work1)));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        work1Thread.start();
        waitForAgentRuntimeState(agentController.getAgentRuntimeInfo(), AgentRuntimeStatus.Building);
        SleepWork work2 = new SleepWork("work2", 1);
        agentController.process(new Message(assignWork, MessageEncoding.encodeWork(work2)));
        work1Thread.join(AgentWebSocketClientControllerTest.MAX_WAIT_IN_TEST);
        Mockito.verify(artifactsManipulator).setProperty(null, new Property("work1_result", "done_canceled"));
        Mockito.verify(artifactsManipulator).setProperty(null, new Property("work2_result", "done"));
    }
}

