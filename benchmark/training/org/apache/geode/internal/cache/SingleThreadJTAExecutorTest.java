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


import java.util.concurrent.ExecutorService;
import org.apache.geode.CancelCriterion;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;


public class SingleThreadJTAExecutorTest {
    private SingleThreadJTAExecutor singleThreadJTAExecutor;

    private TXState txState;

    private ExecutorService executor;

    private BeforeCompletion beforeCompletion;

    private AfterCompletion afterCompletion;

    private CancelCriterion cancelCriterion;

    @Test
    public void executeBeforeCompletionCallsDoOps() {
        InOrder inOrder = Mockito.inOrder(beforeCompletion, afterCompletion);
        singleThreadJTAExecutor.executeBeforeCompletion(txState, executor, cancelCriterion);
        Mockito.verify(beforeCompletion, Mockito.times(1)).execute(ArgumentMatchers.eq(cancelCriterion));
        await().untilAsserted(() -> inOrder.verify(beforeCompletion, times(1)).doOp(eq(txState)));
        await().untilAsserted(() -> inOrder.verify(afterCompletion, times(1)).doOp(eq(txState), eq(cancelCriterion)));
    }

    @Test
    public void cleanupInvokesCancel() {
        singleThreadJTAExecutor.cleanup();
        Mockito.verify(afterCompletion, Mockito.times(1)).cancel();
    }

    @Test(expected = RuntimeException.class)
    public void doOpsInvokesAfterCompletionDoOpWhenBeforeCompletionThrows() {
        Mockito.doThrow(RuntimeException.class).when(beforeCompletion).doOp(txState);
        singleThreadJTAExecutor.doOps(txState, cancelCriterion);
        Mockito.verify(afterCompletion, Mockito.times(1)).doOp(ArgumentMatchers.eq(txState), ArgumentMatchers.eq(cancelCriterion));
    }
}

