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
package org.apache.camel.component.aws.swf.integration;


import org.apache.camel.EndpointInject;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Ignore;
import org.junit.Test;


@Ignore("Must be manually tested. Provide your own accessKey and secretKey and also create a SWF domain in advance")
public class CamelSWFEndToEndTest extends CamelTestSupport {
    protected String options = "accessKey=XXX" + ((((("&secretKey=YYY" + "&domainName=ZZZ") + "&activityList=swf-alist") + "&workflowList=swf-wlist") + "&clientConfiguration.endpoint=swf.eu-west-1.amazonaws.com") + "&version=1.0");

    @EndpointInject(uri = "mock:starter")
    private MockEndpoint starter;

    @EndpointInject(uri = "mock:decider")
    private MockEndpoint decider;

    @EndpointInject(uri = "mock:worker")
    private MockEndpoint worker;

    @Test
    public void consumerReceivedPreAndPostEntryCreatedEventNotifications() throws Exception {
        starter.expectedMessageCount(1);
        decider.expectedMinimumMessageCount(1);
        worker.expectedMessageCount(2);
        template.requestBody("direct:start", "Hello world!");
        assertIsSatisfied(context);
    }
}

