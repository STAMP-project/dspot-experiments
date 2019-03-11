/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.streaming.api.operators;


import java.util.concurrent.RunnableFuture;
import org.apache.flink.runtime.state.DoneFuture;
import org.apache.flink.runtime.state.KeyedStateHandle;
import org.apache.flink.runtime.state.OperatorStateHandle;
import org.apache.flink.runtime.state.OperatorStreamStateHandle;
import org.apache.flink.runtime.state.SnapshotResult;
import org.apache.flink.util.TestLogger;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Tests for {@link OperatorSnapshotFutures}.
 */
public class OperatorSnapshotFuturesTest extends TestLogger {
    /**
     * Tests that all runnable futures in an OperatorSnapshotResult are properly cancelled and if
     * the StreamStateHandle result is retrievable that the state handle are discarded.
     */
    @Test
    public void testCancelAndCleanup() throws Exception {
        OperatorSnapshotFutures operatorSnapshotResult = new OperatorSnapshotFutures();
        operatorSnapshotResult.cancel();
        KeyedStateHandle keyedManagedStateHandle = Mockito.mock(KeyedStateHandle.class);
        SnapshotResult<KeyedStateHandle> keyedStateManagedResult = SnapshotResult.of(keyedManagedStateHandle);
        RunnableFuture<SnapshotResult<KeyedStateHandle>> keyedStateManagedFuture = spy(DoneFuture.of(keyedStateManagedResult));
        KeyedStateHandle keyedRawStateHandle = Mockito.mock(KeyedStateHandle.class);
        SnapshotResult<KeyedStateHandle> keyedStateRawResult = SnapshotResult.of(keyedRawStateHandle);
        RunnableFuture<SnapshotResult<KeyedStateHandle>> keyedStateRawFuture = spy(DoneFuture.of(keyedStateRawResult));
        OperatorStateHandle operatorManagedStateHandle = Mockito.mock(OperatorStreamStateHandle.class);
        SnapshotResult<OperatorStateHandle> operatorStateManagedResult = SnapshotResult.of(operatorManagedStateHandle);
        RunnableFuture<SnapshotResult<OperatorStateHandle>> operatorStateManagedFuture = spy(DoneFuture.of(operatorStateManagedResult));
        OperatorStateHandle operatorRawStateHandle = Mockito.mock(OperatorStreamStateHandle.class);
        SnapshotResult<OperatorStateHandle> operatorStateRawResult = SnapshotResult.of(operatorRawStateHandle);
        RunnableFuture<SnapshotResult<OperatorStateHandle>> operatorStateRawFuture = spy(DoneFuture.of(operatorStateRawResult));
        operatorSnapshotResult = new OperatorSnapshotFutures(keyedStateManagedFuture, keyedStateRawFuture, operatorStateManagedFuture, operatorStateRawFuture);
        operatorSnapshotResult.cancel();
        Mockito.verify(keyedStateManagedFuture).cancel(true);
        Mockito.verify(keyedStateRawFuture).cancel(true);
        Mockito.verify(operatorStateManagedFuture).cancel(true);
        Mockito.verify(operatorStateRawFuture).cancel(true);
        Mockito.verify(keyedManagedStateHandle).discardState();
        Mockito.verify(keyedRawStateHandle).discardState();
        Mockito.verify(operatorManagedStateHandle).discardState();
        Mockito.verify(operatorRawStateHandle).discardState();
    }
}

