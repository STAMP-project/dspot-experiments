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


import java.util.List;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.examples.Customer;
import org.apache.camel.examples.MultiSteps;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JpaProducerWithQueryTest extends Assert {
    protected static final Logger LOG = LoggerFactory.getLogger(JpaProducerWithQueryTest.class);

    protected DefaultCamelContext camelContext;

    protected ProducerTemplate template;

    @Test
    public void testProducerWithNamedQuery() throws Exception {
        template.sendBody("direct:deleteCustomers", "");
        Customer c1 = new Customer();
        c1.setName("Willem");
        template.sendBody("direct:addCustomer", c1);
        Customer c2 = new Customer();
        c2.setName("Dummy");
        template.sendBody("direct:addCustomer", c2);
        Object answer = template.requestBody("direct:namedQuery", "Willem");
        List list = ((List) (answer));
        Assert.assertEquals(1, list.size());
        Assert.assertEquals("Willem", ((Customer) (list.get(0))).getName());
        answer = template.requestBody("direct:deleteCustomers", "");
        Assert.assertEquals(2, ((Integer) (answer)).intValue());
    }

    @Test
    public void testProducerWithQuery() throws Exception {
        template.sendBody("direct:deleteMultiSteps", "");
        MultiSteps m1 = new MultiSteps();
        m1.setStep(1);
        template.sendBody("direct:addMultiSteps", m1);
        MultiSteps m2 = new MultiSteps();
        m2.setStep(2);
        template.sendBody("direct:addMultiSteps", m2);
        Object answer = template.requestBody("direct:query", "");
        List list = ((List) (answer));
        Assert.assertEquals(1, list.size());
        Assert.assertEquals(1, ((MultiSteps) (list.get(0))).getStep());
        answer = template.requestBody("direct:deleteMultiSteps", "");
        Assert.assertEquals(2, ((Integer) (answer)).intValue());
    }

    @Test
    public void testProducerWithNativeQuery() throws Exception {
        template.sendBody("direct:deleteMultiSteps", "");
        MultiSteps m1 = new MultiSteps();
        m1.setStep(1);
        template.sendBody("direct:addMultiSteps", m1);
        MultiSteps m2 = new MultiSteps();
        m2.setStep(2);
        template.sendBody("direct:addMultiSteps", m2);
        Object answer = template.requestBody("direct:nativeQuery", "");
        List list = ((List) (answer));
        Assert.assertEquals(1, list.size());
        Assert.assertEquals(1, ((Object[]) (list.get(0)))[2]);
        answer = template.requestBody("direct:deleteMultiSteps", "");
        Assert.assertEquals(2, ((Integer) (answer)).intValue());
    }

    @Test
    public void testProducerWithNativeQueryAndResultClass() throws Exception {
        template.sendBody("direct:deleteMultiSteps", "");
        MultiSteps m1 = new MultiSteps();
        m1.setStep(1);
        template.sendBody("direct:addMultiSteps", m1);
        MultiSteps m2 = new MultiSteps();
        m2.setStep(2);
        template.sendBody("direct:addMultiSteps", m2);
        Object answer = template.requestBody("direct:nativeQueryWithResultClass", "");
        List list = ((List) (answer));
        Assert.assertEquals(1, list.size());
        Assert.assertEquals(1, ((MultiSteps) (list.get(0))).getStep());
        answer = template.requestBody("direct:deleteMultiSteps", "");
        Assert.assertEquals(2, ((Integer) (answer)).intValue());
    }
}

