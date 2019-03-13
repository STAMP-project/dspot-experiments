/**
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.web.method.support;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.core.MethodParameter;

import static org.mockito.Mockito.verify;


/**
 * Test fixture with {@link HandlerMethodReturnValueHandlerComposite}.
 *
 * @author Rossen Stoyanchev
 */
@SuppressWarnings("unused")
public class HandlerMethodReturnValueHandlerCompositeTests {
    private HandlerMethodReturnValueHandlerComposite handlers;

    private HandlerMethodReturnValueHandler integerHandler;

    ModelAndViewContainer mavContainer;

    private MethodParameter integerType;

    private MethodParameter stringType;

    @Test
    public void supportsReturnType() throws Exception {
        Assert.assertTrue(this.handlers.supportsReturnType(this.integerType));
        Assert.assertFalse(this.handlers.supportsReturnType(this.stringType));
    }

    @Test
    public void handleReturnValue() throws Exception {
        this.handlers.handleReturnValue(55, this.integerType, this.mavContainer, null);
        verify(this.integerHandler).handleReturnValue(55, this.integerType, this.mavContainer, null);
    }

    @Test
    public void handleReturnValueWithMultipleHandlers() throws Exception {
        HandlerMethodReturnValueHandler anotherIntegerHandler = Mockito.mock(HandlerMethodReturnValueHandler.class);
        Mockito.when(anotherIntegerHandler.supportsReturnType(this.integerType)).thenReturn(true);
        this.handlers.handleReturnValue(55, this.integerType, this.mavContainer, null);
        verify(this.integerHandler).handleReturnValue(55, this.integerType, this.mavContainer, null);
        Mockito.verifyNoMoreInteractions(anotherIntegerHandler);
    }

    // SPR-13083
    @Test
    public void handleReturnValueWithAsyncHandler() throws Exception {
        HandlerMethodReturnValueHandlerCompositeTests.Promise<Integer> promise = new HandlerMethodReturnValueHandlerCompositeTests.Promise<>();
        MethodParameter promiseType = new MethodParameter(getClass().getDeclaredMethod("handlePromise"), (-1));
        HandlerMethodReturnValueHandler responseBodyHandler = Mockito.mock(HandlerMethodReturnValueHandler.class);
        Mockito.when(responseBodyHandler.supportsReturnType(promiseType)).thenReturn(true);
        this.handlers.addHandler(responseBodyHandler);
        AsyncHandlerMethodReturnValueHandler promiseHandler = Mockito.mock(AsyncHandlerMethodReturnValueHandler.class);
        Mockito.when(promiseHandler.supportsReturnType(promiseType)).thenReturn(true);
        Mockito.when(promiseHandler.isAsyncReturnValue(promise, promiseType)).thenReturn(true);
        this.handlers.addHandler(promiseHandler);
        this.handlers.handleReturnValue(promise, promiseType, this.mavContainer, null);
        verify(promiseHandler).isAsyncReturnValue(promise, promiseType);
        verify(promiseHandler).supportsReturnType(promiseType);
        verify(promiseHandler).handleReturnValue(promise, promiseType, this.mavContainer, null);
        Mockito.verifyNoMoreInteractions(promiseHandler);
        Mockito.verifyNoMoreInteractions(responseBodyHandler);
    }

    @Test(expected = IllegalArgumentException.class)
    public void noSuitableReturnValueHandler() throws Exception {
        this.handlers.handleReturnValue("value", this.stringType, null, null);
    }

    private static class Promise<T> {}
}

