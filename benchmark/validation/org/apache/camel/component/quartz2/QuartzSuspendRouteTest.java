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
package org.apache.camel.component.quartz2;


import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;


public class QuartzSuspendRouteTest extends BaseQuartzTest {
    @Test
    public void testQuartzSuspend() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMinimumMessageCount(1);
        assertMockEndpointsSatisfied();
        context.getRouteController().suspendRoute("foo");
        int size = mock.getReceivedCounter();
        resetMocks();
        mock.expectedMessageCount(0);
        mock.assertIsSatisfied(3000);
        assertEquals("Should not schedule when suspended", size, size);
        resetMocks();
        mock.expectedMinimumMessageCount(1);
        context.getRouteController().resumeRoute("foo");
        assertMockEndpointsSatisfied();
    }
}

