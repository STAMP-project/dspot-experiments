/**
 * Copyright (C) 2015 Karumi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.karumi.rosie.domain.usecase.error;


import java.lang.reflect.InvocationTargetException;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public class ErrorHandlerTest {
    @Mock
    private Error error;

    @Test
    public void shouldNotifyInvocationTargetExceptionWrappingAnErrorNotHandledExceptionError() {
        OnErrorCallback onErrorCallback = givenAnErrorCallbackThatCapturesError();
        ErrorHandler errorHandler = givenAnErrorHandlerWithOnErrorCallbacks(onErrorCallback);
        InvocationTargetException invocationTargetException = givenAnInvocationExceptionWrappingAnErrorNotHandledException();
        errorHandler.notifyException(invocationTargetException, null);
        Mockito.verify(onErrorCallback).onError(error);
    }

    @Test
    public void shouldNotifyBothRegisteredOnErrorCallbacksIfNoneCapturesError() {
        OnErrorCallback firstOnErrorCallback = givenAnErrorCallbackThatDoesNotCaptureError();
        OnErrorCallback secondOnErrorCallback = givenAnErrorCallbackThatDoesNotCaptureError();
        ErrorHandler errorHandler = givenAnErrorHandlerWithOnErrorCallbacks(firstOnErrorCallback, secondOnErrorCallback);
        InvocationTargetException invocationTargetException = givenAnInvocationExceptionWrappingAnErrorNotHandledException();
        errorHandler.notifyException(invocationTargetException, null);
        Mockito.verify(secondOnErrorCallback).onError(error);
    }

    @Test
    public void shouldNotNotifySecondRegisteredOnErrorCallbackIfFirstOneCapturesError() {
        OnErrorCallback firstOnErrorCallback = givenAnErrorCallbackThatCapturesError();
        OnErrorCallback secondOnErrorCallback = givenAnErrorCallbackThatDoesNotCaptureError();
        ErrorHandler errorHandler = givenAnErrorHandlerWithOnErrorCallbacks(firstOnErrorCallback, secondOnErrorCallback);
        InvocationTargetException invocationTargetException = givenAnInvocationExceptionWrappingAnErrorNotHandledException();
        errorHandler.notifyException(invocationTargetException, null);
        Mockito.verify(secondOnErrorCallback, Mockito.never()).onError(error);
    }
}

