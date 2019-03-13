/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hbase;


import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.testclassification.IntegrationTests;
import org.apache.hadoop.hbase.util.MultiThreadedUpdater;
import org.apache.hadoop.hbase.util.MultiThreadedWriter;
import org.apache.hadoop.hbase.util.test.LoadTestDataGenerator;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Integration test for testing async wal replication to secondary region replicas. Sets up a table
 * with given region replication (default 2), and uses LoadTestTool client writer, updater and
 * reader threads for writes and reads and verification. It uses a delay queue with a given delay
 * ("read_delay_ms", default 5000ms) between the writer/updater and reader threads to make the
 * written items available to readers. This means that a reader will only start reading from a row
 * written by the writer / updater after 5secs has passed. The reader thread performs the reads from
 * the given region replica id (default 1) to perform the reads. Async wal replication has to finish
 * with the replication of the edits before read_delay_ms to the given region replica id so that
 * the read and verify will not fail.
 *
 * The job will run for <b>at least</b> given runtime (default 10min) by running a concurrent
 * writer and reader workload followed by a concurrent updater and reader workload for
 * num_keys_per_server.
 * <p>
 * Example usage:
 * </p>
 * <pre>
 * hbase org.apache.hadoop.hbase.IntegrationTestRegionReplicaReplication
 * -DIntegrationTestRegionReplicaReplication.num_keys_per_server=10000
 * -Dhbase.IntegrationTestRegionReplicaReplication.runtime=600000
 * -DIntegrationTestRegionReplicaReplication.read_delay_ms=5000
 * -DIntegrationTestRegionReplicaReplication.region_replication=3
 * -DIntegrationTestRegionReplicaReplication.region_replica_id=2
 * -DIntegrationTestRegionReplicaReplication.num_read_threads=100
 * -DIntegrationTestRegionReplicaReplication.num_write_threads=100
 * </pre>
 */
@Category(IntegrationTests.class)
public class IntegrationTestRegionReplicaReplication extends IntegrationTestIngest {
    private static final String TEST_NAME = IntegrationTestRegionReplicaReplication.class.getSimpleName();

    private static final String OPT_READ_DELAY_MS = "read_delay_ms";

    private static final int DEFAULT_REGION_REPLICATION = 2;

    private static final int SERVER_COUNT = 1;// number of slaves for the smallest cluster


    private static final String[] DEFAULT_COLUMN_FAMILIES = new String[]{ "f1", "f2", "f3" };

    @Override
    @Test
    public void testIngest() throws Exception {
        runIngestTest(IntegrationTestIngest.JUNIT_RUN_TIME, 25000, 10, 1024, 10, 20);
    }

    /**
     * This extends MultiThreadedWriter to add a configurable delay to the keys written by the writer
     * threads to become available to the MultiThradedReader threads. We add this delay because of
     * the async nature of the wal replication to region replicas.
     */
    public static class DelayingMultiThreadedWriter extends MultiThreadedWriter {
        private long delayMs;

        public DelayingMultiThreadedWriter(LoadTestDataGenerator dataGen, Configuration conf, TableName tableName) throws IOException {
            super(dataGen, conf, tableName);
        }

        @Override
        protected BlockingQueue<Long> createWriteKeysQueue(Configuration conf) {
            this.delayMs = conf.getLong(String.format("%s.%s", IntegrationTestRegionReplicaReplication.class.getSimpleName(), IntegrationTestRegionReplicaReplication.OPT_READ_DELAY_MS), 5000);
            return new org.apache.hadoop.hbase.util.ConstantDelayQueue(TimeUnit.MILLISECONDS, delayMs);
        }
    }

    /**
     * This extends MultiThreadedWriter to add a configurable delay to the keys written by the writer
     * threads to become available to the MultiThradedReader threads. We add this delay because of
     * the async nature of the wal replication to region replicas.
     */
    public static class DelayingMultiThreadedUpdater extends MultiThreadedUpdater {
        private long delayMs;

        public DelayingMultiThreadedUpdater(LoadTestDataGenerator dataGen, Configuration conf, TableName tableName, double updatePercent) throws IOException {
            super(dataGen, conf, tableName, updatePercent);
        }

        @Override
        protected BlockingQueue<Long> createWriteKeysQueue(Configuration conf) {
            this.delayMs = conf.getLong(String.format("%s.%s", IntegrationTestRegionReplicaReplication.class.getSimpleName(), IntegrationTestRegionReplicaReplication.OPT_READ_DELAY_MS), 5000);
            return new org.apache.hadoop.hbase.util.ConstantDelayQueue(TimeUnit.MILLISECONDS, delayMs);
        }
    }
}

