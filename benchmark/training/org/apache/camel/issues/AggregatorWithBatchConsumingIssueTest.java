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
package org.apache.camel.issues;


import org.apache.camel.ContextTestSupport;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;


public class AggregatorWithBatchConsumingIssueTest extends ContextTestSupport {
    @Test
    public void testAggregateLostGroupIssue() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(4);
        mock.message(0).body().isEqualTo("0+1+2");
        mock.message(1).body().isEqualTo("3+4+5");
        mock.message(2).body().isEqualTo("6+7+8");
        mock.message(3).body().isEqualTo("9+10+11");
        for (int i = 0; i < 12; i++) {
            sendMessage(i);
        }
        assertMockEndpointsSatisfied();
    }
}

