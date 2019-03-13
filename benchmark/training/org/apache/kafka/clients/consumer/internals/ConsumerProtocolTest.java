/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.clients.consumer.internals;


import ConsumerProtocol.PARTITIONS_KEY_NAME;
import ConsumerProtocol.TOPICS_KEY_NAME;
import ConsumerProtocol.TOPIC_KEY_NAME;
import ConsumerProtocol.TOPIC_PARTITIONS_KEY_NAME;
import ConsumerProtocol.USER_DATA_KEY_NAME;
import ConsumerProtocol.VERSION_KEY_NAME;
import PartitionAssignor.Assignment;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import org.apache.kafka.clients.consumer.internals.PartitionAssignor.Subscription;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.protocol.types.Schema;
import org.apache.kafka.common.protocol.types.Struct;
import org.apache.kafka.common.protocol.types.Type;
import org.junit.Assert;
import org.junit.Test;

import static ConsumerProtocol.CONSUMER_PROTOCOL_HEADER_SCHEMA;
import static ConsumerProtocol.TOPICS_KEY_NAME;
import static ConsumerProtocol.TOPIC_ASSIGNMENT_V0;
import static ConsumerProtocol.TOPIC_PARTITIONS_KEY_NAME;
import static ConsumerProtocol.USER_DATA_KEY_NAME;


public class ConsumerProtocolTest {
    @Test
    public void serializeDeserializeMetadata() {
        Subscription subscription = new Subscription(Arrays.asList("foo", "bar"));
        ByteBuffer buffer = ConsumerProtocol.serializeSubscription(subscription);
        Subscription parsedSubscription = ConsumerProtocol.deserializeSubscription(buffer);
        Assert.assertEquals(subscription.topics(), parsedSubscription.topics());
    }

    @Test
    public void serializeDeserializeNullSubscriptionUserData() {
        Subscription subscription = new Subscription(Arrays.asList("foo", "bar"), null);
        ByteBuffer buffer = ConsumerProtocol.serializeSubscription(subscription);
        Subscription parsedSubscription = ConsumerProtocol.deserializeSubscription(buffer);
        Assert.assertEquals(subscription.topics(), parsedSubscription.topics());
        Assert.assertNull(subscription.userData());
    }

    @Test
    public void deserializeNewSubscriptionVersion() {
        // verify that a new version which adds a field is still parseable
        short version = 100;
        Schema subscriptionSchemaV100 = new Schema(new org.apache.kafka.common.protocol.types.Field(TOPICS_KEY_NAME, new org.apache.kafka.common.protocol.types.ArrayOf(Type.STRING)), new org.apache.kafka.common.protocol.types.Field(USER_DATA_KEY_NAME, Type.BYTES), new org.apache.kafka.common.protocol.types.Field("foo", Type.STRING));
        Struct subscriptionV100 = new Struct(subscriptionSchemaV100);
        subscriptionV100.set(TOPICS_KEY_NAME, new Object[]{ "topic" });
        subscriptionV100.set(USER_DATA_KEY_NAME, ByteBuffer.wrap(new byte[0]));
        subscriptionV100.set("foo", "bar");
        Struct headerV100 = new Struct(CONSUMER_PROTOCOL_HEADER_SCHEMA);
        headerV100.set(VERSION_KEY_NAME, version);
        ByteBuffer buffer = ByteBuffer.allocate(((subscriptionV100.sizeOf()) + (headerV100.sizeOf())));
        headerV100.writeTo(buffer);
        subscriptionV100.writeTo(buffer);
        buffer.flip();
        Subscription subscription = ConsumerProtocol.deserializeSubscription(buffer);
        Assert.assertEquals(Arrays.asList("topic"), subscription.topics());
    }

    @Test
    public void serializeDeserializeAssignment() {
        List<TopicPartition> partitions = Arrays.asList(new TopicPartition("foo", 0), new TopicPartition("bar", 2));
        ByteBuffer buffer = ConsumerProtocol.serializeAssignment(new PartitionAssignor.Assignment(partitions));
        PartitionAssignor.Assignment parsedAssignment = ConsumerProtocol.deserializeAssignment(buffer);
        Assert.assertEquals(ConsumerProtocolTest.toSet(partitions), ConsumerProtocolTest.toSet(parsedAssignment.partitions()));
    }

    @Test
    public void deserializeNullAssignmentUserData() {
        List<TopicPartition> partitions = Arrays.asList(new TopicPartition("foo", 0), new TopicPartition("bar", 2));
        ByteBuffer buffer = ConsumerProtocol.serializeAssignment(new PartitionAssignor.Assignment(partitions, null));
        PartitionAssignor.Assignment parsedAssignment = ConsumerProtocol.deserializeAssignment(buffer);
        Assert.assertEquals(ConsumerProtocolTest.toSet(partitions), ConsumerProtocolTest.toSet(parsedAssignment.partitions()));
        Assert.assertNull(parsedAssignment.userData());
    }

    @Test
    public void deserializeNewAssignmentVersion() {
        // verify that a new version which adds a field is still parseable
        short version = 100;
        Schema assignmentSchemaV100 = new Schema(new org.apache.kafka.common.protocol.types.Field(TOPIC_PARTITIONS_KEY_NAME, new org.apache.kafka.common.protocol.types.ArrayOf(TOPIC_ASSIGNMENT_V0)), new org.apache.kafka.common.protocol.types.Field(USER_DATA_KEY_NAME, Type.BYTES), new org.apache.kafka.common.protocol.types.Field("foo", Type.STRING));
        Struct assignmentV100 = new Struct(assignmentSchemaV100);
        assignmentV100.set(TOPIC_PARTITIONS_KEY_NAME, new Object[]{ new Struct(TOPIC_ASSIGNMENT_V0).set(TOPIC_KEY_NAME, "foo").set(PARTITIONS_KEY_NAME, new Object[]{ 1 }) });
        assignmentV100.set(USER_DATA_KEY_NAME, ByteBuffer.wrap(new byte[0]));
        assignmentV100.set("foo", "bar");
        Struct headerV100 = new Struct(CONSUMER_PROTOCOL_HEADER_SCHEMA);
        headerV100.set(VERSION_KEY_NAME, version);
        ByteBuffer buffer = ByteBuffer.allocate(((assignmentV100.sizeOf()) + (headerV100.sizeOf())));
        headerV100.writeTo(buffer);
        assignmentV100.writeTo(buffer);
        buffer.flip();
        PartitionAssignor.Assignment assignment = ConsumerProtocol.deserializeAssignment(buffer);
        Assert.assertEquals(ConsumerProtocolTest.toSet(Arrays.asList(new TopicPartition("foo", 1))), ConsumerProtocolTest.toSet(assignment.partitions()));
    }
}

