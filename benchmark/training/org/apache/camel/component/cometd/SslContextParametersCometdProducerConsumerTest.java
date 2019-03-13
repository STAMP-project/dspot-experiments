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
package org.apache.camel.component.cometd;


import java.util.List;
import org.apache.camel.Exchange;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


/**
 * Unit testing for using a CometdProducer and a CometdConsumer
 */
public class SslContextParametersCometdProducerConsumerTest extends CamelTestSupport {
    private int port;

    private String uri;

    @Test
    public void testProducer() throws Exception {
        SslContextParametersCometdProducerConsumerTest.Person person = new SslContextParametersCometdProducerConsumerTest.Person("David", "Greco");
        template.requestBody("direct:input", person);
        MockEndpoint ep = context.getEndpoint("mock:test", MockEndpoint.class);
        List<Exchange> exchanges = ep.getReceivedExchanges();
        for (Exchange exchange : exchanges) {
            SslContextParametersCometdProducerConsumerTest.Person person1 = ((SslContextParametersCometdProducerConsumerTest.Person) (exchange.getIn().getBody()));
            assertEquals("David", person1.getName());
            assertEquals("Greco", person1.getSurname());
        }
    }

    public static class Person {
        private String name;

        private String surname;

        Person(String name, String surname) {
            this.name = name;
            this.surname = surname;
        }

        public String getName() {
            return name;
        }

        public String getSurname() {
            return surname;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setSurname(String surname) {
            this.surname = surname;
        }
    }
}

