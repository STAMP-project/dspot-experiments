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
package org.apache.flink.runtime.checkpoint;


import CheckpointRetentionPolicy.NEVER_RETAIN_AFTER_TERMINATION;
import JobStatus.CANCELED;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import org.apache.flink.api.common.JobID;
import org.apache.flink.core.io.SimpleVersionedSerializer;
import org.apache.flink.runtime.executiongraph.ExecutionAttemptID;
import org.apache.flink.runtime.executiongraph.ExecutionJobVertex;
import org.apache.flink.runtime.executiongraph.ExecutionVertex;
import org.apache.flink.runtime.jobgraph.JobVertexID;
import org.apache.flink.runtime.jobgraph.OperatorID;
import org.apache.flink.runtime.state.testutils.TestCompletedCheckpointStorageLocation;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


/**
 * Tests for the user-defined hooks that the checkpoint coordinator can call.
 */
public class CheckpointCoordinatorMasterHooksTest {
    // ------------------------------------------------------------------------
    // hook registration
    // ------------------------------------------------------------------------
    /**
     * This method tests that hooks with the same identifier are not registered
     * multiple times.
     */
    @Test
    public void testDeduplicateOnRegister() {
        final CheckpointCoordinator cc = CheckpointCoordinatorMasterHooksTest.instantiateCheckpointCoordinator(new JobID());
        MasterTriggerRestoreHook<?> hook1 = Mockito.mock(MasterTriggerRestoreHook.class);
        Mockito.when(hook1.getIdentifier()).thenReturn("test id");
        MasterTriggerRestoreHook<?> hook2 = Mockito.mock(MasterTriggerRestoreHook.class);
        Mockito.when(hook2.getIdentifier()).thenReturn("test id");
        MasterTriggerRestoreHook<?> hook3 = Mockito.mock(MasterTriggerRestoreHook.class);
        Mockito.when(hook3.getIdentifier()).thenReturn("anotherId");
        Assert.assertTrue(cc.addMasterHook(hook1));
        Assert.assertFalse(cc.addMasterHook(hook2));
        Assert.assertTrue(cc.addMasterHook(hook3));
    }

    /**
     * Test that validates correct exceptions when supplying hooks with invalid IDs.
     */
    @Test
    public void testNullOrInvalidId() {
        final CheckpointCoordinator cc = CheckpointCoordinatorMasterHooksTest.instantiateCheckpointCoordinator(new JobID());
        try {
            cc.addMasterHook(null);
            Assert.fail("expected an exception");
        } catch (NullPointerException ignored) {
        }
        try {
            cc.addMasterHook(Mockito.mock(MasterTriggerRestoreHook.class));
            Assert.fail("expected an exception");
        } catch (IllegalArgumentException ignored) {
        }
        try {
            MasterTriggerRestoreHook<?> hook = Mockito.mock(MasterTriggerRestoreHook.class);
            Mockito.when(hook.getIdentifier()).thenReturn("        ");
            cc.addMasterHook(hook);
            Assert.fail("expected an exception");
        } catch (IllegalArgumentException ignored) {
        }
    }

    @Test
    public void testHookReset() throws Exception {
        final String id1 = "id1";
        final String id2 = "id2";
        final MasterTriggerRestoreHook<String> hook1 = CheckpointCoordinatorMasterHooksTest.mockGeneric(MasterTriggerRestoreHook.class);
        Mockito.when(hook1.getIdentifier()).thenReturn(id1);
        final MasterTriggerRestoreHook<String> hook2 = CheckpointCoordinatorMasterHooksTest.mockGeneric(MasterTriggerRestoreHook.class);
        Mockito.when(hook2.getIdentifier()).thenReturn(id2);
        // create the checkpoint coordinator
        final JobID jid = new JobID();
        final ExecutionAttemptID execId = new ExecutionAttemptID();
        final ExecutionVertex ackVertex = CheckpointCoordinatorTest.mockExecutionVertex(execId);
        final CheckpointCoordinator cc = CheckpointCoordinatorMasterHooksTest.instantiateCheckpointCoordinator(jid, ackVertex);
        cc.addMasterHook(hook1);
        cc.addMasterHook(hook2);
        // initialize the hooks
        cc.restoreLatestCheckpointedState(Collections.<JobVertexID, ExecutionJobVertex>emptyMap(), false, false);
        Mockito.verify(hook1, Mockito.times(1)).reset();
        Mockito.verify(hook2, Mockito.times(1)).reset();
        // shutdown
        cc.shutdown(CANCELED);
        Mockito.verify(hook1, Mockito.times(1)).close();
        Mockito.verify(hook2, Mockito.times(1)).close();
    }

    // ------------------------------------------------------------------------
    // trigger / restore behavior
    // ------------------------------------------------------------------------
    @Test
    public void testHooksAreCalledOnTrigger() throws Exception {
        final String id1 = "id1";
        final String id2 = "id2";
        final String state1 = "the-test-string-state";
        final byte[] state1serialized = new CheckpointCoordinatorMasterHooksTest.StringSerializer().serialize(state1);
        final long state2 = 987654321L;
        final byte[] state2serialized = new CheckpointCoordinatorMasterHooksTest.LongSerializer().serialize(state2);
        final MasterTriggerRestoreHook<String> statefulHook1 = CheckpointCoordinatorMasterHooksTest.mockGeneric(MasterTriggerRestoreHook.class);
        Mockito.when(statefulHook1.getIdentifier()).thenReturn(id1);
        Mockito.when(statefulHook1.createCheckpointDataSerializer()).thenReturn(new CheckpointCoordinatorMasterHooksTest.StringSerializer());
        Mockito.when(statefulHook1.triggerCheckpoint(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.any(Executor.class))).thenReturn(CompletableFuture.completedFuture(state1));
        final MasterTriggerRestoreHook<Long> statefulHook2 = CheckpointCoordinatorMasterHooksTest.mockGeneric(MasterTriggerRestoreHook.class);
        Mockito.when(statefulHook2.getIdentifier()).thenReturn(id2);
        Mockito.when(statefulHook2.createCheckpointDataSerializer()).thenReturn(new CheckpointCoordinatorMasterHooksTest.LongSerializer());
        Mockito.when(statefulHook2.triggerCheckpoint(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.any(Executor.class))).thenReturn(CompletableFuture.completedFuture(state2));
        final MasterTriggerRestoreHook<Void> statelessHook = CheckpointCoordinatorMasterHooksTest.mockGeneric(MasterTriggerRestoreHook.class);
        Mockito.when(statelessHook.getIdentifier()).thenReturn("some-id");
        // create the checkpoint coordinator
        final JobID jid = new JobID();
        final ExecutionAttemptID execId = new ExecutionAttemptID();
        final ExecutionVertex ackVertex = CheckpointCoordinatorTest.mockExecutionVertex(execId);
        final CheckpointCoordinator cc = CheckpointCoordinatorMasterHooksTest.instantiateCheckpointCoordinator(jid, ackVertex);
        cc.addMasterHook(statefulHook1);
        cc.addMasterHook(statelessHook);
        cc.addMasterHook(statefulHook2);
        // trigger a checkpoint
        Assert.assertTrue(cc.triggerCheckpoint(System.currentTimeMillis(), false));
        Assert.assertEquals(1, cc.getNumberOfPendingCheckpoints());
        Mockito.verify(statefulHook1, Mockito.times(1)).triggerCheckpoint(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.any(Executor.class));
        Mockito.verify(statefulHook2, Mockito.times(1)).triggerCheckpoint(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.any(Executor.class));
        Mockito.verify(statelessHook, Mockito.times(1)).triggerCheckpoint(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.any(Executor.class));
        final long checkpointId = cc.getPendingCheckpoints().values().iterator().next().getCheckpointId();
        cc.receiveAcknowledgeMessage(new org.apache.flink.runtime.messages.checkpoint.AcknowledgeCheckpoint(jid, execId, checkpointId));
        Assert.assertEquals(0, cc.getNumberOfPendingCheckpoints());
        Assert.assertEquals(1, cc.getNumberOfRetainedSuccessfulCheckpoints());
        final CompletedCheckpoint chk = cc.getCheckpointStore().getLatestCheckpoint();
        final Collection<MasterState> masterStates = chk.getMasterHookStates();
        Assert.assertEquals(2, masterStates.size());
        for (MasterState ms : masterStates) {
            if (ms.name().equals(id1)) {
                Assert.assertArrayEquals(state1serialized, ms.bytes());
                Assert.assertEquals(CheckpointCoordinatorMasterHooksTest.StringSerializer.VERSION, ms.version());
            } else
                if (ms.name().equals(id2)) {
                    Assert.assertArrayEquals(state2serialized, ms.bytes());
                    Assert.assertEquals(CheckpointCoordinatorMasterHooksTest.LongSerializer.VERSION, ms.version());
                } else {
                    Assert.fail(("unrecognized state name: " + (ms.name())));
                }

        }
    }

    @Test
    public void testHooksAreCalledOnRestore() throws Exception {
        final String id1 = "id1";
        final String id2 = "id2";
        final String state1 = "the-test-string-state";
        final byte[] state1serialized = new CheckpointCoordinatorMasterHooksTest.StringSerializer().serialize(state1);
        final long state2 = 987654321L;
        final byte[] state2serialized = new CheckpointCoordinatorMasterHooksTest.LongSerializer().serialize(state2);
        final List<MasterState> masterHookStates = Arrays.asList(new MasterState(id1, state1serialized, CheckpointCoordinatorMasterHooksTest.StringSerializer.VERSION), new MasterState(id2, state2serialized, CheckpointCoordinatorMasterHooksTest.LongSerializer.VERSION));
        final MasterTriggerRestoreHook<String> statefulHook1 = CheckpointCoordinatorMasterHooksTest.mockGeneric(MasterTriggerRestoreHook.class);
        Mockito.when(statefulHook1.getIdentifier()).thenReturn(id1);
        Mockito.when(statefulHook1.createCheckpointDataSerializer()).thenReturn(new CheckpointCoordinatorMasterHooksTest.StringSerializer());
        Mockito.when(statefulHook1.triggerCheckpoint(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.any(Executor.class))).thenThrow(new Exception("not expected"));
        final MasterTriggerRestoreHook<Long> statefulHook2 = CheckpointCoordinatorMasterHooksTest.mockGeneric(MasterTriggerRestoreHook.class);
        Mockito.when(statefulHook2.getIdentifier()).thenReturn(id2);
        Mockito.when(statefulHook2.createCheckpointDataSerializer()).thenReturn(new CheckpointCoordinatorMasterHooksTest.LongSerializer());
        Mockito.when(statefulHook2.triggerCheckpoint(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.any(Executor.class))).thenThrow(new Exception("not expected"));
        final MasterTriggerRestoreHook<Void> statelessHook = CheckpointCoordinatorMasterHooksTest.mockGeneric(MasterTriggerRestoreHook.class);
        Mockito.when(statelessHook.getIdentifier()).thenReturn("some-id");
        final JobID jid = new JobID();
        final long checkpointId = 13L;
        final CompletedCheckpoint checkpoint = new CompletedCheckpoint(jid, checkpointId, 123L, 125L, Collections.<OperatorID, OperatorState>emptyMap(), masterHookStates, CheckpointProperties.forCheckpoint(NEVER_RETAIN_AFTER_TERMINATION), new TestCompletedCheckpointStorageLocation());
        final ExecutionAttemptID execId = new ExecutionAttemptID();
        final ExecutionVertex ackVertex = CheckpointCoordinatorTest.mockExecutionVertex(execId);
        final CheckpointCoordinator cc = CheckpointCoordinatorMasterHooksTest.instantiateCheckpointCoordinator(jid, ackVertex);
        cc.addMasterHook(statefulHook1);
        cc.addMasterHook(statelessHook);
        cc.addMasterHook(statefulHook2);
        cc.getCheckpointStore().addCheckpoint(checkpoint);
        cc.restoreLatestCheckpointedState(Collections.<JobVertexID, ExecutionJobVertex>emptyMap(), true, false);
        Mockito.verify(statefulHook1, Mockito.times(1)).restoreCheckpoint(ArgumentMatchers.eq(checkpointId), ArgumentMatchers.eq(state1));
        Mockito.verify(statefulHook2, Mockito.times(1)).restoreCheckpoint(ArgumentMatchers.eq(checkpointId), ArgumentMatchers.eq(state2));
        Mockito.verify(statelessHook, Mockito.times(1)).restoreCheckpoint(ArgumentMatchers.eq(checkpointId), ArgumentMatchers.isNull(Void.class));
    }

    @Test
    public void checkUnMatchedStateOnRestore() throws Exception {
        final String id1 = "id1";
        final String id2 = "id2";
        final String state1 = "the-test-string-state";
        final byte[] state1serialized = new CheckpointCoordinatorMasterHooksTest.StringSerializer().serialize(state1);
        final long state2 = 987654321L;
        final byte[] state2serialized = new CheckpointCoordinatorMasterHooksTest.LongSerializer().serialize(state2);
        final List<MasterState> masterHookStates = Arrays.asList(new MasterState(id1, state1serialized, CheckpointCoordinatorMasterHooksTest.StringSerializer.VERSION), new MasterState(id2, state2serialized, CheckpointCoordinatorMasterHooksTest.LongSerializer.VERSION));
        final MasterTriggerRestoreHook<String> statefulHook = CheckpointCoordinatorMasterHooksTest.mockGeneric(MasterTriggerRestoreHook.class);
        Mockito.when(statefulHook.getIdentifier()).thenReturn(id1);
        Mockito.when(statefulHook.createCheckpointDataSerializer()).thenReturn(new CheckpointCoordinatorMasterHooksTest.StringSerializer());
        Mockito.when(statefulHook.triggerCheckpoint(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.any(Executor.class))).thenThrow(new Exception("not expected"));
        final MasterTriggerRestoreHook<Void> statelessHook = CheckpointCoordinatorMasterHooksTest.mockGeneric(MasterTriggerRestoreHook.class);
        Mockito.when(statelessHook.getIdentifier()).thenReturn("some-id");
        final JobID jid = new JobID();
        final long checkpointId = 44L;
        final CompletedCheckpoint checkpoint = new CompletedCheckpoint(jid, checkpointId, 123L, 125L, Collections.<OperatorID, OperatorState>emptyMap(), masterHookStates, CheckpointProperties.forCheckpoint(NEVER_RETAIN_AFTER_TERMINATION), new TestCompletedCheckpointStorageLocation());
        final ExecutionAttemptID execId = new ExecutionAttemptID();
        final ExecutionVertex ackVertex = CheckpointCoordinatorTest.mockExecutionVertex(execId);
        final CheckpointCoordinator cc = CheckpointCoordinatorMasterHooksTest.instantiateCheckpointCoordinator(jid, ackVertex);
        cc.addMasterHook(statefulHook);
        cc.addMasterHook(statelessHook);
        cc.getCheckpointStore().addCheckpoint(checkpoint);
        // since we have unmatched state, this should fail
        try {
            cc.restoreLatestCheckpointedState(Collections.<JobVertexID, ExecutionJobVertex>emptyMap(), true, false);
            Assert.fail("exception expected");
        } catch (IllegalStateException ignored) {
        }
        // permitting unmatched state should succeed
        cc.restoreLatestCheckpointedState(Collections.<JobVertexID, ExecutionJobVertex>emptyMap(), true, true);
        Mockito.verify(statefulHook, Mockito.times(1)).restoreCheckpoint(ArgumentMatchers.eq(checkpointId), ArgumentMatchers.eq(state1));
        Mockito.verify(statelessHook, Mockito.times(1)).restoreCheckpoint(ArgumentMatchers.eq(checkpointId), ArgumentMatchers.isNull(Void.class));
    }

    // ------------------------------------------------------------------------
    // failure scenarios
    // ------------------------------------------------------------------------
    /**
     * This test makes sure that the checkpoint is already registered by the time
     * that the hooks are called
     */
    @Test
    public void ensureRegisteredAtHookTime() throws Exception {
        final String id = "id";
        // create the checkpoint coordinator
        final JobID jid = new JobID();
        final ExecutionAttemptID execId = new ExecutionAttemptID();
        final ExecutionVertex ackVertex = CheckpointCoordinatorTest.mockExecutionVertex(execId);
        final CheckpointCoordinator cc = CheckpointCoordinatorMasterHooksTest.instantiateCheckpointCoordinator(jid, ackVertex);
        final MasterTriggerRestoreHook<Void> hook = CheckpointCoordinatorMasterHooksTest.mockGeneric(MasterTriggerRestoreHook.class);
        Mockito.when(hook.getIdentifier()).thenReturn(id);
        Mockito.when(hook.triggerCheckpoint(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.any(Executor.class))).thenAnswer(new Answer<CompletableFuture<Void>>() {
            @Override
            public CompletableFuture<Void> answer(InvocationOnMock invocation) throws Throwable {
                Assert.assertEquals(1, cc.getNumberOfPendingCheckpoints());
                long checkpointId = ((Long) (invocation.getArguments()[0]));
                Assert.assertNotNull(cc.getPendingCheckpoints().get(checkpointId));
                return null;
            }
        });
        cc.addMasterHook(hook);
        // trigger a checkpoint
        Assert.assertTrue(cc.triggerCheckpoint(System.currentTimeMillis(), false));
    }

    // ------------------------------------------------------------------------
    private static final class StringSerializer implements SimpleVersionedSerializer<String> {
        static final int VERSION = 77;

        @Override
        public int getVersion() {
            return CheckpointCoordinatorMasterHooksTest.StringSerializer.VERSION;
        }

        @Override
        public byte[] serialize(String checkpointData) throws IOException {
            return checkpointData.getBytes(StandardCharsets.UTF_8);
        }

        @Override
        public String deserialize(int version, byte[] serialized) throws IOException {
            if (version != (CheckpointCoordinatorMasterHooksTest.StringSerializer.VERSION)) {
                throw new IOException("version mismatch");
            }
            return new String(serialized, StandardCharsets.UTF_8);
        }
    }

    // ------------------------------------------------------------------------
    private static final class LongSerializer implements SimpleVersionedSerializer<Long> {
        static final int VERSION = 5;

        @Override
        public int getVersion() {
            return CheckpointCoordinatorMasterHooksTest.LongSerializer.VERSION;
        }

        @Override
        public byte[] serialize(Long checkpointData) throws IOException {
            final byte[] bytes = new byte[8];
            ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).putLong(0, checkpointData);
            return bytes;
        }

        @Override
        public Long deserialize(int version, byte[] serialized) throws IOException {
            Assert.assertEquals(CheckpointCoordinatorMasterHooksTest.LongSerializer.VERSION, version);
            Assert.assertEquals(8, serialized.length);
            return ByteBuffer.wrap(serialized).order(ByteOrder.LITTLE_ENDIAN).getLong(0);
        }
    }
}

