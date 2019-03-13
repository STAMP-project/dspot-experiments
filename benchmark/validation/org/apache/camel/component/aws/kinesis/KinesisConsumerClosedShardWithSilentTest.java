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


import KinesisConstants.APPROX_ARRIVAL_TIME;
import KinesisConstants.PARTITION_KEY;
import KinesisConstants.SEQUENCE_NUMBER;
import ShardIteratorType.AFTER_SEQUENCE_NUMBER;
import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.model.DescribeStreamRequest;
import com.amazonaws.services.kinesis.model.GetRecordsRequest;
import com.amazonaws.services.kinesis.model.GetRecordsResult;
import com.amazonaws.services.kinesis.model.GetShardIteratorRequest;
import com.amazonaws.services.kinesis.model.Record;
import java.util.Date;
import org.apache.camel.AsyncCallback;
import org.apache.camel.AsyncProcessor;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class KinesisConsumerClosedShardWithSilentTest {
    @Mock
    private AmazonKinesis kinesisClient;

    @Mock
    private AsyncProcessor processor;

    private final CamelContext context = new DefaultCamelContext();

    private final KinesisComponent component = new KinesisComponent(context);

    private KinesisConsumer undertest;

    @Test
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

    @Test
    public void itDoesNotMakeADescribeStreamRequestIfShardIdIsSet() throws Exception {
        undertest.getEndpoint().getConfiguration().setShardId("shardIdPassedAsUrlParam");
        undertest.poll();
        final ArgumentCaptor<GetShardIteratorRequest> getShardIteratorReqCap = ArgumentCaptor.forClass(GetShardIteratorRequest.class);
        Mockito.verify(kinesisClient).getShardIterator(getShardIteratorReqCap.capture());
        Assert.assertThat(getShardIteratorReqCap.getValue().getStreamName(), CoreMatchers.is("streamName"));
        Assert.assertThat(getShardIteratorReqCap.getValue().getShardId(), CoreMatchers.is("shardIdPassedAsUrlParam"));
        Assert.assertThat(getShardIteratorReqCap.getValue().getShardIteratorType(), CoreMatchers.is("LATEST"));
    }

    @Test
    public void itObtainsAShardIteratorOnFirstPollForSequenceNumber() throws Exception {
        undertest.getEndpoint().getConfiguration().setSequenceNumber("12345");
        undertest.getEndpoint().getConfiguration().setIteratorType(AFTER_SEQUENCE_NUMBER);
        undertest.poll();
        final ArgumentCaptor<DescribeStreamRequest> describeStreamReqCap = ArgumentCaptor.forClass(DescribeStreamRequest.class);
        final ArgumentCaptor<GetShardIteratorRequest> getShardIteratorReqCap = ArgumentCaptor.forClass(GetShardIteratorRequest.class);
        Mockito.verify(kinesisClient).describeStream(describeStreamReqCap.capture());
        Assert.assertThat(describeStreamReqCap.getValue().getStreamName(), CoreMatchers.is("streamName"));
        Mockito.verify(kinesisClient).getShardIterator(getShardIteratorReqCap.capture());
        Assert.assertThat(getShardIteratorReqCap.getValue().getStreamName(), CoreMatchers.is("streamName"));
        Assert.assertThat(getShardIteratorReqCap.getValue().getShardId(), CoreMatchers.is("shardId"));
        Assert.assertThat(getShardIteratorReqCap.getValue().getShardIteratorType(), CoreMatchers.is("AFTER_SEQUENCE_NUMBER"));
        Assert.assertThat(getShardIteratorReqCap.getValue().getStartingSequenceNumber(), CoreMatchers.is("12345"));
    }

    @Test
    public void itUsesTheShardIteratorOnPolls() throws Exception {
        undertest.poll();
        final ArgumentCaptor<GetRecordsRequest> getRecordsReqCap = ArgumentCaptor.forClass(GetRecordsRequest.class);
        Mockito.verify(kinesisClient).getRecords(getRecordsReqCap.capture());
        Assert.assertThat(getRecordsReqCap.getValue().getShardIterator(), CoreMatchers.is("shardIterator"));
    }

    @Test
    public void itUsesTheShardIteratorOnSubsiquentPolls() throws Exception {
        undertest.poll();
        undertest.poll();
        final ArgumentCaptor<GetRecordsRequest> getRecordsReqCap = ArgumentCaptor.forClass(GetRecordsRequest.class);
        Mockito.verify(kinesisClient, Mockito.times(1)).describeStream(ArgumentMatchers.any(DescribeStreamRequest.class));
        Mockito.verify(kinesisClient, Mockito.times(1)).getShardIterator(ArgumentMatchers.any(GetShardIteratorRequest.class));
        Mockito.verify(kinesisClient, Mockito.times(2)).getRecords(getRecordsReqCap.capture());
        Assert.assertThat(getRecordsReqCap.getAllValues().get(0).getShardIterator(), CoreMatchers.is("shardIterator"));
        Assert.assertThat(getRecordsReqCap.getAllValues().get(1).getShardIterator(), CoreMatchers.is("nextShardIterator"));
    }

    @Test
    public void recordsAreSentToTheProcessor() throws Exception {
        Mockito.when(kinesisClient.getRecords(ArgumentMatchers.any(GetRecordsRequest.class))).thenReturn(new GetRecordsResult().withNextShardIterator("nextShardIterator").withRecords(new Record().withSequenceNumber("1"), new Record().withSequenceNumber("2")));
        int messageCount = undertest.poll();
        Assert.assertThat(messageCount, CoreMatchers.is(2));
        final ArgumentCaptor<Exchange> exchangeCaptor = ArgumentCaptor.forClass(Exchange.class);
        Mockito.verify(processor, Mockito.times(2)).process(exchangeCaptor.capture(), ArgumentMatchers.any(AsyncCallback.class));
        Assert.assertThat(exchangeCaptor.getAllValues().get(0).getIn().getBody(Record.class).getSequenceNumber(), CoreMatchers.is("1"));
        Assert.assertThat(exchangeCaptor.getAllValues().get(1).getIn().getBody(Record.class).getSequenceNumber(), CoreMatchers.is("2"));
    }

    @Test
    public void exchangePropertiesAreSet() throws Exception {
        String partitionKey = "partitionKey";
        String sequenceNumber = "1";
        Mockito.when(kinesisClient.getRecords(ArgumentMatchers.any(GetRecordsRequest.class))).thenReturn(new GetRecordsResult().withNextShardIterator("nextShardIterator").withRecords(new Record().withSequenceNumber(sequenceNumber).withApproximateArrivalTimestamp(new Date(42)).withPartitionKey(partitionKey)));
        undertest.poll();
        final ArgumentCaptor<Exchange> exchangeCaptor = ArgumentCaptor.forClass(Exchange.class);
        Mockito.verify(processor).process(exchangeCaptor.capture(), ArgumentMatchers.any(AsyncCallback.class));
        Assert.assertThat(exchangeCaptor.getValue().getIn().getHeader(APPROX_ARRIVAL_TIME, long.class), CoreMatchers.is(42L));
        Assert.assertThat(exchangeCaptor.getValue().getIn().getHeader(PARTITION_KEY, String.class), CoreMatchers.is(partitionKey));
        Assert.assertThat(exchangeCaptor.getValue().getIn().getHeader(SEQUENCE_NUMBER, String.class), CoreMatchers.is(sequenceNumber));
    }
}

