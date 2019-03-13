/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.aws.kinesis;


import KinesisConstants.SEQUENCE_NUMBER;
import KinesisConstants.SHARD_ID;
import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.model.PutRecordRequest;
import com.amazonaws.services.kinesis.model.PutRecordResult;
import java.nio.ByteBuffer;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class KinesisProducerTest {
    private static final String SHARD_ID = "SHARD145";

    private static final String SEQUENCE_NUMBER = "SEQ123";

    private static final String STREAM_NAME = "streams";

    private static final String SAMPLE_RECORD_BODY = "SAMPLE";

    private static final String PARTITION_KEY = "partition";

    private static final ByteBuffer SAMPLE_BUFFER = ByteBuffer.wrap(KinesisProducerTest.SAMPLE_RECORD_BODY.getBytes());

    @Mock
    private AmazonKinesis kinesisClient;

    @Mock
    private KinesisEndpoint kinesisEndpoint;

    @Mock
    private KinesisConfiguration kinesisConfiguration;

    @Mock
    private Message outMessage;

    @Mock
    private Message inMessage;

    @Mock
    private PutRecordResult putRecordResult;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Exchange exchange;

    private KinesisProducer kinesisProducer;

    @Test
    public void shouldPutRecordInRightStreamWhenProcessingExchange() throws Exception {
        kinesisProducer.process(exchange);
        ArgumentCaptor<PutRecordRequest> capture = ArgumentCaptor.forClass(PutRecordRequest.class);
        Mockito.verify(kinesisClient).putRecord(capture.capture());
        PutRecordRequest request = capture.getValue();
        ByteBuffer byteBuffer = request.getData();
        byte[] actualArray = byteBuffer.array();
        byte[] sampleArray = KinesisProducerTest.SAMPLE_BUFFER.array();
        Assert.assertEquals(sampleArray, actualArray);
        Assert.assertEquals(KinesisProducerTest.STREAM_NAME, request.getStreamName());
    }

    @Test
    public void shouldHaveProperHeadersWhenSending() throws Exception {
        String seqNoForOrdering = "1851";
        Mockito.when(inMessage.getHeader(KinesisConstants.SEQUENCE_NUMBER)).thenReturn(seqNoForOrdering);
        kinesisProducer.process(exchange);
        ArgumentCaptor<PutRecordRequest> capture = ArgumentCaptor.forClass(PutRecordRequest.class);
        Mockito.verify(kinesisClient).putRecord(capture.capture());
        PutRecordRequest request = capture.getValue();
        Assert.assertEquals(KinesisProducerTest.PARTITION_KEY, request.getPartitionKey());
        Assert.assertEquals(seqNoForOrdering, request.getSequenceNumberForOrdering());
        Mockito.verify(outMessage).setHeader(KinesisConstants.SEQUENCE_NUMBER, KinesisProducerTest.SEQUENCE_NUMBER);
        Mockito.verify(outMessage).setHeader(KinesisConstants.SHARD_ID, KinesisProducerTest.SHARD_ID);
    }
}

