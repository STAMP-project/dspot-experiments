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
package org.apache.flink.runtime.state.ttl;


import StateTtlConfig.StateVisibility.NeverReturnExpired;
import StateTtlConfig.StateVisibility.ReturnExpiredIfNotCleanedUp;
import StateTtlConfig.UpdateType.OnCreateAndWrite;
import StateTtlConfig.UpdateType.OnReadAndWrite;
import java.util.List;
import java.util.concurrent.RunnableFuture;
import java.util.function.Consumer;
import org.apache.flink.api.common.state.StateTtlConfig;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.runtime.state.KeyedStateHandle;
import org.apache.flink.runtime.state.SnapshotResult;
import org.apache.flink.runtime.state.heap.CopyOnWriteStateTable;
import org.apache.flink.util.StateMigrationException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * State TTL base test suite.
 */
@RunWith(Parameterized.class)
public abstract class TtlStateTestBase {
    protected static final long TTL = 100;

    private static final int INC_CLEANUP_ALL_KEYS = (((CopyOnWriteStateTable.DEFAULT_CAPACITY) >> 1) + ((CopyOnWriteStateTable.DEFAULT_CAPACITY) >> 2)) + 1;

    protected MockTtlTimeProvider timeProvider;

    protected StateBackendTestContext sbetc;

    protected static final String UNEXPIRED_AVAIL = "Unexpired state should be available";

    protected static final String UPDATED_UNEXPIRED_AVAIL = "Unexpired state should be available after update";

    protected static final String EXPIRED_UNAVAIL = "Expired state should be unavailable";

    private static final String EXPIRED_AVAIL = "Expired state should be available";

    private StateTtlConfig ttlConfig;

    @Parameterized.Parameter
    public TtlStateTestContextBase<?, ?, ?> ctx;

    @Test
    public void testNonExistentValue() throws Exception {
        initTest();
        Assert.assertEquals("Non-existing state should be empty", ctx().emptyValue, ctx().get());
    }

    @Test
    public void testExactExpirationOnWrite() throws Exception {
        initTest(OnCreateAndWrite, NeverReturnExpired);
        takeAndRestoreSnapshot();
        timeProvider.time = 0;
        ctx().update(ctx().updateEmpty);
        takeAndRestoreSnapshot();
        timeProvider.time = 20;
        Assert.assertEquals(TtlStateTestBase.UNEXPIRED_AVAIL, ctx().getUpdateEmpty, ctx().get());
        takeAndRestoreSnapshot();
        timeProvider.time = 50;
        ctx().update(ctx().updateUnexpired);
        takeAndRestoreSnapshot();
        timeProvider.time = 120;
        Assert.assertEquals(TtlStateTestBase.UPDATED_UNEXPIRED_AVAIL, ctx().getUnexpired, ctx().get());
        takeAndRestoreSnapshot();
        timeProvider.time = 170;
        ctx().update(ctx().updateExpired);
        takeAndRestoreSnapshot();
        timeProvider.time = 220;
        Assert.assertEquals(TtlStateTestBase.UPDATED_UNEXPIRED_AVAIL, ctx().getUpdateExpired, ctx().get());
        takeAndRestoreSnapshot();
        timeProvider.time = 300;
        Assert.assertEquals(TtlStateTestBase.EXPIRED_UNAVAIL, ctx().emptyValue, ctx().get());
        Assert.assertEquals("Original state should be cleared on access", ctx().emptyValue, ctx().getOriginal());
    }

    @Test
    public void testRelaxedExpirationOnWrite() throws Exception {
        initTest(OnCreateAndWrite, ReturnExpiredIfNotCleanedUp);
        timeProvider.time = 0;
        ctx().update(ctx().updateEmpty);
        takeAndRestoreSnapshot();
        timeProvider.time = 120;
        Assert.assertEquals(TtlStateTestBase.EXPIRED_AVAIL, ctx().getUpdateEmpty, ctx().get());
        Assert.assertEquals("Original state should be cleared on access", ctx().emptyValue, ctx().getOriginal());
        Assert.assertEquals("Expired state should be cleared on access", ctx().emptyValue, ctx().get());
    }

    @Test
    public void testExactExpirationOnRead() throws Exception {
        initTest(OnReadAndWrite, NeverReturnExpired);
        timeProvider.time = 0;
        ctx().update(ctx().updateEmpty);
        takeAndRestoreSnapshot();
        timeProvider.time = 50;
        Assert.assertEquals(TtlStateTestBase.UNEXPIRED_AVAIL, ctx().getUpdateEmpty, ctx().get());
        takeAndRestoreSnapshot();
        timeProvider.time = 120;
        Assert.assertEquals("Unexpired state should be available after read", ctx().getUpdateEmpty, ctx().get());
        takeAndRestoreSnapshot();
        timeProvider.time = 250;
        Assert.assertEquals(TtlStateTestBase.EXPIRED_UNAVAIL, ctx().emptyValue, ctx().get());
        Assert.assertEquals("Original state should be cleared on access", ctx().emptyValue, ctx().getOriginal());
    }

    @Test
    public void testRelaxedExpirationOnRead() throws Exception {
        initTest(OnReadAndWrite, ReturnExpiredIfNotCleanedUp);
        timeProvider.time = 0;
        ctx().update(ctx().updateEmpty);
        takeAndRestoreSnapshot();
        timeProvider.time = 50;
        Assert.assertEquals(TtlStateTestBase.UNEXPIRED_AVAIL, ctx().getUpdateEmpty, ctx().get());
        takeAndRestoreSnapshot();
        timeProvider.time = 170;
        Assert.assertEquals(TtlStateTestBase.EXPIRED_AVAIL, ctx().getUpdateEmpty, ctx().get());
        Assert.assertEquals("Expired state should be cleared on access", ctx().emptyValue, ctx().get());
    }

    @Test
    public void testExpirationTimestampOverflow() throws Exception {
        initTest(OnCreateAndWrite, NeverReturnExpired, Long.MAX_VALUE);
        timeProvider.time = 10;
        ctx().update(ctx().updateEmpty);
        takeAndRestoreSnapshot();
        timeProvider.time = 50;
        Assert.assertEquals(TtlStateTestBase.UNEXPIRED_AVAIL, ctx().getUpdateEmpty, ctx().get());
    }

    @Test
    public void testMergeNamespaces() throws Exception {
        Assume.assumeThat(ctx, CoreMatchers.instanceOf(TtlMergingStateTestContext.class));
        initTest();
        timeProvider.time = 0;
        List<Tuple2<String, Object>> expiredUpdatesToMerge = mctx().generateExpiredUpdatesToMerge();
        mctx().applyStateUpdates(expiredUpdatesToMerge);
        takeAndRestoreSnapshot();
        timeProvider.time = 120;
        List<Tuple2<String, Object>> unexpiredUpdatesToMerge = mctx().generateUnexpiredUpdatesToMerge();
        mctx().applyStateUpdates(unexpiredUpdatesToMerge);
        takeAndRestoreSnapshot();
        timeProvider.time = 150;
        List<Tuple2<String, Object>> finalUpdatesToMerge = mctx().generateFinalUpdatesToMerge();
        mctx().applyStateUpdates(finalUpdatesToMerge);
        takeAndRestoreSnapshot();
        timeProvider.time = 230;
        mergeNamespaces("targetNamespace", TtlMergingStateTestContext.NAMESPACES);
        setCurrentNamespace("targetNamespace");
        Assert.assertEquals("Unexpected result of merge operation", mctx().getMergeResult(unexpiredUpdatesToMerge, finalUpdatesToMerge), mctx().get());
    }

    @Test
    public void testMultipleKeys() throws Exception {
        initTest();
        testMultipleStateIds(( id) -> sbetc.setCurrentKey(id), false);
    }

    @Test
    public void testMultipleKeysWithSnapshotCleanup() throws Exception {
        Assume.assumeTrue("full snapshot strategy", fullSnapshot());
        initTest(TtlStateTestBase.getConfBuilder(TtlStateTestBase.TTL).cleanupFullSnapshot().build());
        // set time back after restore to see entry unexpired if it was not cleaned up in snapshot properly
        testMultipleStateIds(( id) -> sbetc.setCurrentKey(id), true);
    }

    @Test
    public void testMultipleNamespaces() throws Exception {
        initTest();
        testMultipleStateIds(( id) -> ctx().ttlState.setCurrentNamespace(id), false);
    }

    @Test
    public void testMultipleNamespacesWithSnapshotCleanup() throws Exception {
        Assume.assumeTrue("full snapshot strategy", fullSnapshot());
        initTest(TtlStateTestBase.getConfBuilder(TtlStateTestBase.TTL).cleanupFullSnapshot().build());
        // set time back after restore to see entry unexpired if it was not cleaned up in snapshot properly
        testMultipleStateIds(( id) -> ctx().ttlState.setCurrentNamespace(id), true);
    }

    @Test
    public void testSnapshotChangeRestore() throws Exception {
        initTest();
        timeProvider.time = 0;
        sbetc.setCurrentKey("k1");
        ctx().update(ctx().updateEmpty);
        timeProvider.time = 50;
        sbetc.setCurrentKey("k1");
        ctx().update(ctx().updateUnexpired);
        timeProvider.time = 100;
        sbetc.setCurrentKey("k2");
        ctx().update(ctx().updateEmpty);
        KeyedStateHandle snapshot = sbetc.takeSnapshot();
        timeProvider.time = 170;
        sbetc.setCurrentKey("k1");
        ctx().update(ctx().updateExpired);
        sbetc.setCurrentKey("k2");
        ctx().update(ctx().updateUnexpired);
        restoreSnapshot(snapshot, StateBackendTestContext.NUMBER_OF_KEY_GROUPS);
        timeProvider.time = 180;
        sbetc.setCurrentKey("k1");
        Assert.assertEquals(TtlStateTestBase.EXPIRED_UNAVAIL, ctx().emptyValue, ctx().get());
        sbetc.setCurrentKey("k2");
        Assert.assertEquals(TtlStateTestBase.UNEXPIRED_AVAIL, ctx().getUpdateEmpty, ctx().get());
    }

    @Test(expected = StateMigrationException.class)
    public void testRestoreTtlAndRegisterNonTtlStateCompatFailure() throws Exception {
        Assume.assumeThat(this, CoreMatchers.not(CoreMatchers.instanceOf(MockTtlStateTest.class)));
        initTest();
        timeProvider.time = 0;
        ctx().update(ctx().updateEmpty);
        KeyedStateHandle snapshot = sbetc.takeSnapshot();
        sbetc.createAndRestoreKeyedStateBackend(snapshot);
        sbetc.setCurrentKey("defaultKey");
        sbetc.createState(ctx().createStateDescriptor(), "");
    }

    @Test
    public void testIncrementalCleanup() throws Exception {
        Assume.assumeTrue(incrementalCleanupSupported());
        initTest(TtlStateTestBase.getConfBuilder(TtlStateTestBase.TTL).cleanupIncrementally(5, true).build());
        final int keysToUpdate = (CopyOnWriteStateTable.DEFAULT_CAPACITY) >> 3;
        timeProvider.time = 0;
        // create enough keys to trigger incremental rehash
        updateKeys(0, TtlStateTestBase.INC_CLEANUP_ALL_KEYS, ctx().updateEmpty);
        timeProvider.time = 50;
        // update some
        updateKeys(0, keysToUpdate, ctx().updateUnexpired);
        RunnableFuture<SnapshotResult<KeyedStateHandle>> snapshotRunnableFuture = sbetc.triggerSnapshot();
        // update more concurrently with snapshotting
        updateKeys(keysToUpdate, (keysToUpdate * 2), ctx().updateUnexpired);
        timeProvider.time = 120;// expire rest

        triggerMoreIncrementalCleanupByOtherOps();
        // check rest expired and cleanup updated
        checkExpiredKeys((keysToUpdate * 2), TtlStateTestBase.INC_CLEANUP_ALL_KEYS);
        KeyedStateHandle snapshot = snapshotRunnableFuture.get().getJobManagerOwnedSnapshot();
        // restore snapshot which should discard concurrent updates
        timeProvider.time = 50;
        restoreSnapshot(snapshot, StateBackendTestContext.NUMBER_OF_KEY_GROUPS);
        // check rest unexpired, also after restore which should discard concurrent updates
        checkUnexpiredKeys(keysToUpdate, TtlStateTestBase.INC_CLEANUP_ALL_KEYS, ctx().getUpdateEmpty);
        timeProvider.time = 120;
        // remove some
        for (int i = keysToUpdate >> 1; i < (keysToUpdate + (keysToUpdate >> 2)); i++) {
            sbetc.setCurrentKey(Integer.toString(i));
            ctx().ttlState.clear();
        }
        // check updated not expired
        checkUnexpiredKeys(0, (keysToUpdate >> 1), ctx().getUnexpired);
        triggerMoreIncrementalCleanupByOtherOps();
        // check that concurrently updated and then restored with original values are expired
        checkExpiredKeys(keysToUpdate, (keysToUpdate * 2));
        timeProvider.time = 170;
        // check rest expired and cleanup updated
        checkExpiredKeys((keysToUpdate >> 1), TtlStateTestBase.INC_CLEANUP_ALL_KEYS);
        // check updated expired
        checkExpiredKeys(0, (keysToUpdate >> 1));
    }
}

