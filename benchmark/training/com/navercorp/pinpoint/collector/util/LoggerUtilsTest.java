/**
 * Copyright 2014 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.navercorp.pinpoint.collector.util;


import LoggerUtils.DEBUG_LEVEL;
import LoggerUtils.ERROR_LEVEL;
import LoggerUtils.INFO_LEVEL;
import LoggerUtils.WARN_LEVEL;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;

import static LoggerUtils.WARN_LEVEL;


/**
 *
 *
 * @author emeroad
 */
public class LoggerUtilsTest {
    @Test
    public void testGetLoggerLevel_debug() throws Exception {
        Logger logger = Mockito.mock(Logger.class);
        Mockito.when(logger.isDebugEnabled()).thenReturn(true);
        int loggerLevel = LoggerUtils.getLoggerLevel(logger);
        Assert.assertEquals(loggerLevel, DEBUG_LEVEL);
        Assert.assertNotEquals(loggerLevel, INFO_LEVEL);
    }

    @Test
    public void testGetLoggerLevel_info() throws Exception {
        Logger logger = Mockito.mock(Logger.class);
        Mockito.when(logger.isInfoEnabled()).thenReturn(true);
        int loggerLevel = LoggerUtils.getLoggerLevel(logger);
        Assert.assertEquals(loggerLevel, INFO_LEVEL);
        Assert.assertNotEquals(loggerLevel, DEBUG_LEVEL);
        Assert.assertNotEquals(loggerLevel, WARN_LEVEL);
    }

    @Test
    public void testGetLoggerLevel_warn() throws Exception {
        Logger logger = Mockito.mock(Logger.class);
        Mockito.when(logger.isWarnEnabled()).thenReturn(true);
        int loggerLevel = LoggerUtils.getLoggerLevel(logger);
        Assert.assertEquals(loggerLevel, WARN_LEVEL);
        Assert.assertNotEquals(loggerLevel, INFO_LEVEL);
        Assert.assertNotEquals(loggerLevel, ERROR_LEVEL);
        if (loggerLevel >= (WARN_LEVEL)) {
            // success
        } else {
            Assert.fail();
        }
    }
}

