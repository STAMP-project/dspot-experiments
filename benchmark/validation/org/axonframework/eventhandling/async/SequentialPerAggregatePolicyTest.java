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
package org.axonframework.eventhandling.async;


import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Allard Buijze
 */
public class SequentialPerAggregatePolicyTest {
    @Test
    public void testSequentialIdentifier() {
        // ok, pretty useless, but everything should be tested
        SequentialPerAggregatePolicy testSubject = new SequentialPerAggregatePolicy();
        String aggregateIdentifier = UUID.randomUUID().toString();
        Object id1 = testSubject.getSequenceIdentifierFor(newStubDomainEvent(aggregateIdentifier));
        Object id2 = testSubject.getSequenceIdentifierFor(newStubDomainEvent(aggregateIdentifier));
        Object id3 = testSubject.getSequenceIdentifierFor(newStubDomainEvent(UUID.randomUUID().toString()));
        Object id4 = testSubject.getSequenceIdentifierFor(new org.axonframework.eventhandling.GenericEventMessage("bla"));
        Assert.assertEquals(id1, id2);
        Assert.assertFalse(id1.equals(id3));
        Assert.assertFalse(id2.equals(id3));
        Assert.assertNull(id4);
    }
}

