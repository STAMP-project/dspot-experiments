package com.baeldung.methodorders;


import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class NameAscendingOrderOfExecutionUnitTest {
    private static StringBuilder output = new StringBuilder("");

    @Test
    public void secondTest() {
        NameAscendingOrderOfExecutionUnitTest.output.append("b");
    }

    @Test
    public void thirdTest() {
        NameAscendingOrderOfExecutionUnitTest.output.append("c");
    }

    @Test
    public void firstTest() {
        NameAscendingOrderOfExecutionUnitTest.output.append("a");
    }
}

