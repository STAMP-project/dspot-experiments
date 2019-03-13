/**
 * Copyright 2018 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.navercorp.pinpoint.flink.process;


import StatType.APP_STST;
import com.navercorp.pinpoint.flink.mapper.thrift.stat.JoinAgentStatBoMapper;
import com.navercorp.pinpoint.flink.vo.RawData;
import java.util.ArrayList;
import org.apache.flink.api.common.functions.util.ListCollector;
import org.apache.flink.api.java.tuple.Tuple3;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author minwoo.jung
 */
public class TBaseFlatMapperTest {
    static final String AGENT_ID = "testAgent";

    static final String APPLICATION_ID = "testApplication";

    @Test
    public void flatMapTest() throws Exception {
        ApplicationCache applicationCache = newMockApplicationCache();
        TBaseFlatMapper mapper = new TBaseFlatMapper(new JoinAgentStatBoMapper(), applicationCache, new DefaultTBaseFlatMapperInterceptor());
        TFAgentStatBatch tfAgentStatBatch = createTFAgentStatBatch();
        ArrayList<Tuple3<String, JoinStatBo, Long>> dataList = new ArrayList<>();
        ListCollector<Tuple3<String, JoinStatBo, Long>> collector = new ListCollector(dataList);
        RawData rawData = newRawData(tfAgentStatBatch);
        mapper.flatMap(rawData, collector);
        Assert.assertEquals(dataList.size(), 2);
        Tuple3<String, JoinStatBo, Long> data1 = dataList.get(0);
        Assert.assertEquals(data1.f0, TBaseFlatMapperTest.AGENT_ID);
        Assert.assertEquals(data1.f2.longValue(), 1491274143454L);
        JoinAgentStatBo joinAgentStatBo = ((JoinAgentStatBo) (data1.f1));
        Assert.assertEquals(joinAgentStatBo.getId(), TBaseFlatMapperTest.AGENT_ID);
        Assert.assertEquals(joinAgentStatBo.getAgentStartTimestamp(), 1491274142454L);
        Assert.assertEquals(joinAgentStatBo.getTimestamp(), 1491274143454L);
        assertJoinCpuLoadBo(joinAgentStatBo.getJoinCpuLoadBoList());
        Tuple3<String, JoinStatBo, Long> data2 = dataList.get(1);
        Assert.assertEquals(data2.f0, TBaseFlatMapperTest.APPLICATION_ID);
        Assert.assertEquals(data2.f2.longValue(), 1491274140000L);
        JoinApplicationStatBo joinApplicationStatBo = ((JoinApplicationStatBo) (data2.f1));
        Assert.assertEquals(joinApplicationStatBo.getId(), TBaseFlatMapperTest.APPLICATION_ID);
        Assert.assertEquals(joinApplicationStatBo.getTimestamp(), 1491274140000L);
        Assert.assertEquals(joinApplicationStatBo.getStatType(), APP_STST);
        assertJoinCpuLoadBo(joinApplicationStatBo.getJoinCpuLoadBoList());
    }

    @Test
    public void flatMap2Test() throws Exception {
        ApplicationCache applicationCache = newMockApplicationCache();
        TBaseFlatMapper mapper = new TBaseFlatMapper(new JoinAgentStatBoMapper(), applicationCache, new DefaultTBaseFlatMapperInterceptor());
        TFAgentStatBatch tfAgentStatBatch = createTFAgentStatBatch2();
        ArrayList<Tuple3<String, JoinStatBo, Long>> dataList = new ArrayList<>();
        ListCollector<Tuple3<String, JoinStatBo, Long>> collector = new ListCollector(dataList);
        RawData rawdata = newRawData(tfAgentStatBatch);
        mapper.flatMap(rawdata, collector);
        Assert.assertEquals(dataList.size(), 2);
        Tuple3<String, JoinStatBo, Long> data1 = dataList.get(0);
        Assert.assertEquals(data1.f0, TBaseFlatMapperTest.AGENT_ID);
        Assert.assertEquals(data1.f2.longValue(), 1491274143454L);
        JoinAgentStatBo joinAgentStatBo = ((JoinAgentStatBo) (data1.f1));
        Assert.assertEquals(joinAgentStatBo.getId(), TBaseFlatMapperTest.AGENT_ID);
        Assert.assertEquals(joinAgentStatBo.getAgentStartTimestamp(), 1491274142454L);
        Assert.assertEquals(joinAgentStatBo.getTimestamp(), 1491274143454L);
        assertJoinMemoryBo(joinAgentStatBo.getJoinMemoryBoList());
        Tuple3<String, JoinStatBo, Long> data2 = dataList.get(1);
        Assert.assertEquals(data2.f0, TBaseFlatMapperTest.APPLICATION_ID);
        Assert.assertEquals(data2.f2.longValue(), 1491274140000L);
        JoinApplicationStatBo joinApplicationStatBo = ((JoinApplicationStatBo) (data2.f1));
        Assert.assertEquals(joinApplicationStatBo.getId(), TBaseFlatMapperTest.APPLICATION_ID);
        Assert.assertEquals(joinApplicationStatBo.getTimestamp(), 1491274140000L);
        Assert.assertEquals(joinApplicationStatBo.getStatType(), APP_STST);
        assertJoinMemoryBo(joinApplicationStatBo.getJoinMemoryBoList());
    }

    @Test
    public void flatMap3Test() throws Exception {
        ApplicationCache applicationCache = newMockApplicationCache();
        TBaseFlatMapper mapper = new TBaseFlatMapper(new JoinAgentStatBoMapper(), applicationCache, new DefaultTBaseFlatMapperInterceptor());
        TFAgentStatBatch tfAgentStatBatch = createTFAgentStatBatch3();
        ArrayList<Tuple3<String, JoinStatBo, Long>> dataList = new ArrayList<>();
        ListCollector<Tuple3<String, JoinStatBo, Long>> collector = new ListCollector(dataList);
        RawData rawData = newRawData(tfAgentStatBatch);
        mapper.flatMap(rawData, collector);
        Assert.assertEquals(dataList.size(), 2);
        Tuple3<String, JoinStatBo, Long> data1 = dataList.get(0);
        Assert.assertEquals(data1.f0, TBaseFlatMapperTest.AGENT_ID);
        Assert.assertEquals(data1.f2.longValue(), 1491274143454L);
        JoinAgentStatBo joinAgentStatBo = ((JoinAgentStatBo) (data1.f1));
        Assert.assertEquals(joinAgentStatBo.getId(), TBaseFlatMapperTest.AGENT_ID);
        Assert.assertEquals(joinAgentStatBo.getAgentStartTimestamp(), 1491274142454L);
        Assert.assertEquals(joinAgentStatBo.getTimestamp(), 1491274143454L);
        assertJoinTransactionBo(joinAgentStatBo.getJoinTransactionBoList());
        Tuple3<String, JoinStatBo, Long> data2 = dataList.get(1);
        Assert.assertEquals(data2.f0, TBaseFlatMapperTest.APPLICATION_ID);
        Assert.assertEquals(data2.f2.longValue(), 1491274140000L);
        JoinApplicationStatBo joinApplicationStatBo = ((JoinApplicationStatBo) (data2.f1));
        Assert.assertEquals(joinApplicationStatBo.getId(), TBaseFlatMapperTest.APPLICATION_ID);
        Assert.assertEquals(joinApplicationStatBo.getTimestamp(), 1491274140000L);
        Assert.assertEquals(joinApplicationStatBo.getStatType(), APP_STST);
        assertJoinTransactionBo(joinApplicationStatBo.getJoinTransactionBoList());
    }

    @Test
    public void flatMap4Test() throws Exception {
        ApplicationCache applicationCache = newMockApplicationCache();
        TBaseFlatMapper mapper = new TBaseFlatMapper(new JoinAgentStatBoMapper(), applicationCache, new DefaultTBaseFlatMapperInterceptor());
        TFAgentStatBatch tfAgentStatBatch = createTFAgentStatBatch4();
        ArrayList<Tuple3<String, JoinStatBo, Long>> dataList = new ArrayList<>();
        ListCollector<Tuple3<String, JoinStatBo, Long>> collector = new ListCollector(dataList);
        RawData rawData = newRawData(tfAgentStatBatch);
        mapper.flatMap(rawData, collector);
        Assert.assertEquals(dataList.size(), 2);
        Tuple3<String, JoinStatBo, Long> data1 = dataList.get(0);
        Assert.assertEquals(data1.f0, TBaseFlatMapperTest.AGENT_ID);
        Assert.assertEquals(data1.f2.longValue(), 1491274143454L);
        JoinAgentStatBo joinAgentStatBo = ((JoinAgentStatBo) (data1.f1));
        Assert.assertEquals(joinAgentStatBo.getId(), TBaseFlatMapperTest.AGENT_ID);
        Assert.assertEquals(joinAgentStatBo.getAgentStartTimestamp(), 1491274142454L);
        Assert.assertEquals(joinAgentStatBo.getTimestamp(), 1491274143454L);
        assertJoinFileDescriptorBo(joinAgentStatBo.getJoinFileDescriptorBoList());
        Tuple3<String, JoinStatBo, Long> data2 = dataList.get(1);
        Assert.assertEquals(data2.f0, TBaseFlatMapperTest.APPLICATION_ID);
        Assert.assertEquals(data2.f2.longValue(), 1491274140000L);
        JoinApplicationStatBo joinApplicationStatBo = ((JoinApplicationStatBo) (data2.f1));
        Assert.assertEquals(joinApplicationStatBo.getId(), TBaseFlatMapperTest.APPLICATION_ID);
        Assert.assertEquals(joinApplicationStatBo.getTimestamp(), 1491274140000L);
        Assert.assertEquals(joinApplicationStatBo.getStatType(), APP_STST);
        assertJoinFileDescriptorBo(joinApplicationStatBo.getJoinFileDescriptorBoList());
    }

    @Test
    public void flatMap5Test() throws Exception {
        ApplicationCache applicationCache = newMockApplicationCache();
        TBaseFlatMapper mapper = new TBaseFlatMapper(new JoinAgentStatBoMapper(), applicationCache, new DefaultTBaseFlatMapperInterceptor());
        TFAgentStatBatch tfAgentStatBatch = createTFAgentStatBatch5();
        ArrayList<Tuple3<String, JoinStatBo, Long>> dataList = new ArrayList<>();
        ListCollector<Tuple3<String, JoinStatBo, Long>> collector = new ListCollector(dataList);
        RawData rawData = newRawData(tfAgentStatBatch);
        mapper.flatMap(rawData, collector);
        Assert.assertEquals(dataList.size(), 2);
        Tuple3<String, JoinStatBo, Long> data1 = dataList.get(0);
        Assert.assertEquals(data1.f0, TBaseFlatMapperTest.AGENT_ID);
        Assert.assertEquals(data1.f2.longValue(), 1491274143454L);
        JoinAgentStatBo joinAgentStatBo = ((JoinAgentStatBo) (data1.f1));
        Assert.assertEquals(joinAgentStatBo.getId(), TBaseFlatMapperTest.AGENT_ID);
        Assert.assertEquals(joinAgentStatBo.getAgentStartTimestamp(), 1491274142454L);
        Assert.assertEquals(joinAgentStatBo.getTimestamp(), 1491274143454L);
        assertJoinDirectBufferBo(joinAgentStatBo.getJoinDirectBufferBoList());
        Tuple3<String, JoinStatBo, Long> data2 = dataList.get(1);
        Assert.assertEquals(data2.f0, TBaseFlatMapperTest.APPLICATION_ID);
        Assert.assertEquals(data2.f2.longValue(), 1491274140000L);
        JoinApplicationStatBo joinApplicationStatBo = ((JoinApplicationStatBo) (data2.f1));
        Assert.assertEquals(joinApplicationStatBo.getId(), TBaseFlatMapperTest.APPLICATION_ID);
        Assert.assertEquals(joinApplicationStatBo.getTimestamp(), 1491274140000L);
        Assert.assertEquals(joinApplicationStatBo.getStatType(), APP_STST);
        assertJoinDirectBufferBo(joinApplicationStatBo.getJoinDirectBufferBoList());
    }
}

