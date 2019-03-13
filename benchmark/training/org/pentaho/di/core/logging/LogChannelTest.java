/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2019 by Hitachi Vantara : http://www.pentaho.com
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


import LogLevel.BASIC;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.core.util.Utils;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ DefaultLogLevel.class, LoggingRegistry.class, LogLevel.class, KettleLogStore.class, Utils.class })
public class LogChannelTest {
    private LogChannel logChannel;

    private String logChannelSubject = "pdi";

    private String channelId = "1234-5678-abcd-efgh";

    private LogLevel logLevel;

    private LogMessageInterface logMsgInterface;

    private LogChannelFileWriterBuffer logChFileWriterBuffer;

    @Test
    public void testPrintlnWithNullLogChannelFileWriterBuffer() {
        when(logLevel.isVisible(ArgumentMatchers.any(LogLevel.class))).thenReturn(true);
        LoggingBuffer loggingBuffer = Mockito.mock(LoggingBuffer.class);
        PowerMockito.mockStatic(KettleLogStore.class);
        when(KettleLogStore.getAppender()).thenReturn(loggingBuffer);
        logChannel.println(logMsgInterface, BASIC);
        Mockito.verify(logChFileWriterBuffer, Mockito.times(1)).addEvent(ArgumentMatchers.any(KettleLoggingEvent.class));
        Mockito.verify(loggingBuffer, Mockito.times(1)).addLogggingEvent(ArgumentMatchers.any(KettleLoggingEvent.class));
    }

    @Test
    public void testPrintlnLogNotVisible() {
        when(logLevel.isVisible(ArgumentMatchers.any(LogLevel.class))).thenReturn(false);
        logChannel.println(logMsgInterface, BASIC);
        Mockito.verify(logChFileWriterBuffer, Mockito.times(0)).addEvent(ArgumentMatchers.any(KettleLoggingEvent.class));
    }

    @Test
    public void testPrintMessageFiltered() {
        LogLevel logLevelFil = PowerMockito.mock(LogLevel.class);
        Whitebox.setInternalState(logLevelFil, "name", "Error");
        Whitebox.setInternalState(logLevelFil, "ordinal", 1);
        when(logLevelFil.isError()).thenReturn(false);
        LogMessageInterface logMsgInterfaceFil = Mockito.mock(LogMessageInterface.class);
        Mockito.when(logMsgInterfaceFil.getLevel()).thenReturn(logLevelFil);
        Mockito.when(logMsgInterfaceFil.toString()).thenReturn("a");
        PowerMockito.mockStatic(Utils.class);
        when(Utils.isEmpty(ArgumentMatchers.anyString())).thenReturn(false);
        when(logLevelFil.isVisible(ArgumentMatchers.any(LogLevel.class))).thenReturn(true);
        logChannel.setFilter("b");
        logChannel.println(logMsgInterfaceFil, BASIC);
        Mockito.verify(logChFileWriterBuffer, Mockito.times(0)).addEvent(ArgumentMatchers.any(KettleLoggingEvent.class));
    }
}

