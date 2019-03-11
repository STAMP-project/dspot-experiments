package com.baeldung.methodorders;


import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;


@FixMethodOrder(MethodSorters.JVM)
public class JVMOrderOfExecutionUnitTest {
    private static StringBuilder output = new StringBuilder("");

    @Test
    public void secondTest() {
        JVMOrderOfExecutionUnitTest.output.append("b");
    }

    @Test
    public void thirdTest() {
        JVMOrderOfExecutionUnitTest.output.append("c");
    }

    @Test
    public void firstTest() {
        JVMOrderOfExecutionUnitTest.output.append("a");
    }
}

