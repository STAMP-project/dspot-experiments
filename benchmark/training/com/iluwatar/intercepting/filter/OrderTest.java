/**
 * The MIT License
 * Copyright (c) 2014-2016 Ilkka Sepp?l?
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.iluwatar.intercepting.filter;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Date: 12/13/15 - 2:57 PM
 *
 * @author Jeroen Meulemeester
 */
public class OrderTest {
    private static final String EXPECTED_VALUE = "test";

    @Test
    public void testSetName() {
        final Order order = new Order();
        order.setName(OrderTest.EXPECTED_VALUE);
        Assertions.assertEquals(OrderTest.EXPECTED_VALUE, order.getName());
    }

    @Test
    public void testSetContactNumber() {
        final Order order = new Order();
        order.setContactNumber(OrderTest.EXPECTED_VALUE);
        Assertions.assertEquals(OrderTest.EXPECTED_VALUE, order.getContactNumber());
    }

    @Test
    public void testSetAddress() {
        final Order order = new Order();
        order.setAddress(OrderTest.EXPECTED_VALUE);
        Assertions.assertEquals(OrderTest.EXPECTED_VALUE, order.getAddress());
    }

    @Test
    public void testSetDepositNumber() {
        final Order order = new Order();
        order.setDepositNumber(OrderTest.EXPECTED_VALUE);
        Assertions.assertEquals(OrderTest.EXPECTED_VALUE, order.getDepositNumber());
    }

    @Test
    public void testSetOrder() {
        final Order order = new Order();
        order.setOrderItem(OrderTest.EXPECTED_VALUE);
        Assertions.assertEquals(OrderTest.EXPECTED_VALUE, order.getOrderItem());
    }
}

