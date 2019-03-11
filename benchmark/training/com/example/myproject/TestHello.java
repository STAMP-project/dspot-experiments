package com.example.myproject;


import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import org.junit.Assert;
import org.junit.Test;

import static Greeter.out;


/**
 * Tests different numbers of arguments to main().
 *
 * <p>With an empty args array, {@link Greeter} should print "Hello world". If there are one or more
 * args, {@link Greeter} should print "Hello &lt;arg[0]&gt;".</p>
 */
public class TestHello {
    @Test
    public void testNoArgument() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out = new PrintStream(out);
        Greeter.main();
        Assert.assertEquals("Hello world\n", new String(out.toByteArray(), StandardCharsets.UTF_8));
    }

    @Test
    public void testWithArgument() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out = new PrintStream(out);
        Greeter.main("toto");
        Assert.assertEquals("Hello toto\n", new String(out.toByteArray(), StandardCharsets.UTF_8));
    }
}

