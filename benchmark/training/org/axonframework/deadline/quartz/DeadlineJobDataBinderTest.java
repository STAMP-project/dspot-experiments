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
package org.axonframework.deadline.quartz;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import org.axonframework.deadline.DeadlineMessage;
import org.axonframework.deadline.GenericDeadlineMessage;
import org.axonframework.messaging.MetaData;
import org.axonframework.messaging.ScopeDescriptor;
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
public class DeadlineJobDataBinderTest {
    private static final String TEST_DEADLINE_NAME = "deadline-name";

    private static final String TEST_DEADLINE_PAYLOAD = "deadline-payload";

    private final Serializer serializer;

    private final Function<Class, String> expectedSerializedClassType;

    private final Predicate<Object> revisionMatcher;

    private final DeadlineMessage<String> testDeadlineMessage;

    private final MetaData testMetaData;

    private final ScopeDescriptor testDeadlineScope;

    // Test name used to give sensible name to parameterized test
    @SuppressWarnings("unused")
    public DeadlineJobDataBinderTest(String testName, Serializer serializer, Function<Class, String> expectedSerializedClassType, Predicate<Object> revisionMatcher) {
        this.serializer = Mockito.spy(serializer);
        this.expectedSerializedClassType = expectedSerializedClassType;
        this.revisionMatcher = revisionMatcher;
        DeadlineMessage<String> testDeadlineMessage = GenericDeadlineMessage.asDeadlineMessage(DeadlineJobDataBinderTest.TEST_DEADLINE_NAME, DeadlineJobDataBinderTest.TEST_DEADLINE_PAYLOAD);
        testMetaData = MetaData.with("some-key", "some-value");
        this.testDeadlineMessage = testDeadlineMessage.withMetaData(testMetaData);
        testDeadlineScope = new DeadlineJobDataBinderTest.TestScopeDescriptor("aggregate-type", "aggregate-identifier");
    }

    @Test
    public void testToJobData() {
        JobDataMap result = toJobData(serializer, testDeadlineMessage, testDeadlineScope);
        Assert.assertEquals(DeadlineJobDataBinderTest.TEST_DEADLINE_NAME, result.get(DEADLINE_NAME));
        Assert.assertEquals(testDeadlineMessage.getIdentifier(), result.get(MESSAGE_ID));
        Assert.assertEquals(testDeadlineMessage.getTimestamp().toEpochMilli(), result.get(MESSAGE_TIMESTAMP));
        String expectedPayloadType = expectedSerializedClassType.apply(testDeadlineMessage.getPayloadType());
        Assert.assertEquals(expectedPayloadType, result.get(MESSAGE_TYPE));
        Object resultRevision = result.get(MESSAGE_REVISION);
        Assert.assertTrue(revisionMatcher.test(resultRevision));
        Assert.assertNotNull(result.get(SERIALIZED_MESSAGE_PAYLOAD));
        Assert.assertNotNull(result.get(MESSAGE_METADATA));
        Assert.assertNotNull(result.get(SERIALIZED_DEADLINE_SCOPE));
        Assert.assertEquals(testDeadlineScope.getClass().getName(), result.get(SERIALIZED_DEADLINE_SCOPE_CLASS_NAME));
        Mockito.verify(serializer).serialize(DeadlineJobDataBinderTest.TEST_DEADLINE_PAYLOAD, byte[].class);
        Mockito.verify(serializer).serialize(testMetaData, byte[].class);
        Mockito.verify(serializer).serialize(testDeadlineScope, byte[].class);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testRetrievingDeadlineMessage() {
        JobDataMap testJobDataMap = toJobData(serializer, testDeadlineMessage, testDeadlineScope);
        DeadlineMessage<String> result = deadlineMessage(serializer, testJobDataMap);
        Assert.assertEquals(testDeadlineMessage.getDeadlineName(), result.getDeadlineName());
        Assert.assertEquals(testDeadlineMessage.getIdentifier(), result.getIdentifier());
        Assert.assertEquals(testDeadlineMessage.getTimestamp().truncatedTo(ChronoUnit.MILLIS), result.getTimestamp());
        Assert.assertEquals(testDeadlineMessage.getPayload(), result.getPayload());
        Assert.assertEquals(testDeadlineMessage.getPayloadType(), result.getPayloadType());
        Assert.assertEquals(testDeadlineMessage.getMetaData(), result.getMetaData());
        Mockito.verify(serializer, Mockito.times(2)).deserialize(((SimpleSerializedObject<?>) (ArgumentMatchers.argThat(this::assertDeadlineMessageSerializedObject))));
    }

    @Test
    public void testRetrievingDeadlineScope() {
        JobDataMap testJobDataMap = toJobData(serializer, testDeadlineMessage, testDeadlineScope);
        ScopeDescriptor result = deadlineScope(serializer, testJobDataMap);
        Assert.assertEquals(testDeadlineScope, result);
        Mockito.verify(serializer).deserialize(((SimpleSerializedObject<?>) (ArgumentMatchers.argThat(this::assertDeadlineScopeSerializedObject))));
    }

    private static class TestScopeDescriptor implements ScopeDescriptor {
        private static final long serialVersionUID = 3584695571254668002L;

        private final String type;

        private Object identifier;

        @JsonCreator
        public TestScopeDescriptor(@JsonProperty("type")
        String type, @JsonProperty("identifier")
        Object identifier) {
            this.type = type;
            this.identifier = identifier;
        }

        public String getType() {
            return type;
        }

        public Object getIdentifier() {
            return identifier;
        }

        @Override
        public String scopeDescription() {
            return String.format("TestScopeDescriptor for type [%s] and identifier [%s]", type, identifier);
        }

        @Override
        public int hashCode() {
            return Objects.hash(type, identifier);
        }

        @Override
        public boolean equals(Object obj) {
            if ((this) == obj) {
                return true;
            }
            if ((obj == null) || ((getClass()) != (obj.getClass()))) {
                return false;
            }
            final DeadlineJobDataBinderTest.TestScopeDescriptor other = ((DeadlineJobDataBinderTest.TestScopeDescriptor) (obj));
            return (Objects.equals(this.type, other.type)) && (Objects.equals(this.identifier, other.identifier));
        }

        @Override
        public String toString() {
            return ((((("TestScopeDescriptor{" + "type=") + (type)) + ", identifier='") + (identifier)) + '\'') + '}';
        }
    }
}

