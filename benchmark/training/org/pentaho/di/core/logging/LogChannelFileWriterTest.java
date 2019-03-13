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


import java.io.OutputStream;
import org.apache.commons.vfs2.FileContent;
import org.apache.commons.vfs2.FileObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import static LogLevel.BASIC;


@RunWith(MockitoJUnitRunner.class)
public class LogChannelFileWriterTest {
    String id = "1";

    String logMessage = "Log message";

    @Mock
    FileObject fileObject;

    @Mock
    FileContent fileContent;

    @Mock
    OutputStream outputStream;

    @Captor
    ArgumentCaptor<byte[]> captor;

    @Test
    public void test() throws Exception {
        LogChannelFileWriter writer = new LogChannelFileWriter(id, fileObject, false);
        LoggingRegistry.getInstance().getLogChannelFileWriterBuffer(id).addEvent(new KettleLoggingEvent(logMessage, System.currentTimeMillis(), BASIC));
        writer.flush();
        Mockito.verify(outputStream).write(captor.capture());
        String arguments = new String(captor.getValue());
        Assert.assertTrue(arguments.contains(logMessage));
    }

    @Test
    public void testStartStopLogging() throws Exception {
        LogChannelFileWriter writer = new LogChannelFileWriter(id, fileObject, false);
        Mockito.doAnswer(( invocationOnMock) -> {
            Thread.sleep(2000);
            return null;
        }).when(outputStream).close();
        writer.startLogging();
        Thread.sleep(500);
        Mockito.verify(outputStream, Mockito.atLeastOnce()).write(ArgumentMatchers.any(byte[].class));
        Mockito.verify(outputStream, Mockito.atLeastOnce()).flush();
        Mockito.verify(outputStream, Mockito.never()).close();
        writer.stopLogging();
        Mockito.verify(outputStream).close();
    }
}

