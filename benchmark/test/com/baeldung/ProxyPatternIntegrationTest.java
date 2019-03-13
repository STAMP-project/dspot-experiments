package com.baeldung;


import com.baeldung.proxy.ExpensiveObject;
import com.baeldung.proxy.ExpensiveObjectProxy;
import java.util.List;
import org.apache.log4j.spi.LoggingEvent;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class ProxyPatternIntegrationTest {
    public static TestAppenderDP appender;

    @Test
    public void givenExpensiveObjectProxy_WhenObjectInitialized_thenInitializedOnlyOnce() {
        ExpensiveObject object = new ExpensiveObjectProxy();
        object.process();
        object.process();
        final List<LoggingEvent> log = ProxyPatternIntegrationTest.appender.getLog();
        Assert.assertThat(((String) (log.get(0).getMessage())), CoreMatchers.is("Loading initial configuration.."));
        Assert.assertThat(((String) (log.get(1).getMessage())), CoreMatchers.is("processing complete."));
        Assert.assertThat(((String) (log.get(2).getMessage())), CoreMatchers.is("processing complete."));
    }
}

