package com.example.badlog;


import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class BadLogTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(BadLogTest.class);

    private static ByteArrayOutputStream out;

    private static ByteArrayOutputStream err;

    private static PrintStream oldOut = System.out;

    private static PrintStream oldErr = System.err;

    @Test
    public void thatLoggingIsntBrokenOnCleanup() throws Exception {
        BadLogApp.runMe(new String[]{ "server" });
        BadLogTest.LOGGER.info("I'm after the test");
        Thread.sleep(100);
        assertThat(new String(BadLogTest.out.toByteArray(), StandardCharsets.UTF_8)).contains("Mayday we're going down").contains("I'm after the test");
        assertThat(new String(BadLogTest.err.toByteArray(), StandardCharsets.UTF_8)).contains("I'm a bad app");
    }
}

