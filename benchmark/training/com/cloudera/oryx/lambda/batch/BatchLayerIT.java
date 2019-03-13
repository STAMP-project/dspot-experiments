/**
 * Copyright (c) 2014, Cloudera, Inc. All Rights Reserved.
 *
 * Cloudera, Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"). You may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the
 * License.
 */
package com.cloudera.oryx.lambda.batch;


import com.cloudera.oryx.common.settings.ConfigUtils;
import com.typesafe.config.Config;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests {@link BatchLayer}.
 */
public final class BatchLayerIT extends AbstractBatchIT {
    private static final Logger log = LoggerFactory.getLogger(BatchLayerIT.class);

    private static final int DATA_TO_WRITE = 600;

    private static final int WRITE_INTERVAL_MSEC = 20;

    private static final int GEN_INTERVAL_SEC = 3;

    @Test
    public void testBatchLayer() throws Exception {
        Path tempDir = getTempDir();
        Path dataDir = tempDir.resolve("data");
        Map<String, Object> overlayConfig = new HashMap<>();
        overlayConfig.put("oryx.batch.update-class", MockBatchUpdate.class.getName());
        ConfigUtils.set(overlayConfig, "oryx.batch.storage.data-dir", dataDir);
        ConfigUtils.set(overlayConfig, "oryx.batch.storage.model-dir", tempDir.resolve("model"));
        overlayConfig.put("oryx.batch.streaming.generation-interval-sec", BatchLayerIT.GEN_INTERVAL_SEC);
        Config config = ConfigUtils.overlayOn(overlayConfig, getConfig());
        List<IntervalData<String, String>> intervalData = MockBatchUpdate.getIntervalDataHolder();
        intervalData.clear();
        startMessaging();
        startServerProduceConsumeTopics(config, BatchLayerIT.DATA_TO_WRITE, BatchLayerIT.WRITE_INTERVAL_MSEC);
        int numIntervals = intervalData.size();
        BatchLayerIT.log.info("{} intervals: {}", numIntervals, intervalData);
        AbstractBatchIT.checkOutputData(dataDir, BatchLayerIT.DATA_TO_WRITE);
        AbstractBatchIT.checkIntervals(numIntervals, BatchLayerIT.DATA_TO_WRITE, BatchLayerIT.WRITE_INTERVAL_MSEC, BatchLayerIT.GEN_INTERVAL_SEC);
        IntervalData<String, String> last = intervalData.get(0);
        BatchLayerIT.log.info("Interval 0: {}", last);
        for (int i = 1; i < numIntervals; i++) {
            IntervalData<String, String> current = intervalData.get(i);
            BatchLayerIT.log.info("Interval {}: {}", i, current);
            assertGreater(current.getTimestamp(), last.getTimestamp());
            assertGreaterOrEqual(current.getPastData().size(), last.getPastData().size());
            assertEquals(((last.getPastData().size()) + (last.getCurrentData().size())), current.getPastData().size());
            last = current;
        }
    }
}

