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


import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author minwoo.jung
 */
public class TFAgentStatMapperTest {
    public static final String TEST_AGENT = "test_agent";

    public static final long startTimestamp = 1496370596375L;

    public static final long collectTime1st = (TFAgentStatMapperTest.startTimestamp) + 5000;

    public static final long collectTime2nd = (TFAgentStatMapperTest.collectTime1st) + 5000;

    public static final long collectTime3rd = (TFAgentStatMapperTest.collectTime2nd) + 5000;

    @Test
    public void mapTest() throws Exception {
        final AgentStatBo agentStatBo = new AgentStatBo();
        agentStatBo.setStartTimestamp(TFAgentStatMapperTest.startTimestamp);
        agentStatBo.setAgentId(TFAgentStatMapperTest.TEST_AGENT);
        agentStatBo.setCpuLoadBos(createCpuLoadBoList());
        List<TFAgentStat> tFAgentStatList = new TFAgentStatMapper().map(agentStatBo);
        Assert.assertEquals(3, tFAgentStatList.size());
        TFAgentStat tFAgentStat1 = tFAgentStatList.get(0);
        Assert.assertEquals(TFAgentStatMapperTest.TEST_AGENT, tFAgentStat1.getAgentId());
        Assert.assertEquals(TFAgentStatMapperTest.startTimestamp, tFAgentStat1.getStartTimestamp());
        Assert.assertEquals(TFAgentStatMapperTest.collectTime1st, tFAgentStat1.getTimestamp());
        Assert.assertEquals(4, tFAgentStat1.getCpuLoad().getJvmCpuLoad(), 0);
        Assert.assertEquals(3, tFAgentStat1.getCpuLoad().getSystemCpuLoad(), 0);
        TFAgentStat tFAgentStat2 = tFAgentStatList.get(1);
        Assert.assertEquals(TFAgentStatMapperTest.TEST_AGENT, tFAgentStat2.getAgentId());
        Assert.assertEquals(TFAgentStatMapperTest.startTimestamp, tFAgentStat2.getStartTimestamp());
        Assert.assertEquals(TFAgentStatMapperTest.collectTime2nd, tFAgentStat2.getTimestamp());
        Assert.assertEquals(5, tFAgentStat2.getCpuLoad().getJvmCpuLoad(), 0);
        Assert.assertEquals(6, tFAgentStat2.getCpuLoad().getSystemCpuLoad(), 0);
        TFAgentStat tFAgentStat3 = tFAgentStatList.get(2);
        Assert.assertEquals(TFAgentStatMapperTest.TEST_AGENT, tFAgentStat3.getAgentId());
        Assert.assertEquals(TFAgentStatMapperTest.startTimestamp, tFAgentStat3.getStartTimestamp());
        Assert.assertEquals(TFAgentStatMapperTest.collectTime3rd, tFAgentStat3.getTimestamp());
        Assert.assertEquals(8, tFAgentStat3.getCpuLoad().getJvmCpuLoad(), 0);
        Assert.assertEquals(9, tFAgentStat3.getCpuLoad().getSystemCpuLoad(), 0);
    }

    @Test
    public void map2Test() throws Exception {
        final AgentStatBo agentStatBo = new AgentStatBo();
        agentStatBo.setStartTimestamp(TFAgentStatMapperTest.startTimestamp);
        agentStatBo.setAgentId(TFAgentStatMapperTest.TEST_AGENT);
        agentStatBo.setDataSourceListBos(createDataSourceListBoList());
        List<TFAgentStat> tFAgentStatList = new TFAgentStatMapper().map(agentStatBo);
        Assert.assertEquals(2, tFAgentStatList.size());
        TFAgentStat tFAgentStat1 = tFAgentStatList.get(0);
        Assert.assertEquals(tFAgentStat1.getAgentId(), TFAgentStatMapperTest.TEST_AGENT);
        Assert.assertEquals(tFAgentStat1.getStartTimestamp(), TFAgentStatMapperTest.startTimestamp);
        Assert.assertEquals(tFAgentStat1.getTimestamp(), TFAgentStatMapperTest.collectTime1st);
        Assert.assertEquals(tFAgentStat1.isSetDataSourceList(), true);
        List<TFDataSource> dataSourceList1 = tFAgentStat1.getDataSourceList().getDataSourceList();
        Assert.assertEquals(dataSourceList1.size(), 2);
        TFDataSource tfDataSource1_1 = dataSourceList1.get(0);
        TFDataSource tfDataSource1_2 = dataSourceList1.get(1);
        Assert.assertEquals(tfDataSource1_1.getId(), 1);
        Assert.assertEquals(tfDataSource1_1.getUrl(), "jdbc:mysql");
        Assert.assertEquals(tfDataSource1_1.getServiceTypeCode(), 1000);
        Assert.assertEquals(tfDataSource1_1.getActiveConnectionSize(), 15);
        Assert.assertEquals(tfDataSource1_1.getMaxConnectionSize(), 30);
        Assert.assertEquals(tfDataSource1_1.getDatabaseName(), "pinpoint1");
        Assert.assertEquals(tfDataSource1_2.getId(), 2);
        Assert.assertEquals(tfDataSource1_2.getUrl(), "jdbc:mssql");
        Assert.assertEquals(tfDataSource1_2.getServiceTypeCode(), 2000);
        Assert.assertEquals(tfDataSource1_2.getActiveConnectionSize(), 25);
        Assert.assertEquals(tfDataSource1_2.getMaxConnectionSize(), 40);
        Assert.assertEquals(tfDataSource1_2.getDatabaseName(), "pinpoint2");
        TFAgentStat tFAgentStat2 = tFAgentStatList.get(1);
        Assert.assertEquals(tFAgentStat2.getAgentId(), TFAgentStatMapperTest.TEST_AGENT);
        Assert.assertEquals(tFAgentStat2.getStartTimestamp(), TFAgentStatMapperTest.startTimestamp);
        Assert.assertEquals(tFAgentStat2.getTimestamp(), TFAgentStatMapperTest.collectTime2nd);
        Assert.assertEquals(tFAgentStat2.isSetDataSourceList(), true);
        List<TFDataSource> dataSourceList2 = tFAgentStat2.getDataSourceList().getDataSourceList();
        Assert.assertEquals(dataSourceList2.size(), 2);
        TFDataSource tfDataSource2_1 = dataSourceList2.get(0);
        TFDataSource tfDataSource2_2 = dataSourceList2.get(1);
        Assert.assertEquals(tfDataSource2_1.getId(), 1);
        Assert.assertEquals(tfDataSource2_1.getUrl(), "jdbc:mysql");
        Assert.assertEquals(tfDataSource2_1.getServiceTypeCode(), 1000);
        Assert.assertEquals(tfDataSource2_1.getActiveConnectionSize(), 16);
        Assert.assertEquals(tfDataSource2_1.getMaxConnectionSize(), 31);
        Assert.assertEquals(tfDataSource2_1.getDatabaseName(), "pinpoint1");
        Assert.assertEquals(tfDataSource2_2.getId(), 2);
        Assert.assertEquals(tfDataSource2_2.getUrl(), "jdbc:mssql");
        Assert.assertEquals(tfDataSource2_2.getServiceTypeCode(), 2000);
        Assert.assertEquals(tfDataSource2_2.getActiveConnectionSize(), 26);
        Assert.assertEquals(tfDataSource2_2.getMaxConnectionSize(), 41);
        Assert.assertEquals(tfDataSource2_2.getDatabaseName(), "pinpoint2");
    }

    @Test
    public void map3Test() throws Exception {
        final AgentStatBo agentStatBo = new AgentStatBo();
        agentStatBo.setStartTimestamp(TFAgentStatMapperTest.startTimestamp);
        agentStatBo.setAgentId(TFAgentStatMapperTest.TEST_AGENT);
        agentStatBo.setJvmGcBos(createJvmGcBoList());
        List<TFAgentStat> tFAgentStatList = new TFAgentStatMapper().map(agentStatBo);
        Assert.assertEquals(2, tFAgentStatList.size());
        TFAgentStat tFAgentStat1 = tFAgentStatList.get(0);
        Assert.assertEquals(tFAgentStat1.getAgentId(), TFAgentStatMapperTest.TEST_AGENT);
        Assert.assertEquals(tFAgentStat1.getStartTimestamp(), TFAgentStatMapperTest.startTimestamp);
        Assert.assertEquals(tFAgentStat1.getTimestamp(), TFAgentStatMapperTest.collectTime1st);
        TFJvmGc tFJvmGc1 = tFAgentStat1.getGc();
        Assert.assertEquals(tFJvmGc1.getJvmMemoryHeapUsed(), 3000);
        Assert.assertEquals(tFJvmGc1.getJvmMemoryNonHeapUsed(), 300);
        TFAgentStat tFAgentStat2 = tFAgentStatList.get(1);
        Assert.assertEquals(tFAgentStat2.getAgentId(), TFAgentStatMapperTest.TEST_AGENT);
        Assert.assertEquals(tFAgentStat2.getStartTimestamp(), TFAgentStatMapperTest.startTimestamp);
        Assert.assertEquals(tFAgentStat2.getTimestamp(), TFAgentStatMapperTest.collectTime2nd);
        TFJvmGc tFJvmGc2 = tFAgentStat2.getGc();
        Assert.assertEquals(tFJvmGc2.getJvmMemoryHeapUsed(), 3100);
        Assert.assertEquals(tFJvmGc2.getJvmMemoryNonHeapUsed(), 310);
    }

    @Test
    public void map4Test() {
        final AgentStatBo agentStatBo = new AgentStatBo();
        agentStatBo.setStartTimestamp(TFAgentStatMapperTest.startTimestamp);
        agentStatBo.setAgentId(TFAgentStatMapperTest.TEST_AGENT);
        agentStatBo.setActiveTraceBos(createActiveTraceBoList());
        List<TFAgentStat> tFAgentStatList = new TFAgentStatMapper().map(agentStatBo);
        Assert.assertEquals(2, tFAgentStatList.size());
        TFAgentStat tFAgentStat1 = tFAgentStatList.get(0);
        Assert.assertEquals(tFAgentStat1.getAgentId(), TFAgentStatMapperTest.TEST_AGENT);
        Assert.assertEquals(tFAgentStat1.getStartTimestamp(), TFAgentStatMapperTest.startTimestamp);
        Assert.assertEquals(tFAgentStat1.getTimestamp(), TFAgentStatMapperTest.collectTime1st);
        TFActiveTrace activeTrace1 = tFAgentStat1.getActiveTrace();
        TFActiveTraceHistogram histogram1 = activeTrace1.getHistogram();
        List<Integer> activeTraceCount1 = histogram1.getActiveTraceCount();
        Assert.assertEquals(((int) (activeTraceCount1.get(0))), 30);
        Assert.assertEquals(((int) (activeTraceCount1.get(1))), 40);
        Assert.assertEquals(((int) (activeTraceCount1.get(2))), 10);
        Assert.assertEquals(((int) (activeTraceCount1.get(3))), 50);
        TFAgentStat tFAgentStat2 = tFAgentStatList.get(1);
        Assert.assertEquals(tFAgentStat2.getAgentId(), TFAgentStatMapperTest.TEST_AGENT);
        Assert.assertEquals(tFAgentStat2.getStartTimestamp(), TFAgentStatMapperTest.startTimestamp);
        Assert.assertEquals(tFAgentStat2.getTimestamp(), TFAgentStatMapperTest.collectTime2nd);
        TFActiveTrace activeTrace2 = tFAgentStat2.getActiveTrace();
        TFActiveTraceHistogram histogram2 = activeTrace2.getHistogram();
        List<Integer> activeTraceCount2 = histogram2.getActiveTraceCount();
        Assert.assertEquals(((int) (activeTraceCount2.get(0))), 31);
        Assert.assertEquals(((int) (activeTraceCount2.get(1))), 41);
        Assert.assertEquals(((int) (activeTraceCount2.get(2))), 11);
        Assert.assertEquals(((int) (activeTraceCount2.get(3))), 51);
    }

    @Test
    public void map5Test() {
        final AgentStatBo agentStatBo = new AgentStatBo();
        agentStatBo.setStartTimestamp(TFAgentStatMapperTest.startTimestamp);
        agentStatBo.setAgentId(TFAgentStatMapperTest.TEST_AGENT);
        agentStatBo.setResponseTimeBos(createResponseTimeBoList());
        List<TFAgentStat> tFAgentStatList = new TFAgentStatMapper().map(agentStatBo);
        Assert.assertEquals(2, tFAgentStatList.size());
        TFAgentStat tFAgentStat1 = tFAgentStatList.get(0);
        Assert.assertEquals(tFAgentStat1.getAgentId(), TFAgentStatMapperTest.TEST_AGENT);
        Assert.assertEquals(tFAgentStat1.getStartTimestamp(), TFAgentStatMapperTest.startTimestamp);
        Assert.assertEquals(tFAgentStat1.getTimestamp(), TFAgentStatMapperTest.collectTime1st);
        TFResponseTime responseTime1 = tFAgentStat1.getResponseTime();
        Assert.assertEquals(responseTime1.getAvg(), 1000);
        TFAgentStat tFAgentStat2 = tFAgentStatList.get(1);
        Assert.assertEquals(tFAgentStat2.getAgentId(), TFAgentStatMapperTest.TEST_AGENT);
        Assert.assertEquals(tFAgentStat2.getStartTimestamp(), TFAgentStatMapperTest.startTimestamp);
        Assert.assertEquals(tFAgentStat2.getTimestamp(), TFAgentStatMapperTest.collectTime2nd);
        TFResponseTime responseTime2 = tFAgentStat2.getResponseTime();
        Assert.assertEquals(responseTime2.getAvg(), 2000);
    }

    @Test
    public void map6Test() throws Exception {
        final AgentStatBo agentStatBo = new AgentStatBo();
        agentStatBo.setStartTimestamp(TFAgentStatMapperTest.startTimestamp);
        agentStatBo.setAgentId(TFAgentStatMapperTest.TEST_AGENT);
        agentStatBo.setFileDescriptorBos(createFileDescriptorBoList());
        List<TFAgentStat> tFAgentStatList = new TFAgentStatMapper().map(agentStatBo);
        Assert.assertEquals(3, tFAgentStatList.size());
        TFAgentStat tFAgentStat1 = tFAgentStatList.get(0);
        Assert.assertEquals(TFAgentStatMapperTest.TEST_AGENT, tFAgentStat1.getAgentId());
        Assert.assertEquals(TFAgentStatMapperTest.startTimestamp, tFAgentStat1.getStartTimestamp());
        Assert.assertEquals(TFAgentStatMapperTest.collectTime1st, tFAgentStat1.getTimestamp());
        Assert.assertEquals(4, tFAgentStat1.getFileDescriptor().getOpenFileDescriptorCount(), 0);
        TFAgentStat tFAgentStat2 = tFAgentStatList.get(1);
        Assert.assertEquals(TFAgentStatMapperTest.TEST_AGENT, tFAgentStat2.getAgentId());
        Assert.assertEquals(TFAgentStatMapperTest.startTimestamp, tFAgentStat2.getStartTimestamp());
        Assert.assertEquals(TFAgentStatMapperTest.collectTime2nd, tFAgentStat2.getTimestamp());
        Assert.assertEquals(5, tFAgentStat2.getFileDescriptor().getOpenFileDescriptorCount(), 0);
        TFAgentStat tFAgentStat3 = tFAgentStatList.get(2);
        Assert.assertEquals(TFAgentStatMapperTest.TEST_AGENT, tFAgentStat3.getAgentId());
        Assert.assertEquals(TFAgentStatMapperTest.startTimestamp, tFAgentStat3.getStartTimestamp());
        Assert.assertEquals(TFAgentStatMapperTest.collectTime3rd, tFAgentStat3.getTimestamp());
        Assert.assertEquals(8, tFAgentStat3.getFileDescriptor().getOpenFileDescriptorCount(), 0);
    }
}

