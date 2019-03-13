package com.baeldung.exception;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


public class ExceptionAssertionUnitTest {
    @Test
    public void whenExceptionThrown_thenAssertionSucceeds() {
        String test = null;
        Assertions.assertThrows(NullPointerException.class, () -> {
            test.length();
        });
    }

    @Test
    public void whenDerivedExceptionThrown_thenAssertionSucceds() {
        String test = null;
        Assertions.assertThrows(RuntimeException.class, () -> {
            test.length();
        });
    }
}

