package com.baeldung.reladomo;


import com.gs.fw.common.mithra.test.MithraTestResource;
import org.junit.Assert;
import org.junit.Test;


public class ReladomoIntegrationTest {
    private MithraTestResource mithraTestResource;

    @Test
    public void whenGetTestData_thenOk() {
        Employee employee = EmployeeFinder.findByPrimaryKey(1);
        Assert.assertEquals(employee.getName(), "Paul");
    }
}

