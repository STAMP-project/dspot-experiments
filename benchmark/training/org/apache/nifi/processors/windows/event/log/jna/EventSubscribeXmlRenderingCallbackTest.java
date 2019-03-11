/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.processors.windows.event.log.jna;


import EventSubscribeXmlRenderingCallback.MISSING_EVENT_MESSAGE;
import WEvtApi.EvtSubscribeErrors.ERROR_EVT_QUERY_RESULT_STALE;
import WEvtApi.EvtSubscribeNotifyAction.DELIVER;
import WEvtApi.EvtSubscribeNotifyAction.ERROR;
import WinNT.HANDLE;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import java.util.Arrays;
import java.util.function.Consumer;
import org.apache.nifi.logging.ComponentLog;
import org.apache.nifi.processors.windows.event.log.ConsumeWindowsEventLogTest;
import org.apache.nifi.processors.windows.event.log.JNAJUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;

import static EventSubscribeXmlRenderingCallback.EVT_RENDER_RETURNED_THE_FOLLOWING_ERROR_CODE;
import static EventSubscribeXmlRenderingCallback.RECEIVED_THE_FOLLOWING_WIN32_ERROR;


@RunWith(JNAJUnitRunner.class)
public class EventSubscribeXmlRenderingCallbackTest {
    @Mock
    ComponentLog logger;

    @Mock
    Consumer<String> consumer;

    @Mock
    WEvtApi wEvtApi;

    @Mock
    Kernel32 kernel32;

    @Mock
    ErrorLookup errorLookup;

    @Mock
    HANDLE handle;

    private EventSubscribeXmlRenderingCallback eventSubscribeXmlRenderingCallback;

    private int maxBufferSize;

    @Test
    public void testErrorJustLogs() {
        int errorCode = 111;
        Pointer pointer = Mockito.mock(Pointer.class);
        Mockito.when(handle.getPointer()).thenReturn(pointer);
        Mockito.when(pointer.getInt(0)).thenReturn(errorCode);
        eventSubscribeXmlRenderingCallback.onEvent(ERROR, null, handle);
        Mockito.verify(logger).error(((RECEIVED_THE_FOLLOWING_WIN32_ERROR) + errorCode));
    }

    @Test
    public void testMissingRecordLog() {
        Pointer pointer = Mockito.mock(Pointer.class);
        Mockito.when(handle.getPointer()).thenReturn(pointer);
        Mockito.when(pointer.getInt(0)).thenReturn(ERROR_EVT_QUERY_RESULT_STALE);
        eventSubscribeXmlRenderingCallback.onEvent(ERROR, null, handle);
        Mockito.verify(logger).error(MISSING_EVENT_MESSAGE);
    }

    @Test
    public void testSuccessfulRender() {
        String small = "abc";
        handle = ConsumeWindowsEventLogTest.mockEventHandles(wEvtApi, kernel32, Arrays.asList((small + "\u0000"))).get(0);
        eventSubscribeXmlRenderingCallback.onEvent(DELIVER, null, handle);
        Mockito.verify(consumer).accept(small);
    }

    @Test
    public void testUnsuccessfulRender() {
        String large = "abcde";
        handle = ConsumeWindowsEventLogTest.mockEventHandles(wEvtApi, kernel32, Arrays.asList(large)).get(0);
        eventSubscribeXmlRenderingCallback.onEvent(DELIVER, null, handle);
        Mockito.verify(consumer, Mockito.never()).accept(ArgumentMatchers.anyString());
    }

    @Test
    public void testResizeRender() {
        // Make a string too big to fit into initial buffer
        StringBuilder testStringBuilder = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            testStringBuilder.append(i);
        }
        String base = testStringBuilder.toString();
        testStringBuilder = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            testStringBuilder.append(base);
        }
        String veryLarge = testStringBuilder.toString();
        handle = ConsumeWindowsEventLogTest.mockEventHandles(wEvtApi, kernel32, Arrays.asList(veryLarge)).get(0);
        eventSubscribeXmlRenderingCallback = new EventSubscribeXmlRenderingCallback(logger, consumer, 2048, wEvtApi, kernel32, errorLookup);
        eventSubscribeXmlRenderingCallback.onEvent(DELIVER, null, handle);
        Mockito.verify(consumer).accept(veryLarge);
    }

    @Test
    public void testErrorRendering() {
        int value = 225;
        String code = "225code";
        Mockito.when(kernel32.GetLastError()).thenReturn(value);
        Mockito.when(errorLookup.getLastError()).thenReturn(code);
        eventSubscribeXmlRenderingCallback.onEvent(DELIVER, null, handle);
        Mockito.verify(logger).error((((EVT_RENDER_RETURNED_THE_FOLLOWING_ERROR_CODE) + code) + "."));
    }
}

