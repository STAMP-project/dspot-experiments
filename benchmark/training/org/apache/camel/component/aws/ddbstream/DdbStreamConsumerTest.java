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
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBStreams;
import com.amazonaws.services.dynamodbv2.model.ExpiredIteratorException;
import com.amazonaws.services.dynamodbv2.model.GetRecordsRequest;
import com.amazonaws.services.dynamodbv2.model.GetRecordsResult;
import com.amazonaws.services.dynamodbv2.model.Record;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;


@RunWith(MockitoJUnitRunner.class)
public class DdbStreamConsumerTest {
    private DdbStreamConsumer undertest;

    @Mock
    private AmazonDynamoDBStreams amazonDynamoDBStreams;

    @Mock
    private AsyncProcessor processor;

    @Mock
    private ShardIteratorHandler shardIteratorHandler;

    private final CamelContext context = new DefaultCamelContext();

    private final DdbStreamComponent component = new DdbStreamComponent(context);

    private final DdbStreamEndpoint endpoint = new DdbStreamEndpoint(null, new DdbStreamConfiguration(), component);

    private DdbStreamConsumerTest.GetRecordsAnswer recordsAnswer;

    @Test
    public void itResumesFromAfterTheLastSeenSequenceNumberWhenAShardIteratorHasExpired() throws Exception {
        endpoint.getConfiguration().setIteratorType(LATEST);
        Mockito.when(shardIteratorHandler.getShardIterator(ArgumentMatchers.isNull())).thenReturn("shard_iterator_b_000", "shard_iterator_b_001");
        Mockito.when(shardIteratorHandler.getShardIterator(ArgumentMatchers.anyString())).thenReturn("shard_iterator_b_001");
        Mockito.when(amazonDynamoDBStreams.getRecords(ArgumentMatchers.any(GetRecordsRequest.class))).thenAnswer(recordsAnswer).thenThrow(new ExpiredIteratorException("expired shard")).thenAnswer(recordsAnswer);
        undertest.poll();
        undertest.poll();
        ArgumentCaptor<Exchange> exchangeCaptor = ArgumentCaptor.forClass(Exchange.class);
        Mockito.verify(processor, Mockito.times(3)).process(exchangeCaptor.capture(), ArgumentMatchers.any(AsyncCallback.class));
        Mockito.verify(shardIteratorHandler, Mockito.times(2)).getShardIterator(null);// first poll. Second poll, getRecords fails with an expired shard.

        Mockito.verify(shardIteratorHandler).getShardIterator("9");// second poll, with a resumeFrom.

        Assert.assertThat(exchangeCaptor.getAllValues().get(0).getIn().getBody(Record.class).getDynamodb().getSequenceNumber(), CoreMatchers.is("9"));
        Assert.assertThat(exchangeCaptor.getAllValues().get(1).getIn().getBody(Record.class).getDynamodb().getSequenceNumber(), CoreMatchers.is("11"));
        Assert.assertThat(exchangeCaptor.getAllValues().get(2).getIn().getBody(Record.class).getDynamodb().getSequenceNumber(), CoreMatchers.is("13"));
    }

    @Test
    public void atSeqNumber35GivesFirstRecordWithSeq35() throws Exception {
        endpoint.getConfiguration().setIteratorType(AT_SEQUENCE_NUMBER);
        endpoint.getConfiguration().setSequenceNumberProvider(new StaticSequenceNumberProvider("35"));
        Mockito.when(shardIteratorHandler.getShardIterator(ArgumentMatchers.isNull())).thenReturn("shard_iterator_d_001", "shard_iterator_d_002");
        for (int i = 0; i < 10; ++i) {
            // poll lots.
            undertest.poll();
        }
        ArgumentCaptor<Exchange> exchangeCaptor = ArgumentCaptor.forClass(Exchange.class);
        Mockito.verify(processor, Mockito.times(2)).process(exchangeCaptor.capture(), ArgumentMatchers.any(AsyncCallback.class));
        Assert.assertThat(exchangeCaptor.getAllValues().get(0).getIn().getBody(Record.class).getDynamodb().getSequenceNumber(), CoreMatchers.is("35"));
        Assert.assertThat(exchangeCaptor.getAllValues().get(1).getIn().getBody(Record.class).getDynamodb().getSequenceNumber(), CoreMatchers.is("40"));
    }

    @Test
    public void afterSeqNumber35GivesFirstRecordWithSeq40() throws Exception {
        endpoint.getConfiguration().setIteratorType(AFTER_SEQUENCE_NUMBER);
        endpoint.getConfiguration().setSequenceNumberProvider(new StaticSequenceNumberProvider("35"));
        Mockito.when(shardIteratorHandler.getShardIterator(ArgumentMatchers.isNull())).thenReturn("shard_iterator_d_001", "shard_iterator_d_002");
        for (int i = 0; i < 10; ++i) {
            // poll lots.
            undertest.poll();
        }
        ArgumentCaptor<Exchange> exchangeCaptor = ArgumentCaptor.forClass(Exchange.class);
        Mockito.verify(processor, Mockito.times(1)).process(exchangeCaptor.capture(), ArgumentMatchers.any(AsyncCallback.class));
        Assert.assertThat(exchangeCaptor.getAllValues().get(0).getIn().getBody(Record.class).getDynamodb().getSequenceNumber(), CoreMatchers.is("40"));
    }

    private class GetRecordsAnswer implements Answer<GetRecordsResult> {
        private final Map<String, String> shardIterators;

        private final Map<String, Collection<Record>> answers;

        private final Pattern shardIteratorPattern = Pattern.compile("shard_iterator_d_0*(\\d+)");

        GetRecordsAnswer(Map<String, String> shardIterators, Map<String, Collection<Record>> answers) {
            this.shardIterators = shardIterators;
            this.answers = answers;
        }

        @Override
        public GetRecordsResult answer(InvocationOnMock invocation) throws Throwable {
            final String shardIterator = getShardIterator();
            // note that HashMap returns null when there is no entry in the map.
            // A null 'nextShardIterator' indicates that the shard has finished
            // and we should move onto the next shard.
            String nextShardIterator = shardIterators.get(shardIterator);
            Matcher m = shardIteratorPattern.matcher(shardIterator);
            Collection<Record> ans = answers.get(shardIterator);
            if ((nextShardIterator == null) && (m.matches())) {
                // last shard iterates forever.
                Integer num = Integer.parseInt(m.group(1));
                nextShardIterator = "shard_iterator_d_" + (pad(Integer.toString((num + 1)), 3));
            }
            if (null == ans) {
                // default to an empty list of records.
                ans = DdbStreamConsumerTest.createRecords();
            }
            return new GetRecordsResult().withRecords(ans).withNextShardIterator(nextShardIterator);
        }
    }
}

