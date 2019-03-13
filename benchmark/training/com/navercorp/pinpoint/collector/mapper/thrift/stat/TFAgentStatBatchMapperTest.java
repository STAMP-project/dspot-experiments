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
package com.navercorp.pinpoint.collector.mapper.thrift.stat;


import com.navercorp.pinpoint.common.server.bo.stat.AgentStatBo;
import com.navercorp.pinpoint.thrift.dto.flink.TFAgentStat;
import com.navercorp.pinpoint.thrift.dto.flink.TFAgentStatBatch;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author minwoo.jung
 */
public class TFAgentStatBatchMapperTest {
    public static final String TEST_AGENT = "test_agent";

    public static final long startTimestamp = 1496370596375L;

    public static final long collectTime1st = (TFAgentStatBatchMapperTest.startTimestamp) + 5000;

    public static final long collectTime2nd = (TFAgentStatBatchMapperTest.collectTime1st) + 5000;

    public static final long collectTime3rd = (TFAgentStatBatchMapperTest.collectTime2nd) + 5000;

    @Test
    public void mapTest() throws Exception {
        final AgentStatBo agentStatBo = new AgentStatBo();
        agentStatBo.setStartTimestamp(TFAgentStatBatchMapperTest.startTimestamp);
        agentStatBo.setAgentId(TFAgentStatBatchMapperTest.TEST_AGENT);
        agentStatBo.setCpuLoadBos(createCpuLoadBoList());
        TFAgentStatBatchMapper mapper = new TFAgentStatBatchMapper();
        TFAgentStatBatch tFAgentStatBatch = mapper.map(agentStatBo);
        Assert.assertEquals(TFAgentStatBatchMapperTest.TEST_AGENT, tFAgentStatBatch.getAgentId());
        Assert.assertEquals(TFAgentStatBatchMapperTest.startTimestamp, tFAgentStatBatch.getStartTimestamp());
        List<TFAgentStat> agentStatList = tFAgentStatBatch.getAgentStats();
        Assert.assertEquals(agentStatList.size(), 3);
    }
}

