package com.example.myproject;


import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import org.junit.Assert;
import org.junit.Test;

import static Greeter.out;


/**
 * Tests using a resource file to replace "Hello" in the output.
 */
public class TestCustomGreeting {
    @Test
    public void testNoArgument() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out = new PrintStream(out);
        Greeter.main();
        Assert.assertEquals("Bye world", new String(out.toByteArray(), StandardCharsets.UTF_8).trim());
    }

    @Test
    public void testWithArgument() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out = new PrintStream(out);
        Greeter.main("toto");
        Assert.assertEquals("Bye toto", new String(out.toByteArray(), StandardCharsets.UTF_8).trim());
    }
}

