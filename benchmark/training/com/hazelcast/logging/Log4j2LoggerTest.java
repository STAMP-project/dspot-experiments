/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.logging;


import Log4j2Factory.Log4j2Logger;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.logging.Level;
import org.apache.logging.log4j.spi.ExtendedLogger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class Log4j2LoggerTest extends AbstractLoggerTest {
    private static final String LOGGER_NAME = Log4j2Logger.class.getName();

    private ExtendedLogger mockLogger;

    private ILogger hazelcastLogger;

    @Test
    public void isLoggable_whenLevelOff_shouldReturnFalse() {
        Assert.assertFalse(hazelcastLogger.isLoggable(Level.OFF));
    }

    @Test
    public void logWithThrowable_shouldCallLogWithThrowable() {
        hazelcastLogger.warning(AbstractLoggerTest.MESSAGE, AbstractLoggerTest.THROWABLE);
        Mockito.verify(mockLogger, Mockito.times(1)).logIfEnabled(Log4j2LoggerTest.LOGGER_NAME, WARN, null, AbstractLoggerTest.MESSAGE, AbstractLoggerTest.THROWABLE);
    }

    @Test
    public void logAtLevelOff_shouldLogAtLevelOff() {
        hazelcastLogger.log(Level.OFF, AbstractLoggerTest.MESSAGE);
        Mockito.verify(mockLogger, Mockito.times(1)).logIfEnabled(Log4j2LoggerTest.LOGGER_NAME, OFF, null, AbstractLoggerTest.MESSAGE);
    }

    @Test
    public void logFinest_shouldLogTrace() {
        hazelcastLogger.finest(AbstractLoggerTest.MESSAGE);
        Mockito.verify(mockLogger, Mockito.times(1)).logIfEnabled(Log4j2LoggerTest.LOGGER_NAME, TRACE, null, AbstractLoggerTest.MESSAGE);
    }

    @Test
    public void logFiner_shouldLogDebug() {
        hazelcastLogger.log(Level.FINER, AbstractLoggerTest.MESSAGE);
        Mockito.verify(mockLogger, Mockito.times(1)).logIfEnabled(Log4j2LoggerTest.LOGGER_NAME, DEBUG, null, AbstractLoggerTest.MESSAGE);
    }

    @Test
    public void logFine_shouldLogDebug() {
        hazelcastLogger.fine(AbstractLoggerTest.MESSAGE);
        Mockito.verify(mockLogger, Mockito.times(1)).logIfEnabled(Log4j2LoggerTest.LOGGER_NAME, DEBUG, null, AbstractLoggerTest.MESSAGE);
    }

    @Test
    public void logInfo_shouldLogInfo() {
        hazelcastLogger.info(AbstractLoggerTest.MESSAGE);
        Mockito.verify(mockLogger, Mockito.times(1)).logIfEnabled(Log4j2LoggerTest.LOGGER_NAME, INFO, null, AbstractLoggerTest.MESSAGE);
    }

    @Test
    public void logWarning_shouldLogWarn() {
        hazelcastLogger.warning(AbstractLoggerTest.MESSAGE);
        Mockito.verify(mockLogger, Mockito.times(1)).logIfEnabled(Log4j2LoggerTest.LOGGER_NAME, WARN, null, AbstractLoggerTest.MESSAGE);
    }

    @Test
    public void logSevere_shouldLogError() {
        hazelcastLogger.severe(AbstractLoggerTest.MESSAGE);
        Mockito.verify(mockLogger, Mockito.times(1)).logIfEnabled(Log4j2LoggerTest.LOGGER_NAME, ERROR, null, AbstractLoggerTest.MESSAGE);
    }

    @Test
    public void logEvent_shouldLogWithCorrectLevel() {
        hazelcastLogger.log(AbstractLoggerTest.LOG_EVENT);
        Mockito.verify(mockLogger, Mockito.times(1)).logIfEnabled(Log4j2LoggerTest.LOGGER_NAME, WARN, null, AbstractLoggerTest.MESSAGE, AbstractLoggerTest.THROWABLE);
    }
}

