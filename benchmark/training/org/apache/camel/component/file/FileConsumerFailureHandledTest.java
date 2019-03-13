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
package org.apache.camel.component.file;


import Exchange.FILE_NAME;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.RuntimeCamelException;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;


/**
 * Unit test for consuming files but the exchange fails and is handled
 * by the failure handler (usually the DeadLetterChannel)
 */
public class FileConsumerFailureHandledTest extends ContextTestSupport {
    @Test
    public void testParis() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:valid");
        mock.expectedBodiesReceived("Hello Paris");
        template.sendBodyAndHeader("file:target/data/messages/input/", "Paris", FILE_NAME, "paris.txt");
        mock.assertIsSatisfied();
        oneExchangeDone.matchesMockWaitTime();
        FileConsumerFailureHandledTest.assertFiles("paris.txt", true);
    }

    @Test
    public void testLondon() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:invalid");
        // we get the original input so its not Hello London but only London
        mock.expectedBodiesReceived("London");
        template.sendBodyAndHeader("file:target/data/messages/input/", "London", FILE_NAME, "london.txt");
        mock.assertIsSatisfied();
        oneExchangeDone.matchesMockWaitTime();
        // london should be deleted as we have failure handled it
        FileConsumerFailureHandledTest.assertFiles("london.txt", true);
    }

    @Test
    public void testDublin() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:beer");
        // we get the original input so its not Hello London but only London
        mock.expectedBodiesReceived("Dublin");
        template.sendBodyAndHeader("file:target/data/messages/input/", "Dublin", FILE_NAME, "dublin.txt");
        mock.assertIsSatisfied();
        oneExchangeDone.matchesMockWaitTime();
        // dublin should NOT be deleted, but should be retired on next consumer
        FileConsumerFailureHandledTest.assertFiles("dublin.txt", false);
    }

    @Test
    public void testMadrid() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:error");
        // we get the original input so its not Hello London but only London
        mock.expectedBodiesReceived("Madrid");
        template.sendBodyAndHeader("file:target/data/messages/input/", "Madrid", FILE_NAME, "madrid.txt");
        mock.assertIsSatisfied();
        oneExchangeDone.matchesMockWaitTime();
        // madrid should be deleted as the DLC handles it
        FileConsumerFailureHandledTest.assertFiles("madrid.txt", true);
    }

    private static class MyValidatorProcessor implements Processor {
        public void process(Exchange exchange) throws Exception {
            String body = exchange.getIn().getBody(String.class);
            if ("London".equals(body)) {
                throw new org.apache.camel.ValidationException(exchange, "Forced exception by unit test");
            } else
                if ("Madrid".equals(body)) {
                    throw new RuntimeCamelException("Madrid is not a supported city");
                } else
                    if ("Dublin".equals(body)) {
                        throw new org.apache.camel.ValidationException(exchange, "Dublin have good beer");
                    }


            exchange.getOut().setBody(("Hello " + body));
        }
    }
}

