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
package org.apache.camel.component.braintree;


import com.braintreegateway.Address;
import com.braintreegateway.AddressRequest;
import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Customer;
import com.braintreegateway.Result;
import java.util.LinkedList;
import java.util.List;
import org.apache.camel.component.braintree.internal.AddressGatewayApiMethod;
import org.apache.camel.component.braintree.internal.BraintreeApiCollection;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AddressGatewayIntegrationTest extends AbstractBraintreeTestSupport {
    private static final Logger LOG = LoggerFactory.getLogger(AddressGatewayIntegrationTest.class);

    private static final String PATH_PREFIX = BraintreeApiCollection.getCollection().getApiName(AddressGatewayApiMethod.class).getName();

    private BraintreeGateway gateway;

    private Customer customer;

    private final List<String> addressIds;

    // *************************************************************************
    // 
    // *************************************************************************
    public AddressGatewayIntegrationTest() {
        this.customer = null;
        this.gateway = null;
        this.addressIds = new LinkedList<>();
    }

    // *************************************************************************
    // 
    // *************************************************************************
    @Test
    public void testCreate() throws Exception {
        assertNotNull("BraintreeGateway can't be null", this.gateway);
        assertNotNull("Customer can't be null", this.customer);
        final Result<Address> address = requestBodyAndHeaders("direct://CREATE", null, new AbstractBraintreeTestSupport.BraintreeHeaderBuilder().add("customerId", customer.getId()).add("request", new AddressRequest().company("Apache").streetAddress("1901 Munsey Drive").locality("Forest Hill")).build(), Result.class);
        assertNotNull("create", address);
        assertTrue(address.isSuccess());
        AddressGatewayIntegrationTest.LOG.info("Address created - customer={}, id={}", customer.getId(), address.getTarget().getId());
        this.addressIds.add(address.getTarget().getId());
    }

    @Test
    public void testDelete() throws Exception {
        assertNotNull("BraintreeGateway can't be null", this.gateway);
        assertNotNull("Customer can't be null", this.customer);
        final Address address = createAddress();
        final Result<Address> result = requestBodyAndHeaders("direct://DELETE", null, new AbstractBraintreeTestSupport.BraintreeHeaderBuilder().add("customerId", customer.getId()).add("id", address.getId()).build(), Result.class);
        assertNotNull("delete", address);
        assertTrue(result.isSuccess());
        AddressGatewayIntegrationTest.LOG.info("Address deleted - customer={}, id={}", customer.getId(), address.getId());
    }

    @Test
    public void testFind() throws Exception {
        assertNotNull("BraintreeGateway can't be null", this.gateway);
        assertNotNull("Customer can't be null", this.customer);
        final Address addressRef = createAddress();
        this.addressIds.add(addressRef.getId());
        final Address address = requestBodyAndHeaders("direct://FIND", null, new AbstractBraintreeTestSupport.BraintreeHeaderBuilder().add("customerId", customer.getId()).add("id", addressRef.getId()).build(), Address.class);
        assertNotNull("find", address);
        AddressGatewayIntegrationTest.LOG.info("Address found - customer={}, id={}", customer.getId(), address.getId());
    }

    @Test
    public void testUpdate() throws Exception {
        assertNotNull("BraintreeGateway can't be null", this.gateway);
        assertNotNull("Customer can't be null", this.customer);
        final Address addressRef = createAddress();
        this.addressIds.add(addressRef.getId());
        final Result<Address> result = requestBodyAndHeaders("direct://UPDATE", null, new AbstractBraintreeTestSupport.BraintreeHeaderBuilder().add("customerId", customer.getId()).add("id", addressRef.getId()).add("request", new AddressRequest().company("Apache").streetAddress(customer.getId()).locality(customer.getId())).build(), Result.class);
        assertNotNull("update", result);
        assertTrue(result.isSuccess());
        AddressGatewayIntegrationTest.LOG.info("Address updated - customer={}, id={}", customer.getId(), result.getTarget().getId());
    }
}

