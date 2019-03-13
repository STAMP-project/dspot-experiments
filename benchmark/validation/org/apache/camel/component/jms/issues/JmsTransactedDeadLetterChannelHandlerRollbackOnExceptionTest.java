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


import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


public class JmsTransactedDeadLetterChannelHandlerRollbackOnExceptionTest extends CamelTestSupport {
    public static class BadErrorHandler {
        @Handler
        public void onException(Exchange exchange, Exception exception) throws Exception {
            throw new RuntimeException("error in errorhandler");
        }
    }

    protected final String testingEndpoint = "activemq:test." + (getClass().getName());

    @Test
    public void shouldNotLoseMessagesOnExceptionInErrorHandler() throws Exception {
        template.sendBody(testingEndpoint, "Hello World");
        // as we handle new exception, then the exception is ignored
        // and causes the transaction to commit, so there is no message in the ActiveMQ DLQ queue
        Object dlqBody = consumer.receiveBody("activemq:ActiveMQ.DLQ", 2000);
        assertNull("Should not rollback the transaction", dlqBody);
    }
}

