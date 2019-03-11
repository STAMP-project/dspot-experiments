/**
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.batch.sample.domain.trade;


import java.math.BigDecimal;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 *
 *
 * @author Lucas Ward
 */
public class CustomerUpdateProcessorTests {
    private CustomerDao customerDao;

    private InvalidCustomerLogger logger;

    private CustomerUpdateProcessor processor;

    @Test
    public void testSuccessfulAdd() throws Exception {
        CustomerUpdate customerUpdate = new CustomerUpdate(CustomerOperation.ADD, "test customer", new BigDecimal("232.2"));
        Mockito.when(customerDao.getCustomerByName("test customer")).thenReturn(null);
        Assert.assertEquals(customerUpdate, processor.process(customerUpdate));
    }

    @Test
    public void testInvalidAdd() throws Exception {
        CustomerUpdate customerUpdate = new CustomerUpdate(CustomerOperation.ADD, "test customer", new BigDecimal("232.2"));
        Mockito.when(customerDao.getCustomerByName("test customer")).thenReturn(new CustomerCredit());
        logger.log(customerUpdate);
        Assert.assertNull("Processor should return null", processor.process(customerUpdate));
    }

    @Test
    public void testDelete() throws Exception {
        CustomerUpdate customerUpdate = new CustomerUpdate(CustomerOperation.DELETE, "test customer", new BigDecimal("232.2"));
        logger.log(customerUpdate);
        Assert.assertNull("Processor should return null", processor.process(customerUpdate));
    }

    @Test
    public void testSuccessfulUpdate() throws Exception {
        CustomerUpdate customerUpdate = new CustomerUpdate(CustomerOperation.UPDATE, "test customer", new BigDecimal("232.2"));
        Mockito.when(customerDao.getCustomerByName("test customer")).thenReturn(new CustomerCredit());
        Assert.assertEquals(customerUpdate, processor.process(customerUpdate));
    }

    @Test
    public void testInvalidUpdate() throws Exception {
        CustomerUpdate customerUpdate = new CustomerUpdate(CustomerOperation.UPDATE, "test customer", new BigDecimal("232.2"));
        Mockito.when(customerDao.getCustomerByName("test customer")).thenReturn(null);
        logger.log(customerUpdate);
        Assert.assertNull("Processor should return null", processor.process(customerUpdate));
    }
}

