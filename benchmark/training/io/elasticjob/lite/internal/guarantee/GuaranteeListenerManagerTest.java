/**
 * Copyright 1999-2015 dangdang.com.
 * <p>
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
 * </p>
 */
package io.elasticjob.lite.internal.guarantee;


import Type.NODE_REMOVED;
import Type.NODE_UPDATED;
import io.elasticjob.lite.api.listener.AbstractDistributeOnceElasticJobListener;
import io.elasticjob.lite.api.listener.ElasticJobListener;
import io.elasticjob.lite.internal.listener.AbstractJobListener;
import io.elasticjob.lite.internal.storage.JobNodeStorage;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


public final class GuaranteeListenerManagerTest {
    @Mock
    private JobNodeStorage jobNodeStorage;

    @Mock
    private ElasticJobListener elasticJobListener;

    @Mock
    private AbstractDistributeOnceElasticJobListener distributeOnceElasticJobListener;

    private GuaranteeListenerManager guaranteeListenerManager;

    @Test
    public void assertStart() {
        guaranteeListenerManager.start();
        Mockito.verify(jobNodeStorage, Mockito.times(2)).addDataListener(ArgumentMatchers.<AbstractJobListener>any());
    }

    @Test
    public void assertStartedNodeRemovedJobListenerWhenIsNotRemoved() {
        guaranteeListenerManager.new StartedNodeRemovedJobListener().dataChanged("/test_job/guarantee/started", NODE_UPDATED, "");
        Mockito.verify(distributeOnceElasticJobListener, Mockito.times(0)).notifyWaitingTaskStart();
    }

    @Test
    public void assertStartedNodeRemovedJobListenerWhenIsNotStartedNode() {
        guaranteeListenerManager.new StartedNodeRemovedJobListener().dataChanged("/other_job/guarantee/started", NODE_REMOVED, "");
        Mockito.verify(distributeOnceElasticJobListener, Mockito.times(0)).notifyWaitingTaskStart();
    }

    @Test
    public void assertStartedNodeRemovedJobListenerWhenIsRemovedAndStartedNode() {
        guaranteeListenerManager.new StartedNodeRemovedJobListener().dataChanged("/test_job/guarantee/started", NODE_REMOVED, "");
        Mockito.verify(distributeOnceElasticJobListener).notifyWaitingTaskStart();
    }

    @Test
    public void assertCompletedNodeRemovedJobListenerWhenIsNotRemoved() {
        guaranteeListenerManager.new CompletedNodeRemovedJobListener().dataChanged("/test_job/guarantee/completed", NODE_UPDATED, "");
        Mockito.verify(distributeOnceElasticJobListener, Mockito.times(0)).notifyWaitingTaskStart();
    }

    @Test
    public void assertCompletedNodeRemovedJobListenerWhenIsNotCompletedNode() {
        guaranteeListenerManager.new CompletedNodeRemovedJobListener().dataChanged("/other_job/guarantee/completed", NODE_REMOVED, "");
        Mockito.verify(distributeOnceElasticJobListener, Mockito.times(0)).notifyWaitingTaskStart();
    }

    @Test
    public void assertCompletedNodeRemovedJobListenerWhenIsRemovedAndCompletedNode() {
        guaranteeListenerManager.new CompletedNodeRemovedJobListener().dataChanged("/test_job/guarantee/completed", NODE_REMOVED, "");
        Mockito.verify(distributeOnceElasticJobListener).notifyWaitingTaskComplete();
    }
}

