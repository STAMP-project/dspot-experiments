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
package org.pentaho.di.core.logging;


import java.io.IOException;
import java.io.OutputStream;
import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.di.core.Const;


public class Log4jFileAppenderTest extends Log4jAppenderTest {
    private Log4jFileAppender log4jFileAppender;

    private OutputStream outputStream;

    @Test
    public void doAppend() throws IOException {
        LoggingEvent loggingEvent = Mockito.mock(LoggingEvent.class);
        Layout testLayout = Mockito.mock(Layout.class);
        Mockito.when(testLayout.format(loggingEvent)).thenReturn("LOG_TEST_LINE");
        log4jFileAppender.setLayout(testLayout);
        log4jFileAppender.doAppend(loggingEvent);
        Mockito.verify(outputStream).write(("LOG_TEST_LINE" + (Const.CR)).getBytes());
    }

    @Test
    public void close() throws IOException {
        log4jFileAppender.close();
        Mockito.verify(outputStream).close();
    }
}

