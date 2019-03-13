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
package com.iluwatar.dao;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Tests {@link Customer}.
 */
public class CustomerTest {
    private Customer customer;

    private static final int ID = 1;

    private static final String FIRSTNAME = "Winston";

    private static final String LASTNAME = "Churchill";

    @Test
    public void getAndSetId() {
        final int newId = 2;
        customer.setId(newId);
        Assertions.assertEquals(newId, customer.getId());
    }

    @Test
    public void getAndSetFirstname() {
        final String newFirstname = "Bill";
        customer.setFirstName(newFirstname);
        Assertions.assertEquals(newFirstname, customer.getFirstName());
    }

    @Test
    public void getAndSetLastname() {
        final String newLastname = "Clinton";
        customer.setLastName(newLastname);
        Assertions.assertEquals(newLastname, customer.getLastName());
    }

    @Test
    public void notEqualWithDifferentId() {
        final int newId = 2;
        final Customer otherCustomer = new Customer(newId, CustomerTest.FIRSTNAME, CustomerTest.LASTNAME);
        Assertions.assertNotEquals(customer, otherCustomer);
        Assertions.assertNotEquals(customer.hashCode(), otherCustomer.hashCode());
    }

    @Test
    public void equalsWithSameObjectValues() {
        final Customer otherCustomer = new Customer(CustomerTest.ID, CustomerTest.FIRSTNAME, CustomerTest.LASTNAME);
        Assertions.assertEquals(customer, otherCustomer);
        Assertions.assertEquals(customer.hashCode(), otherCustomer.hashCode());
    }

    @Test
    public void equalsWithSameObjects() {
        Assertions.assertEquals(customer, customer);
        Assertions.assertEquals(customer.hashCode(), customer.hashCode());
    }

    @Test
    public void testToString() {
        Assertions.assertEquals(String.format("Customer{id=%s, firstName='%s', lastName='%s'}", customer.getId(), customer.getFirstName(), customer.getLastName()), customer.toString());
    }
}

