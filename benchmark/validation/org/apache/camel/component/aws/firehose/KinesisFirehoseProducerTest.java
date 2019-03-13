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
package org.apache.camel.component.aws.firehose;


import KinesisFirehoseConstants.RECORD_ID;
import com.amazonaws.services.kinesisfirehose.AmazonKinesisFirehose;
import com.amazonaws.services.kinesisfirehose.model.PutRecordResult;
import java.nio.ByteBuffer;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class KinesisFirehoseProducerTest {
    private static final String STREAM_NAME = "streams";

    private static final String RECORD_ID = "sample_record_id";

    private static final String SAMPLE_RECORD_BODY = "SAMPLE";

    private static final ByteBuffer SAMPLE_BUFFER = ByteBuffer.wrap(KinesisFirehoseProducerTest.SAMPLE_RECORD_BODY.getBytes());

    @Mock
    private AmazonKinesisFirehose kinesisFirehoseClient;

    @Mock
    private KinesisFirehoseEndpoint kinesisFirehoseEndpoint;

    @Mock
    private KinesisFirehoseConfiguration kinesisFirehoseConfiguration;

    @Mock
    private Message inMessage;

    @Mock
    private Message outMessage;

    @Mock
    private PutRecordResult putRecordResult;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Exchange exchange;

    private KinesisFirehoseProducer kinesisFirehoseProducer;

    @Test
    public void shouldPutRecordIntoStreamWhenProcessingExchange() throws Exception {
        kinesisFirehoseProducer.process(exchange);
        Mockito.verify(outMessage).setHeader(KinesisFirehoseConstants.RECORD_ID, KinesisFirehoseProducerTest.RECORD_ID);
    }
}

