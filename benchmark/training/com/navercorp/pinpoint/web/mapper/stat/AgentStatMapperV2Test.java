/**
 * Copyright 2016 Naver Corp.
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
package com.navercorp.pinpoint.web.mapper.stat;


import AgentStatMapperV2.REVERSE_TIMESTAMP_COMPARATOR;
import HBaseTables.AGENT_STAT_CF_STATISTICS;
import com.navercorp.pinpoint.common.buffer.Buffer;
import com.navercorp.pinpoint.common.hbase.distributor.RangeOneByteSimpleHash;
import com.navercorp.pinpoint.common.server.bo.codec.stat.AgentStatCodec;
import com.navercorp.pinpoint.common.server.bo.codec.stat.AgentStatDecoder;
import com.navercorp.pinpoint.common.server.bo.codec.stat.AgentStatEncoder;
import com.navercorp.pinpoint.common.server.bo.serializer.stat.AgentStatDecodingContext;
import com.navercorp.pinpoint.common.server.bo.serializer.stat.AgentStatHbaseOperationFactory;
import com.navercorp.pinpoint.common.server.bo.serializer.stat.AgentStatRowKeyDecoder;
import com.navercorp.pinpoint.common.server.bo.serializer.stat.AgentStatRowKeyEncoder;
import com.navercorp.pinpoint.common.server.bo.serializer.stat.AgentStatSerializer;
import com.navercorp.pinpoint.common.server.bo.stat.AgentStatDataPoint;
import com.navercorp.pinpoint.common.server.bo.stat.AgentStatType;
import com.navercorp.pinpoint.web.mapper.TimestampFilter;
import com.sematext.hbase.wd.AbstractRowKeyDistributor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.apache.commons.lang3.RandomUtils;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author HyunGil Jeong
 */
public class AgentStatMapperV2Test {
    private static final int MAX_NUM_TEST_VALUES = 10 + 1;// Random API's upper bound field is exclusive


    private static final String AGENT_ID = "testAgent";

    private static final AgentStatType AGENT_STAT_TYPE = AgentStatType.UNKNOWN;

    private static final long COLLECT_INVERVAL = 5000L;

    private static final Random RANDOM = new Random();

    private static final TimestampFilter TEST_FILTER = new TimestampFilter() {
        @Override
        public boolean filter(long timestamp) {
            return false;
        }
    };

    private final AgentStatRowKeyEncoder rowKeyEncoder = new AgentStatRowKeyEncoder();

    private final AgentStatRowKeyDecoder rowKeyDecoder = new AgentStatRowKeyDecoder();

    private final AbstractRowKeyDistributor rowKeyDistributor = new com.sematext.hbase.wd.RowKeyDistributorByHashPrefix(new RangeOneByteSimpleHash(0, 33, 64));

    private final AgentStatHbaseOperationFactory hbaseOperationFactory = new AgentStatHbaseOperationFactory(this.rowKeyEncoder, this.rowKeyDecoder, this.rowKeyDistributor);

    private final AgentStatCodec<AgentStatMapperV2Test.TestAgentStat> codec = new AgentStatMapperV2Test.TestAgentStatCodec();

    private final AgentStatEncoder<AgentStatMapperV2Test.TestAgentStat> encoder = new AgentStatMapperV2Test.TestAgentStatEncoder(this.codec);

    private final AgentStatDecoder<AgentStatMapperV2Test.TestAgentStat> decoder = new AgentStatMapperV2Test.TestAgentStatDecoder(this.codec);

    private final AgentStatSerializer<AgentStatMapperV2Test.TestAgentStat> serializer = new AgentStatMapperV2Test.TestAgentStatSerializer(this.encoder);

    @Test
    public void mapperTest() throws Exception {
        // Given
        List<AgentStatMapperV2Test.TestAgentStat> givenAgentStats = new ArrayList<>();
        List<Put> puts = new ArrayList<>();
        long initialTimestamp = System.currentTimeMillis();
        int numBatch = RandomUtils.nextInt(1, AgentStatMapperV2Test.MAX_NUM_TEST_VALUES);
        for (int i = 0; i < numBatch; i++) {
            int batchSize = RandomUtils.nextInt(1, AgentStatMapperV2Test.MAX_NUM_TEST_VALUES);
            List<AgentStatMapperV2Test.TestAgentStat> agentStatBatch = createAgentStats(initialTimestamp, AgentStatMapperV2Test.COLLECT_INVERVAL, batchSize);
            givenAgentStats.addAll(agentStatBatch);
            puts.addAll(this.hbaseOperationFactory.createPuts(AgentStatMapperV2Test.AGENT_ID, AgentStatMapperV2Test.AGENT_STAT_TYPE, agentStatBatch, this.serializer));
            initialTimestamp += batchSize * (AgentStatMapperV2Test.COLLECT_INVERVAL);
        }
        List<Cell> cellsToPut = new ArrayList<>();
        for (Put put : puts) {
            List<Cell> cells = put.getFamilyCellMap().get(AGENT_STAT_CF_STATISTICS);
            cellsToPut.addAll(cells);
        }
        Result result = Result.create(cellsToPut);
        // When
        AgentStatMapperV2<AgentStatMapperV2Test.TestAgentStat> mapper = new AgentStatMapperV2(this.hbaseOperationFactory, this.decoder, AgentStatMapperV2Test.TEST_FILTER);
        List<AgentStatMapperV2Test.TestAgentStat> mappedAgentStats = mapper.mapRow(result, 0);
        // Then
        givenAgentStats.sort(REVERSE_TIMESTAMP_COMPARATOR);
        Assert.assertEquals(givenAgentStats, mappedAgentStats);
    }

    private static class TestAgentStatCodec implements AgentStatCodec<AgentStatMapperV2Test.TestAgentStat> {
        @Override
        public byte getVersion() {
            return 0;
        }

        @Override
        public void encodeValues(Buffer valueBuffer, List<AgentStatMapperV2Test.TestAgentStat> agentStats) {
            valueBuffer.putInt(agentStats.size());
            for (AgentStatMapperV2Test.TestAgentStat agentStat : agentStats) {
                valueBuffer.putLong(agentStat.getTimestamp());
                valueBuffer.putLong(agentStat.getValue());
            }
        }

        @Override
        public List<AgentStatMapperV2Test.TestAgentStat> decodeValues(Buffer valueBuffer, AgentStatDecodingContext decodingContext) {
            int size = valueBuffer.readInt();
            List<AgentStatMapperV2Test.TestAgentStat> agentStats = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                AgentStatMapperV2Test.TestAgentStat agentStat = new AgentStatMapperV2Test.TestAgentStat();
                agentStat.setAgentId(decodingContext.getAgentId());
                agentStat.setTimestamp(valueBuffer.readLong());
                agentStat.setValue(valueBuffer.readLong());
                agentStats.add(agentStat);
            }
            return agentStats;
        }
    }

    private static class TestAgentStatEncoder extends AgentStatEncoder<AgentStatMapperV2Test.TestAgentStat> {
        protected TestAgentStatEncoder(AgentStatCodec<AgentStatMapperV2Test.TestAgentStat> codec) {
            super(codec);
        }
    }

    private static class TestAgentStatDecoder extends AgentStatDecoder<AgentStatMapperV2Test.TestAgentStat> {
        protected TestAgentStatDecoder(AgentStatCodec<AgentStatMapperV2Test.TestAgentStat> codec) {
            super(Arrays.asList(codec));
        }
    }

    private static class TestAgentStatSerializer extends AgentStatSerializer<AgentStatMapperV2Test.TestAgentStat> {
        protected TestAgentStatSerializer(AgentStatEncoder<AgentStatMapperV2Test.TestAgentStat> encoder) {
            super(encoder);
        }
    }

    private static class TestAgentStat implements AgentStatDataPoint {
        private String agentId;

        private long startTimestamp;

        private long timestamp;

        private long value;

        @Override
        public String getAgentId() {
            return this.agentId;
        }

        @Override
        public void setAgentId(String agentId) {
            this.agentId = agentId;
        }

        @Override
        public long getStartTimestamp() {
            return startTimestamp;
        }

        @Override
        public void setStartTimestamp(long startTimestamp) {
            this.startTimestamp = startTimestamp;
        }

        @Override
        public long getTimestamp() {
            return this.timestamp;
        }

        @Override
        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        public long getValue() {
            return this.value;
        }

        public void setValue(long value) {
            this.value = value;
        }

        @Override
        public AgentStatType getAgentStatType() {
            return AgentStatMapperV2Test.AGENT_STAT_TYPE;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o)
                return true;

            if ((o == null) || ((getClass()) != (o.getClass())))
                return false;

            AgentStatMapperV2Test.TestAgentStat that = ((AgentStatMapperV2Test.TestAgentStat) (o));
            if ((startTimestamp) != (that.startTimestamp))
                return false;

            if ((timestamp) != (that.timestamp))
                return false;

            if ((value) != (that.value))
                return false;

            return (agentId) != null ? agentId.equals(that.agentId) : (that.agentId) == null;
        }

        @Override
        public int hashCode() {
            int result = ((agentId) != null) ? agentId.hashCode() : 0;
            result = (31 * result) + ((int) ((startTimestamp) ^ ((startTimestamp) >>> 32)));
            result = (31 * result) + ((int) ((timestamp) ^ ((timestamp) >>> 32)));
            result = (31 * result) + ((int) ((value) ^ ((value) >>> 32)));
            return result;
        }

        @Override
        public String toString() {
            return ((((((((("TestAgentStat{" + "agentId='") + (agentId)) + '\'') + ", startTimestamp=") + (startTimestamp)) + ", timestamp=") + (timestamp)) + ", value=") + (value)) + '}';
        }
    }
}

