package com.example;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


public class CalculatorJUnit5Test {
    @Tag("fast")
    @Test
    public void testAdd() {
        Assertions.assertEquals(42, Integer.sum(19, 23));
    }

    @Tag("slow")
    @Test
    public void testAddMaxInteger() {
        Assertions.assertEquals(2147483646, Integer.sum(2147183646, 300000));
    }

    @Tag("fast")
    @Test
    public void testAddZero() {
        Assertions.assertEquals(21, Integer.sum(21, 0));
    }

    @Tag("fast")
    @Test
    public void testDivide() {
        Assertions.assertThrows(ArithmeticException.class, () -> {
            Integer.divideUnsigned(42, 0);
        });
    }
}

