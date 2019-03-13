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
import S3Constants.KEY;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.io.File;
import org.apache.camel.BindToRegistry;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


public class S3ComponentFileTest extends CamelTestSupport {
    @BindToRegistry("amazonS3Client")
    AmazonS3ClientMock client = new AmazonS3ClientMock();

    @EndpointInject(uri = "direct:startKeep")
    ProducerTemplate templateKeep;

    @EndpointInject(uri = "direct:startDelete")
    ProducerTemplate templateDelete;

    @EndpointInject(uri = "mock:result")
    MockEndpoint result;

    File testFile;

    @Test
    public void sendFile() throws Exception {
        result.expectedMessageCount(1);
        Exchange exchange = templateKeep.send("direct:startKeep", InOnly, new Processor() {
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(KEY, "CamelUnitTest");
                exchange.getIn().setBody(testFile);
            }
        });
        assertMockEndpointsSatisfied();
        assertResultExchange(result.getExchanges().get(0), true);
        PutObjectRequest putObjectRequest = client.putObjectRequests.get(0);
        assertEquals(getCamelBucket(), putObjectRequest.getBucketName());
        assertResponseMessage(exchange.getIn());
        assertFileExists(testFile.getAbsolutePath());
    }
}

