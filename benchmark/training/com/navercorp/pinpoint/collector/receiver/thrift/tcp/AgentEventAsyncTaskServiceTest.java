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


import com.navercorp.pinpoint.collector.service.AgentEventService;
import com.navercorp.pinpoint.collector.service.async.AgentEventAsyncTaskService;
import com.navercorp.pinpoint.common.server.bo.event.AgentEventBo;
import com.navercorp.pinpoint.common.server.util.AgentEventType;
import com.navercorp.pinpoint.rpc.server.PinpointServer;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
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
public class AgentEventAsyncTaskServiceTest {
    @Mock
    private PinpointServer pinpointServer;

    @Mock
    private AgentEventService agentEventService;

    @InjectMocks
    private AgentEventAsyncTaskService agentEventAsyncTaskService = new AgentEventAsyncTaskService();

    private static final String TEST_AGENT_ID = "TEST_AGENT";

    private static final long TEST_START_TIMESTAMP = System.currentTimeMillis();

    private static final long TEST_EVENT_TIMESTAMP = (AgentEventAsyncTaskServiceTest.TEST_START_TIMESTAMP) + 10;

    private static final Map<Object, Object> TEST_CHANNEL_PROPERTIES = AgentEventAsyncTaskServiceTest.createTestChannelProperties();

    @Test
    public void handler_should_handle_events_with_empty_message_body() throws Exception {
        // given
        final AgentEventType expectedEventType = AgentEventType.AGENT_CONNECTED;
        ArgumentCaptor<AgentEventBo> argCaptor = ArgumentCaptor.forClass(AgentEventBo.class);
        // when
        this.agentEventAsyncTaskService.handleEvent(this.pinpointServer.getChannelProperties(), AgentEventAsyncTaskServiceTest.TEST_EVENT_TIMESTAMP, expectedEventType);
        Mockito.verify(this.agentEventService, Mockito.times(1)).insert(argCaptor.capture());
        // then
        AgentEventBo actualAgentEventBo = argCaptor.getValue();
        Assert.assertEquals(AgentEventAsyncTaskServiceTest.TEST_AGENT_ID, actualAgentEventBo.getAgentId());
        Assert.assertEquals(AgentEventAsyncTaskServiceTest.TEST_START_TIMESTAMP, actualAgentEventBo.getStartTimestamp());
        Assert.assertEquals(AgentEventAsyncTaskServiceTest.TEST_EVENT_TIMESTAMP, actualAgentEventBo.getEventTimestamp());
        Assert.assertEquals(expectedEventType, actualAgentEventBo.getEventType());
        Assert.assertEquals(0, actualAgentEventBo.getEventBody().length);
    }
}

