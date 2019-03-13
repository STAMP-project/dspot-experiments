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


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.axonframework.eventhandling.DomainEventMessage;
import org.junit.Assert;
import org.junit.Test;


public class FilteringDomainEventStreamTest {
    private DomainEventMessage event1;

    private DomainEventMessage event2;

    private DomainEventMessage event3;

    @Test
    public void testForEachRemainingType1() {
        List<DomainEventMessage> expectedMessages = Arrays.asList(event1);
        DomainEventStream concat = // Initial stream - add all elements
        new FilteringDomainEventStream(DomainEventStream.of(event1, event2, event3), ( e) -> e.getType().equals("type"));
        List<DomainEventMessage<?>> actualMessages = new ArrayList<>();
        concat.forEachRemaining(actualMessages::add);
        Assert.assertEquals(expectedMessages, actualMessages);
    }

    @Test
    public void testForEachRemainingType2() {
        List<DomainEventMessage> expectedMessages = Arrays.asList(event2, event3);
        DomainEventStream concat = // Initial stream - add all elements
        new FilteringDomainEventStream(DomainEventStream.of(event1, event2, event3), ( e) -> e.getType().equals("type2"));
        List<DomainEventMessage<?>> actualMessages = new ArrayList<>();
        concat.forEachRemaining(actualMessages::add);
        Assert.assertEquals(expectedMessages, actualMessages);
    }
}

