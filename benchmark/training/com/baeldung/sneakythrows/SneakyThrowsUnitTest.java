package com.baeldung.sneakythrows;


import junit.framework.TestCase;
import org.junit.Test;


public class SneakyThrowsUnitTest {
    @Test
    public void whenCallSneakyMethod_thenThrowSneakyException() {
        try {
            SneakyThrows.throwsSneakyIOException();
        } catch (Exception ex) {
            TestCase.assertEquals("sneaky", ex.getMessage().toString());
        }
    }
}

