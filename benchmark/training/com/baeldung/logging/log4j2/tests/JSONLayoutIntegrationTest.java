package com.baeldung.logging.log4j2.tests;


import com.baeldung.logging.log4j2.Log4j2BaseIntegrationTest;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;


public class JSONLayoutIntegrationTest extends Log4j2BaseIntegrationTest {
    private static Logger logger;

    private ByteArrayOutputStream consoleOutput = new ByteArrayOutputStream();

    private PrintStream ps = new PrintStream(consoleOutput);

    @Test
    public void whenLogLayoutInJSON_thenOutputIsCorrectJSON() {
        JSONLayoutIntegrationTest.logger.debug("Debug message");
        String currentLog = consoleOutput.toString();
        Assert.assertTrue(currentLog.isEmpty());
        Assert.assertTrue(JSONLayoutIntegrationTest.isValidJSON(currentLog));
    }
}

