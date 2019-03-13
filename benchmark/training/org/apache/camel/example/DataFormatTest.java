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
package org.apache.camel.example;


import org.apache.camel.Exchange;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.foo.bar.PersonType;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


public class DataFormatTest extends CamelTestSupport {
    private MockEndpoint resultEndpoint;

    @Test
    public void testMarshalThenUnmarshalBean() throws Exception {
        PurchaseOrder bean = new PurchaseOrder();
        bean.setName("Beer");
        bean.setAmount(23);
        bean.setPrice(2.5);
        resultEndpoint.expectedBodiesReceived(bean);
        template.sendBody("direct:start", bean);
        resultEndpoint.assertIsSatisfied();
    }

    @Test
    public void testMarshalPrettyPrint() throws Exception {
        PersonType person = new PersonType();
        person.setFirstName("Willem");
        person.setLastName("Jiang");
        resultEndpoint.expectedMessageCount(1);
        template.sendBody("direct:prettyPrint", person);
        resultEndpoint.assertIsSatisfied();
        Exchange exchange = resultEndpoint.getExchanges().get(0);
        String result = exchange.getIn().getBody(String.class);
        assertNotNull("The result should not be null", result);
        int indexPerson = result.indexOf("<Person>");
        int indexFirstName = result.indexOf("<firstName>");
        assertTrue("we should find the <Person>", (indexPerson > 0));
        assertTrue("we should find the <firstName>", (indexFirstName > 0));
        assertTrue("There should some sapce between <Person> and <firstName>", ((indexFirstName - indexPerson) > 8));
    }
}

