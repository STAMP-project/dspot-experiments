/**
 * Copyright 2014-2018 the original author or authors.
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
package example.springdata.mongodb.aggregation;


import java.util.Date;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


/**
 * Integration tests for {@link OrderRepository}.
 *
 * @author Thomas Darimont
 * @author Oliver Gierke
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderRepositoryIntegrationTests {
    @Autowired
    OrderRepository repository;

    private static final LineItem product1 = new LineItem("p1", 1.23);

    private static final LineItem product2 = new LineItem("p2", 0.87, 2);

    private static final LineItem product3 = new LineItem("p3", 5.33);

    @Test
    public void createsInvoiceViaAggregation() {
        Order order = // 
        new Order("c42", new Date()).addItem(OrderRepositoryIntegrationTests.product1).addItem(OrderRepositoryIntegrationTests.product2).addItem(OrderRepositoryIntegrationTests.product3);
        order = repository.save(order);
        Invoice invoice = repository.getInvoiceFor(order);
        Assert.assertThat(invoice, CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(invoice.getOrderId(), CoreMatchers.is(order.getId()));
        Assert.assertThat(invoice.getNetAmount(), CoreMatchers.is(closeTo(8.3, 1)));
        Assert.assertThat(invoice.getTaxAmount(), CoreMatchers.is(closeTo(1.577, 1)));
        Assert.assertThat(invoice.getTotalAmount(), CoreMatchers.is(closeTo(9.877, 1)));
    }
}

