package com.baeldung.findanelement;


import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class FindACustomerInGivenListUnitTest {
    private static List<Customer> customers = new ArrayList<>();

    static {
        FindACustomerInGivenListUnitTest.customers.add(new Customer(1, "Jack"));
        FindACustomerInGivenListUnitTest.customers.add(new Customer(2, "James"));
        FindACustomerInGivenListUnitTest.customers.add(new Customer(3, "Sam"));
    }

    private static FindACustomerInGivenList findACustomerInGivenList = new FindACustomerInGivenList();

    @Test
    public void givenAnIndex_whenFoundUsingGivenIndex_thenReturnCustomer() {
        Customer customer = FindACustomerInGivenListUnitTest.findACustomerInGivenList.findUsingGivenIndex(0, FindACustomerInGivenListUnitTest.customers);
        Assert.assertEquals(1, customer.getId());
    }

    @Test
    public void givenAnIndex_whenNotFoundUsingGivenIndex_thenReturnNull() {
        Customer customer = FindACustomerInGivenListUnitTest.findACustomerInGivenList.findUsingGivenIndex(5, FindACustomerInGivenListUnitTest.customers);
        Assert.assertNull(customer);
    }

    @Test
    public void givenACustomer_whenFoundUsingContains_thenReturnTrue() {
        Customer james = new Customer(2, "James");
        boolean isJamesPresent = FindACustomerInGivenListUnitTest.findACustomerInGivenList.findUsingContains(james, FindACustomerInGivenListUnitTest.customers);
        Assert.assertEquals(true, isJamesPresent);
    }

    @Test
    public void givenACustomer_whenNotFoundUsingContains_thenReturnFalse() {
        Customer john = new Customer(5, "John");
        boolean isJohnPresent = FindACustomerInGivenListUnitTest.findACustomerInGivenList.findUsingContains(john, FindACustomerInGivenListUnitTest.customers);
        Assert.assertEquals(false, isJohnPresent);
    }

    @Test
    public void givenACustomer_whenFoundUsingIndexOf_thenReturnItsIndex() {
        Customer james = new Customer(2, "James");
        int indexOfJames = FindACustomerInGivenListUnitTest.findACustomerInGivenList.findUsingIndexOf(james, FindACustomerInGivenListUnitTest.customers);
        Assert.assertEquals(1, indexOfJames);
    }

    @Test
    public void givenACustomer_whenNotFoundUsingIndexOf_thenReturnMinus1() {
        Customer john = new Customer(5, "John");
        int indexOfJohn = FindACustomerInGivenListUnitTest.findACustomerInGivenList.findUsingIndexOf(john, FindACustomerInGivenListUnitTest.customers);
        Assert.assertEquals((-1), indexOfJohn);
    }

    @Test
    public void givenName_whenCustomerWithNameFoundUsingIterator_thenReturnCustomer() {
        Customer james = FindACustomerInGivenListUnitTest.findACustomerInGivenList.findUsingIterator("James", FindACustomerInGivenListUnitTest.customers);
        Assert.assertEquals("James", james.getName());
        Assert.assertEquals(2, james.getId());
    }

    @Test
    public void givenName_whenCustomerWithNameNotFoundUsingIterator_thenReturnNull() {
        Customer john = FindACustomerInGivenListUnitTest.findACustomerInGivenList.findUsingIterator("John", FindACustomerInGivenListUnitTest.customers);
        Assert.assertNull(john);
    }

    @Test
    public void givenName_whenCustomerWithNameFoundUsingEnhancedFor_thenReturnCustomer() {
        Customer james = FindACustomerInGivenListUnitTest.findACustomerInGivenList.findUsingEnhancedForLoop("James", FindACustomerInGivenListUnitTest.customers);
        Assert.assertEquals("James", james.getName());
        Assert.assertEquals(2, james.getId());
    }

    @Test
    public void givenName_whenCustomerWithNameNotFoundUsingEnhancedFor_thenReturnNull() {
        Customer john = FindACustomerInGivenListUnitTest.findACustomerInGivenList.findUsingEnhancedForLoop("John", FindACustomerInGivenListUnitTest.customers);
        Assert.assertNull(john);
    }

    @Test
    public void givenName_whenCustomerWithNameFoundUsingStream_thenReturnCustomer() {
        Customer james = FindACustomerInGivenListUnitTest.findACustomerInGivenList.findUsingStream("James", FindACustomerInGivenListUnitTest.customers);
        Assert.assertEquals("James", james.getName());
        Assert.assertEquals(2, james.getId());
    }

    @Test
    public void givenName_whenCustomerWithNameNotFoundUsingStream_thenReturnNull() {
        Customer john = FindACustomerInGivenListUnitTest.findACustomerInGivenList.findUsingStream("John", FindACustomerInGivenListUnitTest.customers);
        Assert.assertNull(john);
    }

    @Test
    public void givenName_whenCustomerWithNameFoundUsingParallelStream_thenReturnCustomer() {
        Customer james = FindACustomerInGivenListUnitTest.findACustomerInGivenList.findUsingParallelStream("James", FindACustomerInGivenListUnitTest.customers);
        Assert.assertEquals("James", james.getName());
        Assert.assertEquals(2, james.getId());
    }

    @Test
    public void givenName_whenCustomerWithNameNotFoundUsingParallelStream_thenReturnNull() {
        Customer john = FindACustomerInGivenListUnitTest.findACustomerInGivenList.findUsingParallelStream("John", FindACustomerInGivenListUnitTest.customers);
        Assert.assertNull(john);
    }

    @Test
    public void givenName_whenCustomerWithNameFoundUsingApacheCommon_thenReturnCustomer() {
        Customer james = FindACustomerInGivenListUnitTest.findACustomerInGivenList.findUsingApacheCommon("James", FindACustomerInGivenListUnitTest.customers);
        Assert.assertEquals("James", james.getName());
        Assert.assertEquals(2, james.getId());
    }

    @Test
    public void givenName_whenCustomerWithNameNotFoundUsingApacheCommon_thenReturnNull() {
        Customer john = FindACustomerInGivenListUnitTest.findACustomerInGivenList.findUsingApacheCommon("John", FindACustomerInGivenListUnitTest.customers);
        Assert.assertNull(john);
    }

    @Test
    public void givenName_whenCustomerWithNameFoundUsingGuava_thenReturnCustomer() {
        Customer james = FindACustomerInGivenListUnitTest.findACustomerInGivenList.findUsingGuava("James", FindACustomerInGivenListUnitTest.customers);
        Assert.assertEquals("James", james.getName());
        Assert.assertEquals(2, james.getId());
    }

    @Test
    public void givenName_whenCustomerWithNameNotFoundUsingGuava_thenReturnNull() {
        Customer john = FindACustomerInGivenListUnitTest.findACustomerInGivenList.findUsingGuava("John", FindACustomerInGivenListUnitTest.customers);
        Assert.assertNull(john);
    }
}

