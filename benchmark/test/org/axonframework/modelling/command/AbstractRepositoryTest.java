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
package org.axonframework.modelling.command;


import junit.framework.Assert;
import junit.framework.TestCase;
import org.axonframework.deadline.DeadlineMessage;
import org.axonframework.deadline.GenericDeadlineMessage;
import org.axonframework.messaging.Message;
import org.axonframework.messaging.unitofwork.CurrentUnitOfWork;
import org.axonframework.messaging.unitofwork.UnitOfWork;
import org.axonframework.modelling.command.inspection.AnnotatedAggregate;
import org.axonframework.modelling.saga.SagaScopeDescriptor;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;


/**
 *
 *
 * @author Allard Buijze
 */
public class AbstractRepositoryTest {
    private static final String AGGREGATE_ID = "some-identifier";

    private AbstractRepository testSubject;

    private AnnotatedAggregate<JpaAggregate> spiedAggregate;

    private Message<?> failureMessage = null;

    @Test
    public void testAggregateTypeVerification_CorrectType() throws Exception {
        // noinspection unchecked
        testSubject.newInstance(() -> new JpaAggregate("hi"));
    }

    @Test
    public void testAggregateTypeVerification_SubclassesAreAllowed() throws Exception {
        // noinspection unchecked
        testSubject.newInstance(() -> new JpaAggregate("hi") {});
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAggregateTypeVerification_WrongType() throws Exception {
        // noinspection unchecked
        testSubject.newInstance(() -> "Not allowed");
    }

    @Test
    public void testCanResolveReturnsTrueForMatchingAggregateDescriptor() {
        TestCase.assertTrue(testSubject.canResolve(new AggregateScopeDescriptor(JpaAggregate.class.getSimpleName(), AbstractRepositoryTest.AGGREGATE_ID)));
    }

    @Test
    public void testCanResolveReturnsFalseNonAggregateScopeDescriptorImplementation() {
        Assert.assertFalse(testSubject.canResolve(new SagaScopeDescriptor("some-saga-type", AbstractRepositoryTest.AGGREGATE_ID)));
    }

    @Test
    public void testCanResolveReturnsFalseForNonMatchingAggregateType() {
        Assert.assertFalse(testSubject.canResolve(new AggregateScopeDescriptor("other-non-matching-type", AbstractRepositoryTest.AGGREGATE_ID)));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSendWorksAsExpected() throws Exception {
        DeadlineMessage<String> testMsg = GenericDeadlineMessage.asDeadlineMessage("deadline-name", "payload");
        AggregateScopeDescriptor testDescriptor = new AggregateScopeDescriptor(JpaAggregate.class.getSimpleName(), AbstractRepositoryTest.AGGREGATE_ID);
        testSubject.send(testMsg, testDescriptor);
        Mockito.verify(spiedAggregate).handle(testMsg);
    }

    @SuppressWarnings("unchecked")
    @Test(expected = IllegalArgumentException.class)
    public void testSendThrowsIllegalArgumentExceptionIfHandleFails() throws Exception {
        AggregateScopeDescriptor testDescriptor = new AggregateScopeDescriptor(JpaAggregate.class.getSimpleName(), AbstractRepositoryTest.AGGREGATE_ID);
        testSubject.send(failureMessage, testDescriptor);
        Mockito.verify(spiedAggregate).handle(failureMessage);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSendFailsSilentlyOnAggregateNotFoundException() throws Exception {
        DeadlineMessage<String> testMsg = GenericDeadlineMessage.asDeadlineMessage("deadline-name", "payload");
        AggregateScopeDescriptor testDescriptor = new AggregateScopeDescriptor(JpaAggregate.class.getSimpleName(), "some-other-aggregate-id");
        testSubject.send(testMsg, testDescriptor);
        Mockito.verifyZeroInteractions(spiedAggregate);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCheckedExceptionFromConstructorDoesNotAttemptToStoreAggregate() throws Exception {
        // committing the unit of work does not throw an exception
        UnitOfWork uow = CurrentUnitOfWork.get();
        uow.executeWithResult(() -> testSubject.newInstance(() -> {
            throw new Exception("Throwing checked exception");
        }), RuntimeException.class::isInstance);
        Assert.assertFalse(uow.isActive());
        Assert.assertFalse(uow.isRolledBack());
        TestCase.assertTrue(uow.getExecutionResult().isExceptionResult());
        assertEquals("Throwing checked exception", uow.getExecutionResult().getExceptionResult().getMessage());
    }
}

