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
package org.apache.camel.dataformat.soap;


import SoapJaxbDataFormat.SOAP_UNMARSHALLED_HEADER_LIST;
import com.example.customerservice.multipart.Company;
import com.example.customerservice.multipart.Customer;
import com.example.customerservice.multipart.GetCustomersByName;
import com.example.customerservice.multipart.GetCustomersByNameResponse;
import com.example.customerservice.multipart.MultiPartCustomerService;
import com.example.customerservice.multipart.Product;
import com.example.customerservice.multipart.SaveCustomer;
import java.util.List;
import javax.xml.ws.Endpoint;
import javax.xml.ws.Holder;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.bean.BeanInvocation;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class MultiPartCxfServerTest extends RouteBuilder {
    protected static Endpoint endpoint;

    @Produce(uri = "direct:start")
    ProducerTemplate producerTemplate;

    @Test
    public void testSendRequestWithInPart() throws Exception {
        Exchange exchange = producerTemplate.send("direct:start", new Processor() {
            public void process(Exchange exchange) throws Exception {
                BeanInvocation beanInvocation = new BeanInvocation();
                GetCustomersByName getCustomersByName = new GetCustomersByName();
                getCustomersByName.setName("Dr. Multipart");
                beanInvocation.setMethod(MultiPartCustomerService.class.getMethod("getCustomersByName", GetCustomersByName.class, Product.class));
                Product product = new Product();
                product.setName("Multipart Product");
                product.setDescription("Useful for lots of things.");
                Object[] args = new Object[]{ getCustomersByName, product };
                beanInvocation.setArgs(args);
                exchange.getIn().setBody(beanInvocation);
            }
        });
        if ((exchange.getException()) != null) {
            throw exchange.getException();
        }
        Object responseObj = exchange.getOut().getBody();
        Assert.assertTrue((responseObj instanceof GetCustomersByNameResponse));
        GetCustomersByNameResponse response = ((GetCustomersByNameResponse) (responseObj));
        Assert.assertTrue(getName().equals("Multipart Product"));
    }

    @Test
    public void testSendRequestWithInAndInOutParts() throws Exception {
        Exchange exchange = producerTemplate.send("direct:start", new Processor() {
            public void process(Exchange exchange) throws Exception {
                BeanInvocation beanInvocation = new BeanInvocation();
                beanInvocation.setMethod(MultiPartCustomerService.class.getMethod("saveCustomer", SaveCustomer.class, Product.class, Holder.class));
                Customer customer = new Customer();
                customer.setName("TestCustomer");
                customer.setRevenue(50000);
                SaveCustomer saveCustomer = new SaveCustomer();
                saveCustomer.setCustomer(customer);
                Product product = new Product();
                product.setName("Multiuse Product");
                product.setDescription("Useful for lots of things.");
                Holder<Company> holder = new Holder<>();
                Object[] args = new Object[]{ saveCustomer, product, holder };
                beanInvocation.setArgs(args);
                exchange.getIn().setBody(beanInvocation);
            }
        });
        if ((exchange.getException()) != null) {
            throw exchange.getException();
        }
        @SuppressWarnings("unchecked")
        List<Object> headers = ((List<Object>) (exchange.getOut().getHeader(SOAP_UNMARSHALLED_HEADER_LIST)));
        Assert.assertTrue(((headers.size()) == 1));
        Object companyHeaderObj = headers.get(0);
        Assert.assertTrue((companyHeaderObj instanceof Company));
        Assert.assertTrue(getName().equals("MultipartSoft"));
    }

    /**
     * This test validates the end-to-end behavior of the service interface mapping when a parameter type
     * is defined with a different QName in two different Web method. It also tests the case where a
     * QName and type are directly reused across methods.
     */
    @Test
    public void testSendRequestWithReusedInAndInOutParts() throws Exception {
        Exchange exchange = producerTemplate.send("direct:start", new Processor() {
            public void process(Exchange exchange) throws Exception {
                BeanInvocation beanInvocation = new BeanInvocation();
                beanInvocation.setMethod(MultiPartCustomerService.class.getMethod("saveCustomerToo", SaveCustomer.class, Product.class, Holder.class));
                Customer customer = new Customer();
                customer.setName("TestCustomerToo");
                customer.setRevenue(50000);
                SaveCustomer saveCustomer = new SaveCustomer();
                saveCustomer.setCustomer(customer);
                Product product = new Product();
                product.setName("Multiuse Product");
                product.setDescription("Useful for lots of things.");
                Holder<Company> holder = new Holder<>();
                Object[] args = new Object[]{ saveCustomer, product, holder };
                beanInvocation.setArgs(args);
                exchange.getIn().setBody(beanInvocation);
            }
        });
        if ((exchange.getException()) != null) {
            throw exchange.getException();
        }
        @SuppressWarnings("unchecked")
        List<Object> headers = ((List<Object>) (exchange.getOut().getHeader(SOAP_UNMARSHALLED_HEADER_LIST)));
        Assert.assertTrue(((headers.size()) == 1));
        Object companyHeaderObj = headers.get(0);
        Assert.assertTrue((companyHeaderObj instanceof Company));
        Assert.assertTrue(getName().equals("MultipartSoft"));
    }
}

