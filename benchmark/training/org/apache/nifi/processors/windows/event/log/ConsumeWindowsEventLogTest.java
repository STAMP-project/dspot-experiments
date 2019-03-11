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
package org.apache.nifi.processors.windows.event.log;


import ConsumeWindowsEventLog.DEFAULT_CHANNEL;
import ConsumeWindowsEventLog.DEFAULT_XPATH;
import ConsumeWindowsEventLog.MAX_EVENT_QUEUE_SIZE;
import ConsumeWindowsEventLog.PROPERTY_DESCRIPTORS;
import ConsumeWindowsEventLog.RELATIONSHIPS;
import ConsumeWindowsEventLog.REL_SUCCESS;
import WEvtApi.EvtSubscribeFlags;
import WEvtApi.EvtSubscribeNotifyAction.DELIVER;
import WinDef.PVOID;
import WinError.ERROR_ACCESS_DENIED;
import WinNT.HANDLE;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinNT;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.nifi.annotation.lifecycle.OnScheduled;
import org.apache.nifi.annotation.lifecycle.OnStopped;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.processors.windows.event.log.jna.EventSubscribeXmlRenderingCallback;
import org.apache.nifi.processors.windows.event.log.jna.WEvtApi;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.ReflectionUtils;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


@RunWith(JNAJUnitRunner.class)
public class ConsumeWindowsEventLogTest {
    @Mock
    Kernel32 kernel32;

    @Mock
    WEvtApi wEvtApi;

    @Mock
    HANDLE subscriptionHandle;

    @Mock
    Pointer subscriptionPointer;

    private ConsumeWindowsEventLog evtSubscribe;

    private TestRunner testRunner;

    @Test(timeout = 10 * 1000)
    public void testProcessesBlockedEvents() throws UnsupportedEncodingException {
        testRunner.setProperty(MAX_EVENT_QUEUE_SIZE, "1");
        testRunner.run(1, false, true);
        EventSubscribeXmlRenderingCallback renderingCallback = getRenderingCallback();
        List<String> eventXmls = Arrays.asList("one", "two", "three", "four", "five", "six");
        List<WinNT.HANDLE> eventHandles = ConsumeWindowsEventLogTest.mockEventHandles(wEvtApi, kernel32, eventXmls);
        AtomicBoolean done = new AtomicBoolean(false);
        new Thread(() -> {
            for (WinNT.HANDLE eventHandle : eventHandles) {
                renderingCallback.onEvent(DELIVER, null, eventHandle);
            }
            done.set(true);
        }).start();
        // Wait until the thread has really started
        while ((testRunner.getFlowFilesForRelationship(REL_SUCCESS).size()) == 0) {
            testRunner.run(1, false, false);
        } 
        // Process rest of events
        while (!(done.get())) {
            testRunner.run(1, false, false);
        } 
        testRunner.run(1, true, false);
        List<MockFlowFile> flowFilesForRelationship = testRunner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(eventXmls.size(), flowFilesForRelationship.size());
        for (int i = 0; i < (eventXmls.size()); i++) {
            flowFilesForRelationship.get(i).assertContentEquals(eventXmls.get(i));
        }
    }

    @Test
    public void testStopProcessesQueue() throws IllegalAccessException, InvocationTargetException {
        testRunner.run(1, false);
        List<String> eventXmls = Arrays.asList("one", "two", "three");
        for (WinNT.HANDLE eventHandle : ConsumeWindowsEventLogTest.mockEventHandles(wEvtApi, kernel32, eventXmls)) {
            getRenderingCallback().onEvent(DELIVER, null, eventHandle);
        }
        ReflectionUtils.invokeMethodsWithAnnotation(OnStopped.class, evtSubscribe, testRunner.getProcessContext());
        List<MockFlowFile> flowFilesForRelationship = testRunner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(eventXmls.size(), flowFilesForRelationship.size());
        for (int i = 0; i < (eventXmls.size()); i++) {
            flowFilesForRelationship.get(i).assertContentEquals(eventXmls.get(i));
        }
    }

    @Test
    public void testScheduleErrorThenTriggerSubscribe() throws IllegalAccessException, InvocationTargetException {
        evtSubscribe = new ConsumeWindowsEventLog(wEvtApi, kernel32);
        Mockito.when(subscriptionHandle.getPointer()).thenReturn(subscriptionPointer);
        Mockito.when(wEvtApi.EvtSubscribe(ArgumentMatchers.isNull(HANDLE.class), ArgumentMatchers.isNull(HANDLE.class), ArgumentMatchers.eq(DEFAULT_CHANNEL), ArgumentMatchers.eq(DEFAULT_XPATH), ArgumentMatchers.isNull(HANDLE.class), ArgumentMatchers.isNull(PVOID.class), ArgumentMatchers.isA(EventSubscribeXmlRenderingCallback.class), ArgumentMatchers.eq(((EvtSubscribeFlags.SUBSCRIBE_TO_FUTURE) | (EvtSubscribeFlags.EVT_SUBSCRIBE_STRICT))))).thenReturn(null).thenReturn(subscriptionHandle);
        testRunner = TestRunners.newTestRunner(evtSubscribe);
        testRunner.run(1, false, true);
        WinNT.HANDLE handle = ConsumeWindowsEventLogTest.mockEventHandles(wEvtApi, kernel32, Arrays.asList("test")).get(0);
        List<EventSubscribeXmlRenderingCallback> renderingCallbacks = getRenderingCallbacks(2);
        EventSubscribeXmlRenderingCallback subscribeRenderingCallback = renderingCallbacks.get(0);
        EventSubscribeXmlRenderingCallback renderingCallback = renderingCallbacks.get(1);
        renderingCallback.onEvent(DELIVER, null, handle);
        testRunner.run(1, true, false);
        Assert.assertNotEquals(subscribeRenderingCallback, renderingCallback);
        Mockito.verify(wEvtApi).EvtClose(subscriptionHandle);
    }

    @Test
    public void testScheduleError() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        evtSubscribe = new ConsumeWindowsEventLog(wEvtApi, kernel32);
        Mockito.when(wEvtApi.EvtSubscribe(ArgumentMatchers.isNull(HANDLE.class), ArgumentMatchers.isNull(HANDLE.class), ArgumentMatchers.eq(DEFAULT_CHANNEL), ArgumentMatchers.eq(DEFAULT_XPATH), ArgumentMatchers.isNull(HANDLE.class), ArgumentMatchers.isNull(PVOID.class), ArgumentMatchers.isA(EventSubscribeXmlRenderingCallback.class), ArgumentMatchers.eq(((EvtSubscribeFlags.SUBSCRIBE_TO_FUTURE) | (EvtSubscribeFlags.EVT_SUBSCRIBE_STRICT))))).thenReturn(null);
        Mockito.when(kernel32.GetLastError()).thenReturn(ERROR_ACCESS_DENIED);
        testRunner = TestRunners.newTestRunner(evtSubscribe);
        testRunner.run(1);
        Assert.assertEquals(0, ConsumeWindowsEventLogTest.getCreatedSessions(testRunner).size());
        Mockito.verify(wEvtApi, Mockito.never()).EvtClose(ArgumentMatchers.any(HANDLE.class));
    }

    @Test
    public void testStopClosesHandle() {
        testRunner.run(1);
        Mockito.verify(wEvtApi).EvtClose(subscriptionHandle);
    }

    @Test(expected = ProcessException.class)
    public void testScheduleQueueStopThrowsException() throws Throwable {
        ReflectionUtils.invokeMethodsWithAnnotation(OnScheduled.class, evtSubscribe, testRunner.getProcessContext());
        WinNT.HANDLE handle = ConsumeWindowsEventLogTest.mockEventHandles(wEvtApi, kernel32, Arrays.asList("test")).get(0);
        getRenderingCallback().onEvent(DELIVER, null, handle);
        try {
            ReflectionUtils.invokeMethodsWithAnnotation(OnStopped.class, evtSubscribe, testRunner.getProcessContext());
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }

    @Test
    public void testGetSupportedPropertyDescriptors() {
        Assert.assertEquals(PROPERTY_DESCRIPTORS, evtSubscribe.getSupportedPropertyDescriptors());
    }

    @Test
    public void testGetRelationships() {
        Assert.assertEquals(RELATIONSHIPS, evtSubscribe.getRelationships());
    }
}

