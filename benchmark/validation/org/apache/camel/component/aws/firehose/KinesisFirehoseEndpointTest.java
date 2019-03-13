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


import Regions.US_EAST_1;
import com.amazonaws.services.kinesisfirehose.AmazonKinesisFirehose;
import org.apache.camel.CamelContext;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class KinesisFirehoseEndpointTest {
    @Mock
    private AmazonKinesisFirehose amazonKinesisFirehoseClient;

    private CamelContext camelContext;

    @Test
    public void allEndpointParams() throws Exception {
        KinesisFirehoseEndpoint endpoint = ((KinesisFirehoseEndpoint) (camelContext.getEndpoint(("aws-kinesis-firehose://some_stream_name" + "?amazonKinesisFirehoseClient=#firehoseClient"))));
        endpoint.start();
        Assert.assertThat(endpoint.getClient(), CoreMatchers.is(amazonKinesisFirehoseClient));
        Assert.assertThat(endpoint.getConfiguration().getStreamName(), CoreMatchers.is("some_stream_name"));
    }

    @Test
    public void allClientCreationParams() throws Exception {
        KinesisFirehoseEndpoint endpoint = ((KinesisFirehoseEndpoint) (camelContext.getEndpoint(("aws-kinesis-firehose://some_stream_name" + "?accessKey=xxx&secretKey=yyy&region=us-east-1"))));
        Assert.assertThat(endpoint.getConfiguration().getRegion(), CoreMatchers.is(US_EAST_1.getName()));
        Assert.assertThat(endpoint.getConfiguration().getAccessKey(), CoreMatchers.is("xxx"));
        Assert.assertThat(endpoint.getConfiguration().getSecretKey(), CoreMatchers.is("yyy"));
        Assert.assertThat(endpoint.getConfiguration().getStreamName(), CoreMatchers.is("some_stream_name"));
    }
}

