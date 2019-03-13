package cn.myperf4j.base.test;


import PropertyValues.DEFAULT_LOG_RESERVE_COUNT;
import PropertyValues.LOG_ROLLING_TIME_MINUTELY;
import PropertyValues.NULL_FILE;
import cn.myperf4j.base.config.ProfilingConfig;
import cn.myperf4j.base.log.LoggerFactory;
import org.junit.Test;


public class ILoggerTest {
    @Test
    public void test() {
        ProfilingConfig.getInstance().setLogRollingTimeUnit(LOG_ROLLING_TIME_MINUTELY);
        ProfilingConfig.getInstance().setLogReserveCount(DEFAULT_LOG_RESERVE_COUNT);
        test(LoggerFactory.getLogger("/tmp/testLogger.log"));
        test(LoggerFactory.getLogger(NULL_FILE));
    }
}

