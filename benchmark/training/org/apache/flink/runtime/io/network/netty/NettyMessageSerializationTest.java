/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.runtime.io.network.netty;


import NettyMessage.AddCredit;
import NettyMessage.CancelPartitionRequest;
import NettyMessage.CloseRequest;
import NettyMessage.ErrorResponse;
import NettyMessage.PartitionRequest;
import NettyMessage.TaskEventRequest;
import java.util.Random;
import org.apache.flink.runtime.event.task.IntegerTaskEvent;
import org.apache.flink.runtime.executiongraph.ExecutionAttemptID;
import org.apache.flink.runtime.io.network.partition.consumer.InputChannelID;
import org.apache.flink.runtime.jobgraph.IntermediateResultPartitionID;
import org.apache.flink.shaded.netty4.io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the serialization and deserialization of the various {@link NettyMessage} sub-classes.
 */
public class NettyMessageSerializationTest {
    public static final boolean RESTORE_OLD_NETTY_BEHAVIOUR = false;

    private final EmbeddedChannel channel = // outbound messages
    new EmbeddedChannel(new NettyMessage.NettyMessageEncoder(), new NettyMessage.NettyMessageDecoder(NettyMessageSerializationTest.RESTORE_OLD_NETTY_BEHAVIOUR));// inbound messages


    private final Random random = new Random();

    @Test
    public void testEncodeDecode() {
        testEncodeDecodeBuffer(false);
        testEncodeDecodeBuffer(true);
        {
            {
                IllegalStateException expectedError = new IllegalStateException();
                InputChannelID receiverId = new InputChannelID();
                NettyMessage.ErrorResponse expected = new NettyMessage.ErrorResponse(expectedError, receiverId);
                NettyMessage.ErrorResponse actual = encodeAndDecode(expected);
                Assert.assertEquals(expected.cause.getClass(), actual.cause.getClass());
                Assert.assertEquals(expected.cause.getMessage(), actual.cause.getMessage());
                Assert.assertEquals(receiverId, actual.receiverId);
            }
            {
                IllegalStateException expectedError = new IllegalStateException("Illegal illegal illegal");
                InputChannelID receiverId = new InputChannelID();
                NettyMessage.ErrorResponse expected = new NettyMessage.ErrorResponse(expectedError, receiverId);
                NettyMessage.ErrorResponse actual = encodeAndDecode(expected);
                Assert.assertEquals(expected.cause.getClass(), actual.cause.getClass());
                Assert.assertEquals(expected.cause.getMessage(), actual.cause.getMessage());
                Assert.assertEquals(receiverId, actual.receiverId);
            }
            {
                IllegalStateException expectedError = new IllegalStateException("Illegal illegal illegal");
                NettyMessage.ErrorResponse expected = new NettyMessage.ErrorResponse(expectedError);
                NettyMessage.ErrorResponse actual = encodeAndDecode(expected);
                Assert.assertEquals(expected.cause.getClass(), actual.cause.getClass());
                Assert.assertEquals(expected.cause.getMessage(), actual.cause.getMessage());
                Assert.assertNull(actual.receiverId);
                Assert.assertTrue(actual.isFatalError());
            }
        }
        {
            NettyMessage.PartitionRequest expected = new NettyMessage.PartitionRequest(new org.apache.flink.runtime.io.network.partition.ResultPartitionID(new IntermediateResultPartitionID(), new ExecutionAttemptID()), random.nextInt(), new InputChannelID(), random.nextInt());
            NettyMessage.PartitionRequest actual = encodeAndDecode(expected);
            Assert.assertEquals(expected.partitionId, actual.partitionId);
            Assert.assertEquals(expected.queueIndex, actual.queueIndex);
            Assert.assertEquals(expected.receiverId, actual.receiverId);
            Assert.assertEquals(expected.credit, actual.credit);
        }
        {
            NettyMessage.TaskEventRequest expected = new NettyMessage.TaskEventRequest(new IntegerTaskEvent(random.nextInt()), new org.apache.flink.runtime.io.network.partition.ResultPartitionID(new IntermediateResultPartitionID(), new ExecutionAttemptID()), new InputChannelID());
            NettyMessage.TaskEventRequest actual = encodeAndDecode(expected);
            Assert.assertEquals(expected.event, actual.event);
            Assert.assertEquals(expected.partitionId, actual.partitionId);
            Assert.assertEquals(expected.receiverId, actual.receiverId);
        }
        {
            NettyMessage.CancelPartitionRequest expected = new NettyMessage.CancelPartitionRequest(new InputChannelID());
            NettyMessage.CancelPartitionRequest actual = encodeAndDecode(expected);
            Assert.assertEquals(expected.receiverId, actual.receiverId);
        }
        {
            NettyMessage.CloseRequest expected = new NettyMessage.CloseRequest();
            NettyMessage.CloseRequest actual = encodeAndDecode(expected);
            Assert.assertEquals(expected.getClass(), actual.getClass());
        }
        {
            NettyMessage.AddCredit expected = new NettyMessage.AddCredit(new org.apache.flink.runtime.io.network.partition.ResultPartitionID(new IntermediateResultPartitionID(), new ExecutionAttemptID()), ((random.nextInt(Integer.MAX_VALUE)) + 1), new InputChannelID());
            NettyMessage.AddCredit actual = encodeAndDecode(expected);
            Assert.assertEquals(expected.partitionId, actual.partitionId);
            Assert.assertEquals(expected.credit, actual.credit);
            Assert.assertEquals(expected.receiverId, actual.receiverId);
        }
    }
}

