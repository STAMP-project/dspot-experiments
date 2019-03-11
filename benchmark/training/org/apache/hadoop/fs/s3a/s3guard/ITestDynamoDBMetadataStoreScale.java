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
package org.apache.hadoop.fs.s3a.s3guard;


import DynamoDBMetadataStore.HINT_DDB_IOPS_TOO_LOW;
import Statistic.S3GUARD_METADATASTORE_RETRY;
import Statistic.S3GUARD_METADATASTORE_THROTTLED;
import StorageStatistics.LongStatistic;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughputDescription;
import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.StorageStatistics;
import org.apache.hadoop.fs.s3a.AWSServiceThrottledException;
import org.apache.hadoop.fs.s3a.S3AFileStatus;
import org.apache.hadoop.fs.s3a.S3AFileSystem;
import org.apache.hadoop.fs.s3a.S3AStorageStatistics;
import org.apache.hadoop.fs.s3a.scale.AbstractITestS3AMetadataStoreScale;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Scale test for DynamoDBMetadataStore.
 *
 * The throttle tests aren't quite trying to verify that throttling can
 * be recovered from, because that makes for very slow tests: you have
 * to overload the system and them have them back of until they finally complete.
 * Instead
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ITestDynamoDBMetadataStoreScale extends AbstractITestS3AMetadataStoreScale {
    private static final Logger LOG = LoggerFactory.getLogger(ITestDynamoDBMetadataStoreScale.class);

    private static final long BATCH_SIZE = 25;

    /**
     * IO Units for batch size; this sets the size to use for IO capacity.
     * Value: {@value }.
     */
    private static final long MAXIMUM_READ_CAPACITY = 10;

    private static final long MAXIMUM_WRITE_CAPACITY = 15;

    private DynamoDBMetadataStore ddbms;

    private DynamoDB ddb;

    private Table table;

    private String tableName;

    /**
     * was the provisioning changed in test_001_limitCapacity()?
     */
    private boolean isOverProvisionedForTest;

    private ProvisionedThroughputDescription originalCapacity;

    private static final int THREADS = 40;

    private static final int OPERATIONS_PER_THREAD = 50;

    /**
     * The subclass expects the superclass to be throttled; sometimes it is.
     */
    @Test
    @Override
    public void test_020_Moves() throws Throwable {
        ITestDynamoDBMetadataStoreScale.ThrottleTracker tracker = new ITestDynamoDBMetadataStoreScale.ThrottleTracker();
        try {
            // if this doesn't throttle, all is well.
            super.test_020_Moves();
        } catch (AWSServiceThrottledException ex) {
            // if the service was throttled, we ex;ect the exception text
            GenericTestUtils.assertExceptionContains(HINT_DDB_IOPS_TOO_LOW, ex, "Expected throttling message");
        } finally {
            ITestDynamoDBMetadataStoreScale.LOG.info("Statistics {}", tracker);
        }
    }

    /**
     * Though the AWS SDK claims in documentation to handle retries and
     * exponential backoff, we have witnessed
     * com.amazonaws...dynamodbv2.model.ProvisionedThroughputExceededException
     * (Status Code: 400; Error Code: ProvisionedThroughputExceededException)
     * Hypothesis:
     * Happens when the size of a batched write is bigger than the number of
     * provisioned write units.  This test ensures we handle the case
     * correctly, retrying w/ smaller batch instead of surfacing exceptions.
     */
    @Test
    public void test_030_BatchedWrite() throws Exception {
        final int iterations = 15;
        final ArrayList<PathMetadata> toCleanup = new ArrayList<>();
        toCleanup.ensureCapacity(((ITestDynamoDBMetadataStoreScale.THREADS) * iterations));
        // Fail if someone changes a constant we depend on
        assertTrue("Maximum batch size must big enough to run this test", ((S3GUARD_DDB_BATCH_WRITE_REQUEST_LIMIT) >= (ITestDynamoDBMetadataStoreScale.BATCH_SIZE)));
        // We know the dynamodb metadata store will expand a put of a path
        // of depth N into a batch of N writes (all ancestors are written
        // separately up to the root).  (Ab)use this for an easy way to write
        // a batch of stuff that is bigger than the provisioned write units
        try {
            describe("Running %d iterations of batched put, size %d", iterations, ITestDynamoDBMetadataStoreScale.BATCH_SIZE);
            ITestDynamoDBMetadataStoreScale.ThrottleTracker result = execute("prune", 1, true, () -> {
                org.apache.hadoop.fs.s3a.s3guard.ThrottleTracker tracker = new org.apache.hadoop.fs.s3a.s3guard.ThrottleTracker();
                long pruneItems = 0;
                for (long i = 0; i < iterations; i++) {
                    Path longPath = pathOfDepth(BATCH_SIZE, String.valueOf(i));
                    FileStatus status = basicFileStatus(longPath, 0, false, 12345, 12345);
                    PathMetadata pm = new PathMetadata(status);
                    synchronized(toCleanup) {
                        toCleanup.add(pm);
                    }
                    ddbms.put(pm);
                    pruneItems++;
                    if (pruneItems == (BATCH_SIZE)) {
                        describe("pruning files");
                        /* all files */
                        ddbms.prune(Long.MAX_VALUE);
                        pruneItems = 0;
                    }
                    if (tracker.probe()) {
                        // fail fast
                        break;
                    }
                }
            });
            assertNotEquals(("No batch retries in " + result), 0, result.batchThrottles);
        } finally {
            describe("Cleaning up table %s", tableName);
            for (PathMetadata pm : toCleanup) {
                cleanupMetadata(ddbms, pm);
            }
        }
    }

    /**
     * Test Get throttling including using
     * {@link MetadataStore#get(Path, boolean)},
     * as that stresses more of the code.
     */
    @Test
    public void test_040_get() throws Throwable {
        // attempt to create many many get requests in parallel.
        Path path = new Path("s3a://example.org/get");
        S3AFileStatus status = new S3AFileStatus(true, path, "alice");
        PathMetadata metadata = new PathMetadata(status);
        ddbms.put(metadata);
        try {
            execute("get", ITestDynamoDBMetadataStoreScale.OPERATIONS_PER_THREAD, true, () -> ddbms.get(path, true));
        } finally {
            retryingDelete(path);
        }
    }

    /**
     * Ask for the version marker, which is where table init can be overloaded.
     */
    @Test
    public void test_050_getVersionMarkerItem() throws Throwable {
        execute("get", ((ITestDynamoDBMetadataStoreScale.OPERATIONS_PER_THREAD) * 2), true, () -> ddbms.getVersionMarkerItem());
    }

    @Test
    public void test_060_list() throws Throwable {
        // attempt to create many many get requests in parallel.
        Path path = new Path("s3a://example.org/list");
        S3AFileStatus status = new S3AFileStatus(true, path, "alice");
        PathMetadata metadata = new PathMetadata(status);
        ddbms.put(metadata);
        try {
            Path parent = path.getParent();
            execute("list", ITestDynamoDBMetadataStoreScale.OPERATIONS_PER_THREAD, true, () -> ddbms.listChildren(parent));
        } finally {
            retryingDelete(path);
        }
    }

    @Test
    public void test_070_putDirMarker() throws Throwable {
        // attempt to create many many get requests in parallel.
        Path path = new Path("s3a://example.org/putDirMarker");
        S3AFileStatus status = new S3AFileStatus(true, path, "alice");
        PathMetadata metadata = new PathMetadata(status);
        ddbms.put(metadata);
        DirListingMetadata children = ddbms.listChildren(path.getParent());
        try {
            execute("list", ITestDynamoDBMetadataStoreScale.OPERATIONS_PER_THREAD, true, () -> ddbms.put(children));
        } finally {
            retryingDelete(path);
        }
    }

    @Test
    public void test_080_fullPathsToPut() throws Throwable {
        // attempt to create many many get requests in parallel.
        Path base = new Path("s3a://example.org/test_080_fullPathsToPut");
        Path child = new Path(base, "child");
        List<PathMetadata> pms = new ArrayList<>();
        ddbms.put(new PathMetadata(AbstractITestS3AMetadataStoreScale.makeDirStatus(base)));
        ddbms.put(new PathMetadata(AbstractITestS3AMetadataStoreScale.makeDirStatus(child)));
        ddbms.getInvoker().retry("set up directory tree", base.toString(), true, () -> ddbms.put(pms));
        try {
            DDBPathMetadata dirData = ddbms.get(child, true);
            execute("list", ITestDynamoDBMetadataStoreScale.OPERATIONS_PER_THREAD, true, () -> ddbms.fullPathsToPut(dirData));
        } finally {
            retryingDelete(base);
        }
    }

    @Test
    public void test_900_instrumentation() throws Throwable {
        describe("verify the owner FS gets updated after throttling events");
        // we rely on the FS being shared
        S3AFileSystem fs = getFileSystem();
        String fsSummary = fs.toString();
        S3AStorageStatistics statistics = fs.getStorageStatistics();
        for (StorageStatistics.LongStatistic statistic : statistics) {
            ITestDynamoDBMetadataStoreScale.LOG.info("{}", statistic.toString());
        }
        String retryKey = S3GUARD_METADATASTORE_RETRY.getSymbol();
        assertTrue(((("No increment of " + retryKey) + " in ") + fsSummary), ((statistics.getLong(retryKey)) > 0));
        String throttledKey = S3GUARD_METADATASTORE_THROTTLED.getSymbol();
        assertTrue(((("No increment of " + throttledKey) + " in ") + fsSummary), ((statistics.getLong(throttledKey)) > 0));
    }

    /**
     * Something to track throttles.
     * The constructor sets the counters to the current count in the
     * DDB table; a call to {@link #reset()} will set it to the latest values.
     * The {@link #probe()} will pick up the latest values to compare them with
     * the original counts.
     */
    private class ThrottleTracker {
        private long writeThrottleEventOrig = ddbms.getWriteThrottleEventCount();

        private long readThrottleEventOrig = ddbms.getReadThrottleEventCount();

        private long batchWriteThrottleCountOrig = ddbms.getBatchWriteCapacityExceededCount();

        private long readThrottles;

        private long writeThrottles;

        private long batchThrottles;

        ThrottleTracker() {
            reset();
        }

        /**
         * Reset the counters.
         */
        private synchronized void reset() {
            writeThrottleEventOrig = ddbms.getWriteThrottleEventCount();
            readThrottleEventOrig = ddbms.getReadThrottleEventCount();
            batchWriteThrottleCountOrig = ddbms.getBatchWriteCapacityExceededCount();
        }

        /**
         * Update the latest throttle count; synchronized.
         *
         * @return true if throttling has been detected.
         */
        private synchronized boolean probe() {
            readThrottles = (ddbms.getReadThrottleEventCount()) - (readThrottleEventOrig);
            writeThrottles = (ddbms.getWriteThrottleEventCount()) - (writeThrottleEventOrig);
            batchThrottles = (ddbms.getBatchWriteCapacityExceededCount()) - (batchWriteThrottleCountOrig);
            return isThrottlingDetected();
        }

        @Override
        public String toString() {
            return String.format(("Tracker with read throttle events = %d;" + (" write events = %d;" + " batch throttles = %d")), readThrottles, writeThrottles, batchThrottles);
        }

        /**
         * Assert that throttling has been detected.
         */
        void assertThrottlingDetected() {
            assertTrue(((("No throttling detected in " + (this)) + " against ") + (ddbms.toString())), isThrottlingDetected());
        }

        /**
         * Has there been any throttling on an operation?
         *
         * @return true iff read, write or batch operations were throttled.
         */
        private boolean isThrottlingDetected() {
            return (((readThrottles) > 0) || ((writeThrottles) > 0)) || ((batchThrottles) > 0);
        }
    }

    /**
     * Outcome of a thread's execution operation.
     */
    private static class ExecutionOutcome {
        private int completed;

        private int throttled;

        private boolean skipped;

        private final List<Exception> exceptions = new ArrayList<>(1);

        private final List<Exception> throttleExceptions = new ArrayList<>(1);

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("ExecutionOutcome{");
            sb.append("completed=").append(completed);
            sb.append(", skipped=").append(skipped);
            sb.append(", throttled=").append(throttled);
            sb.append(", exception count=").append(exceptions.size());
            sb.append('}');
            return sb.toString();
        }
    }
}

