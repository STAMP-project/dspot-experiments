/**
 * Copyright 2018 NAVER Corp.
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
package com.navercorp.pinpoint.collector.receiver.thrift.tcp;


import ManagedAgentLifeCycle.CLOSED_BY_CLIENT;
import ManagedAgentLifeCycle.CLOSED_BY_SERVER;
import ManagedAgentLifeCycle.RUNNING;
import ManagedAgentLifeCycle.UNEXPECTED_CLOSE_BY_CLIENT;
import ManagedAgentLifeCycle.UNEXPECTED_CLOSE_BY_SERVER;
import com.navercorp.pinpoint.collector.receiver.AgentLifeCycleChangeEventHandler;
import com.navercorp.pinpoint.collector.service.async.AgentEventAsyncTaskService;
import com.navercorp.pinpoint.collector.service.async.AgentLifeCycleAsyncTaskService;
import com.navercorp.pinpoint.collector.util.ManagedAgentLifeCycle;
import com.navercorp.pinpoint.common.server.util.AgentEventType;
import com.navercorp.pinpoint.common.server.util.AgentLifeCycleState;
import com.navercorp.pinpoint.rpc.common.SocketStateCode;
import com.navercorp.pinpoint.rpc.server.PinpointServer;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 *
 *
 * @author HyunGil Jeong
 */
@RunWith(MockitoJUnitRunner.class)
public class AgentLifeCycleChangeEventHandlerTest {
    @Mock
    private AgentLifeCycleAsyncTaskService agentLifeCycleAsyncTaskService;

    @Mock
    private AgentEventAsyncTaskService agentEventAsyncTaskService;

    @Mock
    private PinpointServer server;

    @InjectMocks
    private AgentLifeCycleChangeEventHandler lifeCycleChangeEventHandler = new AgentLifeCycleChangeEventHandler();

    @Test
    public void runningStatesShouldBeHandledCorrectly() throws Exception {
        // given
        final Set<SocketStateCode> runningStates = RUNNING.getManagedStateCodes();
        runAndVerifyByStateCodes(runningStates);
    }

    @Test
    public void closedByClientStatesShouldBeHandledCorrectly() throws Exception {
        // given
        final Set<SocketStateCode> closedByClientStates = CLOSED_BY_CLIENT.getManagedStateCodes();
        runAndVerifyByStateCodes(closedByClientStates);
    }

    @Test
    public void unexpectedCloseByClientStatesShouldBeHandledCorrectly() throws Exception {
        // given
        final Set<SocketStateCode> unexpectedCloseByClientStates = UNEXPECTED_CLOSE_BY_CLIENT.getManagedStateCodes();
        runAndVerifyByStateCodes(unexpectedCloseByClientStates);
    }

    @Test
    public void closedByServerStatesShouldBeHandledCorrectly() throws Exception {
        // given
        final Set<SocketStateCode> closedByServerStates = CLOSED_BY_SERVER.getManagedStateCodes();
        runAndVerifyByStateCodes(closedByServerStates);
    }

    @Test
    public void unexpectedCloseByServerStatesShouldBeHandledCorrectly() throws Exception {
        // given
        final Set<SocketStateCode> unexpectedCloseByServerStates = UNEXPECTED_CLOSE_BY_SERVER.getManagedStateCodes();
        runAndVerifyByStateCodes(unexpectedCloseByServerStates);
    }

    @Test
    public void unmanagedStatesShouldNotBeHandled() throws Exception {
        // given
        final Set<SocketStateCode> unmanagedStates = new HashSet<>();
        for (SocketStateCode socketStateCode : SocketStateCode.values()) {
            if ((ManagedAgentLifeCycle.getManagedAgentLifeCycleByStateCode(socketStateCode)) == (AgentLifeCycleChangeEventHandler.STATE_NOT_MANAGED)) {
                unmanagedStates.add(socketStateCode);
            }
        }
        for (SocketStateCode unmanagedState : unmanagedStates) {
            // when
            this.lifeCycleChangeEventHandler.eventPerformed(this.server, unmanagedState);
            // then
            Mockito.verify(this.agentLifeCycleAsyncTaskService, Mockito.never()).handleLifeCycleEvent(ArgumentMatchers.any(Map.class), ArgumentMatchers.anyLong(), ArgumentMatchers.any(AgentLifeCycleState.class), ArgumentMatchers.anyInt());
            Mockito.verify(this.agentEventAsyncTaskService, Mockito.never()).handleEvent(ArgumentMatchers.any(Map.class), ArgumentMatchers.anyLong(), ArgumentMatchers.any(AgentEventType.class));
        }
    }
}

