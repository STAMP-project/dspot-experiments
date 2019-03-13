package com.querydsl.apt;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;


public class NoteTest extends AbstractProcessorTest {
    private Collection<String> aptOptions;

    private ByteArrayOutputStream err = new ByteArrayOutputStream();

    private static final String packagePath = "src/test/java/com/querydsl/apt/";

    @Test
    public void processDefault() throws IOException {
        aptOptions = Collections.emptyList();
        process();
        Assert.assertTrue(isStdErrEmpty());
    }

    @Test
    public void processEnabled() throws IOException {
        aptOptions = Arrays.asList("-Aquerydsl.logInfo=true");
        process();
        Assert.assertFalse(isStdErrEmpty());
    }

    @Test
    public void processDisabled() throws IOException {
        aptOptions = Arrays.asList("-Aquerydsl.logInfo=false");
        process();
        Assert.assertTrue(isStdErrEmpty());
    }
}

