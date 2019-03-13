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
package org.apache.camel.component.aws.s3;


import ExchangePattern.InOnly;
import S3Constants.BUCKET_DESTINATION_NAME;
import S3Constants.DESTINATION_KEY;
import S3Constants.KEY;
import org.apache.camel.BindToRegistry;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


public class S3ComponentCopyObjectTest extends CamelTestSupport {
    @BindToRegistry("amazonS3Client")
    AmazonS3ClientMock clientMock = new AmazonS3ClientMock();

    @EndpointInject(uri = "direct:start")
    private ProducerTemplate template;

    @EndpointInject(uri = "mock:result")
    private MockEndpoint result;

    @Test
    public void sendIn() throws Exception {
        result.expectedMessageCount(1);
        template.send("direct:start", InOnly, new Processor() {
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(BUCKET_DESTINATION_NAME, "camelDestinationBucket");
                exchange.getIn().setHeader(KEY, "camelKey");
                exchange.getIn().setHeader(DESTINATION_KEY, "camelDestinationKey");
            }
        });
        assertMockEndpointsSatisfied();
        assertResultExchange(result.getExchanges().get(0));
    }
}

