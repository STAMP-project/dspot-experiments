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


import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.camel.Exchange;
import org.apache.camel.component.mock.AssertionClause;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


public class ActiveMQPropagateSerializableHeadersTest extends CamelTestSupport {
    protected Object expectedBody = ("<time>" + (new Date())) + "</time>";

    protected ActiveMQQueue replyQueue = new ActiveMQQueue("test.reply.queue");

    protected String correlationID = "ABC-123";

    protected String messageType = getClass().getName();

    private Calendar calValue;

    private Map<String, Object> mapValue;

    @Test
    public void testForwardingAMessageAcrossJMSKeepingCustomJMSHeaders() throws Exception {
        MockEndpoint resultEndpoint = resolveMandatoryEndpoint("mock:result", MockEndpoint.class);
        resultEndpoint.expectedBodiesReceived(expectedBody);
        AssertionClause firstMessageExpectations = resultEndpoint.message(0);
        firstMessageExpectations.header("myCal").isEqualTo(calValue);
        firstMessageExpectations.header("myMap").isEqualTo(mapValue);
        template.sendBody("activemq:test.a", expectedBody);
        resultEndpoint.assertIsSatisfied();
        List<Exchange> list = resultEndpoint.getReceivedExchanges();
        Exchange exchange = list.get(0);
        {
            String headerValue = exchange.getIn().getHeader("myString", String.class);
            assertEquals("myString", "stringValue", headerValue);
        }
        {
            Calendar headerValue = exchange.getIn().getHeader("myCal", Calendar.class);
            assertEquals("myCal", calValue, headerValue);
        }
        {
            Map<String, Object> headerValue = exchange.getIn().getHeader("myMap", Map.class);
            assertEquals("myMap", mapValue, headerValue);
        }
    }
}

