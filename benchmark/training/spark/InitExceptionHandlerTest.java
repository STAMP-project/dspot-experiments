package spark;


import org.junit.Assert;
import org.junit.Test;


public class InitExceptionHandlerTest {
    private static int NON_VALID_PORT = Integer.MAX_VALUE;

    private static Service service;

    private static String errorMessage = "";

    @Test
    public void testInitExceptionHandler() throws Exception {
        Assert.assertEquals("Custom init error", InitExceptionHandlerTest.errorMessage);
    }
}

