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
package org.axonframework.modelling.saga.metamodel;


import java.util.Optional;
import junit.framework.TestCase;
import org.axonframework.modelling.saga.AssociationValue;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.junit.Assert;
import org.junit.Test;


public class AnnotationSagaMetaModelFactoryTest {
    private AnnotationSagaMetaModelFactory testSubject;

    @Test
    public void testInspectSaga() {
        SagaModel<AnnotationSagaMetaModelFactoryTest.MySaga> sagaModel = testSubject.modelOf(AnnotationSagaMetaModelFactoryTest.MySaga.class);
        Optional<AssociationValue> actual = sagaModel.resolveAssociation(asEventMessage(new AnnotationSagaMetaModelFactoryTest.MySagaStartEvent("value")));
        Assert.assertTrue(actual.isPresent());
        TestCase.assertEquals("value", actual.get().getValue());
        TestCase.assertEquals("property", actual.get().getKey());
    }

    public static class MySaga {
        @StartSaga
        @SagaEventHandler(associationProperty = "property")
        public void handle(AnnotationSagaMetaModelFactoryTest.MySagaStartEvent event) {
        }

        @SagaEventHandler(associationProperty = "property")
        public void handle(AnnotationSagaMetaModelFactoryTest.MySagaUpdateEvent event) {
        }

        @SagaEventHandler(associationProperty = "property")
        public void handle(AnnotationSagaMetaModelFactoryTest.MySagaEndEvent event) {
        }
    }

    public abstract static class MySagaEvent {
        private final String property;

        public MySagaEvent(String property) {
            this.property = property;
        }

        public String getProperty() {
            return property;
        }
    }

    private static class MySagaStartEvent extends AnnotationSagaMetaModelFactoryTest.MySagaEvent {
        public MySagaStartEvent(String property) {
            super(property);
        }
    }

    private static class MySagaUpdateEvent extends AnnotationSagaMetaModelFactoryTest.MySagaEvent {
        public MySagaUpdateEvent(String property) {
            super(property);
        }
    }

    private static class MySagaEndEvent extends AnnotationSagaMetaModelFactoryTest.MySagaEvent {
        public MySagaEndEvent(String property) {
            super(property);
        }
    }
}

