package com.baeldung.logback;


import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;


public class JSONLayoutIntegrationTest {
    private static Logger logger;

    private ByteArrayOutputStream consoleOutput = new ByteArrayOutputStream();

    private PrintStream ps = new PrintStream(consoleOutput);

    @Test
    public void whenLogLayoutInJSON_thenOutputIsCorrectJSON() {
        JSONLayoutIntegrationTest.logger.debug("Debug message");
        String currentLog = consoleOutput.toString();
        Assert.assertTrue(((!(currentLog.isEmpty())) && (JSONLayoutIntegrationTest.isValidJSON(currentLog))));
    }
}

