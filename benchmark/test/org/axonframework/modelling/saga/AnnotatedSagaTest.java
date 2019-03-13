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


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.axonframework.common.AxonConfigurationException;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.messaging.MetaData;
import org.axonframework.messaging.annotation.MessageHandlingMember;
import org.axonframework.modelling.saga.metamodel.AnnotationSagaMetaModelFactory;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Allard Buijze
 * @author Sofia Guy Ang
 */
public class AnnotatedSagaTest {
    @Test
    public void testInvokeSaga() {
        AnnotatedSagaTest.StubAnnotatedSaga testSubject = new AnnotatedSagaTest.StubAnnotatedSaga();
        AnnotatedSaga<AnnotatedSagaTest.StubAnnotatedSaga> s = new AnnotatedSaga("id", Collections.emptySet(), testSubject, new AnnotationSagaMetaModelFactory().modelOf(AnnotatedSagaTest.StubAnnotatedSaga.class));
        s.doAssociateWith(new AssociationValue("propertyName", "id"));
        s.handle(new org.axonframework.eventhandling.GenericEventMessage(new AnnotatedSagaTest.RegularEvent("id")));
        s.handle(new org.axonframework.eventhandling.GenericEventMessage(new AnnotatedSagaTest.RegularEvent("wrongId")));
        s.handle(new org.axonframework.eventhandling.GenericEventMessage(new Object()));
        Assert.assertEquals(1, testSubject.invocationCount);
    }

    @Test(expected = AxonConfigurationException.class)
    public void testInvokeSaga_AssociationPropertyNotExistingInPayload() {
        AnnotatedSagaTest.SagaAssociationPropertyNotExistingInPayload testSubject = new AnnotatedSagaTest.SagaAssociationPropertyNotExistingInPayload();
        AnnotatedSaga<AnnotatedSagaTest.SagaAssociationPropertyNotExistingInPayload> s = new AnnotatedSaga("id", Collections.emptySet(), testSubject, new AnnotationSagaMetaModelFactory().modelOf(AnnotatedSagaTest.SagaAssociationPropertyNotExistingInPayload.class));
        s.doAssociateWith(new AssociationValue("propertyName", "id"));
        s.handle(new org.axonframework.eventhandling.GenericEventMessage(new AnnotatedSagaTest.EventWithoutProperties()));
    }

    @Test
    public void testInvokeSaga_MetaDataAssociationResolver() {
        AnnotatedSagaTest.StubAnnotatedSaga testSubject = new AnnotatedSagaTest.StubAnnotatedSaga();
        AnnotatedSaga<AnnotatedSagaTest.StubAnnotatedSaga> s = new AnnotatedSaga("id", Collections.emptySet(), testSubject, new AnnotationSagaMetaModelFactory().modelOf(AnnotatedSagaTest.StubAnnotatedSaga.class));
        s.doAssociateWith(new AssociationValue("propertyName", "id"));
        Map<String, Object> metaData = new HashMap<>();
        metaData.put("propertyName", "id");
        s.handle(new org.axonframework.eventhandling.GenericEventMessage(new AnnotatedSagaTest.EventWithoutProperties(), new MetaData(metaData)));
        s.handle(new org.axonframework.eventhandling.GenericEventMessage(new AnnotatedSagaTest.EventWithoutProperties()));
        Assert.assertEquals(1, testSubject.invocationCount);
    }

    @Test(expected = AxonConfigurationException.class)
    public void testInvokeSaga_ResolverWithoutNoArgConstructor() {
        AnnotatedSagaTest.SagaUsingResolverWithoutNoArgConstructor testSubject = new AnnotatedSagaTest.SagaUsingResolverWithoutNoArgConstructor();
        AnnotatedSaga<AnnotatedSagaTest.SagaUsingResolverWithoutNoArgConstructor> s = new AnnotatedSaga("id", Collections.emptySet(), testSubject, new AnnotationSagaMetaModelFactory().modelOf(AnnotatedSagaTest.SagaUsingResolverWithoutNoArgConstructor.class));
        s.doAssociateWith(new AssociationValue("propertyName", "id"));
        Map<String, Object> metaData = new HashMap<>();
        metaData.put("propertyName", "id");
        s.handle(new org.axonframework.eventhandling.GenericEventMessage(new AnnotatedSagaTest.EventWithoutProperties(), new MetaData(metaData)));
    }

    @Test
    public void testEndedAfterInvocation_BeanProperty() {
        AnnotatedSagaTest.StubAnnotatedSaga testSubject = new AnnotatedSagaTest.StubAnnotatedSaga();
        AnnotatedSaga<AnnotatedSagaTest.StubAnnotatedSaga> s = new AnnotatedSaga("id", Collections.emptySet(), testSubject, new AnnotationSagaMetaModelFactory().modelOf(AnnotatedSagaTest.StubAnnotatedSaga.class));
        s.doAssociateWith(new AssociationValue("propertyName", "id"));
        s.handle(new org.axonframework.eventhandling.GenericEventMessage(new AnnotatedSagaTest.RegularEvent("id")));
        s.handle(new org.axonframework.eventhandling.GenericEventMessage(new Object()));
        s.handle(new org.axonframework.eventhandling.GenericEventMessage(new AnnotatedSagaTest.SagaEndEvent("id")));
        Assert.assertEquals(2, testSubject.invocationCount);
        Assert.assertFalse(s.isActive());
    }

    @Test
    public void testEndedAfterInvocation_WhenAssociationIsRemoved() {
        AnnotatedSagaTest.StubAnnotatedSaga testSubject = new AnnotatedSagaTest.StubAnnotatedSagaWithExplicitAssociationRemoval();
        AnnotatedSaga<AnnotatedSagaTest.StubAnnotatedSaga> s = new AnnotatedSaga("id", Collections.emptySet(), testSubject, new AnnotationSagaMetaModelFactory().modelOf(AnnotatedSagaTest.StubAnnotatedSaga.class));
        s.doAssociateWith(new AssociationValue("propertyName", "id"));
        s.handle(new org.axonframework.eventhandling.GenericEventMessage(new AnnotatedSagaTest.RegularEvent("id")));
        s.handle(new org.axonframework.eventhandling.GenericEventMessage(new Object()));
        s.handle(new org.axonframework.eventhandling.GenericEventMessage(new AnnotatedSagaTest.SagaEndEvent("id")));
        Assert.assertEquals(2, testSubject.invocationCount);
        Assert.assertFalse(s.isActive());
    }

    @Test
    public void testEndedAfterInvocation_UniformAccessPrinciple() {
        AnnotatedSagaTest.StubAnnotatedSaga testSubject = new AnnotatedSagaTest.StubAnnotatedSaga();
        AnnotatedSaga<AnnotatedSagaTest.StubAnnotatedSaga> s = new AnnotatedSaga("id", Collections.emptySet(), testSubject, new AnnotationSagaMetaModelFactory().modelOf(AnnotatedSagaTest.StubAnnotatedSaga.class));
        s.doAssociateWith(new AssociationValue("propertyName", "id"));
        s.handle(new org.axonframework.eventhandling.GenericEventMessage(new AnnotatedSagaTest.UniformAccessEvent("id")));
        s.handle(new org.axonframework.eventhandling.GenericEventMessage(new Object()));
        s.handle(new org.axonframework.eventhandling.GenericEventMessage(new AnnotatedSagaTest.SagaEndEvent("id")));
        Assert.assertEquals(2, testSubject.invocationCount);
        Assert.assertFalse(s.isActive());
    }

    private static class StubAnnotatedSaga {
        private static final long serialVersionUID = -3224806999195676097L;

        private int invocationCount = 0;

        @SagaEventHandler(associationProperty = "propertyName")
        public void handleStubDomainEvent(AnnotatedSagaTest.RegularEvent event) {
            (invocationCount)++;
        }

        @SagaEventHandler(associationProperty = "propertyName")
        public void handleStubDomainEvent(AnnotatedSagaTest.UniformAccessEvent event) {
            (invocationCount)++;
        }

        @SagaEventHandler(associationProperty = "propertyName", associationResolver = MetaDataAssociationResolver.class)
        public void handleStubDomainEvent(AnnotatedSagaTest.EventWithoutProperties event) {
            (invocationCount)++;
        }

        @EndSaga
        @SagaEventHandler(associationProperty = "propertyName")
        public void handleStubDomainEvent(AnnotatedSagaTest.SagaEndEvent event) {
            (invocationCount)++;
        }
    }

    private static class SagaAssociationPropertyNotExistingInPayload {
        @SagaEventHandler(associationProperty = "propertyName", associationResolver = PayloadAssociationResolver.class)
        public void handleStubDomainEvent(AnnotatedSagaTest.EventWithoutProperties event) {
        }
    }

    private static class SagaUsingResolverWithoutNoArgConstructor {
        @SagaEventHandler(associationProperty = "propertyName", associationResolver = AnnotatedSagaTest.OneArgConstructorAssociationResolver.class)
        public void handleStubDomainEvent(AnnotatedSagaTest.EventWithoutProperties event) {
        }
    }

    private static class StubAnnotatedSagaWithExplicitAssociationRemoval extends AnnotatedSagaTest.StubAnnotatedSaga {
        @Override
        public void handleStubDomainEvent(AnnotatedSagaTest.SagaEndEvent event) {
            // since this method overrides a handler, it doesn't need the annotations anymore
            super.handleStubDomainEvent(event);
            SagaLifecycle.removeAssociationWith("propertyName", event.getPropertyName());
        }
    }

    private static class RegularEvent {
        private final String propertyName;

        public RegularEvent(String propertyName) {
            this.propertyName = propertyName;
        }

        public String getPropertyName() {
            return propertyName;
        }
    }

    private static class UniformAccessEvent {
        private final String propertyName;

        public UniformAccessEvent(String propertyName) {
            this.propertyName = propertyName;
        }

        public String propertyName() {
            return propertyName;
        }
    }

    private static class EventWithoutProperties {}

    private static class SagaEndEvent extends AnnotatedSagaTest.RegularEvent {
        public SagaEndEvent(String propertyName) {
            super(propertyName);
        }
    }

    private static class OneArgConstructorAssociationResolver implements AssociationResolver {
        String someField;

        public OneArgConstructorAssociationResolver(String someField) {
            this.someField = someField;
        }

        @Override
        public <T> void validate(String associationPropertyName, MessageHandlingMember<T> handler) {
        }

        @Override
        public <T> Object resolve(String associationPropertyName, EventMessage<?> message, MessageHandlingMember<T> handler) {
            return null;
        }
    }
}

