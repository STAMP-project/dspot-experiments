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
package org.apache.camel.jaxb;


import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


/**
 *
 */
public class SplitterAndExceptionRouteTwistIssueTest extends CamelTestSupport {
    @Produce(uri = "direct:error")
    protected ProducerTemplate templateError;

    @Produce(uri = "direct:error2")
    protected ProducerTemplate templateError2;

    @EndpointInject(uri = "mock:mockReject")
    protected MockEndpoint mockRejectEndpoint;

    @EndpointInject(uri = "mock:mock_output")
    protected MockEndpoint mockOutput;

    @Test
    public void testErrorHandlingJaxb() throws Exception {
        String correctExample = "abcdef";
        String errorExample = "myerror\u0010";
        mockRejectEndpoint.expectedMessageCount(1);
        mockOutput.expectedMessageCount(4);
        templateError.sendBody(correctExample);
        templateError.sendBody(errorExample);
        templateError.sendBody(correctExample);
        templateError.sendBody(correctExample);
        templateError.sendBody(correctExample);
        mockRejectEndpoint.assertIsSatisfied();
        mockOutput.assertIsSatisfied();
    }

    @Test
    public void testErrorHandlingPlumber() throws Exception {
        String correctExample = "abcdef";
        String errorExample = "myerror\u0010";
        mockRejectEndpoint.expectedMessageCount(1);
        mockOutput.expectedMessageCount(4);
        templateError2.sendBody(correctExample);
        templateError2.sendBody(errorExample);
        templateError2.sendBody(correctExample);
        templateError2.sendBody(correctExample);
        templateError2.sendBody(correctExample);
        mockRejectEndpoint.assertIsSatisfied();
        mockOutput.assertIsSatisfied();
    }
}

