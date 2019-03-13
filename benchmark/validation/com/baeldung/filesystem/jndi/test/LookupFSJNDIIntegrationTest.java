package com.baeldung.filesystem.jndi.test;


import com.baeldung.filesystem.jndi.LookupFSJNDI;
import java.io.File;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.junit.Assert;
import org.junit.Test;


public class LookupFSJNDIIntegrationTest {
    LookupFSJNDI fsjndi;

    InitialContext ctx = null;

    final String FILENAME = "test.find";

    public LookupFSJNDIIntegrationTest() {
        try {
            fsjndi = new LookupFSJNDI();
        } catch (NamingException e) {
            fsjndi = null;
        }
    }

    @Test
    public void whenInitializationLookupFSJNDIIsNotNull_thenSuccess() {
        Assert.assertNotNull("Class LookupFSJNDI has instance", fsjndi);
    }

    @Test
    public void givenLookupFSJNDI_whengetInitialContextIsNotNull_thenSuccess() {
        ctx = fsjndi.getCtx();
        Assert.assertNotNull("Context exists", ctx);
    }

    @Test
    public void givenInitialContext_whenLokupFileExists_thenSuccess() {
        File file = fsjndi.getFile(FILENAME);
        Assert.assertNotNull("File exists", file);
    }
}

