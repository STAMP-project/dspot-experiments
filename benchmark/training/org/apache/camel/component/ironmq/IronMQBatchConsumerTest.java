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
package org.apache.camel.component.ironmq;


import Exchange.BATCH_COMPLETE;
import Exchange.BATCH_INDEX;
import Exchange.BATCH_SIZE;
import io.iron.ironmq.Message;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


public class IronMQBatchConsumerTest extends CamelTestSupport {
    private IronMQEndpoint endpoint;

    @Test
    public void testConsumeBatchDelete() throws Exception {
        for (int counter = 0; counter <= 5; counter++) {
            Message message = new Message();
            message.setBody((("{\"body\": \"Message " + counter) + "\"}"));
            message.setId(("" + counter));
            ((MockQueue) (endpoint.getClient().queue("testqueue"))).add(message);
        }
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(5);
        assertMockEndpointsSatisfied();
        mock.message(0).exchangeProperty(BATCH_INDEX).isEqualTo(0);
        mock.message(1).exchangeProperty(BATCH_INDEX).isEqualTo(1);
        mock.message(2).exchangeProperty(BATCH_INDEX).isEqualTo(2);
        mock.message(3).exchangeProperty(BATCH_INDEX).isEqualTo(3);
        mock.message(4).exchangeProperty(BATCH_INDEX).isEqualTo(4);
        mock.message(0).exchangeProperty(BATCH_COMPLETE).isEqualTo(false);
        mock.message(1).exchangeProperty(BATCH_COMPLETE).isEqualTo(false);
        mock.message(2).exchangeProperty(BATCH_COMPLETE).isEqualTo(false);
        mock.message(3).exchangeProperty(BATCH_COMPLETE).isEqualTo(false);
        mock.message(3).exchangeProperty(BATCH_COMPLETE).isEqualTo(false);
        mock.message(4).exchangeProperty(BATCH_COMPLETE).isEqualTo(true);
        mock.expectedPropertyReceived(BATCH_SIZE, 5);
    }
}

