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
package org.apache.camel.component.aws.ddbstream;


import ShardIteratorType.AFTER_SEQUENCE_NUMBER;
import ShardIteratorType.AT_SEQUENCE_NUMBER;
import ShardIteratorType.LATEST;
import ShardIteratorType.TRIM_HORIZON;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBStreams;
import com.amazonaws.services.dynamodbv2.model.GetShardIteratorRequest;
import org.apache.camel.CamelContext;
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
public class ShardIteratorHandlerTest {
    private ShardIteratorHandler undertest;

    @Mock
    private AmazonDynamoDBStreams amazonDynamoDBStreams;

    private final CamelContext context = new DefaultCamelContext();

    private final DdbStreamComponent component = new DdbStreamComponent(context);

    private final DdbStreamEndpoint endpoint = new DdbStreamEndpoint(null, new DdbStreamConfiguration(), component);

    @Test
    public void latestOnlyUsesTheLastShard() throws Exception {
        endpoint.getConfiguration().setIteratorType(LATEST);
        String shardIterator = undertest.getShardIterator(null);
        ArgumentCaptor<GetShardIteratorRequest> getIteratorCaptor = ArgumentCaptor.forClass(GetShardIteratorRequest.class);
        Mockito.verify(amazonDynamoDBStreams).getShardIterator(getIteratorCaptor.capture());
        Assert.assertThat(getIteratorCaptor.getValue().getShardId(), CoreMatchers.is("d"));
        Assert.assertThat(shardIterator, CoreMatchers.is("shard_iterator_d_000"));
    }

    @Test
    public void cachesRecentShardId() throws Exception {
        endpoint.getConfiguration().setIteratorType(LATEST);
        undertest.updateShardIterator("bar");
        String shardIterator = undertest.getShardIterator(null);
        Mockito.verify(amazonDynamoDBStreams, Mockito.times(0)).getShardIterator(ArgumentMatchers.any(GetShardIteratorRequest.class));
        Assert.assertThat(shardIterator, CoreMatchers.is("bar"));
    }

    @Test
    public void trimHorizonStartsWithTheFirstShard() throws Exception {
        endpoint.getConfiguration().setIteratorType(TRIM_HORIZON);
        String shardIterator = undertest.getShardIterator(null);
        ArgumentCaptor<GetShardIteratorRequest> getIteratorCaptor = ArgumentCaptor.forClass(GetShardIteratorRequest.class);
        Mockito.verify(amazonDynamoDBStreams).getShardIterator(getIteratorCaptor.capture());
        Assert.assertThat(getIteratorCaptor.getValue().getShardId(), CoreMatchers.is("a"));
        Assert.assertThat(shardIterator, CoreMatchers.is("shard_iterator_a_000"));
    }

    @Test
    public void trimHorizonWalksAllShards() throws Exception {
        endpoint.getConfiguration().setIteratorType(TRIM_HORIZON);
        String[] shardIterators = new String[4];
        for (int i = 0; i < (shardIterators.length); ++i) {
            shardIterators[i] = undertest.getShardIterator(null);
            undertest.updateShardIterator(null);
        }
        ArgumentCaptor<GetShardIteratorRequest> getIteratorCaptor = ArgumentCaptor.forClass(GetShardIteratorRequest.class);
        Mockito.verify(amazonDynamoDBStreams, Mockito.times(4)).getShardIterator(getIteratorCaptor.capture());
        String[] shards = new String[]{ "a", "b", "c", "d" };
        for (int i = 0; i < (shards.length); ++i) {
            Assert.assertThat(getIteratorCaptor.getAllValues().get(i).getShardId(), CoreMatchers.is(shards[i]));
        }
        Assert.assertThat(shardIterators, CoreMatchers.is(new String[]{ "shard_iterator_a_000", "shard_iterator_b_000", "shard_iterator_c_000", "shard_iterator_d_000" }));
    }

    @Test
    public void atSeqNumber12StartsWithShardB() throws Exception {
        endpoint.getConfiguration().setIteratorType(AT_SEQUENCE_NUMBER);
        endpoint.getConfiguration().setSequenceNumberProvider(new StaticSequenceNumberProvider("12"));
        String shardIterator = undertest.getShardIterator(null);
        ArgumentCaptor<GetShardIteratorRequest> getIteratorCaptor = ArgumentCaptor.forClass(GetShardIteratorRequest.class);
        Mockito.verify(amazonDynamoDBStreams).getShardIterator(getIteratorCaptor.capture());
        Assert.assertThat(getIteratorCaptor.getValue().getShardId(), CoreMatchers.is("b"));
        Assert.assertThat(shardIterator, CoreMatchers.is("shard_iterator_b_000"));
    }

    @Test
    public void afterSeqNumber16StartsWithShardD() throws Exception {
        endpoint.getConfiguration().setIteratorType(AFTER_SEQUENCE_NUMBER);
        endpoint.getConfiguration().setSequenceNumberProvider(new StaticSequenceNumberProvider("16"));
        String shardIterator = undertest.getShardIterator(null);
        ArgumentCaptor<GetShardIteratorRequest> getIteratorCaptor = ArgumentCaptor.forClass(GetShardIteratorRequest.class);
        Mockito.verify(amazonDynamoDBStreams).getShardIterator(getIteratorCaptor.capture());
        Assert.assertThat(getIteratorCaptor.getValue().getShardId(), CoreMatchers.is("d"));
        Assert.assertThat(shardIterator, CoreMatchers.is("shard_iterator_d_000"));
    }

    @Test
    public void resumingFromSomewhereActuallyUsesTheAfterSequenceNumber() throws Exception {
        endpoint.getConfiguration().setIteratorType(LATEST);
        String shardIterator = undertest.getShardIterator("12");
        ArgumentCaptor<GetShardIteratorRequest> getIteratorCaptor = ArgumentCaptor.forClass(GetShardIteratorRequest.class);
        Mockito.verify(amazonDynamoDBStreams).getShardIterator(getIteratorCaptor.capture());
        Assert.assertThat(getIteratorCaptor.getValue().getShardId(), CoreMatchers.is("b"));
        Assert.assertThat(shardIterator, CoreMatchers.is("shard_iterator_b_000"));
        Assert.assertThat(getIteratorCaptor.getValue().getShardIteratorType(), CoreMatchers.is(AFTER_SEQUENCE_NUMBER.name()));
        Assert.assertThat(getIteratorCaptor.getValue().getSequenceNumber(), CoreMatchers.is("12"));
    }
}

