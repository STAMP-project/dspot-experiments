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
package com.navercorp.pinpoint.flink.mapper.thrift.stat;


import JoinActiveTraceBo.EMPTY_JOIN_ACTIVE_TRACE_BO;
import com.navercorp.pinpoint.common.server.bo.stat.join.JoinActiveTraceBo;
import com.navercorp.pinpoint.thrift.dto.flink.TFActiveTrace;
import com.navercorp.pinpoint.thrift.dto.flink.TFActiveTraceHistogram;
import com.navercorp.pinpoint.thrift.dto.flink.TFAgentStat;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author minwoo.jung
 */
public class JoinActiveTraceBoMapperTest {
    @Test
    public void mapTest() throws Exception {
        final TFAgentStat tFAgentStat = new TFAgentStat();
        final String id = "testAgent";
        tFAgentStat.setAgentId(id);
        tFAgentStat.setTimestamp(1491274148454L);
        final TFActiveTraceHistogram tFActiveTraceHistogram = new TFActiveTraceHistogram();
        List<Integer> activeTraceCount = new ArrayList<>(4);
        activeTraceCount.add(10);
        activeTraceCount.add(20);
        activeTraceCount.add(40);
        activeTraceCount.add(50);
        tFActiveTraceHistogram.setVersion(((short) (2)));
        tFActiveTraceHistogram.setHistogramSchemaType(1);
        tFActiveTraceHistogram.setActiveTraceCount(activeTraceCount);
        final TFActiveTrace tFActiveTrace = new TFActiveTrace();
        tFActiveTrace.setHistogram(tFActiveTraceHistogram);
        tFAgentStat.setActiveTrace(tFActiveTrace);
        final JoinActiveTraceBoMapper joinActiveTraceBoMapper = new JoinActiveTraceBoMapper();
        final JoinActiveTraceBo joinActiveTraceBo = joinActiveTraceBoMapper.map(tFAgentStat);
        Assert.assertNotNull(joinActiveTraceBo);
        Assert.assertEquals(joinActiveTraceBo.getId(), id);
        Assert.assertEquals(joinActiveTraceBo.getTimestamp(), 1491274148454L);
        Assert.assertEquals(joinActiveTraceBo.getVersion(), 2);
        Assert.assertEquals(joinActiveTraceBo.getHistogramSchemaType(), 1);
        Assert.assertEquals(joinActiveTraceBo.getTotalCount(), 120);
        Assert.assertEquals(joinActiveTraceBo.getMaxTotalCount(), 120);
        Assert.assertEquals(joinActiveTraceBo.getMaxTotalCountAgentId(), id);
        Assert.assertEquals(joinActiveTraceBo.getMinTotalCount(), 120);
        Assert.assertEquals(joinActiveTraceBo.getMinTotalCountAgentId(), id);
    }

    @Test
    public void map2Test() {
        final TFAgentStat tFAgentStat = new TFAgentStat();
        final String id = "testAgent";
        tFAgentStat.setAgentId(id);
        tFAgentStat.setTimestamp(1491274148454L);
        final JoinActiveTraceBoMapper joinActiveTraceBoMapper = new JoinActiveTraceBoMapper();
        final JoinActiveTraceBo joinActiveTraceBo = joinActiveTraceBoMapper.map(tFAgentStat);
        Assert.assertEquals(joinActiveTraceBo, EMPTY_JOIN_ACTIVE_TRACE_BO);
    }
}

