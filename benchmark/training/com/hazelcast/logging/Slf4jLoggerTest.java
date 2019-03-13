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
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slf4j.Logger;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class Slf4jLoggerTest extends AbstractLoggerTest {
    private Logger mockLogger;

    private ILogger hazelcastLogger;

    @Test
    public void isLoggable_whenLevelOff_shouldReturnFalse() {
        Assert.assertFalse(hazelcastLogger.isLoggable(Level.OFF));
    }

    @Test
    public void logWithThrowable_shouldCallLogWithThrowable() {
        hazelcastLogger.warning(AbstractLoggerTest.MESSAGE, AbstractLoggerTest.THROWABLE);
        Mockito.verify(mockLogger, Mockito.times(1)).warn(AbstractLoggerTest.MESSAGE, AbstractLoggerTest.THROWABLE);
    }

    @Test
    public void logAtLevelOff_shouldNotLog() {
        hazelcastLogger.log(Level.OFF, AbstractLoggerTest.MESSAGE);
        Mockito.verifyZeroInteractions(mockLogger);
    }

    @Test
    public void logAtLevelOff_shouldNotLog_withThrowable() {
        hazelcastLogger.log(Level.OFF, AbstractLoggerTest.MESSAGE, AbstractLoggerTest.THROWABLE);
        Mockito.verifyZeroInteractions(mockLogger);
    }

    @Test
    public void logAtLevelAll_shouldLogInfo() {
        hazelcastLogger.log(Level.ALL, AbstractLoggerTest.MESSAGE);
        Mockito.verify(mockLogger, Mockito.times(1)).info(AbstractLoggerTest.MESSAGE);
    }

    @Test
    public void logAtLevelAll_shouldLogInfo_withThrowable() {
        hazelcastLogger.log(Level.ALL, AbstractLoggerTest.MESSAGE, AbstractLoggerTest.THROWABLE);
        Mockito.verify(mockLogger, Mockito.times(1)).info(AbstractLoggerTest.MESSAGE, AbstractLoggerTest.THROWABLE);
    }

    @Test
    public void logFinest_shouldLogTrace() {
        hazelcastLogger.finest(AbstractLoggerTest.MESSAGE);
        Mockito.verify(mockLogger, Mockito.times(1)).trace(AbstractLoggerTest.MESSAGE);
    }

    @Test
    public void logFinest_shouldLogTrace_withThrowable() {
        hazelcastLogger.finest(AbstractLoggerTest.MESSAGE, AbstractLoggerTest.THROWABLE);
        Mockito.verify(mockLogger, Mockito.times(1)).trace(AbstractLoggerTest.MESSAGE, AbstractLoggerTest.THROWABLE);
    }

    @Test
    public void logFine_shouldLogDebug() {
        hazelcastLogger.fine(AbstractLoggerTest.MESSAGE);
        Mockito.verify(mockLogger, Mockito.times(1)).debug(AbstractLoggerTest.MESSAGE);
    }

    @Test
    public void logFine_shouldLogDebug_withThrowable() {
        hazelcastLogger.fine(AbstractLoggerTest.MESSAGE, AbstractLoggerTest.THROWABLE);
        Mockito.verify(mockLogger, Mockito.times(1)).debug(AbstractLoggerTest.MESSAGE, AbstractLoggerTest.THROWABLE);
    }

    @Test
    public void logInfo_shouldLogInfo() {
        hazelcastLogger.info(AbstractLoggerTest.MESSAGE);
        Mockito.verify(mockLogger, Mockito.times(1)).info(AbstractLoggerTest.MESSAGE);
    }

    @Test
    public void logInfo_shouldLogInfo_withThrowable() {
        hazelcastLogger.log(Level.INFO, AbstractLoggerTest.MESSAGE, AbstractLoggerTest.THROWABLE);
        Mockito.verify(mockLogger, Mockito.times(1)).info(AbstractLoggerTest.MESSAGE, AbstractLoggerTest.THROWABLE);
    }

    @Test
    public void logWarning_shouldLogWarn() {
        hazelcastLogger.warning(AbstractLoggerTest.MESSAGE);
        Mockito.verify(mockLogger, Mockito.times(1)).warn(AbstractLoggerTest.MESSAGE);
    }

    @Test
    public void logWarning_shouldLogWarn_withThrowable() {
        hazelcastLogger.warning(AbstractLoggerTest.MESSAGE, AbstractLoggerTest.THROWABLE);
        Mockito.verify(mockLogger, Mockito.times(1)).warn(AbstractLoggerTest.MESSAGE, AbstractLoggerTest.THROWABLE);
    }

    @Test
    public void logSevere_shouldLogError() {
        hazelcastLogger.severe(AbstractLoggerTest.MESSAGE);
        Mockito.verify(mockLogger, Mockito.times(1)).error(AbstractLoggerTest.MESSAGE);
    }

    @Test
    public void logSevere_shouldLogError_withThrowable() {
        hazelcastLogger.severe(AbstractLoggerTest.MESSAGE, AbstractLoggerTest.THROWABLE);
        Mockito.verify(mockLogger, Mockito.times(1)).error(AbstractLoggerTest.MESSAGE, AbstractLoggerTest.THROWABLE);
    }

    @Test
    public void logEvent_shouldLogWithCorrectLevel() {
        hazelcastLogger.log(AbstractLoggerTest.LOG_EVENT);
        Mockito.verify(mockLogger, Mockito.times(1)).warn(AbstractLoggerTest.MESSAGE, AbstractLoggerTest.THROWABLE);
    }
}

