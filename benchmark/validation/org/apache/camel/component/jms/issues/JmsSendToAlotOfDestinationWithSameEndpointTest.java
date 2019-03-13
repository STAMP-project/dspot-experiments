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
package org.apache.camel.component.jms.issues;


import ExchangePattern.InOnly;
import JmsConstants.JMS_DESTINATION_NAME;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JmsSendToAlotOfDestinationWithSameEndpointTest extends CamelSpringTestSupport {
    private static final Logger LOG = LoggerFactory.getLogger(JmsSendToAlotOfDestinationWithSameEndpointTest.class);

    private static final String URI = "activemq:queue:foo?autoStartup=false";

    @Test
    public void testSendToAlotOfMessageToQueues() throws Exception {
        int size = 100;
        JmsSendToAlotOfDestinationWithSameEndpointTest.LOG.info((("About to send " + size) + " messages"));
        for (int i = 0; i < size; i++) {
            // use the same endpoint but provide a header with the dynamic queue we send to
            // this allows us to reuse endpoints and not create a new endpoint for each and every jms queue
            // we send to
            if ((i > 0) && ((i % 50) == 0)) {
                JmsSendToAlotOfDestinationWithSameEndpointTest.LOG.info((("Send " + i) + " messages so far"));
            }
            template.sendBodyAndHeader(JmsSendToAlotOfDestinationWithSameEndpointTest.URI, InOnly, ("Hello " + i), JMS_DESTINATION_NAME, ("foo" + i));
        }
        JmsSendToAlotOfDestinationWithSameEndpointTest.LOG.info("Send complete use jconsole to view");
        // now we should be able to poll a message from each queue
        // Thread.sleep(99999999);
    }
}

