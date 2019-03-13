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


import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


/**
 * Unit testing for using a CometdProducer and a CometdConsumer
 */
public class MultipCometdProducerConsumerTest extends CamelTestSupport {
    private int port1;

    private String uri1;

    private int port2;

    private String uri2;

    @Test
    public void testProducer() throws Exception {
        MultipCometdProducerConsumerTest.Person person = new MultipCometdProducerConsumerTest.Person("David", "Greco");
        getMockEndpoint("mock:test1").expectedBodiesReceived(person);
        getMockEndpoint("mock:test1").expectedBodiesReceived(person);
        // act
        template.requestBodyAndHeader("direct:input1", person, "testHeading", "value");
        template.requestBodyAndHeader("direct:input2", person, "testHeading", "value");
        assertMockEndpointsSatisfied();
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

