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
package org.apache.camel.component.cxf.converter;


import org.apache.camel.non_wrapper.Person;
import org.apache.camel.non_wrapper.types.GetPerson;
import org.apache.camel.non_wrapper.types.GetPersonResponse;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.junit.Test;
import org.springframework.context.support.AbstractXmlApplicationContext;


public class PayLoadConvertToPOJOTest extends CamelTestSupport {
    protected AbstractXmlApplicationContext applicationContext;

    @Test
    public void testClient() throws Exception {
        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.setAddress((((("http://localhost:" + (PayLoadConvertToPOJOTest.getPort1())) + "/") + (getClass().getSimpleName())) + "/CamelContext/RouterPort"));
        factory.setServiceClass(Person.class);
        Person person = factory.create(Person.class);
        GetPerson payload = new GetPerson();
        payload.setPersonId("1234");
        GetPersonResponse reply = person.getPerson(payload);
        assertEquals("Get the wrong person id.", "1234", reply.getPersonId());
    }
}

