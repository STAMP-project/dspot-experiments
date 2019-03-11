package com.baeldung.facade;


import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class CarEngineFacadeIntegrationTest {
    private CarEngineFacadeIntegrationTest.InMemoryCustomTestAppender appender;

    @Test
    public void whenStartEngine_thenAllNecessaryActionsPerformed() {
        CarEngineFacade carEngineFacade = new CarEngineFacade();
        carEngineFacade.startEngine();
        Assert.assertTrue(appender.logContains("Fuel injector ready to inject fuel."));
        Assert.assertTrue(appender.logContains("Getting air measurements.."));
        Assert.assertTrue(appender.logContains("Air provided!"));
        Assert.assertTrue(appender.logContains("Fuel injector ready to inject fuel."));
        Assert.assertTrue(appender.logContains("Fuel Pump is pumping fuel.."));
        Assert.assertTrue(appender.logContains("Fuel injected."));
        Assert.assertTrue(appender.logContains("Starting.."));
        Assert.assertTrue(appender.logContains("Setting temperature upper limit to 90"));
        Assert.assertTrue(appender.logContains("Cooling Controller ready!"));
        Assert.assertTrue(appender.logContains("Setting radiator speed to 10"));
        Assert.assertTrue(appender.logContains("Catalytic Converter switched on!"));
    }

    @Test
    public void whenStopEngine_thenAllNecessaryActionsPerformed() {
        CarEngineFacade carEngineFacade = new CarEngineFacade();
        carEngineFacade.stopEngine();
        Assert.assertTrue(appender.logContains("Stopping Fuel injector.."));
        Assert.assertTrue(appender.logContains("Catalytic Converter switched off!"));
        Assert.assertTrue(appender.logContains("Scheduled cooling with maximum allowed temperature 50"));
        Assert.assertTrue(appender.logContains("Getting temperature from the sensor.."));
        Assert.assertTrue(appender.logContains("Radiator switched on!"));
        Assert.assertTrue(appender.logContains("Stopping Cooling Controller.."));
        Assert.assertTrue(appender.logContains("Radiator switched off!"));
        Assert.assertTrue(appender.logContains("Air controller switched off."));
    }

    private class InMemoryCustomTestAppender extends AppenderBase<ILoggingEvent> {
        private List<ILoggingEvent> logs = new ArrayList<>();

        public InMemoryCustomTestAppender() {
            addAppender(this);
            start();
        }

        @Override
        protected void append(ILoggingEvent eventObject) {
            logs.add(eventObject);
        }

        public boolean logContains(String message) {
            return logs.stream().anyMatch(( event) -> event.getFormattedMessage().equals(message));
        }
    }
}

