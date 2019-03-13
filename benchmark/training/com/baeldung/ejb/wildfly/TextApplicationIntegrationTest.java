package com.baeldung.ejb.wildfly;


import java.io.ByteArrayOutputStream;
import javax.naming.NamingException;
import org.junit.Assert;
import org.junit.Test;


public class TextApplicationIntegrationTest {
    private static ByteArrayOutputStream outContent;

    @Test
    public void givenInputString_whenCompareTtoStringPrintedToConsole_thenSuccessful() throws NamingException {
        TextApplication.main(new String[]{  });
        Assert.assertEquals("SAMPLE TEXT", TextApplicationIntegrationTest.outContent.toString());
    }
}

