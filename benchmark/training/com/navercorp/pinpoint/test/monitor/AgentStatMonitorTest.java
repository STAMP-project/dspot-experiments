/**
 * Copyright 2016 NAVER Corp.
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
package com.navercorp.pinpoint.test.monitor;


import com.navercorp.pinpoint.profiler.monitor.AgentStatMonitor;
import com.navercorp.pinpoint.profiler.monitor.collector.AgentStatMetricCollector;
import com.navercorp.pinpoint.profiler.sender.DataSender;
import com.navercorp.pinpoint.test.TBaseRecorder;
import com.navercorp.pinpoint.thrift.dto.TAgentStat;
import com.navercorp.pinpoint.thrift.dto.TAgentStatBatch;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 *
 * @author HyunGil Jeong
 */
public class AgentStatMonitorTest {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private TBaseRecorder<TAgentStatBatch> tBaseRecorder;

    private DataSender dataSender;

    @Mock
    private AgentStatMetricCollector<TAgentStat> agentStatCollector;

    @Test
    public void testAgentStatMonitor() throws InterruptedException {
        // Given
        final long collectionIntervalMs = 1000 * 1;
        final int numCollectionsPerBatch = 2;
        final int minNumBatchToTest = 2;
        final long totalTestDurationMs = collectionIntervalMs + ((collectionIntervalMs * numCollectionsPerBatch) * minNumBatchToTest);
        // When
        AgentStatMonitor monitor = new com.navercorp.pinpoint.profiler.monitor.DefaultAgentStatMonitor(this.dataSender, "agentId", System.currentTimeMillis(), agentStatCollector, collectionIntervalMs, numCollectionsPerBatch);
        monitor.start();
        Thread.sleep(totalTestDurationMs);
        monitor.stop();
        // Then
        Assert.assertTrue(((tBaseRecorder.size()) >= minNumBatchToTest));
        for (TAgentStatBatch agentStatBatch : tBaseRecorder) {
            logger.debug("agentStatBatch:{}", agentStatBatch);
            Assert.assertTrue(((agentStatBatch.getAgentStats().size()) <= numCollectionsPerBatch));
        }
    }
}

