/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.internal.cache;


import org.apache.geode.Statistics;
import org.junit.Test;


/**
 * Unit tests for {@link CachePerfStats}.
 */
public class CachePerfStatsTest {
    private static final String TEXT_ID = "cachePerfStats";

    private static final long CLOCK_TIME = 10;

    private Statistics statistics;

    private CachePerfStats cachePerfStats;

    @Test
    public void getPutsDelegatesToStatistics() {
        statistics.incLong(CachePerfStats.putsId, Long.MAX_VALUE);
        assertThat(cachePerfStats.getPuts()).isEqualTo(Long.MAX_VALUE);
    }

    /**
     * Characterization test: Note that the only way to increment {@code puts} is to invoke {@code endPut}.
     */
    @Test
    public void endPutIncrementsPuts() {
        cachePerfStats.endPut(0, false);
        assertThat(statistics.getLong(CachePerfStats.putsId)).isEqualTo(1L);
    }

    /**
     * Characterization test: {@code puts} currently wraps to negative from max long value.
     */
    @Test
    public void putsWrapsFromMaxLongToNegativeValue() {
        statistics.incLong(CachePerfStats.putsId, Long.MAX_VALUE);
        cachePerfStats.endPut(0, false);
        assertThat(cachePerfStats.getPuts()).isNegative();
    }

    @Test
    public void getGetsDelegatesToStatistics() {
        statistics.incLong(CachePerfStats.getsId, Long.MAX_VALUE);
        assertThat(cachePerfStats.getGets()).isEqualTo(Long.MAX_VALUE);
    }

    /**
     * Characterization test: Note that the only way to increment {@code gets} is to invoke {@code endGet}.
     */
    @Test
    public void endGetIncrementsGets() {
        cachePerfStats.endGet(0, false);
        assertThat(statistics.getLong(CachePerfStats.getsId)).isEqualTo(1L);
    }

    /**
     * Characterization test: {@code gets} currently wraps to negative from max long value.
     */
    @Test
    public void getsWrapsFromMaxLongToNegativeValue() {
        statistics.incLong(CachePerfStats.getsId, Long.MAX_VALUE);
        cachePerfStats.endGet(0, false);
        assertThat(cachePerfStats.getGets()).isNegative();
    }

    @Test
    public void getPutTimeDelegatesToStatistics() {
        statistics.incLong(CachePerfStats.putTimeId, Long.MAX_VALUE);
        assertThat(cachePerfStats.getPutTime()).isEqualTo(Long.MAX_VALUE);
    }

    /**
     * Characterization test: Note that the only way to increment {@code putTime} is to invoke {@code endPut}.
     */
    @Test
    public void endPutIncrementsPutTime() {
        cachePerfStats.endPut(0, false);
        assertThat(statistics.getLong(CachePerfStats.putTimeId)).isEqualTo(CachePerfStatsTest.CLOCK_TIME);
    }

    @Test
    public void getGetTimeDelegatesToStatistics() {
        statistics.incLong(CachePerfStats.getTimeId, Long.MAX_VALUE);
        assertThat(cachePerfStats.getGetTime()).isEqualTo(Long.MAX_VALUE);
    }

    /**
     * Characterization test: Note that the only way to increment {@code getTime} is to invoke {@code endGet}.
     */
    @Test
    public void endGetIncrementsGetTime() {
        cachePerfStats.endGet(0, false);
        assertThat(statistics.getLong(CachePerfStats.getTimeId)).isEqualTo(CachePerfStatsTest.CLOCK_TIME);
    }

    @Test
    public void getDestroysDelegatesToStatistics() {
        statistics.incLong(CachePerfStats.destroysId, Long.MAX_VALUE);
        assertThat(cachePerfStats.getDestroys()).isEqualTo(Long.MAX_VALUE);
    }

    @Test
    public void incDestroysIncrementsDestroys() {
        cachePerfStats.incDestroys();
        assertThat(statistics.getLong(CachePerfStats.destroysId)).isEqualTo(1L);
    }

    /**
     * Characterization test: {@code destroys} currently wraps to negative from max long value.
     */
    @Test
    public void destroysWrapsFromMaxLongToNegativeValue() {
        statistics.incLong(CachePerfStats.destroysId, Long.MAX_VALUE);
        cachePerfStats.incDestroys();
        assertThat(cachePerfStats.getDestroys()).isNegative();
    }

    @Test
    public void getCreatesDelegatesToStatistics() {
        statistics.incLong(CachePerfStats.createsId, Long.MAX_VALUE);
        assertThat(cachePerfStats.getCreates()).isEqualTo(Long.MAX_VALUE);
    }

    @Test
    public void incCreatesIncrementsCreates() {
        cachePerfStats.incCreates();
        assertThat(statistics.getLong(CachePerfStats.createsId)).isEqualTo(1L);
    }

    /**
     * Characterization test: {@code creates} currently wraps to negative from max long value.
     */
    @Test
    public void createsWrapsFromMaxLongToNegativeValue() {
        statistics.incLong(CachePerfStats.createsId, Long.MAX_VALUE);
        cachePerfStats.incCreates();
        assertThat(cachePerfStats.getCreates()).isNegative();
    }

    @Test
    public void getPutAllsDelegatesToStatistics() {
        statistics.incInt(CachePerfStats.putAllsId, Integer.MAX_VALUE);
        assertThat(cachePerfStats.getPutAlls()).isEqualTo(Integer.MAX_VALUE);
    }

    /**
     * Characterization test: Note that the only way to increment {@code putalls} is to invoke {@code endPutAll}.
     */
    @Test
    public void endPutAllIncrementsPutAlls() {
        cachePerfStats.endPutAll(0);
        assertThat(statistics.getInt(CachePerfStats.putAllsId)).isEqualTo(1);
    }

    /**
     * Characterization test: {@code putAlls} currently wraps to negative from max integer value.
     */
    @Test
    public void putAllsWrapsFromMaxIntegerToNegativeValue() {
        statistics.incInt(CachePerfStats.putAllsId, Integer.MAX_VALUE);
        cachePerfStats.endPutAll(0);
        assertThat(cachePerfStats.getPutAlls()).isNegative();
    }

    @Test
    public void getRemoveAllsDelegatesToStatistics() {
        statistics.incInt(CachePerfStats.removeAllsId, Integer.MAX_VALUE);
        assertThat(cachePerfStats.getRemoveAlls()).isEqualTo(Integer.MAX_VALUE);
    }

    /**
     * Characterization test: Note that the only way to increment {@code removeAlls} is to invoke
     * {@code endRemoveAll}.
     */
    @Test
    public void endRemoveAllIncrementsRemoveAll() {
        cachePerfStats.endRemoveAll(0);
        assertThat(statistics.getInt(CachePerfStats.removeAllsId)).isEqualTo(1);
    }

    /**
     * Characterization test: {@code removeAlls} currently wraps to negative from max integer value.
     */
    @Test
    public void removeAllsWrapsFromMaxIntegerToNegativeValue() {
        statistics.incInt(CachePerfStats.removeAllsId, Integer.MAX_VALUE);
        cachePerfStats.endRemoveAll(0);
        assertThat(cachePerfStats.getRemoveAlls()).isNegative();
    }

    @Test
    public void getUpdatesDelegatesToStatistics() {
        statistics.incLong(CachePerfStats.updatesId, Long.MAX_VALUE);
        assertThat(cachePerfStats.getUpdates()).isEqualTo(Long.MAX_VALUE);
    }

    /**
     * Characterization test: Note that the only way to increment {@code updates} is to invoke {@code endPut}.
     */
    @Test
    public void endPutIncrementsUpdates() {
        cachePerfStats.endPut(0, true);
        assertThat(statistics.getLong(CachePerfStats.updatesId)).isEqualTo(1L);
    }

    /**
     * Characterization test: {@code updates} currently wraps to negative from max long value.
     */
    @Test
    public void updatesWrapsFromMaxLongToNegativeValue() {
        statistics.incLong(CachePerfStats.updatesId, Long.MAX_VALUE);
        cachePerfStats.endPut(0, true);
        assertThat(cachePerfStats.getUpdates()).isNegative();
    }

    @Test
    public void getInvalidatesDelegatesToStatistics() {
        statistics.incLong(CachePerfStats.invalidatesId, Long.MAX_VALUE);
        assertThat(cachePerfStats.getInvalidates()).isEqualTo(Long.MAX_VALUE);
    }

    @Test
    public void incInvalidatesIncrementsInvalidates() {
        cachePerfStats.incInvalidates();
        assertThat(statistics.getLong(CachePerfStats.invalidatesId)).isEqualTo(1L);
    }

    /**
     * Characterization test: {@code invalidates} currently wraps to negative from max long value.
     */
    @Test
    public void invalidatesWrapsFromMaxLongToNegativeValue() {
        statistics.incLong(CachePerfStats.invalidatesId, Long.MAX_VALUE);
        cachePerfStats.incInvalidates();
        assertThat(cachePerfStats.getInvalidates()).isNegative();
    }

    @Test
    public void getMissesDelegatesToStatistics() {
        statistics.incLong(CachePerfStats.missesId, Long.MAX_VALUE);
        assertThat(cachePerfStats.getMisses()).isEqualTo(Long.MAX_VALUE);
    }

    /**
     * Characterization test: Note that the only way to increment {@code misses} is to invoke {@code endGet}.
     */
    @Test
    public void endGetIncrementsMisses() {
        cachePerfStats.endGet(0, true);
        assertThat(statistics.getLong(CachePerfStats.missesId)).isEqualTo(1L);
    }

    /**
     * Characterization test: {@code misses} currently wraps to negative from max long value.
     */
    @Test
    public void missesWrapsFromMaxLongToNegativeValue() {
        statistics.incLong(CachePerfStats.missesId, Long.MAX_VALUE);
        cachePerfStats.endGet(0, true);
        assertThat(cachePerfStats.getMisses()).isNegative();
    }

    @Test
    public void getRetriesDelegatesToStatistics() {
        statistics.incInt(CachePerfStats.retriesId, Integer.MAX_VALUE);
        assertThat(cachePerfStats.getRetries()).isEqualTo(Integer.MAX_VALUE);
    }

    @Test
    public void incRetriesIncrementsRetries() {
        cachePerfStats.incRetries();
        assertThat(statistics.getInt(CachePerfStats.retriesId)).isEqualTo(1);
    }

    /**
     * Characterization test: {@code retries} currently wraps to negative from max integer value.
     */
    @Test
    public void retriesWrapsFromMaxIntegerToNegativeValue() {
        statistics.incInt(CachePerfStats.retriesId, Integer.MAX_VALUE);
        cachePerfStats.incRetries();
        assertThat(cachePerfStats.getRetries()).isNegative();
    }

    @Test
    public void getClearsDelegatesToStatistics() {
        statistics.incLong(CachePerfStats.clearsId, Long.MAX_VALUE);
        assertThat(cachePerfStats.getClearCount()).isEqualTo(Long.MAX_VALUE);
    }

    @Test
    public void incClearCountIncrementsClears() {
        cachePerfStats.incClearCount();
        assertThat(statistics.getLong(CachePerfStats.clearsId)).isEqualTo(1L);
    }

    /**
     * Characterization test: {@code clears} currently wraps to negative from max long value.
     */
    @Test
    public void clearsWrapsFromMaxLongToNegativeValue() {
        statistics.incLong(CachePerfStats.clearsId, Long.MAX_VALUE);
        cachePerfStats.incClearCount();
        assertThat(cachePerfStats.getClearCount()).isNegative();
    }

    @Test
    public void getLoadsCompletedDelegatesToStatistics() {
        statistics.incInt(CachePerfStats.loadsCompletedId, Integer.MAX_VALUE);
        assertThat(cachePerfStats.getLoadsCompleted()).isEqualTo(Integer.MAX_VALUE);
    }

    /**
     * Characterization test: Note that the only way to increment {@code loadsCompleted} is to invoke
     * {@code endLoad}.
     */
    @Test
    public void endLoadIncrementsMisses() {
        cachePerfStats.endLoad(0);
        assertThat(statistics.getInt(CachePerfStats.loadsCompletedId)).isEqualTo(1);
    }

    /**
     * Characterization test: {@code loads} currently wraps to negative from max integer value.
     */
    @Test
    public void loadsCompletedWrapsFromMaxIntegerToNegativeValue() {
        statistics.incInt(CachePerfStats.loadsCompletedId, Integer.MAX_VALUE);
        cachePerfStats.endLoad(0);
        assertThat(cachePerfStats.getLoadsCompleted()).isNegative();
    }

    @Test
    public void getNetloadsCompletedDelegatesToStatistics() {
        statistics.incInt(CachePerfStats.netloadsCompletedId, Integer.MAX_VALUE);
        assertThat(cachePerfStats.getNetloadsCompleted()).isEqualTo(Integer.MAX_VALUE);
    }

    /**
     * Characterization test: Note that the only way to increment {@code netloadsCompleted} is to
     * invoke {@code endNetload}.
     */
    @Test
    public void endNetloadIncrementsNetloadsCompleted() {
        cachePerfStats.endNetload(0);
        assertThat(statistics.getInt(CachePerfStats.netloadsCompletedId)).isEqualTo(1);
    }

    /**
     * Characterization test: {@code netloadsComplete} currently wraps to negative from max integer
     * value.
     */
    @Test
    public void netloadsCompletedWrapsFromMaxIntegerToNegativeValue() {
        statistics.incInt(CachePerfStats.netloadsCompletedId, Integer.MAX_VALUE);
        cachePerfStats.endNetload(0);
        assertThat(cachePerfStats.getNetloadsCompleted()).isNegative();
    }

    @Test
    public void getNetsearchesCompletedDelegatesToStatistics() {
        statistics.incInt(CachePerfStats.netsearchesCompletedId, Integer.MAX_VALUE);
        assertThat(cachePerfStats.getNetsearchesCompleted()).isEqualTo(Integer.MAX_VALUE);
    }

    /**
     * Characterization test: Note that the only way to increment {@code netsearchesCompleted} is to
     * invoke {@code endNetsearch}.
     */
    @Test
    public void endLoadIncrementsNetsearchesCompleted() {
        cachePerfStats.endNetsearch(0);
        assertThat(statistics.getInt(CachePerfStats.netsearchesCompletedId)).isEqualTo(1);
    }

    /**
     * Characterization test: {@code netsearchesCompleted} currently wraps to negative from max
     * integer value.
     */
    @Test
    public void netsearchesCompletedWrapsFromMaxIntegerToNegativeValue() {
        statistics.incInt(CachePerfStats.netsearchesCompletedId, Integer.MAX_VALUE);
        cachePerfStats.endNetsearch(0);
        assertThat(cachePerfStats.getNetsearchesCompleted()).isNegative();
    }

    @Test
    public void getCacheWriterCallsCompletedDelegatesToStatistics() {
        statistics.incInt(CachePerfStats.cacheWriterCallsCompletedId, Integer.MAX_VALUE);
        assertThat(cachePerfStats.getCacheWriterCallsCompleted()).isEqualTo(Integer.MAX_VALUE);
    }

    /**
     * Characterization test: Note that the only way to increment {@code cacheWriterCallsCompleted} is
     * to invoke {@code endCacheWriterCall}.
     */
    @Test
    public void endCacheWriterCallIncrementsCacheWriterCallsCompleted() {
        cachePerfStats.endCacheWriterCall(0);
        assertThat(statistics.getInt(CachePerfStats.cacheWriterCallsCompletedId)).isEqualTo(1);
    }

    /**
     * Characterization test: {@code cacheWriterCallsCompleted} currently wraps to negative from max
     * integer value.
     */
    @Test
    public void cacheWriterCallsCompletedWrapsFromMaxIntegerToNegativeValue() {
        statistics.incInt(CachePerfStats.cacheWriterCallsCompletedId, Integer.MAX_VALUE);
        cachePerfStats.endCacheWriterCall(0);
        assertThat(cachePerfStats.getCacheWriterCallsCompleted()).isNegative();
    }

    @Test
    public void getCacheListenerCallsCompletedDelegatesToStatistics() {
        statistics.incInt(CachePerfStats.cacheListenerCallsCompletedId, Integer.MAX_VALUE);
        assertThat(cachePerfStats.getCacheListenerCallsCompleted()).isEqualTo(Integer.MAX_VALUE);
    }

    /**
     * Characterization test: Note that the only way to increment {@code cacheListenerCallsCompleted}
     * is to invoke {@code endCacheListenerCall}.
     */
    @Test
    public void endCacheWriterCallIncrementsCacheListenerCallsCompleted() {
        cachePerfStats.endCacheListenerCall(0);
        assertThat(statistics.getInt(CachePerfStats.cacheListenerCallsCompletedId)).isEqualTo(1);
    }

    /**
     * Characterization test: {@code cacheListenerCallsCompleted} currently wraps to negative from max
     * integer value.
     */
    @Test
    public void cacheListenerCallsCompletedWrapsFromMaxIntegerToNegativeValue() {
        statistics.incInt(CachePerfStats.cacheListenerCallsCompletedId, Integer.MAX_VALUE);
        cachePerfStats.endCacheListenerCall(0);
        assertThat(cachePerfStats.getCacheListenerCallsCompleted()).isNegative();
    }

    @Test
    public void getGetInitialImagesCompletedDelegatesToStatistics() {
        statistics.incInt(CachePerfStats.getInitialImagesCompletedId, Integer.MAX_VALUE);
        assertThat(cachePerfStats.getGetInitialImagesCompleted()).isEqualTo(Integer.MAX_VALUE);
    }

    /**
     * Characterization test: Note that the only way to increment {@code getInitialImagesCompleted} is
     * to invoke {@code endGetInitialImage}.
     */
    @Test
    public void endCacheWriterCallIncrementsGetInitialImagesCompleted() {
        cachePerfStats.endGetInitialImage(0);
        assertThat(statistics.getInt(CachePerfStats.getInitialImagesCompletedId)).isEqualTo(1);
    }

    /**
     * Characterization test: {@code getInitialImagesCompleted} currently wraps to negative from max
     * integer value.
     */
    @Test
    public void getInitialImagesCompletedCallsCompletedWrapsFromMaxIntegerToNegativeValue() {
        statistics.incInt(CachePerfStats.getInitialImagesCompletedId, Integer.MAX_VALUE);
        cachePerfStats.endGetInitialImage(0);
        assertThat(cachePerfStats.getGetInitialImagesCompleted()).isNegative();
    }

    @Test
    public void getDeltaGetInitialImagesCompletedDelegatesToStatistics() {
        statistics.incInt(CachePerfStats.deltaGetInitialImagesCompletedId, Integer.MAX_VALUE);
        assertThat(cachePerfStats.getDeltaGetInitialImagesCompleted()).isEqualTo(Integer.MAX_VALUE);
    }

    @Test
    public void incDeltaGIICompletedIncrementsDeltaGetInitialImagesCompleted() {
        cachePerfStats.incDeltaGIICompleted();
        assertThat(statistics.getInt(CachePerfStats.deltaGetInitialImagesCompletedId)).isEqualTo(1);
    }

    /**
     * Characterization test: {@code deltaGetInitialImagesCompleted} currently wraps to negative from
     * max integer value.
     */
    @Test
    public void deltaGetInitialImagesCompletedWrapsFromMaxIntegerToNegativeValue() {
        statistics.incInt(CachePerfStats.deltaGetInitialImagesCompletedId, Integer.MAX_VALUE);
        cachePerfStats.incDeltaGIICompleted();
        assertThat(cachePerfStats.getDeltaGetInitialImagesCompleted()).isNegative();
    }

    @Test
    public void getQueryExecutionsDelegatesToStatistics() {
        statistics.incInt(CachePerfStats.queryExecutionsId, Integer.MAX_VALUE);
        assertThat(cachePerfStats.getQueryExecutions()).isEqualTo(Integer.MAX_VALUE);
    }

    /**
     * Characterization test: Note that the only way to increment {@code queryExecutions} is to invoke
     * {@code endQueryExecution}.
     */
    @Test
    public void endQueryExecutionIncrementsQueryExecutions() {
        cachePerfStats.endQueryExecution(1);
        assertThat(statistics.getInt(CachePerfStats.queryExecutionsId)).isEqualTo(1);
    }

    /**
     * Characterization test: {@code queryExecutions} currently wraps to negative from max integer
     * value.
     */
    @Test
    public void queryExecutionsWrapsFromMaxIntegerToNegativeValue() {
        statistics.incInt(CachePerfStats.queryExecutionsId, Integer.MAX_VALUE);
        cachePerfStats.endQueryExecution(1);
        assertThat(cachePerfStats.getQueryExecutions()).isNegative();
    }

    @Test
    public void getTxCommitsDelegatesToStatistics() {
        statistics.incInt(CachePerfStats.txCommitsId, Integer.MAX_VALUE);
        assertThat(cachePerfStats.getTxCommits()).isEqualTo(Integer.MAX_VALUE);
    }

    /**
     * Characterization test: Note that the only way to increment {@code txCommits} is to invoke
     * {@code txSuccess}.
     */
    @Test
    public void txSuccessIncrementsTxCommits() {
        cachePerfStats.txSuccess(1, 1, 1);
        assertThat(statistics.getInt(CachePerfStats.txCommitsId)).isEqualTo(1);
    }

    /**
     * Characterization test: {@code txCommits} currently wraps to negative from max integer value.
     */
    @Test
    public void txCommitsWrapsFromMaxIntegerToNegativeValue() {
        statistics.incInt(CachePerfStats.txCommitsId, Integer.MAX_VALUE);
        cachePerfStats.txSuccess(1, 1, 1);
        assertThat(cachePerfStats.getTxCommits()).isNegative();
    }

    @Test
    public void getTxFailuresDelegatesToStatistics() {
        statistics.incInt(CachePerfStats.txFailuresId, Integer.MAX_VALUE);
        assertThat(cachePerfStats.getTxFailures()).isEqualTo(Integer.MAX_VALUE);
    }

    /**
     * Characterization test: Note that the only way to increment {@code txFailures} is to invoke
     * {@code txFailure}.
     */
    @Test
    public void txFailureIncrementsTxFailures() {
        cachePerfStats.txFailure(1, 1, 1);
        assertThat(statistics.getInt(CachePerfStats.txFailuresId)).isEqualTo(1);
    }

    /**
     * Characterization test: {@code txFailures} currently wraps to negative from max integer value.
     */
    @Test
    public void txFailuresWrapsFromMaxIntegerToNegativeValue() {
        statistics.incInt(CachePerfStats.txFailuresId, Integer.MAX_VALUE);
        cachePerfStats.txFailure(1, 1, 1);
        assertThat(cachePerfStats.getTxFailures()).isNegative();
    }

    @Test
    public void getTxRollbacksDelegatesToStatistics() {
        statistics.incInt(CachePerfStats.txRollbacksId, Integer.MAX_VALUE);
        assertThat(cachePerfStats.getTxRollbacks()).isEqualTo(Integer.MAX_VALUE);
    }

    /**
     * Characterization test: Note that the only way to increment {@code txRollbacks} is to invoke
     * {@code txRollback}.
     */
    @Test
    public void txRollbackIncrementsTxRollbacks() {
        cachePerfStats.txRollback(1, 1, 1);
        assertThat(statistics.getInt(CachePerfStats.txRollbacksId)).isEqualTo(1);
    }

    /**
     * Characterization test: {@code txRollbacks} currently wraps to negative from max integer value.
     */
    @Test
    public void txRollbacksWrapsFromMaxIntegerToNegativeValue() {
        statistics.incInt(CachePerfStats.txRollbacksId, Integer.MAX_VALUE);
        cachePerfStats.txRollback(1, 1, 1);
        assertThat(cachePerfStats.getTxRollbacks()).isNegative();
    }

    @Test
    public void getTxCommitChangesDelegatesToStatistics() {
        statistics.incInt(CachePerfStats.txCommitChangesId, Integer.MAX_VALUE);
        assertThat(cachePerfStats.getTxCommitChanges()).isEqualTo(Integer.MAX_VALUE);
    }

    /**
     * Characterization test: Note that the only way to increment {@code txCommitChanges} is to invoke
     * {@code txSuccess}.
     */
    @Test
    public void txSuccessIncrementsTxCommitChanges() {
        cachePerfStats.txSuccess(1, 1, 1);
        assertThat(statistics.getInt(CachePerfStats.txCommitChangesId)).isEqualTo(1);
    }

    /**
     * Characterization test: {@code txCommitChanges} currently wraps to negative from max integer
     * value.
     */
    @Test
    public void txCommitChangesWrapsFromMaxIntegerToNegativeValue() {
        statistics.incInt(CachePerfStats.txCommitChangesId, Integer.MAX_VALUE);
        cachePerfStats.txSuccess(1, 1, 1);
        assertThat(cachePerfStats.getTxCommitChanges()).isNegative();
    }

    @Test
    public void getTxFailureChangesDelegatesToStatistics() {
        statistics.incInt(CachePerfStats.txFailureChangesId, Integer.MAX_VALUE);
        assertThat(cachePerfStats.getTxFailureChanges()).isEqualTo(Integer.MAX_VALUE);
    }

    /**
     * Characterization test: Note that the only way to increment {@code txFailureChanges} is to
     * invoke {@code txFailure}.
     */
    @Test
    public void txFailureIncrementsTxFailureChanges() {
        cachePerfStats.txFailure(1, 1, 1);
        assertThat(statistics.getInt(CachePerfStats.txFailureChangesId)).isEqualTo(1);
    }

    /**
     * Characterization test: {@code txFailureChanges} currently wraps to negative from max integer
     * value.
     */
    @Test
    public void txFailureChangesWrapsFromMaxIntegerToNegativeValue() {
        statistics.incInt(CachePerfStats.txFailureChangesId, Integer.MAX_VALUE);
        cachePerfStats.txFailure(1, 1, 1);
        assertThat(cachePerfStats.getTxFailureChanges()).isNegative();
    }

    @Test
    public void getTxRollbackChangesDelegatesToStatistics() {
        statistics.incInt(CachePerfStats.txRollbackChangesId, Integer.MAX_VALUE);
        assertThat(cachePerfStats.getTxRollbackChanges()).isEqualTo(Integer.MAX_VALUE);
    }

    /**
     * Characterization test: Note that the only way to increment {@code txRollbackChanges} is to
     * invoke {@code txRollback}.
     */
    @Test
    public void txRollbackIncrementsTxRollbackChanges() {
        cachePerfStats.txRollback(1, 1, 1);
        assertThat(statistics.getInt(CachePerfStats.txRollbackChangesId)).isEqualTo(1);
    }

    /**
     * Characterization test: {@code txRollbackChanges} currently wraps to negative from max integer
     * value.
     */
    @Test
    public void txRollbackChangesWrapsFromMaxIntegerToNegativeValue() {
        statistics.incInt(CachePerfStats.txRollbackChangesId, Integer.MAX_VALUE);
        cachePerfStats.txRollback(1, 1, 1);
        assertThat(cachePerfStats.getTxRollbackChanges()).isNegative();
    }

    @Test
    public void getEvictorJobsStartedChangesDelegatesToStatistics() {
        statistics.incInt(CachePerfStats.evictorJobsStartedId, Integer.MAX_VALUE);
        assertThat(cachePerfStats.getEvictorJobsStarted()).isEqualTo(Integer.MAX_VALUE);
    }

    @Test
    public void incEvictorJobsStartedIncrementsEvictorJobsStarted() {
        cachePerfStats.incEvictorJobsStarted();
        assertThat(statistics.getInt(CachePerfStats.evictorJobsStartedId)).isEqualTo(1);
    }

    /**
     * Characterization test: {@code evictorJobsStarted} currently wraps to negative from max integer
     * value.
     */
    @Test
    public void evictorJobsStartedWrapsFromMaxIntegerToNegativeValue() {
        statistics.incInt(CachePerfStats.evictorJobsStartedId, Integer.MAX_VALUE);
        cachePerfStats.incEvictorJobsStarted();
        assertThat(cachePerfStats.getEvictorJobsStarted()).isNegative();
    }

    @Test
    public void getEvictorJobsCompletedChangesDelegatesToStatistics() {
        statistics.incInt(CachePerfStats.evictorJobsCompletedId, Integer.MAX_VALUE);
        assertThat(cachePerfStats.getEvictorJobsCompleted()).isEqualTo(Integer.MAX_VALUE);
    }

    @Test
    public void incEvictorJobsCompletedIncrementsEvictorJobsCompleted() {
        cachePerfStats.incEvictorJobsCompleted();
        assertThat(statistics.getInt(CachePerfStats.evictorJobsCompletedId)).isEqualTo(1);
    }

    /**
     * Characterization test: {@code evictorJobsCompleted} currently wraps to negative from max
     * integer value.
     */
    @Test
    public void evictorJobsCompletedWrapsFromMaxIntegerToNegativeValue() {
        statistics.incInt(CachePerfStats.evictorJobsCompletedId, Integer.MAX_VALUE);
        cachePerfStats.incEvictorJobsCompleted();
        assertThat(cachePerfStats.getEvictorJobsCompleted()).isNegative();
    }

    @Test
    public void getIndexUpdateCompletedChangesDelegatesToStatistics() {
        statistics.incInt(CachePerfStats.indexUpdateCompletedId, Integer.MAX_VALUE);
        assertThat(cachePerfStats.getIndexUpdateCompleted()).isEqualTo(Integer.MAX_VALUE);
    }

    /**
     * Characterization test: Note that the only way to increment {@code indexUpdateCompleted} is to
     * invoke {@code endIndexUpdate}.
     */
    @Test
    public void endIndexUpdateIncrementsEvictorJobsCompleted() {
        cachePerfStats.endIndexUpdate(1);
        assertThat(statistics.getInt(CachePerfStats.indexUpdateCompletedId)).isEqualTo(1);
    }

    /**
     * Characterization test: {@code indexUpdateCompleted} currently wraps to negative from max
     * integer value.
     */
    @Test
    public void indexUpdateCompletedWrapsFromMaxIntegerToNegativeValue() {
        statistics.incInt(CachePerfStats.indexUpdateCompletedId, Integer.MAX_VALUE);
        cachePerfStats.endIndexUpdate(1);
        assertThat(cachePerfStats.getIndexUpdateCompleted()).isNegative();
    }

    @Test
    public void getDeltaUpdatesDelegatesToStatistics() {
        statistics.incInt(CachePerfStats.deltaUpdatesId, Integer.MAX_VALUE);
        assertThat(cachePerfStats.getDeltaUpdates()).isEqualTo(Integer.MAX_VALUE);
    }

    /**
     * Characterization test: Note that the only way to increment {@code deltaUpdates} is to invoke
     * {@code endDeltaUpdate}.
     */
    @Test
    public void endDeltaUpdateIncrementsDeltaUpdates() {
        cachePerfStats.endDeltaUpdate(1);
        assertThat(statistics.getInt(CachePerfStats.deltaUpdatesId)).isEqualTo(1);
    }

    /**
     * Characterization test: {@code deltaUpdatesId} currently wraps to negative from max integer
     * value.
     */
    @Test
    public void deltaUpdatesWrapsFromMaxIntegerToNegativeValue() {
        statistics.incInt(CachePerfStats.deltaUpdatesId, Integer.MAX_VALUE);
        cachePerfStats.endDeltaUpdate(1);
        assertThat(cachePerfStats.getDeltaUpdates()).isNegative();
    }

    @Test
    public void getDeltaFailedUpdatesDelegatesToStatistics() {
        statistics.incInt(CachePerfStats.deltaFailedUpdatesId, Integer.MAX_VALUE);
        assertThat(cachePerfStats.getDeltaFailedUpdates()).isEqualTo(Integer.MAX_VALUE);
    }

    @Test
    public void incDeltaFailedUpdatesIncrementsDeltaFailedUpdates() {
        cachePerfStats.incDeltaFailedUpdates();
        assertThat(statistics.getInt(CachePerfStats.deltaFailedUpdatesId)).isEqualTo(1);
    }

    /**
     * Characterization test: {@code deltaFailedUpdates} currently wraps to negative from max integer
     * value.
     */
    @Test
    public void deltaFailedUpdatesWrapsFromMaxIntegerToNegativeValue() {
        statistics.incInt(CachePerfStats.deltaFailedUpdatesId, Integer.MAX_VALUE);
        cachePerfStats.incDeltaFailedUpdates();
        assertThat(cachePerfStats.getDeltaFailedUpdates()).isNegative();
    }

    @Test
    public void getDeltasPreparedUpdatesDelegatesToStatistics() {
        statistics.incInt(CachePerfStats.deltasPreparedId, Integer.MAX_VALUE);
        assertThat(cachePerfStats.getDeltasPrepared()).isEqualTo(Integer.MAX_VALUE);
    }

    /**
     * Characterization test: Note that the only way to increment {@code deltasPrepared} is to invoke
     * {@code endDeltaPrepared}.
     */
    @Test
    public void endDeltaPreparedIncrementsDeltasPrepared() {
        cachePerfStats.endDeltaPrepared(1);
        assertThat(statistics.getInt(CachePerfStats.deltasPreparedId)).isEqualTo(1);
    }

    /**
     * Characterization test: {@code deltasPrepared} currently wraps to negative from max integer
     * value.
     */
    @Test
    public void deltasPreparedWrapsFromMaxIntegerToNegativeValue() {
        statistics.incInt(CachePerfStats.deltasPreparedId, Integer.MAX_VALUE);
        cachePerfStats.endDeltaPrepared(1);
        assertThat(cachePerfStats.getDeltasPrepared()).isNegative();
    }

    @Test
    public void getDeltasSentDelegatesToStatistics() {
        statistics.incInt(CachePerfStats.deltasSentId, Integer.MAX_VALUE);
        assertThat(cachePerfStats.getDeltasSent()).isEqualTo(Integer.MAX_VALUE);
    }

    @Test
    public void incDeltasSentPreparedIncrementsDeltasSent() {
        cachePerfStats.incDeltasSent();
        assertThat(statistics.getInt(CachePerfStats.deltasSentId)).isEqualTo(1);
    }

    /**
     * Characterization test: {@code deltasSent} currently wraps to negative from max integer value.
     */
    @Test
    public void deltasSentWrapsFromMaxIntegerToNegativeValue() {
        statistics.incInt(CachePerfStats.deltasSentId, Integer.MAX_VALUE);
        cachePerfStats.incDeltasSent();
        assertThat(cachePerfStats.getDeltasSent()).isNegative();
    }

    @Test
    public void getDeltaFullValuesSentDelegatesToStatistics() {
        statistics.incInt(CachePerfStats.deltaFullValuesSentId, Integer.MAX_VALUE);
        assertThat(cachePerfStats.getDeltaFullValuesSent()).isEqualTo(Integer.MAX_VALUE);
    }

    @Test
    public void incDeltaFullValuesSentIncrementsDeltaFullValuesSent() {
        cachePerfStats.incDeltaFullValuesSent();
        assertThat(statistics.getInt(CachePerfStats.deltaFullValuesSentId)).isEqualTo(1);
    }

    /**
     * Characterization test: {@code deltaFullValuesSent} currently wraps to negative from max integer
     * value.
     */
    @Test
    public void deltaFullValuesSentWrapsFromMaxIntegerToNegativeValue() {
        statistics.incInt(CachePerfStats.deltaFullValuesSentId, Integer.MAX_VALUE);
        cachePerfStats.incDeltaFullValuesSent();
        assertThat(cachePerfStats.getDeltaFullValuesSent()).isNegative();
    }

    @Test
    public void getDeltaFullValuesRequestedDelegatesToStatistics() {
        statistics.incInt(CachePerfStats.deltaFullValuesRequestedId, Integer.MAX_VALUE);
        assertThat(cachePerfStats.getDeltaFullValuesRequested()).isEqualTo(Integer.MAX_VALUE);
    }

    @Test
    public void incDeltaFullValuesRequestedIncrementsDeltaFullValuesRequested() {
        cachePerfStats.incDeltaFullValuesRequested();
        assertThat(statistics.getInt(CachePerfStats.deltaFullValuesRequestedId)).isEqualTo(1);
    }

    /**
     * Characterization test: {@code deltaFullValuesRequested} currently wraps to negative from max
     * integer value.
     */
    @Test
    public void deltaFullValuesRequestedWrapsFromMaxIntegerToNegativeValue() {
        statistics.incInt(CachePerfStats.deltaFullValuesRequestedId, Integer.MAX_VALUE);
        cachePerfStats.incDeltaFullValuesRequested();
        assertThat(cachePerfStats.getDeltaFullValuesRequested()).isNegative();
    }
}

