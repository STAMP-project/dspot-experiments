/**
 * Copyright (c) 2010-2018. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.axonframework.eventhandling;


import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class MultiEventHandlerInvokerTest {
    private MultiEventHandlerInvoker testSubject;

    private EventHandlerInvoker mockedEventHandlerInvokerOne = Mockito.mock(EventHandlerInvoker.class);

    private EventHandlerInvoker mockedEventHandlerInvokerTwo = Mockito.mock(EventHandlerInvoker.class);

    private EventMessage<String> testEventMessage;

    private Segment testSegment;

    @Test
    public void testDelegatesReturnsSetDelegates() {
        List<EventHandlerInvoker> result = testSubject.delegates();
        Assert.assertTrue(result.contains(mockedEventHandlerInvokerOne));
        Assert.assertTrue(result.contains(mockedEventHandlerInvokerTwo));
    }

    @Test
    public void testCanHandleCallsCanHandleOnTheFirstDelegateToReturn() {
        testSubject.canHandle(testEventMessage, testSegment);
        Mockito.verify(mockedEventHandlerInvokerOne).canHandle(testEventMessage, testSegment);
        Mockito.verifyZeroInteractions(mockedEventHandlerInvokerTwo);
    }

    @Test
    public void testHandleCallsCanHandleAndHandleOfAllDelegates() throws Exception {
        testSubject.handle(testEventMessage, testSegment);
        Mockito.verify(mockedEventHandlerInvokerOne).canHandle(testEventMessage, testSegment);
        Mockito.verify(mockedEventHandlerInvokerOne).handle(testEventMessage, testSegment);
        Mockito.verify(mockedEventHandlerInvokerTwo).canHandle(testEventMessage, testSegment);
        Mockito.verify(mockedEventHandlerInvokerTwo).handle(testEventMessage, testSegment);
    }

    @Test(expected = RuntimeException.class)
    public void testHandleThrowsExceptionIfDelegatesThrowAnException() throws Exception {
        Mockito.doThrow(new RuntimeException()).when(mockedEventHandlerInvokerTwo).handle(testEventMessage, testSegment);
        testSubject.handle(testEventMessage, testSegment);
    }

    @Test
    public void testSupportResetWhenAllSupport() {
        Mockito.when(mockedEventHandlerInvokerOne.supportsReset()).thenReturn(true);
        Mockito.when(mockedEventHandlerInvokerTwo.supportsReset()).thenReturn(true);
        Assert.assertTrue(testSubject.supportsReset());
    }

    @Test
    public void testSupportResetWhenSomeSupport() {
        Mockito.when(mockedEventHandlerInvokerOne.supportsReset()).thenReturn(true);
        Mockito.when(mockedEventHandlerInvokerTwo.supportsReset()).thenReturn(false);
        Assert.assertTrue(testSubject.supportsReset());
    }

    @Test
    public void testSupportResetWhenNoneSupport() {
        Mockito.when(mockedEventHandlerInvokerOne.supportsReset()).thenReturn(false);
        Mockito.when(mockedEventHandlerInvokerTwo.supportsReset()).thenReturn(false);
        Assert.assertFalse(testSubject.supportsReset());
    }

    @Test
    public void testPerformReset() {
        Mockito.when(mockedEventHandlerInvokerOne.supportsReset()).thenReturn(true);
        Mockito.when(mockedEventHandlerInvokerTwo.supportsReset()).thenReturn(false);
        testSubject.performReset();
        Mockito.verify(mockedEventHandlerInvokerOne, Mockito.times(1)).performReset();
        Mockito.verify(mockedEventHandlerInvokerTwo, Mockito.times(0)).performReset();
    }

    @Test(expected = Exception.class)
    public void testPerformResetThrowsException() {
        Mockito.when(mockedEventHandlerInvokerOne.supportsReset()).thenReturn(true);
        Mockito.when(mockedEventHandlerInvokerTwo.supportsReset()).thenReturn(false);
        Mockito.doThrow().when(mockedEventHandlerInvokerOne).performReset();
        testSubject.performReset();
    }
}

