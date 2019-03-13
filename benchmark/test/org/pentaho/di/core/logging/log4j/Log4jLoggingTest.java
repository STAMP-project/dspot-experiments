/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 */
package org.pentaho.di.core.logging.log4j;


import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.di.core.logging.LogLevel;


public class Log4jLoggingTest {
    private Logger logger;

    private Log4jLoggingTest.Log4jLoggingPluginWithMockedLogger log4jPlugin;

    @Test
    public void eventAddedError() {
        eventAdded(new org.pentaho.di.core.logging.KettleLoggingEvent("ERROR_TEST_MESSAGE", 0L, LogLevel.ERROR));
        Mockito.verify(logger).log(Level.ERROR, "ERROR_TEST_MESSAGE");
    }

    @Test
    public void eventAddedDebug() {
        eventAdded(new org.pentaho.di.core.logging.KettleLoggingEvent("DEBUG_TEST_MESSAGE", 0L, LogLevel.DEBUG));
        Mockito.verify(logger).log(Level.DEBUG, "DEBUG_TEST_MESSAGE");
    }

    @Test
    public void eventAddedDetailed() {
        eventAdded(new org.pentaho.di.core.logging.KettleLoggingEvent("DETAILED_TEST_MESSAGE", 0L, LogLevel.DETAILED));
        Mockito.verify(logger).log(Level.INFO, "DETAILED_TEST_MESSAGE");
    }

    @Test
    public void eventAddedRowLevel() {
        eventAdded(new org.pentaho.di.core.logging.KettleLoggingEvent("ROWLEVEL_TEST_MESSAGE", 0L, LogLevel.ROWLEVEL));
        Mockito.verify(logger).log(Level.DEBUG, "ROWLEVEL_TEST_MESSAGE");
    }

    @Test
    public void eventAddedBasic() {
        eventAdded(new org.pentaho.di.core.logging.KettleLoggingEvent("BASIC_TEST_MESSAGE", 0L, LogLevel.BASIC));
        Mockito.verify(logger).log(Level.INFO, "BASIC_TEST_MESSAGE");
    }

    @Test
    public void eventAddedMinimal() {
        eventAdded(new org.pentaho.di.core.logging.KettleLoggingEvent("MINIMAL_TEST_MESSAGE", 0L, LogLevel.MINIMAL));
        Mockito.verify(logger).log(Level.INFO, "MINIMAL_TEST_MESSAGE");
    }

    private static final class Log4jLoggingPluginWithMockedLogger extends Log4jLogging {
        private final Logger logger;

        Log4jLoggingPluginWithMockedLogger(Logger logger) {
            this.logger = logger;
        }

        @Override
        Logger createLogger(String loggerName) {
            return logger;
        }
    }
}

