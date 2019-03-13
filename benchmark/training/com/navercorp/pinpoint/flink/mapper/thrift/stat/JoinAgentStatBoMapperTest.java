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


import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author minwoo.jung
 */
public class JoinAgentStatBoMapperTest {
    @Test
    public void mapTest() {
        final String agentId = "testAgent";
        final JoinAgentStatBoMapper joinAgentStatBoMapper = new JoinAgentStatBoMapper();
        final TFAgentStatBatch tFAgentStatBatch = new TFAgentStatBatch();
        tFAgentStatBatch.setStartTimestamp(1491274138454L);
        tFAgentStatBatch.setAgentId(agentId);
        final TFAgentStat tFAgentStat = new TFAgentStat();
        tFAgentStat.setAgentId(agentId);
        tFAgentStat.setTimestamp(1491274148454L);
        final TFCpuLoad tFCpuLoad = new TFCpuLoad();
        tFCpuLoad.setJvmCpuLoad(10);
        tFCpuLoad.setSystemCpuLoad(30);
        tFAgentStat.setCpuLoad(tFCpuLoad);
        final TFAgentStat tFAgentStat2 = new TFAgentStat();
        tFAgentStat2.setAgentId(agentId);
        tFAgentStat2.setTimestamp(1491275148454L);
        final TFCpuLoad tFCpuLoad2 = new TFCpuLoad();
        tFCpuLoad2.setJvmCpuLoad(20);
        tFCpuLoad2.setSystemCpuLoad(50);
        tFAgentStat2.setCpuLoad(tFCpuLoad2);
        final List<TFAgentStat> tFAgentStatList = new ArrayList<>(2);
        tFAgentStatList.add(tFAgentStat);
        tFAgentStatList.add(tFAgentStat2);
        tFAgentStatBatch.setAgentStats(tFAgentStatList);
        JoinAgentStatBo joinAgentStatBo = joinAgentStatBoMapper.map(tFAgentStatBatch);
        Assert.assertEquals(joinAgentStatBo.getId(), agentId);
        Assert.assertEquals(joinAgentStatBo.getAgentStartTimestamp(), 1491274138454L);
        Assert.assertEquals(joinAgentStatBo.getTimestamp(), 1491274148454L);
        List<JoinCpuLoadBo> joinCpuLoadBoList = joinAgentStatBo.getJoinCpuLoadBoList();
        Assert.assertEquals(joinCpuLoadBoList.size(), 2);
        JoinCpuLoadBo joinCpuLoadBo = joinCpuLoadBoList.get(0);
        Assert.assertEquals(joinCpuLoadBo.getId(), agentId);
        Assert.assertEquals(joinCpuLoadBo.getTimestamp(), 1491274148454L);
        Assert.assertEquals(joinCpuLoadBo.getJvmCpuLoad(), 10, 0);
        Assert.assertEquals(joinCpuLoadBo.getMinJvmCpuLoad(), 10, 0);
        Assert.assertEquals(joinCpuLoadBo.getMaxJvmCpuLoad(), 10, 0);
        Assert.assertEquals(joinCpuLoadBo.getSystemCpuLoad(), 30, 0);
        Assert.assertEquals(joinCpuLoadBo.getMinSystemCpuLoad(), 30, 0);
        Assert.assertEquals(joinCpuLoadBo.getMaxSystemCpuLoad(), 30, 0);
        joinCpuLoadBo = joinCpuLoadBoList.get(1);
        Assert.assertEquals(joinCpuLoadBo.getId(), agentId);
        Assert.assertEquals(joinCpuLoadBo.getTimestamp(), 1491275148454L);
        Assert.assertEquals(joinCpuLoadBo.getJvmCpuLoad(), 20, 0);
        Assert.assertEquals(joinCpuLoadBo.getMinJvmCpuLoad(), 20, 0);
        Assert.assertEquals(joinCpuLoadBo.getMaxJvmCpuLoad(), 20, 0);
        Assert.assertEquals(joinCpuLoadBo.getSystemCpuLoad(), 50, 0);
        Assert.assertEquals(joinCpuLoadBo.getMinSystemCpuLoad(), 50, 0);
        Assert.assertEquals(joinCpuLoadBo.getMaxSystemCpuLoad(), 50, 0);
    }

    @Test
    public void map2Test() {
        final String agentId = "testAgent";
        final JoinAgentStatBoMapper joinAgentStatBoMapper = new JoinAgentStatBoMapper();
        final TFAgentStatBatch tFAgentStatBatch = new TFAgentStatBatch();
        tFAgentStatBatch.setStartTimestamp(1491274138454L);
        tFAgentStatBatch.setAgentId(agentId);
        final TFAgentStat tFAgentStat = new TFAgentStat();
        tFAgentStat.setAgentId(agentId);
        tFAgentStat.setTimestamp(1491274148454L);
        final TFJvmGc tFJvmGc = new TFJvmGc();
        tFJvmGc.setJvmMemoryHeapUsed(1000);
        tFJvmGc.setJvmMemoryNonHeapUsed(300);
        tFAgentStat.setGc(tFJvmGc);
        final TFAgentStat tFAgentStat2 = new TFAgentStat();
        tFAgentStat2.setAgentId(agentId);
        tFAgentStat2.setTimestamp(1491275148454L);
        final TFJvmGc tFJvmGc2 = new TFJvmGc();
        tFJvmGc2.setJvmMemoryHeapUsed(2000);
        tFJvmGc2.setJvmMemoryNonHeapUsed(500);
        tFAgentStat2.setGc(tFJvmGc2);
        final List<TFAgentStat> tFAgentStatList = new ArrayList<>(2);
        tFAgentStatList.add(tFAgentStat);
        tFAgentStatList.add(tFAgentStat2);
        tFAgentStatBatch.setAgentStats(tFAgentStatList);
        JoinAgentStatBo joinAgentStatBo = joinAgentStatBoMapper.map(tFAgentStatBatch);
        Assert.assertEquals(joinAgentStatBo.getId(), agentId);
        Assert.assertEquals(joinAgentStatBo.getAgentStartTimestamp(), 1491274138454L);
        Assert.assertEquals(joinAgentStatBo.getTimestamp(), 1491274148454L);
        List<JoinMemoryBo> joinMemoryBoList = joinAgentStatBo.getJoinMemoryBoList();
        Assert.assertEquals(joinMemoryBoList.size(), 2);
        JoinMemoryBo joinMemoryBo = joinMemoryBoList.get(0);
        Assert.assertEquals(joinMemoryBo.getId(), agentId);
        Assert.assertEquals(joinMemoryBo.getTimestamp(), 1491274148454L);
        Assert.assertEquals(joinMemoryBo.getHeapUsed(), 1000);
        Assert.assertEquals(joinMemoryBo.getNonHeapUsed(), 300);
        JoinMemoryBo joinMemoryBo2 = joinMemoryBoList.get(1);
        Assert.assertEquals(joinMemoryBo2.getId(), agentId);
        Assert.assertEquals(joinMemoryBo2.getTimestamp(), 1491275148454L);
        Assert.assertEquals(joinMemoryBo2.getHeapUsed(), 2000);
        Assert.assertEquals(joinMemoryBo2.getNonHeapUsed(), 500);
    }

    @Test
    public void map3Test() {
        final String agentId = "testAgent";
        final JoinAgentStatBoMapper joinAgentStatBoMapper = new JoinAgentStatBoMapper();
        final TFAgentStatBatch tFAgentStatBatch = new TFAgentStatBatch();
        tFAgentStatBatch.setStartTimestamp(1491274138454L);
        tFAgentStatBatch.setAgentId(agentId);
        final TFAgentStat tFAgentStat = new TFAgentStat();
        tFAgentStat.setAgentId(agentId);
        tFAgentStat.setTimestamp(1491274148454L);
        tFAgentStat.setCollectInterval(5000);
        final TFTransaction tFTransaction = new TFTransaction();
        tFTransaction.setSampledNewCount(10);
        tFTransaction.setSampledContinuationCount(20);
        tFTransaction.setUnsampledNewCount(40);
        tFTransaction.setUnsampledContinuationCount(50);
        tFAgentStat.setTransaction(tFTransaction);
        final TFAgentStat tFAgentStat2 = new TFAgentStat();
        tFAgentStat2.setAgentId(agentId);
        tFAgentStat2.setTimestamp(1491275148454L);
        tFAgentStat2.setCollectInterval(5000);
        final TFTransaction tFTransaction2 = new TFTransaction();
        tFTransaction2.setSampledNewCount(11);
        tFTransaction2.setSampledContinuationCount(21);
        tFTransaction2.setUnsampledNewCount(41);
        tFTransaction2.setUnsampledContinuationCount(51);
        tFAgentStat2.setTransaction(tFTransaction2);
        final List<TFAgentStat> tFAgentStatList = new ArrayList<>(2);
        tFAgentStatList.add(tFAgentStat);
        tFAgentStatList.add(tFAgentStat2);
        tFAgentStatBatch.setAgentStats(tFAgentStatList);
        JoinAgentStatBo joinAgentStatBo = joinAgentStatBoMapper.map(tFAgentStatBatch);
        Assert.assertEquals(joinAgentStatBo.getId(), agentId);
        Assert.assertEquals(joinAgentStatBo.getAgentStartTimestamp(), 1491274138454L);
        Assert.assertEquals(joinAgentStatBo.getTimestamp(), 1491274148454L);
        List<JoinTransactionBo> joinTransactionBoList = joinAgentStatBo.getJoinTransactionBoList();
        Assert.assertEquals(joinTransactionBoList.size(), 2);
        JoinTransactionBo joinTransactionBo = joinTransactionBoList.get(0);
        Assert.assertEquals(joinTransactionBo.getId(), agentId);
        Assert.assertEquals(joinTransactionBo.getTimestamp(), 1491274148454L);
        Assert.assertEquals(joinTransactionBo.getCollectInterval(), 5000);
        Assert.assertEquals(joinTransactionBo.getTotalCount(), 120);
        Assert.assertEquals(joinTransactionBo.getMaxTotalCount(), 120);
        Assert.assertEquals(joinTransactionBo.getMaxTotalCountAgentId(), agentId);
        Assert.assertEquals(joinTransactionBo.getMinTotalCount(), 120);
        Assert.assertEquals(joinTransactionBo.getMinTotalCountAgentId(), agentId);
        JoinTransactionBo joinTransactionBo2 = joinTransactionBoList.get(1);
        Assert.assertEquals(joinTransactionBo2.getId(), agentId);
        Assert.assertEquals(joinTransactionBo2.getTimestamp(), 1491275148454L);
        Assert.assertEquals(joinTransactionBo2.getCollectInterval(), 5000);
        Assert.assertEquals(joinTransactionBo2.getTotalCount(), 124);
        Assert.assertEquals(joinTransactionBo2.getMaxTotalCount(), 124);
        Assert.assertEquals(joinTransactionBo2.getMaxTotalCountAgentId(), agentId);
        Assert.assertEquals(joinTransactionBo2.getMinTotalCount(), 124);
        Assert.assertEquals(joinTransactionBo2.getMinTotalCountAgentId(), agentId);
    }

    @Test
    public void map4Test() {
        final String agentId = "testAgent";
        final JoinAgentStatBoMapper joinAgentStatBoMapper = new JoinAgentStatBoMapper();
        final TFAgentStatBatch tFAgentStatBatch = new TFAgentStatBatch();
        tFAgentStatBatch.setStartTimestamp(1491274138454L);
        tFAgentStatBatch.setAgentId(agentId);
        final TFAgentStat tFAgentStat = new TFAgentStat();
        tFAgentStat.setAgentId(agentId);
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
        final TFActiveTrace tfActiveTrace = new TFActiveTrace();
        tfActiveTrace.setHistogram(tFActiveTraceHistogram);
        tFAgentStat.setActiveTrace(tfActiveTrace);
        final TFAgentStat tFAgentStat2 = new TFAgentStat();
        tFAgentStat2.setAgentId(agentId);
        tFAgentStat2.setTimestamp(1491275148454L);
        tFAgentStat2.setCollectInterval(5000);
        final TFActiveTraceHistogram tFActiveTraceHistogram2 = new TFActiveTraceHistogram();
        List<Integer> activeTraceCount2 = new ArrayList<>(4);
        activeTraceCount2.add(11);
        activeTraceCount2.add(21);
        activeTraceCount2.add(41);
        activeTraceCount2.add(51);
        tFActiveTraceHistogram2.setVersion(((short) (2)));
        tFActiveTraceHistogram2.setHistogramSchemaType(1);
        tFActiveTraceHistogram2.setActiveTraceCount(activeTraceCount2);
        final TFActiveTrace tfActiveTrace2 = new TFActiveTrace();
        tfActiveTrace2.setHistogram(tFActiveTraceHistogram2);
        tFAgentStat2.setActiveTrace(tfActiveTrace2);
        final List<TFAgentStat> tFAgentStatList = new ArrayList<>(2);
        tFAgentStatList.add(tFAgentStat);
        tFAgentStatList.add(tFAgentStat2);
        tFAgentStatBatch.setAgentStats(tFAgentStatList);
        JoinAgentStatBo joinAgentStatBo = joinAgentStatBoMapper.map(tFAgentStatBatch);
        Assert.assertEquals(joinAgentStatBo.getId(), agentId);
        Assert.assertEquals(joinAgentStatBo.getAgentStartTimestamp(), 1491274138454L);
        Assert.assertEquals(joinAgentStatBo.getTimestamp(), 1491274148454L);
        List<JoinActiveTraceBo> joinActiveTraceBoList = joinAgentStatBo.getJoinActiveTraceBoList();
        Assert.assertEquals(joinActiveTraceBoList.size(), 2);
        JoinActiveTraceBo joinActiveTraceBo = joinActiveTraceBoList.get(0);
        Assert.assertEquals(joinActiveTraceBo.getId(), agentId);
        Assert.assertEquals(joinActiveTraceBo.getTimestamp(), 1491274148454L);
        Assert.assertEquals(joinActiveTraceBo.getVersion(), 2);
        Assert.assertEquals(joinActiveTraceBo.getHistogramSchemaType(), 1);
        Assert.assertEquals(joinActiveTraceBo.getTotalCount(), 120);
        Assert.assertEquals(joinActiveTraceBo.getMaxTotalCount(), 120);
        Assert.assertEquals(joinActiveTraceBo.getMaxTotalCountAgentId(), agentId);
        Assert.assertEquals(joinActiveTraceBo.getMinTotalCount(), 120);
        Assert.assertEquals(joinActiveTraceBo.getMinTotalCountAgentId(), agentId);
        JoinActiveTraceBo joinActiveTraceBo2 = joinActiveTraceBoList.get(1);
        Assert.assertEquals(joinActiveTraceBo2.getId(), agentId);
        Assert.assertEquals(joinActiveTraceBo2.getTimestamp(), 1491275148454L);
        Assert.assertEquals(joinActiveTraceBo2.getVersion(), 2);
        Assert.assertEquals(joinActiveTraceBo2.getHistogramSchemaType(), 1);
        Assert.assertEquals(joinActiveTraceBo2.getTotalCount(), 124);
        Assert.assertEquals(joinActiveTraceBo2.getMaxTotalCount(), 124);
        Assert.assertEquals(joinActiveTraceBo2.getMaxTotalCountAgentId(), agentId);
        Assert.assertEquals(joinActiveTraceBo2.getMinTotalCount(), 124);
        Assert.assertEquals(joinActiveTraceBo2.getMinTotalCountAgentId(), agentId);
    }

    @Test
    public void map5Test() {
        final String agentId = "testAgent";
        final JoinAgentStatBoMapper joinAgentStatBoMapper = new JoinAgentStatBoMapper();
        final TFAgentStatBatch tFAgentStatBatch = new TFAgentStatBatch();
        tFAgentStatBatch.setStartTimestamp(1491274138454L);
        tFAgentStatBatch.setAgentId(agentId);
        final TFResponseTime tFResponseTime = new TFResponseTime();
        tFResponseTime.setAvg(100);
        final TFAgentStat tFAgentStat = new TFAgentStat();
        tFAgentStat.setAgentId(agentId);
        tFAgentStat.setTimestamp(1491274148454L);
        tFAgentStat.setResponseTime(tFResponseTime);
        final TFResponseTime tFResponseTime2 = new TFResponseTime();
        tFResponseTime2.setAvg(120);
        final TFAgentStat tFAgentStat2 = new TFAgentStat();
        tFAgentStat2.setAgentId(agentId);
        tFAgentStat2.setTimestamp(1491275148454L);
        tFAgentStat2.setResponseTime(tFResponseTime2);
        final List<TFAgentStat> tFAgentStatList = new ArrayList<>(2);
        tFAgentStatList.add(tFAgentStat);
        tFAgentStatList.add(tFAgentStat2);
        tFAgentStatBatch.setAgentStats(tFAgentStatList);
        JoinAgentStatBo joinAgentStatBo = joinAgentStatBoMapper.map(tFAgentStatBatch);
        Assert.assertEquals(joinAgentStatBo.getId(), agentId);
        Assert.assertEquals(joinAgentStatBo.getAgentStartTimestamp(), 1491274138454L);
        Assert.assertEquals(joinAgentStatBo.getTimestamp(), 1491274148454L);
        List<JoinResponseTimeBo> joinResponseTimeBoList = joinAgentStatBo.getJoinResponseTimeBoList();
        Assert.assertEquals(joinResponseTimeBoList.size(), 2);
        JoinResponseTimeBo joinResponseTimeBo = joinResponseTimeBoList.get(0);
        Assert.assertEquals(joinResponseTimeBo.getId(), agentId);
        Assert.assertEquals(joinResponseTimeBo.getTimestamp(), 1491274148454L);
        Assert.assertEquals(joinResponseTimeBo.getAvg(), 100);
        Assert.assertEquals(joinResponseTimeBo.getMinAvg(), 100);
        Assert.assertEquals(joinResponseTimeBo.getMinAvgAgentId(), agentId);
        Assert.assertEquals(joinResponseTimeBo.getMaxAvg(), 100);
        Assert.assertEquals(joinResponseTimeBo.getMaxAvgAgentId(), agentId);
        JoinResponseTimeBo joinResponseTimeBo2 = joinResponseTimeBoList.get(1);
        Assert.assertEquals(joinResponseTimeBo2.getId(), agentId);
        Assert.assertEquals(joinResponseTimeBo2.getTimestamp(), 1491275148454L);
        Assert.assertEquals(joinResponseTimeBo2.getAvg(), 120);
        Assert.assertEquals(joinResponseTimeBo2.getMinAvg(), 120);
        Assert.assertEquals(joinResponseTimeBo2.getMinAvgAgentId(), agentId);
        Assert.assertEquals(joinResponseTimeBo2.getMaxAvg(), 120);
        Assert.assertEquals(joinResponseTimeBo2.getMaxAvgAgentId(), agentId);
    }
}

