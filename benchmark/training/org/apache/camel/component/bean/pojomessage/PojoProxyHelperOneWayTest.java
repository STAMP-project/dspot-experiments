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
package org.apache.camel.component.bean.pojomessage;


import java.util.ArrayList;
import java.util.List;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Endpoint;
import org.apache.camel.component.bean.PojoProxyHelper;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Assert;
import org.junit.Test;


public class PojoProxyHelperOneWayTest extends ContextTestSupport {
    PojoProxyHelperOneWayTest.PersonReceiver receiver = new PojoProxyHelperOneWayTest.PersonReceiver();

    @Test
    public void testOneWay() throws Exception {
        Endpoint personEndpoint = context.getEndpoint("direct:person");
        MockEndpoint result = context.getEndpoint("mock:result", MockEndpoint.class);
        Person person = new Person("Chris");
        result.expectedBodiesReceived(person);
        PojoProxyHelperOneWayTest.PersonHandler sender = PojoProxyHelper.createProxy(personEndpoint, PojoProxyHelperOneWayTest.PersonHandler.class);
        sender.onPerson(person);
        result.assertIsSatisfied();
        Assert.assertEquals(1, receiver.receivedPersons.size());
        Assert.assertEquals(person.getName(), receiver.receivedPersons.get(0).getName());
    }

    public final class PersonReceiver implements PojoProxyHelperOneWayTest.PersonHandler {
        public List<Person> receivedPersons = new ArrayList<>();

        @Override
        public void onPerson(Person person) {
            receivedPersons.add(person);
        }
    }

    public interface PersonHandler {
        void onPerson(Person person);
    }
}

