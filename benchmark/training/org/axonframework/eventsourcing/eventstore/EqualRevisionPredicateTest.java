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
package org.axonframework.eventsourcing.eventstore;


import org.axonframework.serialization.Revision;
import org.junit.Assert;
import org.junit.Test;


public class EqualRevisionPredicateTest {
    private static final String PAYLOAD = "payload";

    private static final String AGGREGATE = "aggregate";

    private static final String TYPE = "type";

    private static final String METADATA = "metadata";

    private EqualRevisionPredicate testSubject;

    @Test
    public void testSameRevisionForAggregateAndPayload() {
        Assert.assertTrue(testSubject.test(EqualRevisionPredicateTest.createEntry(EqualRevisionPredicateTest.WithAnnotationAggregate.class.getName(), "2.3-TEST")));
    }

    @Test
    public void testDifferentRevisionsForAggregateAndPayload() {
        Assert.assertFalse(testSubject.test(EqualRevisionPredicateTest.createEntry(EqualRevisionPredicateTest.WithAnnotationAggregate.class.getName(), "2.3-TEST-DIFFERENT")));
    }

    @Test
    public void testNoRevisionForAggregateAndPayload() {
        Assert.assertTrue(testSubject.test(EqualRevisionPredicateTest.createEntry(EqualRevisionPredicateTest.WithoutAnnotationAggregate.class.getName())));
    }

    @Test
    public void testNoRevisionForPayload() {
        Assert.assertFalse(testSubject.test(EqualRevisionPredicateTest.createEntry(EqualRevisionPredicateTest.WithAnnotationAggregate.class.getName())));
    }

    @Test
    public void testNoRevisionForAggregate() {
        Assert.assertFalse(testSubject.test(EqualRevisionPredicateTest.createEntry(EqualRevisionPredicateTest.WithoutAnnotationAggregate.class.getName(), "2.3-TEST")));
    }

    @Revision("2.3-TEST")
    private class WithAnnotationAggregate {}

    private class WithoutAnnotationAggregate {}
}

