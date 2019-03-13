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
package org.axonframework.messaging.annotation;


import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.axonframework.messaging.Message;
import org.junit.Assert;
import org.junit.Test;


public class HandlerComparatorTest {
    private MessageHandlingMember<?> stringHandler;

    private MessageHandlingMember<?> objectHandler;

    private MessageHandlingMember<?> longHandler;

    private MessageHandlingMember<?> numberHandler;

    private Comparator<MessageHandlingMember<?>> testSubject;

    @Test
    public void testSubclassesBeforeSuperclasses() {
        Assert.assertTrue("String should appear before Object", ((testSubject.compare(stringHandler, objectHandler)) < 0));
        Assert.assertTrue("String should appear before Object", ((testSubject.compare(objectHandler, stringHandler)) > 0));
        Assert.assertTrue("Number should appear before Object", ((testSubject.compare(numberHandler, objectHandler)) < 0));
        Assert.assertTrue("Number should appear before Object", ((testSubject.compare(objectHandler, numberHandler)) > 0));
        Assert.assertTrue("Long should appear before Number", ((testSubject.compare(longHandler, numberHandler)) < 0));
        Assert.assertTrue("Long should appear before Number", ((testSubject.compare(numberHandler, longHandler)) > 0));
        Assert.assertTrue("Long should appear before Object", ((testSubject.compare(longHandler, objectHandler)) < 0));
        Assert.assertTrue("Long should appear before Object", ((testSubject.compare(objectHandler, longHandler)) > 0));
    }

    @Test
    public void testHandlersIsEqualWithItself() {
        Assert.assertEquals(0, testSubject.compare(stringHandler, stringHandler));
        Assert.assertEquals(0, testSubject.compare(objectHandler, objectHandler));
        Assert.assertEquals(0, testSubject.compare(longHandler, longHandler));
        Assert.assertEquals(0, testSubject.compare(numberHandler, numberHandler));
        Assert.assertNotEquals(0, testSubject.compare(stringHandler, objectHandler));
        Assert.assertNotEquals(0, testSubject.compare(longHandler, stringHandler));
        Assert.assertNotEquals(0, testSubject.compare(numberHandler, stringHandler));
        Assert.assertNotEquals(0, testSubject.compare(objectHandler, longHandler));
        Assert.assertNotEquals(0, testSubject.compare(objectHandler, numberHandler));
    }

    @Test
    public void testHandlersSortedCorrectly() {
        List<MessageHandlingMember<?>> members = new java.util.ArrayList(Arrays.asList(objectHandler, numberHandler, stringHandler, longHandler));
        members.sort(this.testSubject);
        Assert.assertTrue(((members.indexOf(longHandler)) < (members.indexOf(numberHandler))));
        Assert.assertEquals(3, members.indexOf(objectHandler));
    }

    @Test
    public void testNotInSameHierarchyUsesPriorityBasedEvaluation() {
        Assert.assertTrue("Number should appear before String based on priority", ((testSubject.compare(numberHandler, stringHandler)) < 0));
        Assert.assertTrue("Number should appear before String based on priority", ((testSubject.compare(stringHandler, numberHandler)) > 0));
    }

    private static class StubMessageHandlingMember implements MessageHandlingMember<Object> {
        private final Class<?> payloadType;

        private final int priority;

        public StubMessageHandlingMember(Class<?> payloadType, int priority) {
            this.payloadType = payloadType;
            this.priority = priority;
        }

        @Override
        public Class<?> payloadType() {
            return payloadType;
        }

        @Override
        public int priority() {
            return priority;
        }

        @Override
        public boolean canHandle(Message<?> message) {
            throw new UnsupportedOperationException("Not implemented yet");
        }

        @Override
        public Object handle(Message<?> message, Object target) {
            throw new UnsupportedOperationException("Not implemented yet");
        }

        @Override
        public <HT> Optional<HT> unwrap(Class<HT> handlerType) {
            return Optional.empty();
        }

        @Override
        public Optional<Map<String, Object>> annotationAttributes(Class<? extends Annotation> annotationType) {
            return Optional.empty();
        }

        @Override
        public boolean hasAnnotation(Class<? extends Annotation> annotationType) {
            return false;
        }
    }
}

