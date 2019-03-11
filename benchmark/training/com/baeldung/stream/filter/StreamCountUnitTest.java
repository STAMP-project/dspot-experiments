package com.baeldung.stream.filter;


import java.util.List;
import org.junit.Test;


public class StreamCountUnitTest {
    private List<Customer> customers;

    @Test
    public void givenListOfCustomers_whenCount_thenGetListSize() {
        long count = customers.stream().count();
        assertThat(count).isEqualTo(4L);
    }

    @Test
    public void givenListOfCustomers_whenFilterByPointsOver100AndCount_thenGetTwo() {
        long countBigCustomers = customers.stream().filter(( c) -> (c.getPoints()) > 100).count();
        assertThat(countBigCustomers).isEqualTo(2L);
    }

    @Test
    public void givenListOfCustomers_whenFilterByPointsAndNameAndCount_thenGetOne() {
        long count = customers.stream().filter(( c) -> ((c.getPoints()) > 10) && (c.getName().startsWith("Charles"))).count();
        assertThat(count).isEqualTo(1L);
    }

    @Test
    public void givenListOfCustomers_whenNoneMatchesFilterAndCount_thenGetZero() {
        long count = customers.stream().filter(( c) -> (c.getPoints()) > 500).count();
        assertThat(count).isEqualTo(0L);
    }

    @Test
    public void givenListOfCustomers_whenUsingMethodOverHundredPointsAndCount_thenGetTwo() {
        long count = customers.stream().filter(Customer::hasOverHundredPoints).count();
        assertThat(count).isEqualTo(2L);
    }
}

