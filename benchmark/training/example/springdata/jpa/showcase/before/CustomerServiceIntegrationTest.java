/**
 * Copyright 2011-2018 the original author or authors.
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
package example.springdata.jpa.showcase.before;


import example.springdata.jpa.showcase.AbstractShowcaseTest;
import example.springdata.jpa.showcase.core.Customer;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * Integration test for {@link CustomerService}.
 *
 * @author Oliver Gierke
 */
public class CustomerServiceIntegrationTest extends AbstractShowcaseTest {
    @Autowired
    CustomerService repository;

    @Test
    public void findsAllCustomers() throws Exception {
        List<Customer> result = repository.findAll();
        Assert.assertThat(result, CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertFalse(result.isEmpty());
    }

    @Test
    public void findsPageOfMatthews() throws Exception {
        List<Customer> customers = repository.findByLastname("Matthews", 0, 2);
        Assert.assertThat(customers.size(), CoreMatchers.is(2));
    }

    @Test
    public void findsCustomerById() throws Exception {
        Customer customer = repository.findById(2L);
        Assert.assertThat(customer.getFirstname(), CoreMatchers.is("Carter"));
        Assert.assertThat(customer.getLastname(), CoreMatchers.is("Beauford"));
    }
}

