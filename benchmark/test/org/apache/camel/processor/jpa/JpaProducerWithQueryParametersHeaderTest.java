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


import JpaConstants.JPA_PARAMETERS_HEADER;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.examples.Customer;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JpaProducerWithQueryParametersHeaderTest extends Assert {
    protected static final Logger LOG = LoggerFactory.getLogger(JpaProducerWithQueryParametersHeaderTest.class);

    protected DefaultCamelContext camelContext;

    protected ProducerTemplate template;

    @Test
    @SuppressWarnings("rawtypes")
    public void testProducerWithNamedQuery() throws Exception {
        template.sendBody("direct:deleteCustomers", "");
        Customer c1 = new Customer();
        c1.setName("Willem");
        template.sendBody("direct:addCustomer", c1);
        Customer c2 = new Customer();
        c2.setName("Dummy");
        template.sendBody("direct:addCustomer", c2);
        Map<String, Object> params = new HashMap<>();
        params.put("custName", "${body}");
        List list = template.requestBodyAndHeader("direct:namedQuery", "Willem", JPA_PARAMETERS_HEADER, params, List.class);
        Assert.assertEquals(1, list.size());
        Assert.assertEquals("Willem", ((Customer) (list.get(0))).getName());
        int integer = template.requestBody("direct:deleteCustomers", null, int.class);
        Assert.assertEquals(2, integer);
    }
}

