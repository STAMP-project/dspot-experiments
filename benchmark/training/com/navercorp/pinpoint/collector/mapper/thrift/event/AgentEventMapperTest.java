/**
 * Copyright 2017 NAVER Corp.
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
package com.navercorp.pinpoint.collector.mapper.thrift.event;


import com.navercorp.pinpoint.common.server.bo.event.AgentEventBo;
import com.navercorp.pinpoint.common.server.bo.event.DeadlockBo;
import com.navercorp.pinpoint.common.server.bo.event.DeadlockEventBo;
import com.navercorp.pinpoint.common.server.util.AgentEventType;
import com.navercorp.pinpoint.thrift.dto.TAgentStat;
import com.navercorp.pinpoint.thrift.dto.TDeadlock;
import org.junit.Assert;
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
 * @author Taejin Koo
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class AgentEventMapperTest {
    @Mock
    private DeadlockEventBoMapper deadlockEventBoMapper;

    @Mock
    private DeadlockBoMapper deadlockBoMapper;

    @InjectMocks
    private AgentEventMapper agentEventMapper;

    @Test
    public void simpleTest1() {
        final String agentId = "agentId";
        final long startTimestamp = Long.MAX_VALUE;
        final long eventTimestamp = startTimestamp;
        final TAgentStat agentStat = createAgentStat(agentId, startTimestamp, eventTimestamp, 2);
        DeadlockBo deadlockBo = new DeadlockBo();
        deadlockBo.setDeadlockedThreadCount(agentStat.getDeadlock().getDeadlockedThreadCount());
        DeadlockEventBo expectedEventBo = new DeadlockEventBo(agentId, startTimestamp, eventTimestamp, AgentEventType.AGENT_DEADLOCK_DETECTED, deadlockBo);
        Mockito.when(this.deadlockEventBoMapper.map(agentId, startTimestamp, startTimestamp, agentStat.getDeadlock())).thenReturn(expectedEventBo);
        AgentEventBo actualEventBo = agentEventMapper.map(agentStat);
        Assert.assertEquals(expectedEventBo, actualEventBo);
    }

    @Test
    public void simpleTest2() {
        final String agentId = "agentId";
        final long startTimestamp = Long.MAX_VALUE;
        final long eventTimestamp = startTimestamp;
        final TAgentStat agentStat = createAgentStat(agentId, startTimestamp, eventTimestamp, 0);
        DeadlockBo deadlockBo = new DeadlockBo();
        DeadlockEventBo expectedEventBo = new DeadlockEventBo(agentId, startTimestamp, eventTimestamp, AgentEventType.AGENT_DEADLOCK_DETECTED, deadlockBo);
        Mockito.when(this.deadlockEventBoMapper.map(ArgumentMatchers.any(String.class), ArgumentMatchers.any(Long.class), ArgumentMatchers.any(Long.class), ArgumentMatchers.any(TDeadlock.class))).thenReturn(expectedEventBo);
        AgentEventBo actualEventBo = agentEventMapper.map(agentStat);
        Assert.assertNull(actualEventBo);
    }
}

