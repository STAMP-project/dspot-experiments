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


import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import static org.apache.log4j.Level.DEBUG;
import static org.apache.log4j.Level.ERROR;
import static org.apache.log4j.Level.INFO;
import static org.apache.log4j.Level.OFF;
import static org.apache.log4j.Level.TRACE;
import static org.apache.log4j.Level.WARN;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class Log4jLoggerTest extends AbstractLoggerTest {
    private Logger mockLogger;

    private ILogger hazelcastLogger;

    @Test
    public void isLoggable_whenLevelOff_shouldReturnFalse() {
        Assert.assertFalse(hazelcastLogger.isLoggable(Level.OFF));
    }

    @Test
    public void logWithThrowable_shouldCallLogWithThrowable() {
        hazelcastLogger.warning(AbstractLoggerTest.MESSAGE, AbstractLoggerTest.THROWABLE);
        Mockito.verify(mockLogger, Mockito.times(1)).log(WARN, AbstractLoggerTest.MESSAGE, AbstractLoggerTest.THROWABLE);
    }

    @Test
    public void logAtLevelOff_shouldLogAtLevelOff() {
        hazelcastLogger.log(Level.OFF, AbstractLoggerTest.MESSAGE);
        Mockito.verify(mockLogger, Mockito.times(1)).log(OFF, AbstractLoggerTest.MESSAGE);
    }

    @Test
    public void logFinest_shouldLogTrace() {
        hazelcastLogger.finest(AbstractLoggerTest.MESSAGE);
        Mockito.verify(mockLogger, Mockito.times(1)).log(TRACE, AbstractLoggerTest.MESSAGE);
    }

    @Test
    public void logFiner_shouldLogDebug() {
        hazelcastLogger.log(Level.FINER, AbstractLoggerTest.MESSAGE);
        Mockito.verify(mockLogger, Mockito.times(1)).log(DEBUG, AbstractLoggerTest.MESSAGE);
    }

    @Test
    public void logFine_shouldLogDebug() {
        hazelcastLogger.fine(AbstractLoggerTest.MESSAGE);
        Mockito.verify(mockLogger, Mockito.times(1)).log(DEBUG, AbstractLoggerTest.MESSAGE);
    }

    @Test
    public void logInfo_shouldLogInfo() {
        hazelcastLogger.info(AbstractLoggerTest.MESSAGE);
        Mockito.verify(mockLogger, Mockito.times(1)).log(INFO, AbstractLoggerTest.MESSAGE);
    }

    @Test
    public void logWarning_shouldLogWarn() {
        hazelcastLogger.warning(AbstractLoggerTest.MESSAGE);
        Mockito.verify(mockLogger, Mockito.times(1)).log(WARN, AbstractLoggerTest.MESSAGE);
    }

    @Test
    public void logSevere_shouldLogError() {
        hazelcastLogger.severe(AbstractLoggerTest.MESSAGE);
        Mockito.verify(mockLogger, Mockito.times(1)).log(ERROR, AbstractLoggerTest.MESSAGE);
    }

    @Test
    @SuppressWarnings("ThrowableNotThrown")
    public void logEvent_shouldLog() {
        LogRecord logRecord = AbstractLoggerTest.LOG_EVENT.getLogRecord();
        hazelcastLogger.log(AbstractLoggerTest.LOG_EVENT);
        Mockito.verifyZeroInteractions(mockLogger);
        Mockito.verify(logRecord, Mockito.atLeastOnce()).getLevel();
        Mockito.verify(logRecord, Mockito.atLeastOnce()).getLoggerName();
        Mockito.verify(logRecord, Mockito.atLeastOnce()).getMessage();
        Mockito.verify(logRecord, Mockito.atLeastOnce()).getThrown();
        Mockito.verifyNoMoreInteractions(logRecord);
    }

    @Test
    public void logEvent_withLogLevelOff_shouldNotLog() {
        LogRecord logRecord = AbstractLoggerTest.LOG_EVENT_OFF.getLogRecord();
        hazelcastLogger.log(AbstractLoggerTest.LOG_EVENT_OFF);
        Mockito.verifyZeroInteractions(mockLogger);
        Mockito.verify(logRecord, Mockito.atLeastOnce()).getLevel();
        Mockito.verifyNoMoreInteractions(logRecord);
    }
}

