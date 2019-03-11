package com.baeldung.junit5vstestng;


import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.function.Executable;


public class CalculatorUnitTest {
    @Test
    public void whenDividerIsZero_thenDivideByZeroExceptionIsThrown() {
        Calculator calculator = new Calculator();
        Assertions.assertThrows(DivideByZeroException.class, () -> calculator.divide(10, 0));
    }
}

