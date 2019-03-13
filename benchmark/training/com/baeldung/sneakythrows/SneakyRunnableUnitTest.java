package com.baeldung.sneakythrows;


import junit.framework.TestCase;
import org.junit.Test;


public class SneakyRunnableUnitTest {
    @Test
    public void whenCallSneakyRunnableMethod_thenThrowException() {
        try {
            new SneakyRunnable().run();
        } catch (Exception e) {
            TestCase.assertEquals(InterruptedException.class, e.getStackTrace());
        }
    }
}

