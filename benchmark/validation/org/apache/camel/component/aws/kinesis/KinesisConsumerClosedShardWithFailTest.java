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


import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.model.DescribeStreamRequest;
import com.amazonaws.services.kinesis.model.GetShardIteratorRequest;
import org.apache.camel.AsyncProcessor;
import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class KinesisConsumerClosedShardWithFailTest {
    @Mock
    private AmazonKinesis kinesisClient;

    @Mock
    private AsyncProcessor processor;

    private final CamelContext context = new DefaultCamelContext();

    private final KinesisComponent component = new KinesisComponent(context);

    private KinesisConsumer undertest;

    @Test(expected = ReachedClosedStatusException.class)
    public void itObtainsAShardIteratorOnFirstPoll() throws Exception {
        undertest.poll();
        final ArgumentCaptor<DescribeStreamRequest> describeStreamReqCap = ArgumentCaptor.forClass(DescribeStreamRequest.class);
        final ArgumentCaptor<GetShardIteratorRequest> getShardIteratorReqCap = ArgumentCaptor.forClass(GetShardIteratorRequest.class);
        Mockito.verify(kinesisClient).describeStream(describeStreamReqCap.capture());
        Assert.assertThat(describeStreamReqCap.getValue().getStreamName(), CoreMatchers.is("streamName"));
        Mockito.verify(kinesisClient).getShardIterator(getShardIteratorReqCap.capture());
        Assert.assertThat(getShardIteratorReqCap.getValue().getStreamName(), CoreMatchers.is("streamName"));
        Assert.assertThat(getShardIteratorReqCap.getValue().getShardId(), CoreMatchers.is("shardId"));
        Assert.assertThat(getShardIteratorReqCap.getValue().getShardIteratorType(), CoreMatchers.is("LATEST"));
    }
}

