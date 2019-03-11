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
package org.axonframework.eventhandling.scheduling.quartz;


import QuartzEventScheduler.DirectEventJobDataBinder;
import java.time.temporal.ChronoUnit;
import java.util.function.Function;
import java.util.function.Predicate;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.eventhandling.GenericEventMessage;
import org.axonframework.messaging.MetaData;
import org.axonframework.serialization.Serializer;
import org.axonframework.serialization.SimpleSerializedObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.quartz.JobDataMap;


@RunWith(Parameterized.class)
public class DirectEventJobDataBinderTest {
    private static final String TEST_EVENT_PAYLOAD = "event-payload";

    private DirectEventJobDataBinder testSubject;

    private final Serializer serializer;

    private final Function<Class, String> expectedSerializedClassType;

    private final Predicate<Object> revisionMatcher;

    private final EventMessage<String> testEventMessage;

    private final MetaData testMetaData;

    // Test name used to give sensible name to parameterized test
    @SuppressWarnings("unused")
    public DirectEventJobDataBinderTest(String testName, Serializer serializer, Function<Class, String> expectedSerializedClassType, Predicate<Object> revisionMatcher) {
        this.serializer = Mockito.spy(serializer);
        this.expectedSerializedClassType = expectedSerializedClassType;
        this.revisionMatcher = revisionMatcher;
        EventMessage<String> testEventMessage = GenericEventMessage.asEventMessage(DirectEventJobDataBinderTest.TEST_EVENT_PAYLOAD);
        testMetaData = MetaData.with("some-key", "some-value");
        this.testEventMessage = testEventMessage.withMetaData(testMetaData);
        testSubject = new QuartzEventScheduler.DirectEventJobDataBinder(this.serializer);
    }

    @Test
    public void testEventMessageToJobData() {
        JobDataMap result = testSubject.toJobData(testEventMessage);
        Assert.assertEquals(testEventMessage.getIdentifier(), result.get(MESSAGE_ID));
        Assert.assertEquals(testEventMessage.getTimestamp().toEpochMilli(), result.get(MESSAGE_TIMESTAMP));
        String expectedPayloadType = expectedSerializedClassType.apply(testEventMessage.getPayloadType());
        Assert.assertEquals(expectedPayloadType, result.get(MESSAGE_TYPE));
        Object resultRevision = result.get(MESSAGE_REVISION);
        Assert.assertTrue(revisionMatcher.test(resultRevision));
        Assert.assertNotNull(result.get(SERIALIZED_MESSAGE_PAYLOAD));
        Assert.assertNotNull(result.get(MESSAGE_METADATA));
        Mockito.verify(serializer).serialize(DirectEventJobDataBinderTest.TEST_EVENT_PAYLOAD, byte[].class);
        Mockito.verify(serializer).serialize(testMetaData, byte[].class);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testEventMessageFromJobData() {
        JobDataMap testJobDataMap = testSubject.toJobData(testEventMessage);
        Object result = testSubject.fromJobData(testJobDataMap);
        Assert.assertTrue((result instanceof EventMessage));
        EventMessage<String> resultEventMessage = ((EventMessage<String>) (result));
        Assert.assertEquals(testEventMessage.getIdentifier(), resultEventMessage.getIdentifier());
        Assert.assertEquals(testEventMessage.getTimestamp().truncatedTo(ChronoUnit.MILLIS), resultEventMessage.getTimestamp());
        Assert.assertEquals(testEventMessage.getPayload(), resultEventMessage.getPayload());
        Assert.assertEquals(testEventMessage.getPayloadType(), resultEventMessage.getPayloadType());
        Assert.assertEquals(testEventMessage.getMetaData(), resultEventMessage.getMetaData());
        Mockito.verify(serializer, Mockito.times(2)).deserialize(((SimpleSerializedObject<?>) (ArgumentMatchers.argThat(this::assertEventMessageSerializedObject))));
    }
}

