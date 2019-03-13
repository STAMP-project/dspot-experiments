/**
 * The MIT License
 * Copyright (c) 2014-2016 Ilkka Sepp?l?
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.iluwatar.halfsynchalfasync;


import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;


/**
 * Date: 12/12/15 - 11:15 PM
 *
 * @author Jeroen Meulemeester
 */
public class AsynchronousServiceTest {
    private AsynchronousService service;

    private AsyncTask<Object> task;

    @Test
    public void testPerfectExecution() throws Exception {
        final Object result = new Object();
        Mockito.when(task.call()).thenReturn(result);
        service.execute(task);
        Mockito.verify(task, Mockito.timeout(2000)).onPostCall(ArgumentMatchers.eq(result));
        final InOrder inOrder = Mockito.inOrder(task);
        inOrder.verify(task, Mockito.times(1)).onPreCall();
        inOrder.verify(task, Mockito.times(1)).call();
        inOrder.verify(task, Mockito.times(1)).onPostCall(ArgumentMatchers.eq(result));
        Mockito.verifyNoMoreInteractions(task);
    }

    @Test
    public void testCallException() throws Exception {
        final IOException exception = new IOException();
        Mockito.when(task.call()).thenThrow(exception);
        service.execute(task);
        Mockito.verify(task, Mockito.timeout(2000)).onError(ArgumentMatchers.eq(exception));
        final InOrder inOrder = Mockito.inOrder(task);
        inOrder.verify(task, Mockito.times(1)).onPreCall();
        inOrder.verify(task, Mockito.times(1)).call();
        inOrder.verify(task, Mockito.times(1)).onError(exception);
        Mockito.verifyNoMoreInteractions(task);
    }

    @Test
    public void testPreCallException() {
        final IllegalStateException exception = new IllegalStateException();
        Mockito.doThrow(exception).when(task).onPreCall();
        service.execute(task);
        Mockito.verify(task, Mockito.timeout(2000)).onError(ArgumentMatchers.eq(exception));
        final InOrder inOrder = Mockito.inOrder(task);
        inOrder.verify(task, Mockito.times(1)).onPreCall();
        inOrder.verify(task, Mockito.times(1)).onError(exception);
        Mockito.verifyNoMoreInteractions(task);
    }
}

