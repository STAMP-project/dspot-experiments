package com.baeldung.methodorders;


import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;


@FixMethodOrder(MethodSorters.DEFAULT)
public class DefaultOrderOfExecutionUnitTest {
    private static StringBuilder output = new StringBuilder("");

    @Test
    public void secondTest() {
        DefaultOrderOfExecutionUnitTest.output.append("b");
    }

    @Test
    public void thirdTest() {
        DefaultOrderOfExecutionUnitTest.output.append("c");
    }

    @Test
    public void firstTest() {
        DefaultOrderOfExecutionUnitTest.output.append("a");
    }
}

