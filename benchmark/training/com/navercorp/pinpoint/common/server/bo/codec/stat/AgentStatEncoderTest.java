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
package com.navercorp.pinpoint.common.server.bo.codec.stat;


import com.navercorp.pinpoint.common.buffer.Buffer;
import com.navercorp.pinpoint.common.buffer.FixedBuffer;
import com.navercorp.pinpoint.common.server.bo.serializer.stat.AgentStatDecodingContext;
import com.navercorp.pinpoint.common.server.bo.serializer.stat.AgentStatUtils;
import com.navercorp.pinpoint.common.server.bo.stat.AgentStatDataPoint;
import com.navercorp.pinpoint.common.server.bo.stat.AgentStatType;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;


/**
 *
 *
 * @author HyunGil Jeong
 */
public class AgentStatEncoderTest {
    private static final String AGENT_ID = "testAgentId";

    private static final long AGENT_START_TIMESTAMP = System.currentTimeMillis();

    private static final long COLLECT_INTERVAL = 5000L;

    private static final Random RANDOM = new Random();

    private AgentStatCodec<AgentStatEncoderTest.TestAgentStat> codec = new AgentStatEncoderTest.TestAgentStatCodec();

    private AgentStatEncoder<AgentStatEncoderTest.TestAgentStat> encoder = new AgentStatEncoder<AgentStatEncoderTest.TestAgentStat>(codec);

    private AgentStatDecoder<AgentStatEncoderTest.TestAgentStat> decoder = new AgentStatDecoder<AgentStatEncoderTest.TestAgentStat>(Arrays.asList(codec));

    @Test
    public void stats_should_be_encoded_and_decoded_into_same_value() {
        long initialTimestamp = System.currentTimeMillis();
        int numStats = RandomUtils.nextInt(1, 21);
        List<AgentStatEncoderTest.TestAgentStat> expectedAgentStats = this.createTestAgentStats(initialTimestamp, numStats);
        long baseTimestamp = AgentStatUtils.getBaseTimestamp(initialTimestamp);
        long timestampDelta = initialTimestamp - baseTimestamp;
        ByteBuffer qualifierBuffer = encoder.encodeQualifier(timestampDelta);
        ByteBuffer valueBuffer = encoder.encodeValue(expectedAgentStats);
        Buffer encodedQualifierBuffer = new FixedBuffer(qualifierBuffer.array());
        Buffer encodedValueBuffer = new FixedBuffer(valueBuffer.array());
        AgentStatDecodingContext context = new AgentStatDecodingContext();
        context.setAgentId(AgentStatEncoderTest.AGENT_ID);
        context.setBaseTimestamp(baseTimestamp);
        List<AgentStatEncoderTest.TestAgentStat> decodedAgentStats = decode(encodedQualifierBuffer, encodedValueBuffer, context);
        verify(expectedAgentStats, decodedAgentStats);
    }

    private static class TestAgentStatCodec implements AgentStatCodec<AgentStatEncoderTest.TestAgentStat> {
        @Override
        public byte getVersion() {
            return 0;
        }

        @Override
        public void encodeValues(Buffer valueBuffer, List<AgentStatEncoderTest.TestAgentStat> agentStats) {
            valueBuffer.putInt(agentStats.size());
            for (AgentStatEncoderTest.TestAgentStat agentStat : agentStats) {
                valueBuffer.putLong(agentStat.getStartTimestamp());
                valueBuffer.putLong(agentStat.getTimestamp());
                valueBuffer.putLong(agentStat.getValue());
            }
        }

        @Override
        public List<AgentStatEncoderTest.TestAgentStat> decodeValues(Buffer valueBuffer, AgentStatDecodingContext decodingContext) {
            int size = valueBuffer.readInt();
            List<AgentStatEncoderTest.TestAgentStat> agentStats = new ArrayList<AgentStatEncoderTest.TestAgentStat>(size);
            for (int i = 0; i < size; i++) {
                AgentStatEncoderTest.TestAgentStat agentStat = new AgentStatEncoderTest.TestAgentStat();
                agentStat.setAgentId(decodingContext.getAgentId());
                agentStat.setStartTimestamp(valueBuffer.readLong());
                agentStat.setTimestamp(valueBuffer.readLong());
                agentStat.setValue(valueBuffer.readLong());
                agentStats.add(agentStat);
            }
            return agentStats;
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
            return AgentStatType.UNKNOWN;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o)
                return true;

            if ((o == null) || ((getClass()) != (o.getClass())))
                return false;

            AgentStatEncoderTest.TestAgentStat that = ((AgentStatEncoderTest.TestAgentStat) (o));
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

