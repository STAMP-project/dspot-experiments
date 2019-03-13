/**
 * Copyright (c) 2010-2018. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.axonframework.messaging.unitofwork;


import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import junit.framework.TestCase;
import org.axonframework.messaging.GenericResultMessage;
import org.axonframework.messaging.Message;
import org.axonframework.utils.MockException;
import org.junit.Test;


/**
 *
 *
 * @author Rene de Waele
 */
public class BatchingUnitOfWorkTest {
    private List<BatchingUnitOfWorkTest.PhaseTransition> transitions;

    private BatchingUnitOfWork<?> subject;

    @Test
    public void testExecuteTask() throws Exception {
        List<Message<?>> messages = Arrays.asList(BatchingUnitOfWorkTest.toMessage(0), BatchingUnitOfWorkTest.toMessage(1), BatchingUnitOfWorkTest.toMessage(2));
        subject = new BatchingUnitOfWork(messages);
        subject.executeWithResult(() -> {
            registerListeners(subject);
            return resultFor(subject.getMessage());
        });
        validatePhaseTransitions(Arrays.asList(PREPARE_COMMIT, COMMIT, AFTER_COMMIT, CLEANUP), messages);
        Map<Message<?>, ExecutionResult> expectedResults = new HashMap<>();
        messages.forEach(( m) -> expectedResults.put(m, new ExecutionResult(asResultMessage(resultFor(m)))));
        assertExecutionResults(expectedResults, subject.getExecutionResults());
    }

    @Test
    public void testRollback() {
        List<Message<?>> messages = Arrays.asList(BatchingUnitOfWorkTest.toMessage(0), BatchingUnitOfWorkTest.toMessage(1), BatchingUnitOfWorkTest.toMessage(2));
        subject = new BatchingUnitOfWork(messages);
        MockException e = new MockException();
        try {
            subject.executeWithResult(() -> {
                registerListeners(subject);
                if (subject.getMessage().getPayload().equals(1)) {
                    throw e;
                }
                return resultFor(subject.getMessage());
            });
        } catch (Exception ignored) {
        }
        validatePhaseTransitions(Arrays.asList(ROLLBACK, CLEANUP), messages.subList(0, 2));
        Map<Message<?>, ExecutionResult> expectedResult = new HashMap<>();
        messages.forEach(( m) -> expectedResult.put(m, new ExecutionResult(asResultMessage(e))));
        assertExecutionResults(expectedResult, subject.getExecutionResults());
    }

    @Test
    public void testSuppressedExceptionOnRollback() {
        List<Message<?>> messages = Arrays.asList(BatchingUnitOfWorkTest.toMessage(0), BatchingUnitOfWorkTest.toMessage(1), BatchingUnitOfWorkTest.toMessage(2));
        subject = new BatchingUnitOfWork(messages);
        MockException taskException = new MockException("task exception");
        MockException commitException = new MockException("commit exception");
        try {
            subject.executeWithResult(() -> {
                registerListeners(subject);
                if (subject.getMessage().getPayload().equals(2)) {
                    subject.addHandler(PREPARE_COMMIT, ( u) -> {
                        throw commitException;
                    });
                    throw taskException;
                }
                return resultFor(subject.getMessage());
            }, ( e) -> false);
        } catch (Exception ignored) {
        }
        validatePhaseTransitions(Arrays.asList(PREPARE_COMMIT, ROLLBACK, CLEANUP), messages);
        Map<Message<?>, ExecutionResult> expectedResult = new HashMap<>();
        expectedResult.put(messages.get(0), new ExecutionResult(GenericResultMessage.asResultMessage(commitException)));
        expectedResult.put(messages.get(1), new ExecutionResult(GenericResultMessage.asResultMessage(commitException)));
        expectedResult.put(messages.get(2), new ExecutionResult(GenericResultMessage.asResultMessage(taskException)));
        assertExecutionResults(expectedResult, subject.getExecutionResults());
        TestCase.assertSame(commitException, taskException.getSuppressed()[0]);
    }

    private static class PhaseTransition {
        private final Phase.Phase phase;

        private final Message<?> message;

        public PhaseTransition(Message<?> message, UnitOfWork.Phase phase) {
            this.message = message;
            this.phase = phase;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o)
                return true;

            if ((o == null) || ((getClass()) != (o.getClass())))
                return false;

            BatchingUnitOfWorkTest.PhaseTransition that = ((BatchingUnitOfWorkTest.PhaseTransition) (o));
            return ((phase) == (that.phase)) && (Objects.equals(message, that.message));
        }

        @Override
        public int hashCode() {
            return Objects.hash(phase, message);
        }

        @Override
        public String toString() {
            return ((phase) + " -> ") + (message.getPayload());
        }
    }
}

