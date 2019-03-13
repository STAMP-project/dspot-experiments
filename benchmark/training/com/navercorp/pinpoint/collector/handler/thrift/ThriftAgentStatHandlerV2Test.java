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
package com.navercorp.pinpoint.collector.handler.thrift;


import com.navercorp.pinpoint.collector.dao.AgentStatDaoV2;
import com.navercorp.pinpoint.collector.mapper.thrift.stat.AgentStatBatchMapper;
import com.navercorp.pinpoint.collector.mapper.thrift.stat.AgentStatMapper;
import com.navercorp.pinpoint.collector.service.AgentStatService;
import com.navercorp.pinpoint.collector.service.HBaseAgentStatService;
import com.navercorp.pinpoint.common.server.bo.stat.ActiveTraceBo;
import com.navercorp.pinpoint.common.server.bo.stat.AgentStatBo;
import com.navercorp.pinpoint.common.server.bo.stat.CpuLoadBo;
import com.navercorp.pinpoint.common.server.bo.stat.DataSourceListBo;
import com.navercorp.pinpoint.common.server.bo.stat.DeadlockThreadCountBo;
import com.navercorp.pinpoint.common.server.bo.stat.DirectBufferBo;
import com.navercorp.pinpoint.common.server.bo.stat.FileDescriptorBo;
import com.navercorp.pinpoint.common.server.bo.stat.JvmGcBo;
import com.navercorp.pinpoint.common.server.bo.stat.JvmGcDetailedBo;
import com.navercorp.pinpoint.common.server.bo.stat.ResponseTimeBo;
import com.navercorp.pinpoint.common.server.bo.stat.TransactionBo;
import com.navercorp.pinpoint.thrift.dto.TAgentInfo;
import com.navercorp.pinpoint.thrift.dto.TAgentStat;
import com.navercorp.pinpoint.thrift.dto.TAgentStatBatch;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;


/**
 *
 *
 * @author HyunGil Jeong
 */
public class ThriftAgentStatHandlerV2Test {
    @Mock
    private AgentStatMapper agentStatMapper;

    @Mock
    private AgentStatBatchMapper agentStatBatchMapper;

    @Mock
    private AgentStatDaoV2<JvmGcBo> jvmGcDao;

    @Mock
    private AgentStatDaoV2<JvmGcDetailedBo> jvmGcDetailedDao;

    @Mock
    private AgentStatDaoV2<CpuLoadBo> cpuLoadDao;

    @Mock
    private AgentStatDaoV2<TransactionBo> transactionDao;

    @Mock
    private AgentStatDaoV2<ActiveTraceBo> activeTraceDao;

    @Mock
    private AgentStatDaoV2<DataSourceListBo> dataSourceDao;

    @Mock
    private AgentStatDaoV2<ResponseTimeBo> responseTimeDao;

    @Mock
    private AgentStatDaoV2<DeadlockThreadCountBo> deadlockDao;

    @Mock
    private AgentStatDaoV2<FileDescriptorBo> fileDescriptorDao;

    @Mock
    private AgentStatDaoV2<DirectBufferBo> directBufferDao;

    @InjectMocks
    private HBaseAgentStatService hBaseAgentStatService = new HBaseAgentStatService();

    @Spy
    private List<AgentStatService> agentStatServiceList = new ArrayList<>();

    @InjectMocks
    private ThriftAgentStatHandlerV2 thriftAgentStatHandlerV2 = new ThriftAgentStatHandlerV2();

    @Test
    public void testHandleForTAgentStat() {
        // Given
        final String agentId = "agentId";
        final long startTimestamp = Long.MAX_VALUE;
        final TAgentStat agentStat = createAgentStat(agentId, startTimestamp);
        final AgentStatBo mappedAgentStat = new AgentStatBo();
        Mockito.when(this.agentStatMapper.map(agentStat)).thenReturn(mappedAgentStat);
        // When
        thriftAgentStatHandlerV2.handleSimple(agentStat);
        // Then
        Mockito.verify(jvmGcDao).insert(mappedAgentStat.getAgentId(), mappedAgentStat.getJvmGcBos());
        Mockito.verify(jvmGcDetailedDao).insert(mappedAgentStat.getAgentId(), mappedAgentStat.getJvmGcDetailedBos());
        Mockito.verify(cpuLoadDao).insert(mappedAgentStat.getAgentId(), mappedAgentStat.getCpuLoadBos());
        Mockito.verify(transactionDao).insert(mappedAgentStat.getAgentId(), mappedAgentStat.getTransactionBos());
        Mockito.verify(activeTraceDao).insert(mappedAgentStat.getAgentId(), mappedAgentStat.getActiveTraceBos());
        Mockito.verify(dataSourceDao).insert(mappedAgentStat.getAgentId(), mappedAgentStat.getDataSourceListBos());
        Mockito.verify(responseTimeDao).insert(mappedAgentStat.getAgentId(), mappedAgentStat.getResponseTimeBos());
        Mockito.verify(deadlockDao).insert(mappedAgentStat.getAgentId(), mappedAgentStat.getDeadlockThreadCountBos());
        Mockito.verify(fileDescriptorDao).insert(mappedAgentStat.getAgentId(), mappedAgentStat.getFileDescriptorBos());
        Mockito.verify(directBufferDao).insert(mappedAgentStat.getAgentId(), mappedAgentStat.getDirectBufferBos());
    }

    @Test
    public void testHandleForTAgentStatBatch() {
        // Given
        final int numBatches = 6;
        final String agentId = "agentId";
        final long startTimestamp = Long.MAX_VALUE;
        final TAgentStatBatch agentStatBatch = createAgentStatBatch(agentId, startTimestamp, numBatches);
        final AgentStatBo mappedAgentStat = new AgentStatBo();
        Mockito.when(this.agentStatBatchMapper.map(agentStatBatch)).thenReturn(mappedAgentStat);
        // When
        thriftAgentStatHandlerV2.handleSimple(agentStatBatch);
        // Then
        Mockito.verify(jvmGcDao).insert(mappedAgentStat.getAgentId(), mappedAgentStat.getJvmGcBos());
        Mockito.verify(jvmGcDetailedDao).insert(mappedAgentStat.getAgentId(), mappedAgentStat.getJvmGcDetailedBos());
        Mockito.verify(cpuLoadDao).insert(mappedAgentStat.getAgentId(), mappedAgentStat.getCpuLoadBos());
        Mockito.verify(transactionDao).insert(mappedAgentStat.getAgentId(), mappedAgentStat.getTransactionBos());
        Mockito.verify(activeTraceDao).insert(mappedAgentStat.getAgentId(), mappedAgentStat.getActiveTraceBos());
        Mockito.verify(dataSourceDao).insert(mappedAgentStat.getAgentId(), mappedAgentStat.getDataSourceListBos());
        Mockito.verify(responseTimeDao).insert(mappedAgentStat.getAgentId(), mappedAgentStat.getResponseTimeBos());
        Mockito.verify(deadlockDao).insert(mappedAgentStat.getAgentId(), mappedAgentStat.getDeadlockThreadCountBos());
        Mockito.verify(fileDescriptorDao).insert(mappedAgentStat.getAgentId(), mappedAgentStat.getFileDescriptorBos());
        Mockito.verify(directBufferDao).insert(mappedAgentStat.getAgentId(), mappedAgentStat.getDirectBufferBos());
    }

    @Test
    public void insertShouldNotBeCalledIfTAgentStatIsMappedToNull() {
        // Given
        final String agentId = "agentId";
        final long startTimestamp = Long.MAX_VALUE;
        final TAgentStat agentStat = createAgentStat(agentId, startTimestamp);
        final AgentStatBo mappedAgentStat = null;
        Mockito.when(this.agentStatMapper.map(agentStat)).thenReturn(mappedAgentStat);
        // When
        thriftAgentStatHandlerV2.handleSimple(agentStat);
        // Then
        Mockito.verifyZeroInteractions(jvmGcDao);
        Mockito.verifyZeroInteractions(jvmGcDetailedDao);
        Mockito.verifyZeroInteractions(cpuLoadDao);
        Mockito.verifyZeroInteractions(transactionDao);
        Mockito.verifyZeroInteractions(activeTraceDao);
        Mockito.verifyZeroInteractions(dataSourceDao);
        Mockito.verifyZeroInteractions(responseTimeDao);
        Mockito.verifyZeroInteractions(fileDescriptorDao);
        Mockito.verifyZeroInteractions(directBufferDao);
    }

    @Test
    public void insertShouldNotBeCalledIfTAgentStatBatchIsMappedToNull() {
        // Given
        final int numBatches = 6;
        final String agentId = "agentId";
        final long startTimestamp = Long.MAX_VALUE;
        final TAgentStatBatch agentStatBatch = createAgentStatBatch(agentId, startTimestamp, numBatches);
        final AgentStatBo mappedAgentStat = null;
        Mockito.when(this.agentStatBatchMapper.map(agentStatBatch)).thenReturn(mappedAgentStat);
        // When
        thriftAgentStatHandlerV2.handleSimple(agentStatBatch);
        // Then
        Mockito.verifyZeroInteractions(jvmGcDao);
        Mockito.verifyZeroInteractions(jvmGcDetailedDao);
        Mockito.verifyZeroInteractions(cpuLoadDao);
        Mockito.verifyZeroInteractions(transactionDao);
        Mockito.verifyZeroInteractions(activeTraceDao);
        Mockito.verifyZeroInteractions(dataSourceDao);
        Mockito.verifyZeroInteractions(responseTimeDao);
        Mockito.verifyZeroInteractions(fileDescriptorDao);
        Mockito.verifyZeroInteractions(directBufferDao);
    }

    @Test(expected = IllegalArgumentException.class)
    public void handleShouldThrowIllegalArgumentExceptionForIncorrectTBaseObjects() {
        // Given
        final TAgentInfo wrongTBaseObject = new TAgentInfo();
        // When
        thriftAgentStatHandlerV2.handleSimple(wrongTBaseObject);
        // Then
        Assert.fail();
    }
}

