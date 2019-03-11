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
package org.axonframework.modelling.saga;


import Segment.ROOT_SEGMENT;
import java.util.Collections;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.eventhandling.ListenerInvocationErrorHandler;
import org.axonframework.eventhandling.Segment;
import org.axonframework.messaging.ResultMessage;
import org.axonframework.messaging.unitofwork.UnitOfWork;
import org.axonframework.modelling.utils.MockException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static SagaCreationPolicy.NONE;


public class SagaManagerTest {
    private AbstractSagaManager<Object> testSubject;

    private SagaRepository<Object> mockSagaRepository;

    private ListenerInvocationErrorHandler mockErrorHandler;

    private Saga<Object> mockSaga1;

    private Saga<Object> mockSaga2;

    private Saga<Object> mockSaga3;

    private AssociationValue associationValue;

    @Test
    public void testSagasLoaded() throws Exception {
        EventMessage<?> event = new org.axonframework.eventhandling.GenericEventMessage(new Object());
        UnitOfWork<? extends EventMessage<?>> unitOfWork = new org.axonframework.messaging.unitofwork.DefaultUnitOfWork(event);
        unitOfWork.executeWithResult(() -> {
            testSubject.handle(event, Segment.ROOT_SEGMENT);
            return null;
        });
        Mockito.verify(mockSagaRepository).find(associationValue);
        Mockito.verify(mockSaga1).handle(event);
        Mockito.verify(mockSaga2).handle(event);
        Mockito.verify(mockSaga3, Mockito.never()).handle(event);
    }

    @Test
    public void testExceptionPropagated() throws Exception {
        EventMessage<?> event = new org.axonframework.eventhandling.GenericEventMessage(new Object());
        MockException toBeThrown = new MockException();
        Mockito.doThrow(toBeThrown).when(mockSaga1).handle(event);
        Mockito.doThrow(toBeThrown).when(mockErrorHandler).onError(toBeThrown, event, mockSaga1);
        UnitOfWork<? extends EventMessage<?>> unitOfWork = new org.axonframework.messaging.unitofwork.DefaultUnitOfWork(event);
        ResultMessage<Object> resultMessage = unitOfWork.executeWithResult(() -> {
            testSubject.handle(event, Segment.ROOT_SEGMENT);
            return null;
        });
        if (resultMessage.isExceptional()) {
            Throwable e = resultMessage.exceptionResult();
            e.printStackTrace();
            Assert.assertEquals("Mock", e.getMessage());
        } else {
            Assert.fail("Expected exception to be propagated");
        }
        Mockito.verify(mockSaga1, Mockito.times(1)).handle(event);
        Mockito.verify(mockErrorHandler).onError(toBeThrown, event, mockSaga1);
    }

    @Test
    public void testSagaIsCreatedInRootSegment() throws Exception {
        testSubject = SagaManagerTest.TestableAbstractSagaManager.builder().sagaRepository(mockSagaRepository).listenerInvocationErrorHandler(mockErrorHandler).sagaCreationPolicy(SagaCreationPolicy.IF_NONE_FOUND).associationValue(new AssociationValue("someKey", "someValue")).build();
        EventMessage<?> event = new org.axonframework.eventhandling.GenericEventMessage(new Object());
        Mockito.when(mockSagaRepository.createInstance(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(mockSaga1);
        Mockito.when(mockSagaRepository.find(ArgumentMatchers.any())).thenReturn(Collections.emptySet());
        testSubject.handle(event, ROOT_SEGMENT);
        Mockito.verify(mockSagaRepository).createInstance(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void testSagaIsOnlyCreatedInSegmentMatchingAssociationValue() throws Exception {
        testSubject = SagaManagerTest.TestableAbstractSagaManager.builder().sagaRepository(mockSagaRepository).listenerInvocationErrorHandler(mockErrorHandler).sagaCreationPolicy(SagaCreationPolicy.IF_NONE_FOUND).associationValue(new AssociationValue("someKey", "someValue")).build();
        Segment[] segments = ROOT_SEGMENT.split();
        Segment matchingSegment = (segments[0].matches("someValue")) ? segments[0] : segments[1];
        Segment otherSegment = (segments[0].matches("someValue")) ? segments[1] : segments[0];
        EventMessage<?> event = new org.axonframework.eventhandling.GenericEventMessage(new Object());
        ArgumentCaptor<String> createdSaga = ArgumentCaptor.forClass(String.class);
        Mockito.when(mockSagaRepository.createInstance(createdSaga.capture(), ArgumentMatchers.any())).thenReturn(mockSaga1);
        Mockito.when(mockSagaRepository.find(ArgumentMatchers.any())).thenReturn(Collections.emptySet());
        testSubject.handle(event, otherSegment);
        Mockito.verify(mockSagaRepository, Mockito.never()).createInstance(ArgumentMatchers.any(), ArgumentMatchers.any());
        testSubject.handle(event, matchingSegment);
        Mockito.verify(mockSagaRepository).createInstance(ArgumentMatchers.any(), ArgumentMatchers.any());
        createdSaga.getAllValues().forEach(( sagaId) -> Assert.assertTrue(("Saga ID doesn't match segment that should have created it: " + sagaId), matchingSegment.matches(sagaId)));
        createdSaga.getAllValues().forEach(( sagaId) -> Assert.assertFalse(("Saga ID matched against the wrong segment: " + sagaId), otherSegment.matches(sagaId)));
    }

    @Test
    public void testExceptionSuppressed() throws Exception {
        EventMessage<?> event = new org.axonframework.eventhandling.GenericEventMessage(new Object());
        MockException toBeThrown = new MockException();
        Mockito.doThrow(toBeThrown).when(mockSaga1).handle(event);
        testSubject.handle(event, ROOT_SEGMENT);
        Mockito.verify(mockSaga1).handle(event);
        Mockito.verify(mockSaga2).handle(event);
        Mockito.verify(mockSaga3, Mockito.never()).handle(event);
        Mockito.verify(mockErrorHandler).onError(toBeThrown, event, mockSaga1);
    }

    private static class TestableAbstractSagaManager extends AbstractSagaManager<Object> {
        private final SagaCreationPolicy sagaCreationPolicy;

        private final AssociationValue associationValue;

        private TestableAbstractSagaManager(SagaManagerTest.TestableAbstractSagaManager.Builder builder) {
            super(builder);
            this.sagaCreationPolicy = builder.sagaCreationPolicy;
            this.associationValue = builder.associationValue;
        }

        public static SagaManagerTest.TestableAbstractSagaManager.Builder builder() {
            return new SagaManagerTest.TestableAbstractSagaManager.Builder();
        }

        @Override
        public boolean canHandle(EventMessage<?> eventMessage, Segment segment) {
            return true;
        }

        @Override
        protected SagaInitializationPolicy getSagaCreationPolicy(EventMessage<?> event) {
            return new SagaInitializationPolicy(sagaCreationPolicy, associationValue);
        }

        @Override
        protected Set<AssociationValue> extractAssociationValues(EventMessage<?> event) {
            return Collections.singleton(associationValue);
        }

        public static class Builder extends AbstractSagaManager.Builder<Object> {
            private SagaCreationPolicy sagaCreationPolicy = NONE;

            private AssociationValue associationValue;

            private Builder() {
                super.sagaType(Object.class);
                super.sagaFactory(Object::new);
            }

            @Override
            public SagaManagerTest.TestableAbstractSagaManager.Builder sagaRepository(SagaRepository<Object> sagaRepository) {
                super.sagaRepository(sagaRepository);
                return this;
            }

            @Override
            public SagaManagerTest.TestableAbstractSagaManager.Builder sagaType(Class<Object> sagaType) {
                super.sagaType(sagaType);
                return this;
            }

            @Override
            public SagaManagerTest.TestableAbstractSagaManager.Builder sagaFactory(Supplier<Object> sagaFactory) {
                super.sagaFactory(sagaFactory);
                return this;
            }

            @Override
            public SagaManagerTest.TestableAbstractSagaManager.Builder listenerInvocationErrorHandler(ListenerInvocationErrorHandler listenerInvocationErrorHandler) {
                super.listenerInvocationErrorHandler(listenerInvocationErrorHandler);
                return this;
            }

            private SagaManagerTest.TestableAbstractSagaManager.Builder sagaCreationPolicy(SagaCreationPolicy sagaCreationPolicy) {
                this.sagaCreationPolicy = sagaCreationPolicy;
                return this;
            }

            private SagaManagerTest.TestableAbstractSagaManager.Builder associationValue(AssociationValue associationValue) {
                this.associationValue = associationValue;
                return this;
            }

            public SagaManagerTest.TestableAbstractSagaManager build() {
                return new SagaManagerTest.TestableAbstractSagaManager(this);
            }
        }
    }
}

