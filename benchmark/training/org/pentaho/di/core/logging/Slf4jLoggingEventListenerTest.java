/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2018 by Hitachi Vantara : http://www.pentaho.com
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
import LogLevel.ERROR;
import LoggingObjectType.JOB;
import LoggingObjectType.TRANS;
import java.util.function.Function;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;


@RunWith(MockitoJUnitRunner.class)
public class Slf4jLoggingEventListenerTest {
    @Mock
    private Logger transLogger;

    @Mock
    private Logger jobLogger;

    @Mock
    private Logger diLogger;

    @Mock
    private KettleLoggingEvent logEvent;

    @Mock
    private LoggingObjectInterface loggingObject;

    @Mock
    private LogMessage message;

    @Mock
    private Function<String, LoggingObjectInterface> logObjProvider;

    private String logChannelId = "logChannelId";

    private String msgText = "message";

    private String messageSub = "subject";

    private LogLevel logLevel = LogLevel.BASIC;

    private Slf4jLoggingEventListener listener = new Slf4jLoggingEventListener();

    @Test
    public void testAddLogEventNoRegisteredLogObject() {
        listener.eventAdded(logEvent);
        Mockito.verify(diLogger).info((((messageSub) + " ") + (msgText)));
        Mockito.when(message.getLevel()).thenReturn(LogLevel.ERROR);
        listener.eventAdded(logEvent);
        Mockito.verify(diLogger).error((((messageSub) + " ") + (msgText)));
        Mockito.verifyZeroInteractions(transLogger);
        Mockito.verifyZeroInteractions(jobLogger);
    }

    @Test
    public void testAddLogEventTrans() {
        Mockito.when(logObjProvider.apply(logChannelId)).thenReturn(loggingObject);
        Mockito.when(loggingObject.getLogChannelId()).thenReturn(logChannelId);
        Mockito.when(loggingObject.getObjectType()).thenReturn(TRANS);
        Mockito.when(loggingObject.getFilename()).thenReturn("filename");
        Mockito.when(message.getLevel()).thenReturn(BASIC);
        listener.eventAdded(logEvent);
        Mockito.verify(transLogger).info(("[filename]  " + (msgText)));
        Mockito.when(message.getLevel()).thenReturn(ERROR);
        listener.eventAdded(logEvent);
        Mockito.verify(transLogger).error(("[filename]  " + (msgText)));
        Mockito.verifyZeroInteractions(diLogger);
        Mockito.verifyZeroInteractions(jobLogger);
    }

    @Test
    public void testAddLogEventJob() {
        Mockito.when(logObjProvider.apply(logChannelId)).thenReturn(loggingObject);
        Mockito.when(loggingObject.getLogChannelId()).thenReturn(logChannelId);
        Mockito.when(loggingObject.getObjectType()).thenReturn(JOB);
        Mockito.when(loggingObject.getFilename()).thenReturn("filename");
        Mockito.when(message.getLevel()).thenReturn(BASIC);
        listener.eventAdded(logEvent);
        Mockito.verify(jobLogger).info(("[filename]  " + (msgText)));
        Mockito.when(message.getLevel()).thenReturn(ERROR);
        listener.eventAdded(logEvent);
        Mockito.verify(jobLogger).error(("[filename]  " + (msgText)));
        Mockito.verifyZeroInteractions(diLogger);
        Mockito.verifyZeroInteractions(transLogger);
    }
}

