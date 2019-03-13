/**
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
 *
 * **************************************************************************
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
 */
package org.pentaho.di.core.util;


import java.io.InputStream;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.core.logging.LogLevel;


public class ConfigurableStreamLoggerTest {
    public static String INPUT = "str1\nstr2";

    public static String PREFIX = "OUTPUT";

    public static String OUT1 = "OUTPUT str1";

    public static String OUT2 = "OUTPUT str2";

    private ConfigurableStreamLogger streamLogger;

    private LogChannelInterface log;

    private InputStream is;

    @Test
    public void testLogError() {
        streamLogger = new ConfigurableStreamLogger(log, is, LogLevel.ERROR, ConfigurableStreamLoggerTest.PREFIX);
        streamLogger.run();
        Mockito.verify(log).logError(ConfigurableStreamLoggerTest.OUT1);
        Mockito.verify(log).logError(ConfigurableStreamLoggerTest.OUT2);
    }

    @Test
    public void testLogMinimal() {
        streamLogger = new ConfigurableStreamLogger(log, is, LogLevel.MINIMAL, ConfigurableStreamLoggerTest.PREFIX);
        streamLogger.run();
        Mockito.verify(log).logMinimal(ConfigurableStreamLoggerTest.OUT1);
        Mockito.verify(log).logMinimal(ConfigurableStreamLoggerTest.OUT2);
    }

    @Test
    public void testLogBasic() {
        streamLogger = new ConfigurableStreamLogger(log, is, LogLevel.BASIC, ConfigurableStreamLoggerTest.PREFIX);
        streamLogger.run();
        Mockito.verify(log).logBasic(ConfigurableStreamLoggerTest.OUT1);
        Mockito.verify(log).logBasic(ConfigurableStreamLoggerTest.OUT2);
    }

    @Test
    public void testLogDetailed() {
        streamLogger = new ConfigurableStreamLogger(log, is, LogLevel.DETAILED, ConfigurableStreamLoggerTest.PREFIX);
        streamLogger.run();
        Mockito.verify(log).logDetailed(ConfigurableStreamLoggerTest.OUT1);
        Mockito.verify(log).logDetailed(ConfigurableStreamLoggerTest.OUT2);
    }

    @Test
    public void testLogDebug() {
        streamLogger = new ConfigurableStreamLogger(log, is, LogLevel.DEBUG, ConfigurableStreamLoggerTest.PREFIX);
        streamLogger.run();
        Mockito.verify(log).logDebug(ConfigurableStreamLoggerTest.OUT1);
        Mockito.verify(log).logDebug(ConfigurableStreamLoggerTest.OUT2);
    }

    @Test
    public void testLogRowlevel() {
        streamLogger = new ConfigurableStreamLogger(log, is, LogLevel.ROWLEVEL, ConfigurableStreamLoggerTest.PREFIX);
        streamLogger.run();
        Mockito.verify(log).logRowlevel(ConfigurableStreamLoggerTest.OUT1);
        Mockito.verify(log).logRowlevel(ConfigurableStreamLoggerTest.OUT2);
    }
}

