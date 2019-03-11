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
package org.axonframework.axonserver.connector.event.axon;


import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import org.axonframework.eventhandling.DomainEventMessage;
import org.axonframework.eventhandling.GlobalSequenceTrackingToken;
import org.axonframework.eventhandling.TrackedEventMessage;
import org.axonframework.serialization.SerializedObject;
import org.axonframework.serialization.upcasting.event.EventUpcaster;
import org.axonframework.serialization.xml.XStreamSerializer;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class EventBufferTest {
    private EventUpcaster stubUpcaster;

    private EventBuffer testSubject;

    private XStreamSerializer serializer;

    private SerializedObject<byte[]> serializedObject;

    @Test
    public void testDataUpcastAndDeserialized() throws InterruptedException {
        testSubject = new EventBuffer(stubUpcaster, serializer);
        Assert.assertFalse(testSubject.hasNextAvailable());
        testSubject.push(createEventData(1L));
        Assert.assertTrue(testSubject.hasNextAvailable());
        TrackedEventMessage<?> peeked = testSubject.peek().orElseThrow(() -> new AssertionError("Expected value to be available"));
        Assert.assertEquals(new GlobalSequenceTrackingToken(1L), peeked.trackingToken());
        Assert.assertTrue((peeked instanceof DomainEventMessage<?>));
        Assert.assertTrue(testSubject.hasNextAvailable());
        Assert.assertTrue(testSubject.hasNextAvailable(1, TimeUnit.SECONDS));
        testSubject.nextAvailable();
        Assert.assertFalse(testSubject.hasNextAvailable());
        Assert.assertFalse(testSubject.hasNextAvailable(10, TimeUnit.MILLISECONDS));
        Mockito.verify(stubUpcaster).upcast(ArgumentMatchers.isA(Stream.class));
    }

    @Test
    public void testConsumptionIsRecorded() {
        stubUpcaster = ( stream) -> stream.filter(( i) -> false);
        testSubject = new EventBuffer(stubUpcaster, serializer);
        testSubject.push(createEventData(1));
        testSubject.push(createEventData(2));
        testSubject.push(createEventData(3));
        AtomicInteger consumed = new AtomicInteger();
        testSubject.registerConsumeListener(consumed::addAndGet);
        testSubject.peek();// this should consume 3 incoming messages

        Assert.assertEquals(3, consumed.get());
    }
}

