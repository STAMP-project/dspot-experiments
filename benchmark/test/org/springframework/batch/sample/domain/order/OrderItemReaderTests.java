/**
 * Copyright 2008-2014 the original author or authors.
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
package org.springframework.batch.sample.domain.order;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DefaultFieldSet;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.sample.domain.order.internal.OrderItemReader;

import static Address.LINE_ID_BILLING_ADDR;
import static Address.LINE_ID_SHIPPING_ADDR;
import static BillingInfo.LINE_ID_BILLING_INFO;
import static Customer.LINE_ID_NON_BUSINESS_CUST;
import static LineItem.LINE_ID_ITEM;
import static Order.LINE_ID_FOOTER;
import static Order.LINE_ID_HEADER;
import static ShippingInfo.LINE_ID_SHIPPING_INFO;


public class OrderItemReaderTests {
    private OrderItemReader provider;

    private ItemReader<FieldSet> input;

    /* OrderItemProvider is responsible for retrieving validated value object
    from input source. OrderItemProvider.next(): - reads lines from the input
    source - returned as fieldsets - pass fieldsets to the mapper - mapper
    will create value object - pass value object to validator - returns
    validated object

    In testNext method we are going to test these responsibilities. So we
    need create mock objects for input source, mapper and validator.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testNext() throws Exception {
        FieldSet headerFS = new DefaultFieldSet(new String[]{ LINE_ID_HEADER });
        FieldSet customerFS = new DefaultFieldSet(new String[]{ LINE_ID_NON_BUSINESS_CUST });
        FieldSet billingFS = new DefaultFieldSet(new String[]{ LINE_ID_BILLING_ADDR });
        FieldSet shippingFS = new DefaultFieldSet(new String[]{ LINE_ID_SHIPPING_ADDR });
        FieldSet billingInfoFS = new DefaultFieldSet(new String[]{ LINE_ID_BILLING_INFO });
        FieldSet shippingInfoFS = new DefaultFieldSet(new String[]{ LINE_ID_SHIPPING_INFO });
        FieldSet itemFS = new DefaultFieldSet(new String[]{ LINE_ID_ITEM });
        FieldSet footerFS = new DefaultFieldSet(new String[]{ LINE_ID_FOOTER, "100", "3", "3" }, new String[]{ "ID", "TOTAL_PRICE", "TOTAL_LINE_ITEMS", "TOTAL_ITEMS" });
        Mockito.when(input.read()).thenReturn(headerFS, customerFS, billingFS, shippingFS, billingInfoFS, shippingInfoFS, itemFS, itemFS, itemFS, footerFS, null);
        Order order = new Order();
        Customer customer = new Customer();
        Address billing = new Address();
        Address shipping = new Address();
        BillingInfo billingInfo = new BillingInfo();
        ShippingInfo shippingInfo = new ShippingInfo();
        LineItem item = new LineItem();
        @SuppressWarnings("rawtypes")
        FieldSetMapper mapper = Mockito.mock(FieldSetMapper.class);
        Mockito.when(mapper.mapFieldSet(headerFS)).thenReturn(order);
        Mockito.when(mapper.mapFieldSet(customerFS)).thenReturn(customer);
        Mockito.when(mapper.mapFieldSet(billingFS)).thenReturn(billing);
        Mockito.when(mapper.mapFieldSet(shippingFS)).thenReturn(shipping);
        Mockito.when(mapper.mapFieldSet(billingInfoFS)).thenReturn(billingInfo);
        Mockito.when(mapper.mapFieldSet(shippingInfoFS)).thenReturn(shippingInfo);
        Mockito.when(mapper.mapFieldSet(itemFS)).thenReturn(item);
        provider.setAddressMapper(mapper);
        provider.setBillingMapper(mapper);
        provider.setCustomerMapper(mapper);
        provider.setHeaderMapper(mapper);
        provider.setItemMapper(mapper);
        provider.setShippingMapper(mapper);
        Object result = provider.read();
        Assert.assertNotNull(result);
        Order o = ((Order) (result));
        Assert.assertEquals(o, order);
        Assert.assertEquals(o.getCustomer(), customer);
        Assert.assertFalse(o.getCustomer().isBusinessCustomer());
        Assert.assertEquals(o.getBillingAddress(), billing);
        Assert.assertEquals(o.getShippingAddress(), shipping);
        Assert.assertEquals(o.getBilling(), billingInfo);
        Assert.assertEquals(o.getShipping(), shippingInfo);
        Assert.assertEquals(3, o.getLineItems().size());
        for (LineItem lineItem : o.getLineItems()) {
            Assert.assertEquals(lineItem, item);
        }
        Assert.assertNull(provider.read());
    }
}

