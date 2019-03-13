/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.flowable.engine.test.api.event;


import FlowableEngineEventType.ENTITY_CREATED;
import FlowableEngineEventType.ENTITY_INITIALIZED;
import FlowableEngineEventType.PROCESS_COMPLETED;
import FlowableEngineEventType.PROCESS_STARTED;
import FlowableEngineEventType.TASK_COMPLETED;
import FlowableEngineEventType.TASK_CREATED;
import TransactionState.COMMITTED;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEventListener;
import org.flowable.common.engine.impl.util.CollectionUtil;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.flowable.engine.impl.test.PluggableFlowableTestCase;
import org.flowable.engine.test.Deployment;
import org.junit.jupiter.api.Test;


public class TransactionEventListenerTest extends PluggableFlowableTestCase {
    protected TransactionEventListenerTest.TestTransactionEventListener onCommitListener;

    @Test
    public void testRegularProcessExecution() {
        assertEquals(0, TransactionEventListenerTest.TestTransactionEventListener.eventsReceived.size());
        // In a 'normal' process execution, the transaction dependent event listener should
        // be similar to the normal event listener dispatching.
        deployOneTaskTestProcess();
        runtimeService.startProcessInstanceByKey("oneTaskProcess");
        int expectedCreatedEvents = 11;
        if (processEngineConfiguration.getHistoryManager().isHistoryEnabled()) {
            expectedCreatedEvents += 4;
        }
        if (processEngineConfiguration.isAsyncHistoryEnabled()) {
            waitForHistoryJobExecutorToProcessAllJobs(7000L, 200L);
        }
        assertEquals(expectedCreatedEvents, TransactionEventListenerTest.TestTransactionEventListener.eventsReceived.get(ENTITY_CREATED.name()).size());
        assertEquals(expectedCreatedEvents, TransactionEventListenerTest.TestTransactionEventListener.eventsReceived.get(ENTITY_INITIALIZED.name()).size());
        assertEquals(1, TransactionEventListenerTest.TestTransactionEventListener.eventsReceived.get(PROCESS_STARTED.name()).size());
        assertEquals(1, TransactionEventListenerTest.TestTransactionEventListener.eventsReceived.get(TASK_CREATED.name()).size());
        TransactionEventListenerTest.TestTransactionEventListener.eventsReceived.clear();
        taskService.complete(taskService.createTaskQuery().singleResult().getId());
        assertEquals(1, TransactionEventListenerTest.TestTransactionEventListener.eventsReceived.get(TASK_COMPLETED.name()).size());
        assertEquals(1, TransactionEventListenerTest.TestTransactionEventListener.eventsReceived.get(PROCESS_COMPLETED.name()).size());
        if (processEngineConfiguration.isAsyncHistoryEnabled()) {
            waitForHistoryJobExecutorToProcessAllJobs(7000L, 200L);
        }
    }

    @Test
    @Deployment
    public void testProcessExecutionWithRollback() {
        assertEquals(0, TransactionEventListenerTest.TestTransactionEventListener.eventsReceived.size());
        assertEquals(0, runtimeService.createProcessInstanceQuery().count());
        // Regular execution, no exception
        runtimeService.startProcessInstanceByKey("testProcessExecutionWithRollback", CollectionUtil.singletonMap("throwException", false));
        assertTrue(((TransactionEventListenerTest.TestTransactionEventListener.eventsReceived.size()) > 0));
        assertEquals(1, runtimeService.createProcessInstanceQuery().count());
        TransactionEventListenerTest.TestTransactionEventListener.eventsReceived.clear();
        // When process execution rolls back, the events should not be thrown, as they are only thrown on commit.
        assertThatThrownBy(() -> runtimeService.startProcessInstanceByKey("testProcessExecutionWithRollback", CollectionUtil.singletonMap("throwException", true)));
        assertEquals(0, TransactionEventListenerTest.TestTransactionEventListener.eventsReceived.size());
        assertEquals(1, runtimeService.createProcessInstanceQuery().count());
    }

    @Test
    @Deployment
    public void testProcessDefinitionDefinedEventListener() {
        // Only let the event listener of the process definition listen
        processEngineConfiguration.getEventDispatcher().removeEventListener(onCommitListener);
        TransactionEventListenerTest.TestTransactionEventListener.eventsReceived.clear();
        assertEquals(0, TransactionEventListenerTest.TestTransactionEventListener.eventsReceived.size());
        runtimeService.startProcessInstanceByKey("testProcessExecutionWithRollback", CollectionUtil.singletonMap("throwException", false));
        assertTrue(((TransactionEventListenerTest.TestTransactionEventListener.eventsReceived.size()) > 0));
    }

    public static class TestTransactionEventListener implements FlowableEventListener {
        protected String onTransaction;

        public static Map<String, List<FlowableEvent>> eventsReceived = new HashMap<>();

        public TestTransactionEventListener() {
            this.onTransaction = COMMITTED.name();
        }

        public TestTransactionEventListener(String onTransaction) {
            this.onTransaction = onTransaction;
        }

        @Override
        public void onEvent(FlowableEvent event) {
            String eventType = event.getType().name();
            if (!(TransactionEventListenerTest.TestTransactionEventListener.eventsReceived.containsKey(eventType))) {
                TransactionEventListenerTest.TestTransactionEventListener.eventsReceived.put(eventType, new ArrayList());
            }
            TransactionEventListenerTest.TestTransactionEventListener.eventsReceived.get(eventType).add(event);
        }

        @Override
        public boolean isFailOnException() {
            return false;
        }

        @Override
        public boolean isFireOnTransactionLifecycleEvent() {
            return true;
        }

        @Override
        public String getOnTransaction() {
            return onTransaction;
        }
    }

    public static class ThrowExceptionDelegate implements JavaDelegate {
        @Override
        public void execute(DelegateExecution execution) {
            boolean throwException = ((Boolean) (execution.getVariable("throwException")));
            if (throwException) {
                throw new RuntimeException();
            }
        }
    }
}

