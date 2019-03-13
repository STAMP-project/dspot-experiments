package com.vip.vjtools.vjkit.logging;


import ch.qos.logback.classic.Logger;
import org.junit.Test;
import org.slf4j.LoggerFactory;


public class PerformanceUtilsTest {
    Logger logger = ((Logger) (LoggerFactory.getLogger(PerformanceUtilsTest.class)));

    @Test
    public void test() throws InterruptedException {
        PerformanceUtil.start();
        PerformanceUtil.start("test");
        Thread.sleep(1000L);// NOSONAR

        PerformanceUtil.endWithSlowLog(logger, 100L);
        PerformanceUtil.endWithSlowLog(logger, "test", 100L);
    }
}

