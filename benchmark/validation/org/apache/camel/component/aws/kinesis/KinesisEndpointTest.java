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


import ShardIteratorType.AFTER_SEQUENCE_NUMBER;
import ShardIteratorType.AT_SEQUENCE_NUMBER;
import ShardIteratorType.LATEST;
import ShardIteratorType.TRIM_HORIZON;
import com.amazonaws.services.kinesis.AmazonKinesis;
import org.apache.camel.CamelContext;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class KinesisEndpointTest {
    @Mock
    private AmazonKinesis amazonKinesisClient;

    private CamelContext camelContext;

    @Test
    public void allTheEndpointParams() throws Exception {
        KinesisEndpoint endpoint = ((KinesisEndpoint) (camelContext.getEndpoint(("aws-kinesis://some_stream_name" + (((("?amazonKinesisClient=#kinesisClient" + "&maxResultsPerRequest=101") + "&iteratorType=latest") + "&shardId=abc") + "&sequenceNumber=123")))));
        Assert.assertThat(endpoint.getConfiguration().getAmazonKinesisClient(), CoreMatchers.is(amazonKinesisClient));
        Assert.assertThat(endpoint.getConfiguration().getStreamName(), CoreMatchers.is("some_stream_name"));
        Assert.assertThat(endpoint.getConfiguration().getIteratorType(), CoreMatchers.is(LATEST));
        Assert.assertThat(endpoint.getConfiguration().getMaxResultsPerRequest(), CoreMatchers.is(101));
        Assert.assertThat(endpoint.getConfiguration().getSequenceNumber(), CoreMatchers.is("123"));
        Assert.assertThat(endpoint.getConfiguration().getShardId(), CoreMatchers.is("abc"));
    }

    @Test
    public void onlyRequiredEndpointParams() throws Exception {
        KinesisEndpoint endpoint = ((KinesisEndpoint) (camelContext.getEndpoint(("aws-kinesis://some_stream_name" + "?amazonKinesisClient=#kinesisClient"))));
        Assert.assertThat(endpoint.getConfiguration().getAmazonKinesisClient(), CoreMatchers.is(amazonKinesisClient));
        Assert.assertThat(endpoint.getConfiguration().getStreamName(), CoreMatchers.is("some_stream_name"));
        Assert.assertThat(endpoint.getConfiguration().getIteratorType(), CoreMatchers.is(TRIM_HORIZON));
        Assert.assertThat(endpoint.getConfiguration().getMaxResultsPerRequest(), CoreMatchers.is(1));
    }

    @Test
    public void afterSequenceNumberRequiresSequenceNumber() throws Exception {
        KinesisEndpoint endpoint = ((KinesisEndpoint) (camelContext.getEndpoint(("aws-kinesis://some_stream_name" + ((("?amazonKinesisClient=#kinesisClient" + "&iteratorType=AFTER_SEQUENCE_NUMBER") + "&shardId=abc") + "&sequenceNumber=123")))));
        Assert.assertThat(endpoint.getConfiguration().getAmazonKinesisClient(), CoreMatchers.is(amazonKinesisClient));
        Assert.assertThat(endpoint.getConfiguration().getStreamName(), CoreMatchers.is("some_stream_name"));
        Assert.assertThat(endpoint.getConfiguration().getIteratorType(), CoreMatchers.is(AFTER_SEQUENCE_NUMBER));
        Assert.assertThat(endpoint.getConfiguration().getShardId(), CoreMatchers.is("abc"));
        Assert.assertThat(endpoint.getConfiguration().getSequenceNumber(), CoreMatchers.is("123"));
    }

    @Test
    public void atSequenceNumberRequiresSequenceNumber() throws Exception {
        KinesisEndpoint endpoint = ((KinesisEndpoint) (camelContext.getEndpoint(("aws-kinesis://some_stream_name" + ((("?amazonKinesisClient=#kinesisClient" + "&iteratorType=AT_SEQUENCE_NUMBER") + "&shardId=abc") + "&sequenceNumber=123")))));
        Assert.assertThat(endpoint.getConfiguration().getAmazonKinesisClient(), CoreMatchers.is(amazonKinesisClient));
        Assert.assertThat(endpoint.getConfiguration().getStreamName(), CoreMatchers.is("some_stream_name"));
        Assert.assertThat(endpoint.getConfiguration().getIteratorType(), CoreMatchers.is(AT_SEQUENCE_NUMBER));
        Assert.assertThat(endpoint.getConfiguration().getShardId(), CoreMatchers.is("abc"));
        Assert.assertThat(endpoint.getConfiguration().getSequenceNumber(), CoreMatchers.is("123"));
    }
}

