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


import java.util.function.Consumer;
import java.util.function.Function;
import javax.inject.Inject;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.modelling.utils.MockException;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Allard Buijze
 */
public class SimpleResourceInjectorTest {
    private SimpleResourceInjector testSubject;

    @Test
    public void testInjectFieldResource() {
        SimpleResourceInjectorTest.SomeFieldResource expectedFieldResource = new SimpleResourceInjectorTest.SomeFieldResource();
        testSubject = new SimpleResourceInjector(expectedFieldResource);
        final SimpleResourceInjectorTest.StubSaga saga = new SimpleResourceInjectorTest.StubSaga();
        testSubject.injectResources(saga);
        Assert.assertNull(saga.getSomeWeirdResource());
        Assert.assertSame(expectedFieldResource, saga.getSomeFieldResource());
    }

    @Test
    public void testInjectMethodResource() {
        final SimpleResourceInjectorTest.SomeMethodResource expectedMethodResource = new SimpleResourceInjectorTest.SomeMethodResource();
        testSubject = new SimpleResourceInjector(expectedMethodResource);
        final SimpleResourceInjectorTest.StubSaga saga = new SimpleResourceInjectorTest.StubSaga();
        testSubject.injectResources(saga);
        Assert.assertNull(saga.getSomeWeirdResource());
        Assert.assertSame(expectedMethodResource, saga.getSomeMethodResource());
    }

    @Test
    public void testInjectFieldAndMethodResources() {
        final SimpleResourceInjectorTest.SomeFieldResource expectedFieldResource = new SimpleResourceInjectorTest.SomeFieldResource();
        final SimpleResourceInjectorTest.SomeMethodResource expectedMethodResource = new SimpleResourceInjectorTest.SomeMethodResource();
        testSubject = new SimpleResourceInjector(expectedFieldResource, expectedMethodResource);
        final SimpleResourceInjectorTest.StubSaga saga = new SimpleResourceInjectorTest.StubSaga();
        testSubject.injectResources(saga);
        Assert.assertNull(saga.getSomeWeirdResource());
        Assert.assertSame(expectedFieldResource, saga.getSomeFieldResource());
        Assert.assertSame(expectedMethodResource, saga.getSomeMethodResource());
    }

    @Test
    public void testInjectResource_ExceptionsIgnored() {
        final SimpleResourceInjectorTest.SomeMethodResource resource = new SimpleResourceInjectorTest.SomeMethodResource();
        testSubject = new SimpleResourceInjector(resource, new SimpleResourceInjectorTest.SomeWeirdResource());
        final SimpleResourceInjectorTest.StubSaga saga = new SimpleResourceInjectorTest.StubSaga();
        testSubject.injectResources(saga);
        Assert.assertNull(saga.getSomeWeirdResource());
        Assert.assertSame(resource, saga.getSomeMethodResource());
    }

    private static class StubSaga implements Saga<SimpleResourceInjectorTest.StubSaga> {
        @Inject
        private SimpleResourceInjectorTest.SomeFieldResource someFieldResource;

        private SimpleResourceInjectorTest.SomeMethodResource someMethodResource;

        private SimpleResourceInjectorTest.SomeWeirdResource someWeirdResource;

        @Override
        public String getSagaIdentifier() {
            return "id";
        }

        @Override
        public AssociationValues getAssociationValues() {
            return new AssociationValuesImpl();
        }

        @Override
        public <R> R invoke(Function<SimpleResourceInjectorTest.StubSaga, R> invocation) {
            return invocation.apply(this);
        }

        @Override
        public void execute(Consumer<SimpleResourceInjectorTest.StubSaga> invocation) {
            invocation.accept(this);
        }

        @Override
        public boolean canHandle(EventMessage<?> event) {
            return true;
        }

        @Override
        public Object handle(EventMessage event) {
            return null;
        }

        @Override
        public boolean isActive() {
            return true;
        }

        public SimpleResourceInjectorTest.SomeFieldResource getSomeFieldResource() {
            return someFieldResource;
        }

        public SimpleResourceInjectorTest.SomeMethodResource getSomeMethodResource() {
            return someMethodResource;
        }

        @Inject
        public void setSomeMethodResource(SimpleResourceInjectorTest.SomeMethodResource someMethodResource) {
            this.someMethodResource = someMethodResource;
        }

        public SimpleResourceInjectorTest.SomeWeirdResource getSomeWeirdResource() {
            return someWeirdResource;
        }

        public void setSomeWeirdResource(SimpleResourceInjectorTest.SomeWeirdResource someWeirdResource) {
            throw new MockException();
        }
    }

    private static class SomeFieldResource {}

    private static class SomeMethodResource {}

    private static class SomeWeirdResource {}
}

