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
package org.apache.camel.processor.aggregator;


import org.apache.camel.ContextTestSupport;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;


/**
 * Based on user forum issue
 */
public class AggregateLostGroupIssueTest extends ContextTestSupport {
    private int messageIndex;

    @Test
    public void testAggregateLostGroupIssue() throws Exception {
        messageIndex = 0;
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(2);
        mock.message(0).body().isEqualTo("0,1,2,3,4,5,6,7,8,9");
        mock.message(1).body().isEqualTo("10,11,12,13,14,15,16,17,18,19");
        assertMockEndpointsSatisfied();
    }
}

