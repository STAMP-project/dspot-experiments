package com.baeldung.test.dependencyinjection;


import com.baeldung.dependencyinjection.factories.TimeLoggerFactory;
import com.baeldung.dependencyinjection.loggers.TimeLogger;
import org.junit.Test;


public class TimeLoggerFactoryUnitTest {
    @Test
    public void givenTimeLoggerFactory_whenCalledgetTimeLogger_thenOneAssertion() {
        TimeLoggerFactory timeLoggerFactory = new TimeLoggerFactory();
        assertThat(timeLoggerFactory.getTimeLogger()).isInstanceOf(TimeLogger.class);
    }
}

