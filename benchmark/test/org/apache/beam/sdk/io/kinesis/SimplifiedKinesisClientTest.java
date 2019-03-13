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
package org.apache.beam.sdk.io.kinesis;


import ErrorType.Client;
import ErrorType.Service;
import ShardIteratorType.AT_SEQUENCE_NUMBER;
import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.model.Datapoint;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsRequest;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsResult;
import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.model.DescribeStreamResult;
import com.amazonaws.services.kinesis.model.ExpiredIteratorException;
import com.amazonaws.services.kinesis.model.GetRecordsRequest;
import com.amazonaws.services.kinesis.model.GetRecordsResult;
import com.amazonaws.services.kinesis.model.GetShardIteratorRequest;
import com.amazonaws.services.kinesis.model.GetShardIteratorResult;
import com.amazonaws.services.kinesis.model.LimitExceededException;
import com.amazonaws.services.kinesis.model.ProvisionedThroughputExceededException;
import com.amazonaws.services.kinesis.model.Record;
import com.amazonaws.services.kinesis.model.Shard;
import com.amazonaws.services.kinesis.model.StreamDescription;
import java.util.List;
import org.joda.time.Instant;
import org.joda.time.Minutes;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;


/**
 * *
 */
@RunWith(MockitoJUnitRunner.class)
public class SimplifiedKinesisClientTest {
    private static final String STREAM = "stream";

    private static final String SHARD_1 = "shard-01";

    private static final String SHARD_2 = "shard-02";

    private static final String SHARD_3 = "shard-03";

    private static final String SHARD_ITERATOR = "iterator";

    private static final String SEQUENCE_NUMBER = "abc123";

    @Mock
    private AmazonKinesis kinesis;

    @Mock
    private AmazonCloudWatch cloudWatch;

    @InjectMocks
    private SimplifiedKinesisClient underTest;

    @Test
    public void shouldReturnIteratorStartingWithSequenceNumber() throws Exception {
        BDDMockito.given(kinesis.getShardIterator(new GetShardIteratorRequest().withStreamName(SimplifiedKinesisClientTest.STREAM).withShardId(SimplifiedKinesisClientTest.SHARD_1).withShardIteratorType(AT_SEQUENCE_NUMBER).withStartingSequenceNumber(SimplifiedKinesisClientTest.SEQUENCE_NUMBER))).willReturn(new GetShardIteratorResult().withShardIterator(SimplifiedKinesisClientTest.SHARD_ITERATOR));
        String stream = underTest.getShardIterator(SimplifiedKinesisClientTest.STREAM, SimplifiedKinesisClientTest.SHARD_1, AT_SEQUENCE_NUMBER, SimplifiedKinesisClientTest.SEQUENCE_NUMBER, null);
        assertThat(stream).isEqualTo(SimplifiedKinesisClientTest.SHARD_ITERATOR);
    }

    @Test
    public void shouldReturnIteratorStartingWithTimestamp() throws Exception {
        Instant timestamp = Instant.now();
        BDDMockito.given(kinesis.getShardIterator(new GetShardIteratorRequest().withStreamName(SimplifiedKinesisClientTest.STREAM).withShardId(SimplifiedKinesisClientTest.SHARD_1).withShardIteratorType(AT_SEQUENCE_NUMBER).withTimestamp(timestamp.toDate()))).willReturn(new GetShardIteratorResult().withShardIterator(SimplifiedKinesisClientTest.SHARD_ITERATOR));
        String stream = underTest.getShardIterator(SimplifiedKinesisClientTest.STREAM, SimplifiedKinesisClientTest.SHARD_1, AT_SEQUENCE_NUMBER, null, timestamp);
        assertThat(stream).isEqualTo(SimplifiedKinesisClientTest.SHARD_ITERATOR);
    }

    @Test
    public void shouldHandleExpiredIterationExceptionForGetShardIterator() {
        shouldHandleGetShardIteratorError(new ExpiredIteratorException(""), ExpiredIteratorException.class);
    }

    @Test
    public void shouldHandleLimitExceededExceptionForGetShardIterator() {
        shouldHandleGetShardIteratorError(new LimitExceededException(""), TransientKinesisException.class);
    }

    @Test
    public void shouldHandleProvisionedThroughputExceededExceptionForGetShardIterator() {
        shouldHandleGetShardIteratorError(new ProvisionedThroughputExceededException(""), TransientKinesisException.class);
    }

    @Test
    public void shouldHandleServiceErrorForGetShardIterator() {
        shouldHandleGetShardIteratorError(newAmazonServiceException(Service), TransientKinesisException.class);
    }

    @Test
    public void shouldHandleClientErrorForGetShardIterator() {
        shouldHandleGetShardIteratorError(newAmazonServiceException(Client), RuntimeException.class);
    }

    @Test
    public void shouldHandleUnexpectedExceptionForGetShardIterator() {
        shouldHandleGetShardIteratorError(new NullPointerException(), RuntimeException.class);
    }

    @Test
    public void shouldListAllShards() throws Exception {
        Shard shard1 = new Shard().withShardId(SimplifiedKinesisClientTest.SHARD_1);
        Shard shard2 = new Shard().withShardId(SimplifiedKinesisClientTest.SHARD_2);
        Shard shard3 = new Shard().withShardId(SimplifiedKinesisClientTest.SHARD_3);
        BDDMockito.given(kinesis.describeStream(SimplifiedKinesisClientTest.STREAM, null)).willReturn(new DescribeStreamResult().withStreamDescription(new StreamDescription().withShards(shard1, shard2).withHasMoreShards(true)));
        BDDMockito.given(kinesis.describeStream(SimplifiedKinesisClientTest.STREAM, SimplifiedKinesisClientTest.SHARD_2)).willReturn(new DescribeStreamResult().withStreamDescription(new StreamDescription().withShards(shard3).withHasMoreShards(false)));
        List<Shard> shards = underTest.listShards(SimplifiedKinesisClientTest.STREAM);
        assertThat(shards).containsOnly(shard1, shard2, shard3);
    }

    @Test
    public void shouldHandleExpiredIterationExceptionForShardListing() {
        shouldHandleShardListingError(new ExpiredIteratorException(""), ExpiredIteratorException.class);
    }

    @Test
    public void shouldHandleLimitExceededExceptionForShardListing() {
        shouldHandleShardListingError(new LimitExceededException(""), TransientKinesisException.class);
    }

    @Test
    public void shouldHandleProvisionedThroughputExceededExceptionForShardListing() {
        shouldHandleShardListingError(new ProvisionedThroughputExceededException(""), TransientKinesisException.class);
    }

    @Test
    public void shouldHandleServiceErrorForShardListing() {
        shouldHandleShardListingError(newAmazonServiceException(Service), TransientKinesisException.class);
    }

    @Test
    public void shouldHandleClientErrorForShardListing() {
        shouldHandleShardListingError(newAmazonServiceException(Client), RuntimeException.class);
    }

    @Test
    public void shouldHandleUnexpectedExceptionForShardListing() {
        shouldHandleShardListingError(new NullPointerException(), RuntimeException.class);
    }

    @Test
    public void shouldCountBytesWhenSingleDataPointReturned() throws Exception {
        Instant countSince = new Instant("2017-04-06T10:00:00.000Z");
        Instant countTo = new Instant("2017-04-06T11:00:00.000Z");
        Minutes periodTime = Minutes.minutesBetween(countSince, countTo);
        GetMetricStatisticsRequest metricStatisticsRequest = underTest.createMetricStatisticsRequest(SimplifiedKinesisClientTest.STREAM, countSince, countTo, periodTime);
        GetMetricStatisticsResult result = new GetMetricStatisticsResult().withDatapoints(new Datapoint().withSum(1.0));
        BDDMockito.given(cloudWatch.getMetricStatistics(metricStatisticsRequest)).willReturn(result);
        long backlogBytes = underTest.getBacklogBytes(SimplifiedKinesisClientTest.STREAM, countSince, countTo);
        assertThat(backlogBytes).isEqualTo(1L);
    }

    @Test
    public void shouldCountBytesWhenMultipleDataPointsReturned() throws Exception {
        Instant countSince = new Instant("2017-04-06T10:00:00.000Z");
        Instant countTo = new Instant("2017-04-06T11:00:00.000Z");
        Minutes periodTime = Minutes.minutesBetween(countSince, countTo);
        GetMetricStatisticsRequest metricStatisticsRequest = underTest.createMetricStatisticsRequest(SimplifiedKinesisClientTest.STREAM, countSince, countTo, periodTime);
        GetMetricStatisticsResult result = new GetMetricStatisticsResult().withDatapoints(new Datapoint().withSum(1.0), new Datapoint().withSum(3.0), new Datapoint().withSum(2.0));
        BDDMockito.given(cloudWatch.getMetricStatistics(metricStatisticsRequest)).willReturn(result);
        long backlogBytes = underTest.getBacklogBytes(SimplifiedKinesisClientTest.STREAM, countSince, countTo);
        assertThat(backlogBytes).isEqualTo(6L);
    }

    @Test
    public void shouldNotCallCloudWatchWhenSpecifiedPeriodTooShort() throws Exception {
        Instant countSince = new Instant("2017-04-06T10:00:00.000Z");
        Instant countTo = new Instant("2017-04-06T10:00:02.000Z");
        long backlogBytes = underTest.getBacklogBytes(SimplifiedKinesisClientTest.STREAM, countSince, countTo);
        assertThat(backlogBytes).isEqualTo(0L);
        Mockito.verifyZeroInteractions(cloudWatch);
    }

    @Test
    public void shouldHandleLimitExceededExceptionForGetBacklogBytes() {
        shouldHandleGetBacklogBytesError(new LimitExceededException(""), TransientKinesisException.class);
    }

    @Test
    public void shouldHandleProvisionedThroughputExceededExceptionForGetBacklogBytes() {
        shouldHandleGetBacklogBytesError(new ProvisionedThroughputExceededException(""), TransientKinesisException.class);
    }

    @Test
    public void shouldHandleServiceErrorForGetBacklogBytes() {
        shouldHandleGetBacklogBytesError(newAmazonServiceException(Service), TransientKinesisException.class);
    }

    @Test
    public void shouldHandleClientErrorForGetBacklogBytes() {
        shouldHandleGetBacklogBytesError(newAmazonServiceException(Client), RuntimeException.class);
    }

    @Test
    public void shouldHandleUnexpectedExceptionForGetBacklogBytes() {
        shouldHandleGetBacklogBytesError(new NullPointerException(), RuntimeException.class);
    }

    @Test
    public void shouldReturnLimitedNumberOfRecords() throws Exception {
        final Integer limit = 100;
        Mockito.doAnswer(((Answer<GetRecordsResult>) (( invocation) -> {
            GetRecordsRequest request = ((GetRecordsRequest) (invocation.getArguments()[0]));
            List<Record> records = generateRecords(request.getLimit());
            return new GetRecordsResult().withRecords(records).withMillisBehindLatest(1000L);
        }))).when(kinesis).getRecords(ArgumentMatchers.any(GetRecordsRequest.class));
        GetKinesisRecordsResult result = underTest.getRecords(SimplifiedKinesisClientTest.SHARD_ITERATOR, SimplifiedKinesisClientTest.STREAM, SimplifiedKinesisClientTest.SHARD_1, limit);
        assertThat(result.getRecords().size()).isEqualTo(limit);
    }
}

