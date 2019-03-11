package com.baeldung.logging.log4j2.tests;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;


public class LambdaExpressionsIntegrationTest {
    private static final Logger logger = LogManager.getRootLogger();

    @Test
    public void whenCheckLogMessage_thenOk() {
        if (LambdaExpressionsIntegrationTest.logger.isTraceEnabled()) {
            LambdaExpressionsIntegrationTest.logger.trace("Numer is {}", getRandomNumber());
        }
    }

    @Test
    public void whenUseLambdaExpression_thenOk() {
        LambdaExpressionsIntegrationTest.logger.trace("Number is {}", () -> getRandomNumber());
        LambdaExpressionsIntegrationTest.logger.trace("Name is {} and age is {}", () -> getName(), () -> getRandomNumber());
    }
}

