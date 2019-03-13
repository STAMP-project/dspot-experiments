/**
 * Copyright 2017 LinkedIn Corp. All rights reserved.
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
 */
package com.github.ambry.store;


import Account.UNKNOWN_ACCOUNT_ID;
import BlobStoreStats.IO_SCHEDULER_JOB_TYPE;
import Container.UNKNOWN_CONTAINER_ID;
import StatsReportType.ACCOUNT_REPORT;
import StatsReportType.PARTITION_CLASS_REPORT;
import StoreErrorCodes.Store_Shutting_Down;
import Utils.Infinite_Time;
import com.codahale.metrics.MetricRegistry;
import com.github.ambry.server.StatsReportType;
import com.github.ambry.server.StatsSnapshot;
import com.github.ambry.utils.MockTime;
import com.github.ambry.utils.SystemTime;
import com.github.ambry.utils.Throttler;
import com.github.ambry.utils.Time;
import com.github.ambry.utils.Utils;
import com.github.ambry.utils.UtilsTest;
import java.io.File;
import java.io.IOException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Tests for {@link BlobStoreStats}. Tests both segmented and non segmented log use cases.
 */
@RunWith(Parameterized.class)
public class BlobStoreStatsTest {
    private static final long TEST_TIME_INTERVAL_IN_MS = (CuratedLogIndexState.DELAY_BETWEEN_LAST_MODIFIED_TIMES_MS) / 2;

    private static final long BUCKET_SPAN_IN_MS = Time.MsPerSec;

    private static final long QUEUE_PROCESSOR_PERIOD_IN_Ms = 100;

    private static final StoreMetrics METRICS = new StoreMetrics(new MetricRegistry());

    private static final long DEFAULT_WAIT_TIMEOUT_SECS = Time.SecsPerMin;

    private final Map<String, Throttler> throttlers = new HashMap<>();

    private final DiskIOScheduler diskIOScheduler = new DiskIOScheduler(throttlers);

    private final ScheduledExecutorService indexScannerScheduler = Utils.newScheduler(1, true);

    private final ScheduledExecutorService queueProcessorScheduler = Utils.newScheduler(1, true);

    private final boolean bucketingEnabled;

    private final boolean isLogSegmented;

    private CuratedLogIndexState state;

    private File tempDir;

    /**
     * Creates a temporary directory and sets up some test state.
     *
     * @throws IOException
     * 		
     */
    public BlobStoreStatsTest(boolean isLogSegmented, boolean isBucketingEnabled) throws StoreException, IOException {
        tempDir = StoreTestUtils.createTempDirectory(("blobStoreStatsDir-" + (UtilsTest.getRandomString(10))));
        state = new CuratedLogIndexState(isLogSegmented, tempDir, true);
        bucketingEnabled = isBucketingEnabled;
        this.isLogSegmented = isLogSegmented;
    }

    /**
     * Test {@link BlobStoreStats} can be initialized (with bucketing enabled/disabled) and closed properly.
     *
     * @throws InterruptedException
     * 		
     */
    @Test
    public void testInitializationAndClose() throws InterruptedException {
        int bucketCount = (bucketingEnabled) ? 1 : 0;
        final CountDownLatch scanStartedLatch = new CountDownLatch(1);
        BlobStoreStatsTest.MockThrottler mockThrottler = new BlobStoreStatsTest.MockThrottler(scanStartedLatch, new CountDownLatch(0));
        throttlers.put(IO_SCHEDULER_JOB_TYPE, mockThrottler);
        BlobStoreStats blobStoreStats = setupBlobStoreStats(bucketCount, 0);
        if (bucketingEnabled) {
            // IndexScanner should be started if bucketing is enabled
            Assert.assertTrue("IndexScanner took too long to start", scanStartedLatch.await(5, TimeUnit.SECONDS));
        } else {
            // IndexScanner should not be started if bucketing is disabled
            Assert.assertTrue("IndexScanner should not be started", ((mockThrottler.throttleCount.get()) == 0));
        }
        blobStoreStats.close();
    }

    /**
     * Basic test to verify reported valid size information per container by BlobStoreStats.
     *
     * @throws StoreException
     * 		
     */
    @Test
    public void testContainerValidDataSize() throws StoreException {
        Assume.assumeTrue((!(bucketingEnabled)));
        BlobStoreStats blobStoreStats = setupBlobStoreStats(0, 0);
        verifyAndGetContainerValidSize(blobStoreStats, state.time.milliseconds());
        // advance time
        advanceTimeToNextSecond();
        verifyAndGetContainerValidSize(blobStoreStats, state.time.milliseconds());
        blobStoreStats.close();
    }

    /**
     * Basic test to verify reported valid size information per log segment by BlobStoreStats.
     *
     * @throws StoreException
     * 		
     */
    @Test
    public void testLogSegmentValidDataSize() throws StoreException {
        Assume.assumeTrue((!(bucketingEnabled)));
        BlobStoreStats blobStoreStats = setupBlobStoreStats(0, 0);
        long currentTimeInMs = state.time.milliseconds();
        for (long i = 0; i <= (currentTimeInMs + (BlobStoreStatsTest.TEST_TIME_INTERVAL_IN_MS)); i += BlobStoreStatsTest.TEST_TIME_INTERVAL_IN_MS) {
            TimeRange timeRange = new TimeRange(i, 0L);
            verifyAndGetLogSegmentValidSize(blobStoreStats, timeRange);
        }
        blobStoreStats.close();
    }

    /**
     * Tests to verify the correctness of reported stats after new puts via the following steps:
     * 1. Verify reported stats and record the total valid size prior to adding the new puts.
     * 2. Add new puts.
     * 3. Verify reported stats and record the total valid size after new puts are added.
     * 4. Verify the delta of total valid size prior to adding the new puts and after matches with the expected delta.
     *
     * @throws StoreException
     * 		
     * @throws IOException
     * 		
     */
    @Test
    public void testValidDataSizeAfterPuts() throws StoreException, IOException {
        Assume.assumeTrue((!(bucketingEnabled)));
        BlobStoreStats blobStoreStats = setupBlobStoreStats(0, 0);
        // advance time to the next second for deletes/expiration to take effect
        advanceTimeToNextSecond();
        long timeInMsBeforePuts = state.time.milliseconds();
        long totalLogSegmentValidSizeBeforePuts = verifyAndGetLogSegmentValidSize(blobStoreStats, new TimeRange(timeInMsBeforePuts, 0L));
        long totalContainerValidSizeBeforePuts = verifyAndGetContainerValidSize(blobStoreStats, timeInMsBeforePuts);
        // 3 puts
        state.addPutEntries(3, CuratedLogIndexState.PUT_RECORD_SIZE, Infinite_Time);
        long timeInMsAfterPuts = state.time.milliseconds();
        long totalLogSegmentValidSizeAfterPuts = verifyAndGetLogSegmentValidSize(blobStoreStats, new TimeRange(timeInMsAfterPuts, 0L));
        long totalContainerValidSizeAfterPuts = verifyAndGetContainerValidSize(blobStoreStats, timeInMsAfterPuts);
        long expectedIncrement = 3 * (CuratedLogIndexState.PUT_RECORD_SIZE);
        Assert.assertEquals("Put entries are not properly counted for log segment valid size", totalLogSegmentValidSizeAfterPuts, (totalLogSegmentValidSizeBeforePuts + expectedIncrement));
        Assert.assertEquals("Put entries are not properly counted for container valid size", totalContainerValidSizeAfterPuts, (totalContainerValidSizeBeforePuts + expectedIncrement));
        blobStoreStats.close();
    }

    /**
     * Tests to verify the correctness of reported stats with puts that is going to expire via the following steps:
     * 1. Verify reported stats and record the total valid size before adding the new expiring puts.
     * 2. Add new expiring and non-expiring puts.
     * 3. Verify the new puts are being reported correctly.
     * 4. Advance time to let the expiration take effect.
     * 5. Verify reported stats and record the total valid size after new puts are expired.
     * 6. Verify the reported total valid size difference before the new puts and after.
     *
     * @throws StoreException
     * 		
     * @throws IOException
     * 		
     */
    @Test
    public void testValidDataSizeAfterExpiration() throws StoreException, IOException {
        Assume.assumeTrue((!(bucketingEnabled)));
        BlobStoreStats blobStoreStats = setupBlobStoreStats(0, 0);
        // advance time to the next second for previous deletes/expiration to take effect
        advanceTimeToNextSecond();
        long timeInMsBeforePuts = state.time.milliseconds();
        long totalLogSegmentValidSizeBeforePuts = verifyAndGetLogSegmentValidSize(blobStoreStats, new TimeRange(timeInMsBeforePuts, 0L));
        long totalContainerValidSizeBeforePuts = verifyAndGetContainerValidSize(blobStoreStats, state.time.milliseconds());
        // 1 put with no expiry
        state.addPutEntries(1, CuratedLogIndexState.PUT_RECORD_SIZE, Infinite_Time);
        // 3 puts that will expire in 20 seconds (note the two puts should be in the same index segment)
        long expiresAtInMs = (state.time.milliseconds()) + (TimeUnit.SECONDS.toMillis(20));
        state.addPutEntries(3, CuratedLogIndexState.PUT_RECORD_SIZE, expiresAtInMs);
        // advance time to exactly the time of expiration, all new puts should still be valid
        state.advanceTime((expiresAtInMs - (state.time.milliseconds())));
        long expectedDeltaAfterPut = 4 * (CuratedLogIndexState.PUT_RECORD_SIZE);
        long timeInMsAfterPuts = state.time.milliseconds();
        long totalLogSegmentValidSizeAfterPuts = verifyAndGetLogSegmentValidSize(blobStoreStats, new TimeRange(timeInMsAfterPuts, 0L));
        long totalContainerValidSizeAfterPuts = verifyAndGetContainerValidSize(blobStoreStats, timeInMsAfterPuts);
        Assert.assertEquals("Put entries with expiry are not properly counted for log segment valid size", totalLogSegmentValidSizeAfterPuts, (totalLogSegmentValidSizeBeforePuts + expectedDeltaAfterPut));
        Assert.assertEquals("Put entries with expiry are not properly counted for container valid size", totalContainerValidSizeAfterPuts, (totalContainerValidSizeBeforePuts + expectedDeltaAfterPut));
        // advance time to the next second for expiration to take effect
        advanceTimeToNextSecond();
        long expectedDeltaAfterExpiration = CuratedLogIndexState.PUT_RECORD_SIZE;
        long timeInMsAfterExpiration = state.time.milliseconds();
        long totalLogSegmentValidSizeAfterExpiration = verifyAndGetLogSegmentValidSize(blobStoreStats, new TimeRange(timeInMsAfterExpiration, 0L));
        long totalContainerValidSizeAfterExpiration = verifyAndGetContainerValidSize(blobStoreStats, timeInMsAfterExpiration);
        Assert.assertEquals("Expired put entries are not properly counted for log segment valid size", totalLogSegmentValidSizeAfterExpiration, (totalLogSegmentValidSizeBeforePuts + expectedDeltaAfterExpiration));
        Assert.assertEquals("Expired put entries are not properly counted for container valid size", totalContainerValidSizeAfterExpiration, (totalContainerValidSizeBeforePuts + expectedDeltaAfterExpiration));
        blobStoreStats.close();
    }

    /**
     * Tests to verify the correctness of reported stats with after new deletes via the following steps:
     * 1. Add new puts that are going to be deleted later.
     * 2. Verify reported stats and record the total valid size before new deletes.
     * 3. Perform the deletes.
     * 4. Verify reported stats after the deletes but at a time point before the deletes are relevant.
     * 5. Verify reported stats and record the total valid size after the deletes.
     * 6. Verify the delta of total valid size prior to the new deletes and after matches with the expected delta.
     *
     * @throws StoreException
     * 		
     * @throws IOException
     * 		
     */
    @Test
    public void testValidDataSizeAfterDeletes() throws StoreException, IOException {
        Assume.assumeTrue((!(bucketingEnabled)));
        BlobStoreStats blobStoreStats = setupBlobStoreStats(0, 0);
        int numEntries = (((state.getMaxInMemElements()) - (state.referenceIndex.lastEntry().getValue().size())) + (state.getMaxInMemElements())) - 2;
        state.addPutEntries(numEntries, CuratedLogIndexState.PUT_RECORD_SIZE, Infinite_Time);
        long timeInMsBeforeDeletes = state.time.milliseconds();
        long totalLogSegmentValidSizeBeforeDeletes = verifyAndGetLogSegmentValidSize(blobStoreStats, new TimeRange(timeInMsBeforeDeletes, 0L));
        long totalContainerValidSizeBeforeDeletes = verifyAndGetContainerValidSize(blobStoreStats, timeInMsBeforeDeletes);
        // advance time to the next seconds before adding the deletes
        advanceTimeToNextSecond();
        // 2 deletes from the last index segment
        state.addDeleteEntry(state.getIdToDeleteFromIndexSegment(state.referenceIndex.lastKey(), false));
        state.addDeleteEntry(state.getIdToDeleteFromIndexSegment(state.referenceIndex.lastKey(), false));
        long expectedDeltaBeforeDeletesRelevant = 2 * (CuratedLogIndexState.DELETE_RECORD_SIZE);
        long totalLogSegmentValidSizeBeforeDeletesRelevant = verifyAndGetLogSegmentValidSize(blobStoreStats, new TimeRange(timeInMsBeforeDeletes, 0L));
        long totalContainerValidSizeBeforeDeletesRelevant = verifyAndGetContainerValidSize(blobStoreStats, timeInMsBeforeDeletes);
        Assert.assertEquals("Delete entries are not properly counted for log segment valid size", totalLogSegmentValidSizeBeforeDeletesRelevant, (totalLogSegmentValidSizeBeforeDeletes + expectedDeltaBeforeDeletesRelevant));
        Assert.assertEquals("Delete entries are not properly counted for container valid size", totalContainerValidSizeBeforeDeletesRelevant, totalContainerValidSizeBeforeDeletes);
        // advance time to the next second for deletes/expiration to take effect
        advanceTimeToNextSecond();
        long timeInMsAfterDeletes = state.time.milliseconds();
        long totalLogSegmentValidSizeAfterDeletes = verifyAndGetLogSegmentValidSize(blobStoreStats, new TimeRange(timeInMsAfterDeletes, 0L));
        long totalContainerValidSizeAfterDeletes = verifyAndGetContainerValidSize(blobStoreStats, timeInMsAfterDeletes);
        long expectedLogSegmentDecrement = 2 * ((CuratedLogIndexState.PUT_RECORD_SIZE) - (CuratedLogIndexState.DELETE_RECORD_SIZE));
        long expectedContainerDecrement = 2 * (CuratedLogIndexState.PUT_RECORD_SIZE);
        Assert.assertEquals("Delete entries are not properly counted for log segment valid size", totalLogSegmentValidSizeAfterDeletes, (totalLogSegmentValidSizeBeforeDeletes - expectedLogSegmentDecrement));
        Assert.assertEquals("Delete entries are not properly counted for container valid size", totalContainerValidSizeAfterDeletes, (totalContainerValidSizeBeforeDeletes - expectedContainerDecrement));
        blobStoreStats.close();
    }

    /**
     * Basic test to verify that the {@link BlobStoreStats} can scan the index, populate the buckets and use these buckets
     * to report stats correctly.
     *
     * @throws StoreException
     * 		
     * @throws InterruptedException
     * 		
     * @throws IOException
     * 		
     */
    @Test
    public void testBucketingBasic() throws StoreException, IOException, InterruptedException {
        Assume.assumeTrue(bucketingEnabled);
        final CountDownLatch scanStartedLatch = new CountDownLatch(1);
        BlobStoreStatsTest.MockThrottler mockThrottler = new BlobStoreStatsTest.MockThrottler(scanStartedLatch, new CountDownLatch(0));
        throttlers.put(IO_SCHEDULER_JOB_TYPE, mockThrottler);
        long logSegmentForecastOffsetMs = state.time.milliseconds();
        int bucketCount = 2 * ((int) (logSegmentForecastOffsetMs / (BlobStoreStatsTest.BUCKET_SPAN_IN_MS)));
        long expiresAtInMs = (((long) (bucketCount)) - 2) * (BlobStoreStatsTest.BUCKET_SPAN_IN_MS);
        // add 3 puts with expiry
        state.addPutEntries(3, CuratedLogIndexState.PUT_RECORD_SIZE, expiresAtInMs);
        int expectedThrottleCount = state.referenceIndex.size();
        long logSegmentForecastStartTimeInMs = (state.time.milliseconds()) - logSegmentForecastOffsetMs;
        long logSegmentForecastEndTimeInMs = logSegmentForecastStartTimeInMs + (bucketCount * (BlobStoreStatsTest.BUCKET_SPAN_IN_MS));
        long containerForecastEndTimeInMs = (state.time.milliseconds()) + (bucketCount * (BlobStoreStatsTest.BUCKET_SPAN_IN_MS));
        BlobStoreStats blobStoreStats = setupBlobStoreStats(bucketCount, logSegmentForecastOffsetMs);
        // proceed only when the scan is started
        Assert.assertTrue("IndexScanner took too long to start", scanStartedLatch.await(5, TimeUnit.SECONDS));
        verifyAndGetContainerValidSize(blobStoreStats, state.time.milliseconds());
        for (long i = logSegmentForecastStartTimeInMs; i <= ((state.time.milliseconds()) + (BlobStoreStatsTest.TEST_TIME_INTERVAL_IN_MS)); i += BlobStoreStatsTest.TEST_TIME_INTERVAL_IN_MS) {
            verifyAndGetLogSegmentValidSize(blobStoreStats, new TimeRange(i, 0L));
        }
        // advance time to let the added puts to expire
        long timeToLiveInMs = ((expiresAtInMs - (state.time.milliseconds())) < 0) ? 0 : expiresAtInMs - (state.time.milliseconds());
        state.advanceTime((timeToLiveInMs + (Time.MsPerSec)));
        for (long i = logSegmentForecastStartTimeInMs; i <= ((state.time.milliseconds()) + (BlobStoreStatsTest.TEST_TIME_INTERVAL_IN_MS)); i += BlobStoreStatsTest.TEST_TIME_INTERVAL_IN_MS) {
            verifyAndGetLogSegmentValidSize(blobStoreStats, new TimeRange(i, 0L));
        }
        verifyAndGetContainerValidSize(blobStoreStats, state.time.milliseconds());
        // advance time near the end of log segment forecast time
        state.advanceTime(((logSegmentForecastEndTimeInMs - (state.time.milliseconds())) - 1));
        verifyAndGetLogSegmentValidSize(blobStoreStats, new TimeRange(state.time.milliseconds(), Time.MsPerSec));
        // advance time near the end of container forecast time
        state.advanceTime(((containerForecastEndTimeInMs - (state.time.milliseconds())) - 1));
        verifyAndGetContainerValidSize(blobStoreStats, state.time.milliseconds());
        Assert.assertEquals("Throttle count mismatch from expected value", expectedThrottleCount, mockThrottler.throttleCount.get());
        blobStoreStats.close();
    }

    /**
     * Tests to verify requests inside and outside of the forecast coverage can still be served properly while scanning.
     *
     * @throws InterruptedException
     * 		
     * @throws StoreException
     * 		
     * @throws IOException
     * 		
     */
    @Test
    public void testBucketingCoverageTransition() throws StoreException, IOException, InterruptedException {
        Assume.assumeTrue(bucketingEnabled);
        CountDownLatch scanStartedLatch = new CountDownLatch(1);
        // do not hold the initial scan
        BlobStoreStatsTest.MockThrottler mockThrottler = new BlobStoreStatsTest.MockThrottler(scanStartedLatch, new CountDownLatch(0), 50, 100);
        throttlers.put(IO_SCHEDULER_JOB_TYPE, mockThrottler);
        // add a put that is going to expire in 20 seconds
        long expiresAtInMs = (state.time.milliseconds()) + (TimeUnit.SECONDS.toMillis(20));
        state.addPutEntries(1, CuratedLogIndexState.PUT_RECORD_SIZE, expiresAtInMs);
        long initialScanTimeInMs = state.time.milliseconds();
        BlobStoreStats blobStoreStats = setupBlobStoreStats(1, 0);
        int expectedThrottleCount = state.referenceIndex.size();
        // proceed only when the scan is started
        Assert.assertTrue("IndexScanner took too long to start", scanStartedLatch.await(5, TimeUnit.SECONDS));
        verifyAndGetLogSegmentValidSize(blobStoreStats, new TimeRange(initialScanTimeInMs, 0L));
        verifyAndGetContainerValidSize(blobStoreStats, initialScanTimeInMs);
        state.advanceTime((expiresAtInMs - (state.time.milliseconds())));
        // hold the next scan
        CountDownLatch scanHoldLatch = new CountDownLatch(1);
        scanStartedLatch = new CountDownLatch(1);
        mockThrottler.holdLatch = scanHoldLatch;
        mockThrottler.startedLatch = scanStartedLatch;
        mockThrottler.isThrottlerStarted = false;
        Assert.assertTrue("IndexScanner took too long to start", scanStartedLatch.await(5, TimeUnit.SECONDS));
        verifyAndGetLogSegmentValidSize(blobStoreStats, new TimeRange(initialScanTimeInMs, 0L));
        verifyAndGetContainerValidSize(blobStoreStats, initialScanTimeInMs);
        // expectedThrottleCount + 1 because the next scan already started and the throttle count is incremented
        Assert.assertEquals("Throttle count mismatch from expected value", (expectedThrottleCount + 1), mockThrottler.throttleCount.get());
        // request something outside of the forecast coverage
        verifyAndGetLogSegmentValidSize(blobStoreStats, new TimeRange(0L, 0L));
        verifyAndGetContainerValidSize(blobStoreStats, 0L);
        // resume the scan and make a request that will wait for the scan to complete
        scanHoldLatch.countDown();
        verifyAndGetContainerValidSize(blobStoreStats, state.time.milliseconds());
        verifyAndGetLogSegmentValidSize(blobStoreStats, new TimeRange(state.time.milliseconds(), 0L));
        // first two are from the two bucketing scans and the later three are from the requests that are outside of
        // forecast coverage
        expectedThrottleCount = (2 + 3) * expectedThrottleCount;
        Assert.assertEquals("Throttle count mismatch from expected value", expectedThrottleCount, mockThrottler.throttleCount.get());
        blobStoreStats.close();
    }

    @Test
    public void testWithLowIndexEntries() throws StoreException, IOException, InterruptedException {
        state.destroy();
        Assert.assertTrue(((tempDir.getAbsolutePath()) + " could not be deleted"), StoreTestUtils.cleanDirectory(tempDir, true));
        tempDir = StoreTestUtils.createTempDirectory(("blobStoreStatsDir-" + (UtilsTest.getRandomString(10))));
        state = new CuratedLogIndexState(isLogSegmented, tempDir, false, false, true);
        int bucketCount = (bucketingEnabled) ? 1 : 0;
        BlobStoreStats blobStoreStats = setupBlobStoreStats(bucketCount, 0);
        verifyAndGetContainerValidSize(blobStoreStats, state.time.milliseconds());
        verifyAndGetLogSegmentValidSize(blobStoreStats, new TimeRange(state.time.milliseconds(), 0));
        blobStoreStats.close();
        state.addPutEntries(3, CuratedLogIndexState.PUT_RECORD_SIZE, Infinite_Time);
        blobStoreStats = setupBlobStoreStats(bucketCount, 0);
        verifyAndGetContainerValidSize(blobStoreStats, state.time.milliseconds());
        verifyAndGetLogSegmentValidSize(blobStoreStats, new TimeRange(state.time.milliseconds(), 0));
        blobStoreStats.close();
    }

    /**
     * Test to verify that separate scan is triggered to answer the request after waiting for scan timed out.
     *
     * @throws InterruptedException
     * 		
     * @throws StoreException
     * 		
     */
    @Test(timeout = 5000)
    public void testBucketingWaitTimeout() throws StoreException, InterruptedException {
        Assume.assumeTrue(bucketingEnabled);
        CountDownLatch scanStartedLatch = new CountDownLatch(1);
        CountDownLatch scanHoldLatch = new CountDownLatch(1);
        BlobStoreStatsTest.MockThrottler mockThrottler = new BlobStoreStatsTest.MockThrottler(scanStartedLatch, scanHoldLatch);
        throttlers.put(IO_SCHEDULER_JOB_TYPE, mockThrottler);
        int expectedMinimumThrottleCount = 2 * (state.referenceIndex.size());
        BlobStoreStats blobStoreStats = new BlobStoreStats("", state.index, 10, BlobStoreStatsTest.BUCKET_SPAN_IN_MS, 0, BlobStoreStatsTest.QUEUE_PROCESSOR_PERIOD_IN_Ms, 1, state.time, indexScannerScheduler, queueProcessorScheduler, diskIOScheduler, BlobStoreStatsTest.METRICS);
        // proceed only when the scan is started
        Assert.assertTrue("IndexScanner took too long to start", scanStartedLatch.await(5, TimeUnit.SECONDS));
        advanceTimeToNextSecond();
        verifyAndGetContainerValidSize(blobStoreStats, state.time.milliseconds());
        verifyAndGetLogSegmentValidSize(blobStoreStats, new TimeRange(state.time.milliseconds(), 0));
        scanHoldLatch.countDown();
        Assert.assertTrue("Throttle count is lower than the expected minimum throttle count", ((mockThrottler.throttleCount.get()) >= expectedMinimumThrottleCount));
        blobStoreStats.close();
    }

    /**
     * Test to verify that once the {@link BlobStoreStats} is closed (or closing), requests throw {@link StoreException}.
     */
    @Test(timeout = 1000)
    public void testRequestOnClosing() {
        int bucketCount = (bucketingEnabled) ? 1 : 0;
        BlobStoreStats blobStoreStats = setupBlobStoreStats(bucketCount, 0);
        blobStoreStats.close();
        try {
            verifyAndGetContainerValidSize(blobStoreStats, state.time.milliseconds());
            Assert.fail("Expected StoreException thrown upon request when BlobStoreStats is closing");
        } catch (StoreException e) {
            Assert.assertEquals("Mismatch on expected error code", Store_Shutting_Down, e.getErrorCode());
        }
        try {
            verifyAndGetLogSegmentValidSize(blobStoreStats, new TimeRange(state.time.milliseconds(), 0));
            Assert.fail("Expected StoreException thrown upon request when BlobStoreStats is closing");
        } catch (StoreException e) {
            Assert.assertEquals("Mismatch on expected error code", Store_Shutting_Down, e.getErrorCode());
        }
    }

    /**
     * Test to verify {@link BlobStoreStats} is resolving the given {@link TimeRange} correctly and the appropriate action
     * is taken. That is, use the readily available {@link ScanResults} to answer the request or trigger a on demand scan.
     * Specifically the following cases are tested (plus cases when boundaries touch each other):
     *    [_______]  [_______]     [________]   [______]              [______]  <--- forecast range
     *  [___]      ,       [___] ,   [___]    ,         [___] , [___]           <--- given time range
     *
     * @throws InterruptedException
     * 		
     */
    @Test
    public void testTimeRangeResolutionWithStats() throws StoreException, InterruptedException {
        Assume.assumeTrue(bucketingEnabled);
        CountDownLatch scanStartedLatch = new CountDownLatch(1);
        BlobStoreStatsTest.MockThrottler mockThrottler = new BlobStoreStatsTest.MockThrottler(scanStartedLatch, new CountDownLatch(0));
        throttlers.put(IO_SCHEDULER_JOB_TYPE, mockThrottler);
        long logSegmentForecastStartTimeMs = state.time.milliseconds();
        // advance time to ensure the log segment forecast start time is > 0
        state.advanceTime(20000);
        long logSegmentForecastEndTimeMs = (10 * (BlobStoreStatsTest.BUCKET_SPAN_IN_MS)) + logSegmentForecastStartTimeMs;
        BlobStoreStats blobStoreStats = setupBlobStoreStats(10, 20000);
        // proceed only when the scan is started
        Assert.assertTrue("IndexScanner took too long to start", scanStartedLatch.await(5, TimeUnit.SECONDS));
        // ensure the scan is complete before proceeding
        verifyAndGetContainerValidSize(blobStoreStats, state.time.milliseconds());
        int throttleCountBeforeRequests = mockThrottler.throttleCount.get();
        TimeRange timeRange = new TimeRange(logSegmentForecastStartTimeMs, Time.MsPerSec);
        Assert.assertEquals("Unexpected collection time", timeRange.getEndTimeInMs(), blobStoreStats.getValidDataSizeByLogSegment(timeRange).getFirst().longValue());
        timeRange = new TimeRange(logSegmentForecastEndTimeMs, Time.MsPerSec);
        Assert.assertEquals("Unexpected collection time", (logSegmentForecastEndTimeMs - (BlobStoreStatsTest.BUCKET_SPAN_IN_MS)), blobStoreStats.getValidDataSizeByLogSegment(timeRange).getFirst().longValue());
        timeRange = new TimeRange(((logSegmentForecastStartTimeMs + logSegmentForecastEndTimeMs) / 2), Time.MsPerSec);
        Assert.assertEquals("Unexpected collection time", timeRange.getEndTimeInMs(), blobStoreStats.getValidDataSizeByLogSegment(timeRange).getFirst().longValue());
        // time range end time is equal to the start of forecast range
        timeRange = new TimeRange((logSegmentForecastStartTimeMs - (Time.MsPerSec)), Time.MsPerSec);
        Assert.assertEquals("Unexpected collection time", timeRange.getEndTimeInMs(), blobStoreStats.getValidDataSizeByLogSegment(timeRange).getFirst().longValue());
        // all previous time range are inside the forecast range
        Assert.assertEquals("Throttle count mismatch from expected value", throttleCountBeforeRequests, mockThrottler.throttleCount.get());
        // time range start time is equal the end of forecast range (considered to be outside of forecast range)
        timeRange = new TimeRange((logSegmentForecastEndTimeMs + (Time.MsPerSec)), Time.MsPerSec);
        Assert.assertEquals("Unexpected collection time", timeRange.getEndTimeInMs(), blobStoreStats.getValidDataSizeByLogSegment(timeRange).getFirst().longValue());
        timeRange = new TimeRange((logSegmentForecastEndTimeMs + (TimeUnit.SECONDS.toMillis(5))), Time.MsPerSec);
        Assert.assertEquals("Unexpected collection time", timeRange.getEndTimeInMs(), blobStoreStats.getValidDataSizeByLogSegment(timeRange).getFirst().longValue());
        timeRange = new TimeRange((logSegmentForecastStartTimeMs - (TimeUnit.SECONDS.toMillis(5))), Time.MsPerSec);
        Assert.assertEquals("Unexpected collection time", timeRange.getEndTimeInMs(), blobStoreStats.getValidDataSizeByLogSegment(timeRange).getFirst().longValue());
        blobStoreStats.close();
    }

    /**
     * Test the static method that converts the quota stats stored in a nested Map to an {@link StatsSnapshot} object.
     * This test verifies both {@link com.github.ambry.store.BlobStoreStats#convertStoreUsageToAccountStatsSnapshot(Map)} and
     * {@link com.github.ambry.store.BlobStoreStats#convertStoreUsageToContainerStatsSnapshot(Map)}
     */
    @Test
    public void testConvertStoreUsageToStatsSnapshot() {
        Random random = new Random();
        Map<String, Map<String, Long>> utilizationMap = new HashMap<>();
        Map<String, StatsSnapshot> accountSubMap = new HashMap<>();
        Map<String, StatsSnapshot> accountContainerPairSubMap = new HashMap<>();
        long total = 0;
        for (int i = 0; i < 10; i++) {
            Map<String, StatsSnapshot> containerSubMap = new HashMap<>();
            Map<String, Long> innerUtilizationMap = new HashMap<>();
            long subTotal = 0;
            for (int j = 0; j < 3; j++) {
                long randValue = random.nextInt(10000);
                subTotal += randValue;
                innerUtilizationMap.put(String.valueOf(j), randValue);
                containerSubMap.put(String.valueOf(j), new StatsSnapshot(randValue, null));
                accountContainerPairSubMap.put((((String.valueOf(i)) + (Utils.ACCOUNT_CONTAINER_SEPARATOR)) + (String.valueOf(j))), new StatsSnapshot(randValue, null));
            }
            total += subTotal;
            utilizationMap.put(String.valueOf(i), innerUtilizationMap);
            accountSubMap.put(String.valueOf(i), new StatsSnapshot(subTotal, containerSubMap));
        }
        StatsSnapshot expectAccountSnapshot = new StatsSnapshot(total, accountSubMap);
        StatsSnapshot convertedAccountStatsSnapshot = BlobStoreStats.convertStoreUsageToAccountStatsSnapshot(utilizationMap);
        Assert.assertTrue("Mismatch between the converted Account StatsSnapshot and expected StatsSnapshot", expectAccountSnapshot.equals(convertedAccountStatsSnapshot));
        StatsSnapshot convertedContainerStatsSnapshot = BlobStoreStats.convertStoreUsageToContainerStatsSnapshot(utilizationMap);
        StatsSnapshot expectContainerSnapshot = new StatsSnapshot(total, accountContainerPairSubMap);
        Assert.assertTrue("Mismatch between the converted Container StatsSnapshot and expected StatsSnapshot", expectContainerSnapshot.equals(convertedContainerStatsSnapshot));
    }

    /**
     * Test the getStatsSnapshots method by verifying the returned {@link StatsSnapshot} against the original quota {@link Map}.
     */
    @Test
    public void testGetStatsSnapshots() throws StoreException {
        BlobStoreStats blobStoreStats = setupBlobStoreStats(0, 0);
        long deleteAndExpirationRefTimeInMs = state.time.milliseconds();
        Map<String, Map<String, Long>> utilizationMap = blobStoreStats.getValidDataSizeByContainer(deleteAndExpirationRefTimeInMs);
        // Verify account stats snapshot
        Map<StatsReportType, StatsSnapshot> snapshotsByType = blobStoreStats.getStatsSnapshots(EnumSet.of(ACCOUNT_REPORT), deleteAndExpirationRefTimeInMs);
        verifyStatsSnapshots(utilizationMap, snapshotsByType, EnumSet.of(ACCOUNT_REPORT));
        // Verify partition class stats snapshot
        snapshotsByType = blobStoreStats.getStatsSnapshots(EnumSet.of(PARTITION_CLASS_REPORT), deleteAndExpirationRefTimeInMs);
        verifyStatsSnapshots(utilizationMap, snapshotsByType, EnumSet.of(PARTITION_CLASS_REPORT));
        // Verify all types of stats snapshots
        Map<StatsReportType, StatsSnapshot> allStatsSnapshots = blobStoreStats.getStatsSnapshots(EnumSet.allOf(StatsReportType.class), deleteAndExpirationRefTimeInMs);
        verifyStatsSnapshots(utilizationMap, allStatsSnapshots, EnumSet.allOf(StatsReportType.class));
    }

    /**
     * Mock {@link Throttler} with latches and a counter to track and control various states during a scan.
     */
    private class MockThrottler extends Throttler {
        final AtomicInteger throttleCount = new AtomicInteger(0);

        volatile boolean isThrottlerStarted = false;

        CountDownLatch startedLatch;

        CountDownLatch holdLatch;

        CountDownLatch throttleCountLatch;

        CountDownLatch throttleCountHoldLatch;

        int throttleCountAtInterest;

        MockThrottler(CountDownLatch startedLatch, CountDownLatch holdLatch, double desiredRatePerSec, long checkIntervalMs) {
            super(desiredRatePerSec, checkIntervalMs, true, SystemTime.getInstance());
            this.startedLatch = startedLatch;
            this.holdLatch = holdLatch;
        }

        MockThrottler(CountDownLatch startedLatch, CountDownLatch holdLatch) {
            this(startedLatch, holdLatch, null, null, 0);
        }

        MockThrottler(CountDownLatch startedLatch, CountDownLatch holdLatch, CountDownLatch throttleCountLatch, CountDownLatch throttleCountHoldLatch, int throttleCountAtInterest) {
            super(0, 0, true, new MockTime());
            this.startedLatch = startedLatch;
            this.holdLatch = holdLatch;
            this.throttleCountLatch = throttleCountLatch;
            this.throttleCountHoldLatch = throttleCountHoldLatch;
            this.throttleCountAtInterest = throttleCountAtInterest;
        }

        @Override
        public void maybeThrottle(double observed) throws InterruptedException {
            throttleCount.incrementAndGet();
            if (!(isThrottlerStarted)) {
                isThrottlerStarted = true;
                startedLatch.countDown();
                Assert.assertTrue("IndexScanner is held for too long", holdLatch.await(5, TimeUnit.SECONDS));
            }
            if (((throttleCountLatch) != null) && ((throttleCount.get()) == (throttleCountAtInterest))) {
                throttleCountLatch.countDown();
                Assert.assertTrue("IndexScanner is held for too long", throttleCountHoldLatch.await(5, TimeUnit.SECONDS));
            }
        }
    }

    /**
     * Mock {@link IndexValue} with a latch to act as a probe to inform us about the state of the newEntryQueue in
     * {@link BlobStoreStats}.
     */
    private class MockIndexValue extends IndexValue {
        private final CountDownLatch latch;

        MockIndexValue(CountDownLatch latch, Offset offset) {
            super(0, offset, Infinite_Time, Infinite_Time, UNKNOWN_ACCOUNT_ID, UNKNOWN_CONTAINER_ID);
            this.latch = latch;
        }

        @Override
        Offset getOffset() {
            latch.countDown();
            return super.getOffset();
        }
    }
}

