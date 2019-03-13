package com.stackify.services;


import Level.INFO;
import com.stackify.models.User;
import java.time.LocalDate;
import java.time.Month;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.junit.Test;


public class MyServiceUnitTest {
    private static final Logger logger = LogManager.getLogger(MyServiceUnitTest.class);

    @Test
    public void testService() {
        MyService myService = new MyService();
        User user = new User("John", "john@yahoo.com");
        user.setDateOfBirth(LocalDate.of(1980, Month.APRIL, 20));
        MyServiceUnitTest.logger.info("Age of user {} is {}", () -> user.getName(), () -> myService.calculateUserAge(user));
    }

    @Test
    public void testColoredLogger() {
        MyServiceUnitTest.logger.fatal("Fatal level message");
        MyServiceUnitTest.logger.error("Error level message");
        MyServiceUnitTest.logger.warn("Warn level message");
        MyServiceUnitTest.logger.info("Info level message");
        MyServiceUnitTest.logger.debug("Debug level message");
    }

    @Test
    public void testRollingFileAppender() {
        Logger rfLogger = LogManager.getLogger("RollingFileLogger");
        rfLogger.info("Json Message 1");
        rfLogger.info("Json Message 2");
    }

    @Test
    public void testProgrammaticConfig() {
        LoggerContext ctx = ((LoggerContext) (LogManager.getContext(false)));
        Configuration config = ctx.getConfiguration();
        PatternLayout layout = PatternLayout.newBuilder().withConfiguration(config).withPattern("%d{HH:mm:ss.SSS} %level %msg%n").build();
        Appender appender = FileAppender.newBuilder().setConfiguration(config).withName("programmaticFileAppender").withLayout(layout).withFileName("java.log").build();
        appender.start();
        config.addAppender(appender);
        AppenderRef ref = AppenderRef.createAppenderRef("programmaticFileAppender", null, null);
        AppenderRef[] refs = new AppenderRef[]{ ref };
        LoggerConfig loggerConfig = LoggerConfig.createLogger(false, INFO, "programmaticLogger", "true", refs, null, config, null);
        loggerConfig.addAppender(appender, null, null);
        config.addLogger("programmaticLogger", loggerConfig);
        ctx.updateLoggers();
        Logger pLogger = LogManager.getLogger("programmaticLogger");
        pLogger.info("Programmatic Logger Message");
    }

    @Test
    public void testCustomLevel() {
        Level myLevel = Level.forName("NEW_LEVEL", 350);
        MyServiceUnitTest.logger.log(myLevel, "Custom Level Message");
        MyServiceUnitTest.logger.log(Level.getLevel("NEW_XML_LEVEL"), "Custom XML Level Message");
    }
}

