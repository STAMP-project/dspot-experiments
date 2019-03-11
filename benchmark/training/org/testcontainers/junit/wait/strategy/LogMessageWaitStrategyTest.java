package org.testcontainers.junit.wait.strategy;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;


/**
 * Tests for {@link LogMessageWaitStrategy}.
 */
@RunWith(Parameterized.class)
public class LogMessageWaitStrategyTest extends AbstractWaitStrategyTest<LogMessageWaitStrategy> {
    private final String pattern;

    public LogMessageWaitStrategyTest(String pattern) {
        this.pattern = pattern;
    }

    private static final String READY_MESSAGE = "I'm ready!";

    @Test
    public void testWaitUntilReady_Success() {
        waitUntilReadyAndSucceed(((((((("echo -e \"" + (LogMessageWaitStrategyTest.READY_MESSAGE)) + "\";") + "echo -e \"foobar\";") + "echo -e \"") + (LogMessageWaitStrategyTest.READY_MESSAGE)) + "\";") + "sleep 300"));
    }

    @Test
    public void testWaitUntilReady_Timeout() {
        waitUntilReadyAndTimeout((((("echo -e \"" + (LogMessageWaitStrategyTest.READY_MESSAGE)) + "\";") + "echo -e \"foobar\";") + "sleep 300"));
    }
}

