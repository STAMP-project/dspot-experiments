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
package org.apache.camel.component.infinispan;


import InfinispanConstants.CACHE_NAME;
import InfinispanConstants.EVENT_TYPE;
import InfinispanConstants.IS_PRE;
import InfinispanConstants.KEY;
import org.apache.camel.EndpointInject;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;


public class InfinispanConsumerEntryRemovedTest extends InfinispanTestSupport {
    @EndpointInject(uri = "mock:result")
    private MockEndpoint mockResult;

    @Test
    public void consumerReceivedPreAndPostEntryRemoveEventNotifications() throws Exception {
        currentCache().put(InfinispanTestSupport.KEY_ONE, InfinispanTestSupport.VALUE_ONE);
        mockResult.expectedMessageCount(2);
        mockResult.message(0).outHeader(EVENT_TYPE).isEqualTo("CACHE_ENTRY_REMOVED");
        mockResult.message(0).outHeader(IS_PRE).isEqualTo(true);
        mockResult.message(0).outHeader(CACHE_NAME).isNotNull();
        mockResult.message(0).outHeader(KEY).isEqualTo(InfinispanTestSupport.KEY_ONE);
        mockResult.message(1).outHeader(EVENT_TYPE).isEqualTo("CACHE_ENTRY_REMOVED");
        mockResult.message(1).outHeader(IS_PRE).isEqualTo(false);
        mockResult.message(1).outHeader(CACHE_NAME).isNotNull();
        mockResult.message(1).outHeader(KEY).isEqualTo(InfinispanTestSupport.KEY_ONE);
        currentCache().remove(InfinispanTestSupport.KEY_ONE);
        mockResult.assertIsSatisfied();
    }
}

