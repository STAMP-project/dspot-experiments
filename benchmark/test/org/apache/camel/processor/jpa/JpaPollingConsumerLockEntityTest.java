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
package org.apache.camel.processor.jpa;


import java.util.HashMap;
import java.util.Map;
import javax.persistence.OptimisticLockException;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.examples.Customer;
import org.junit.Test;


public class JpaPollingConsumerLockEntityTest extends AbstractJpaTest {
    protected static final String SELECT_ALL_STRING = ("select x from " + (Customer.class.getName())) + " x";

    @Test
    public void testPollingConsumerWithLock() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:locked");
        mock.expectedBodiesReceived("orders: 1", "orders: 2");
        Map<String, Object> headers = new HashMap<>();
        headers.put("name", "Donald%");
        template.asyncRequestBodyAndHeaders("direct:locked", "message", headers);
        template.asyncRequestBodyAndHeaders("direct:locked", "message", headers);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testPollingConsumerWithoutLock() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:not-locked");
        MockEndpoint errMock = getMockEndpoint("mock:error");
        mock.expectedBodiesReceived("orders: 1");
        errMock.expectedMessageCount(1);
        errMock.message(0).body().isInstanceOf(OptimisticLockException.class);
        Map<String, Object> headers = new HashMap<>();
        headers.put("name", "Donald%");
        template.asyncRequestBodyAndHeaders("direct:not-locked", "message", headers);
        template.asyncRequestBodyAndHeaders("direct:not-locked", "message", headers);
        assertMockEndpointsSatisfied();
    }
}

