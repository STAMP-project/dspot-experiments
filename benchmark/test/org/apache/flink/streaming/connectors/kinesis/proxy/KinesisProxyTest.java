/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.streaming.connectors.kinesis.proxy;


import ConsumerConfigConstants.AWS_REGION;
import ConsumerConfigConstants.LIST_SHARDS_RETRIES;
import ErrorType.Client;
import ErrorType.Service;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.ClientConfigurationFactory;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.AmazonKinesisClient;
import com.amazonaws.services.kinesis.model.AmazonKinesisException;
import com.amazonaws.services.kinesis.model.ExpiredIteratorException;
import com.amazonaws.services.kinesis.model.GetRecordsResult;
import com.amazonaws.services.kinesis.model.ListShardsRequest;
import com.amazonaws.services.kinesis.model.ListShardsResult;
import com.amazonaws.services.kinesis.model.ProvisionedThroughputExceededException;
import com.amazonaws.services.kinesis.model.Shard;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.flink.streaming.connectors.kinesis.model.StreamShardHandle;
import org.apache.flink.streaming.connectors.kinesis.testutils.KinesisShardIdGenerator;
import org.apache.flink.streaming.connectors.kinesis.util.AWSUtil;
import org.hamcrest.Description;
import org.hamcrest.MatcherAssert;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.hamcrest.MockitoHamcrest;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.reflect.Whitebox;


/**
 * Test for methods in the {@link KinesisProxy} class.
 */
public class KinesisProxyTest {
    @Test
    public void testIsRecoverableExceptionWithProvisionedThroughputExceeded() {
        final ProvisionedThroughputExceededException ex = new ProvisionedThroughputExceededException("asdf");
        ex.setErrorType(Client);
        Assert.assertTrue(KinesisProxy.isRecoverableException(ex));
    }

    @Test
    public void testIsRecoverableExceptionWithServiceException() {
        final AmazonServiceException ex = new AmazonServiceException("asdf");
        ex.setErrorType(Service);
        Assert.assertTrue(KinesisProxy.isRecoverableException(ex));
    }

    @Test
    public void testIsRecoverableExceptionWithExpiredIteratorException() {
        final ExpiredIteratorException ex = new ExpiredIteratorException("asdf");
        ex.setErrorType(Client);
        Assert.assertFalse(KinesisProxy.isRecoverableException(ex));
    }

    @Test
    public void testIsRecoverableExceptionWithNullErrorType() {
        final AmazonServiceException ex = new AmazonServiceException("asdf");
        ex.setErrorType(null);
        Assert.assertFalse(KinesisProxy.isRecoverableException(ex));
    }

    @Test
    public void testGetRecordsRetry() throws Exception {
        Properties kinesisConsumerConfig = new Properties();
        kinesisConsumerConfig.setProperty(AWS_REGION, "us-east-1");
        final GetRecordsResult expectedResult = new GetRecordsResult();
        MutableInt retries = new MutableInt();
        final Throwable[] retriableExceptions = new Throwable[]{ new AmazonKinesisException("mock") };
        AmazonKinesisClient mockClient = Mockito.mock(AmazonKinesisClient.class);
        Mockito.when(mockClient.getRecords(ArgumentMatchers.any())).thenAnswer(new Answer<GetRecordsResult>() {
            @Override
            public GetRecordsResult answer(InvocationOnMock invocation) throws Throwable {
                if ((retries.intValue()) < (retriableExceptions.length)) {
                    retries.increment();
                    throw retriableExceptions[((retries.intValue()) - 1)];
                }
                return expectedResult;
            }
        });
        KinesisProxy kinesisProxy = new KinesisProxy(kinesisConsumerConfig);
        Whitebox.getField(KinesisProxy.class, "kinesisClient").set(kinesisProxy, mockClient);
        GetRecordsResult result = kinesisProxy.getRecords("fakeShardIterator", 1);
        Assert.assertEquals(retriableExceptions.length, retries.intValue());
        Assert.assertEquals(expectedResult, result);
    }

    @Test
    public void testGetShardList() throws Exception {
        List<String> shardIds = Arrays.asList("shardId-000000000000", "shardId-000000000001", "shardId-000000000002", "shardId-000000000003");
        String nextToken = "NextToken";
        String fakeStreamName = "fake-stream";
        List<Shard> shards = shardIds.stream().map(( shardId) -> new Shard().withShardId(shardId)).collect(Collectors.toList());
        AmazonKinesis mockClient = Mockito.mock(AmazonKinesis.class);
        KinesisProxy kinesisProxy = getProxy(mockClient);
        ListShardsResult responseWithMoreData = new ListShardsResult().withShards(shards.subList(0, 2)).withNextToken(nextToken);
        ListShardsResult responseFinal = new ListShardsResult().withShards(shards.subList(2, shards.size())).withNextToken(null);
        Mockito.doReturn(responseWithMoreData).when(mockClient).listShards(MockitoHamcrest.argThat(KinesisProxyTest.initialListShardsRequestMatcher()));
        Mockito.doReturn(responseFinal).when(mockClient).listShards(MockitoHamcrest.argThat(KinesisProxyTest.listShardsNextToken(nextToken)));
        HashMap<String, String> streamHashMap = KinesisProxyTest.createInitialSubscribedStreamsToLastDiscoveredShardsState(Arrays.asList(fakeStreamName));
        GetShardListResult shardListResult = kinesisProxy.getShardList(streamHashMap);
        Assert.assertEquals(shardListResult.hasRetrievedShards(), true);
        Set<String> expectedStreams = new HashSet<>();
        expectedStreams.add(fakeStreamName);
        Assert.assertEquals(shardListResult.getStreamsWithRetrievedShards(), expectedStreams);
        List<StreamShardHandle> actualShardList = shardListResult.getRetrievedShardListOfStream(fakeStreamName);
        List<StreamShardHandle> expectedStreamShard = new ArrayList<>();
        MatcherAssert.assertThat(actualShardList, hasSize(4));
        for (int i = 0; i < 4; i++) {
            StreamShardHandle shardHandle = new StreamShardHandle(fakeStreamName, new Shard().withShardId(KinesisShardIdGenerator.generateFromShardOrder(i)));
            expectedStreamShard.add(shardHandle);
        }
        Assert.assertThat(actualShardList, containsInAnyOrder(expectedStreamShard.toArray(new StreamShardHandle[actualShardList.size()])));
    }

    @Test
    public void testGetShardListWithNewShardsOnSecondRun() throws Exception {
        // given
        List<String> shardIds = Arrays.asList(KinesisShardIdGenerator.generateFromShardOrder(0), KinesisShardIdGenerator.generateFromShardOrder(1));
        String fakeStreamName = "fake-stream";
        List<Shard> shards = shardIds.stream().map(( shardId) -> new Shard().withShardId(shardId)).collect(Collectors.toList());
        AmazonKinesis mockClient = Mockito.mock(AmazonKinesis.class);
        KinesisProxy kinesisProxy = getProxy(mockClient);
        ListShardsResult responseFirst = new ListShardsResult().withShards(shards).withNextToken(null);
        Mockito.doReturn(responseFirst).when(mockClient).listShards(MockitoHamcrest.argThat(KinesisProxyTest.initialListShardsRequestMatcher()));
        HashMap<String, String> streamHashMap = KinesisProxyTest.createInitialSubscribedStreamsToLastDiscoveredShardsState(Collections.singletonList(fakeStreamName));
        // when
        GetShardListResult shardListResult = kinesisProxy.getShardList(streamHashMap);
        // then
        Assert.assertTrue(shardListResult.hasRetrievedShards());
        Set<String> expectedStreams = new HashSet<>();
        expectedStreams.add(fakeStreamName);
        Assert.assertEquals(shardListResult.getStreamsWithRetrievedShards(), expectedStreams);
        List<StreamShardHandle> actualShardList = shardListResult.getRetrievedShardListOfStream(fakeStreamName);
        Assert.assertThat(actualShardList, hasSize(2));
        List<StreamShardHandle> expectedStreamShard = IntStream.range(0, actualShardList.size()).mapToObj(( i) -> new StreamShardHandle(fakeStreamName, new Shard().withShardId(KinesisShardIdGenerator.generateFromShardOrder(i)))).collect(Collectors.toList());
        Assert.assertThat(actualShardList, containsInAnyOrder(expectedStreamShard.toArray(new StreamShardHandle[actualShardList.size()])));
        // given new shards
        ListShardsResult responseSecond = new ListShardsResult().withShards(new Shard().withShardId(KinesisShardIdGenerator.generateFromShardOrder(2))).withNextToken(null);
        Mockito.doReturn(responseSecond).when(mockClient).listShards(MockitoHamcrest.argThat(KinesisProxyTest.initialListShardsRequestMatcher()));
        // when new shards
        GetShardListResult newShardListResult = kinesisProxy.getShardList(streamHashMap);
        // then new shards
        Assert.assertTrue(newShardListResult.hasRetrievedShards());
        Assert.assertEquals(newShardListResult.getStreamsWithRetrievedShards(), expectedStreams);
        List<StreamShardHandle> newActualShardList = newShardListResult.getRetrievedShardListOfStream(fakeStreamName);
        Assert.assertThat(newActualShardList, hasSize(1));
        List<StreamShardHandle> newExpectedStreamShard = Collections.singletonList(new StreamShardHandle(fakeStreamName, new Shard().withShardId(KinesisShardIdGenerator.generateFromShardOrder(2))));
        Assert.assertThat(newActualShardList, containsInAnyOrder(newExpectedStreamShard.toArray(new StreamShardHandle[newActualShardList.size()])));
    }

    @Test
    public void testGetShardWithNoNewShards() throws Exception {
        // given
        String fakeStreamName = "fake-stream";
        AmazonKinesis mockClient = Mockito.mock(AmazonKinesis.class);
        KinesisProxy kinesisProxy = getProxy(mockClient);
        Mockito.when(mockClient.listShards(new ListShardsRequest().withStreamName(fakeStreamName).withExclusiveStartShardId(KinesisShardIdGenerator.generateFromShardOrder(1)))).thenReturn(new ListShardsResult().withShards(Collections.emptyList()));
        HashMap<String, String> streamHashMap = new HashMap<>();
        streamHashMap.put(fakeStreamName, KinesisShardIdGenerator.generateFromShardOrder(1));
        // when
        GetShardListResult shardListResult = kinesisProxy.getShardList(streamHashMap);
        // then
        Assert.assertFalse(shardListResult.hasRetrievedShards());
    }

    @Test
    public void testGetShardListRetry() throws Exception {
        Properties kinesisConsumerConfig = new Properties();
        kinesisConsumerConfig.setProperty(AWS_REGION, "us-east-1");
        Shard shard = new Shard();
        shard.setShardId("fake-shard-000000000000");
        final ListShardsResult expectedResult = new ListShardsResult();
        expectedResult.withShards(shard);
        MutableInt exceptionCount = new MutableInt();
        final Throwable[] retriableExceptions = new Throwable[]{ new AmazonKinesisException("attempt1"), new AmazonKinesisException("attempt2") };
        AmazonKinesisClient mockClient = Mockito.mock(AmazonKinesisClient.class);
        Mockito.when(mockClient.listShards(ArgumentMatchers.any())).thenAnswer(new Answer<ListShardsResult>() {
            @Override
            public ListShardsResult answer(InvocationOnMock invocation) throws Throwable {
                if ((exceptionCount.intValue()) < (retriableExceptions.length)) {
                    exceptionCount.increment();
                    throw retriableExceptions[((exceptionCount.intValue()) - 1)];
                }
                return expectedResult;
            }
        });
        KinesisProxy kinesisProxy = new KinesisProxy(kinesisConsumerConfig);
        Whitebox.getField(KinesisProxy.class, "kinesisClient").set(kinesisProxy, mockClient);
        HashMap<String, String> streamNames = new HashMap();
        streamNames.put("fake-stream", null);
        GetShardListResult result = kinesisProxy.getShardList(streamNames);
        Assert.assertEquals(retriableExceptions.length, exceptionCount.intValue());
        Assert.assertEquals(true, result.hasRetrievedShards());
        Assert.assertEquals(shard.getShardId(), result.getLastSeenShardOfStream("fake-stream").getShard().getShardId());
        // test max attempt count exceeded
        int maxRetries = 1;
        exceptionCount.setValue(0);
        kinesisConsumerConfig.setProperty(LIST_SHARDS_RETRIES, String.valueOf(maxRetries));
        kinesisProxy = new KinesisProxy(kinesisConsumerConfig);
        Whitebox.getField(KinesisProxy.class, "kinesisClient").set(kinesisProxy, mockClient);
        try {
            kinesisProxy.getShardList(streamNames);
            Assert.fail("exception expected");
        } catch (SdkClientException ex) {
            Assert.assertEquals(retriableExceptions[maxRetries], ex);
        }
        Assert.assertEquals((maxRetries + 1), exceptionCount.intValue());
    }

    @Test
    public void testCustomConfigurationOverride() {
        Properties configProps = new Properties();
        configProps.setProperty(AWSConfigConstants.AWS_REGION, "us-east-1");
        KinesisProxy proxy = new KinesisProxy(configProps) {
            @Override
            protected AmazonKinesis createKinesisClient(Properties configProps) {
                ClientConfiguration clientConfig = new ClientConfigurationFactory().getConfig();
                clientConfig.setSocketTimeout(10000);
                return AWSUtil.createKinesisClient(configProps, clientConfig);
            }
        };
        AmazonKinesis kinesisClient = Whitebox.getInternalState(proxy, "kinesisClient");
        ClientConfiguration clientConfiguration = Whitebox.getInternalState(kinesisClient, "clientConfiguration");
        Assert.assertEquals(10000, clientConfiguration.getSocketTimeout());
    }

    @Test
    public void testClientConfigOverride() {
        Properties configProps = new Properties();
        configProps.setProperty(AWSConfigConstants.AWS_REGION, "us-east-1");
        configProps.setProperty(((AWSUtil.AWS_CLIENT_CONFIG_PREFIX) + "socketTimeout"), "9999");
        KinesisProxyInterface proxy = KinesisProxy.create(configProps);
        AmazonKinesis kinesisClient = Whitebox.getInternalState(proxy, "kinesisClient");
        ClientConfiguration clientConfiguration = Whitebox.getInternalState(kinesisClient, "clientConfiguration");
        Assert.assertEquals(9999, clientConfiguration.getSocketTimeout());
    }

    private static class ListShardsRequestMatcher extends TypeSafeDiagnosingMatcher<ListShardsRequest> {
        private final String shardId;

        private final String nextToken;

        ListShardsRequestMatcher(String shardIdArg, String nextTokenArg) {
            shardId = shardIdArg;
            nextToken = nextTokenArg;
        }

        @Override
        protected boolean matchesSafely(final ListShardsRequest listShardsRequest, final Description description) {
            if ((shardId) == null) {
                if ((listShardsRequest.getExclusiveStartShardId()) != null) {
                    return false;
                }
            } else {
                if (!(shardId.equals(listShardsRequest.getExclusiveStartShardId()))) {
                    return false;
                }
            }
            if ((listShardsRequest.getNextToken()) != null) {
                if (!(((listShardsRequest.getStreamName()) == null) && ((listShardsRequest.getExclusiveStartShardId()) == null))) {
                    return false;
                }
                if (!(listShardsRequest.getNextToken().equals(nextToken))) {
                    return false;
                }
            } else {
                return (nextToken) == null;
            }
            return true;
        }

        @Override
        public void describeTo(final Description description) {
            description.appendText("A ListShardsRequest with a shardId: ").appendValue(shardId).appendText(" and empty nextToken");
        }
    }
}

