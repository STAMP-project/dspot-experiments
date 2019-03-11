package com.baeldung.hikaricp;


import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class HikariCPIntegrationTest {
    @Test
    public void givenConnection_thenFetchDbData() {
        List<Employee> employees = HikariCPDemo.fetchData();
        Assert.assertEquals(4, employees.size());
    }
}

