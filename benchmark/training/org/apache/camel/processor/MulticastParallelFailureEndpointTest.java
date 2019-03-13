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
package org.apache.camel.processor;


import Exchange.FAILURE_ENDPOINT;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Exchange;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Assert;
import org.junit.Test;


public class MulticastParallelFailureEndpointTest extends ContextTestSupport {
    @Test
    public void testMulticastParallel() throws Exception {
        Exchange result = runTest("direct:run");
        Assert.assertNotNull(result);
        Assert.assertEquals("direct://a", result.getProperty(FAILURE_ENDPOINT));
    }

    @Test
    public void testMulticastParallelWithTryCatch() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedBodiesReceived("Bye World");
        Exchange result = runTest("direct:start");
        // try..catch block should clear handled exceptions
        Assert.assertNotNull(result);
        Assert.assertEquals(null, result.getProperty(FAILURE_ENDPOINT));
    }
}

