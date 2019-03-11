package io.cucumber.core.logging;


import java.util.logging.Level;
import java.util.logging.LogRecord;
import org.junit.Assert;
import org.junit.Test;


public class LoggerFactoryTest {
    private final Exception exception = new Exception();

    private LogRecord logged;

    private Logger logger = LoggerFactory.getLogger(LoggerFactoryTest.class);

    @Test
    public void error() {
        logger.error("Error");
        Assert.assertThat(logged, LoggerFactoryTest.logRecord("Error", Level.SEVERE, null));
        logger.error("Error", exception);
        Assert.assertThat(logged, LoggerFactoryTest.logRecord("Error", Level.SEVERE, exception));
    }

    @Test
    public void warn() {
        logger.warn("Warn");
        Assert.assertThat(logged, LoggerFactoryTest.logRecord("Warn", Level.WARNING, null));
        logger.warn("Warn", exception);
        Assert.assertThat(logged, LoggerFactoryTest.logRecord("Warn", Level.WARNING, exception));
    }

    @Test
    public void info() {
        logger.info("Info");
        Assert.assertThat(logged, LoggerFactoryTest.logRecord("Info", Level.INFO, null));
        logger.info("Info", exception);
        Assert.assertThat(logged, LoggerFactoryTest.logRecord("Info", Level.INFO, exception));
    }

    @Test
    public void config() {
        logger.config("Config");
        Assert.assertThat(logged, LoggerFactoryTest.logRecord("Config", Level.CONFIG, null));
        logger.config("Config", exception);
        Assert.assertThat(logged, LoggerFactoryTest.logRecord("Config", Level.CONFIG, exception));
    }

    @Test
    public void debug() {
        logger.debug("Debug");
        Assert.assertThat(logged, LoggerFactoryTest.logRecord("Debug", Level.FINE, null));
        logger.debug("Debug", exception);
        Assert.assertThat(logged, LoggerFactoryTest.logRecord("Debug", Level.FINE, exception));
    }

    @Test
    public void trace() {
        logger.trace("Trace");
        Assert.assertThat(logged, LoggerFactoryTest.logRecord("Trace", Level.FINER, null));
        logger.trace("Trace", exception);
        Assert.assertThat(logged, LoggerFactoryTest.logRecord("Trace", Level.FINER, exception));
    }
}

