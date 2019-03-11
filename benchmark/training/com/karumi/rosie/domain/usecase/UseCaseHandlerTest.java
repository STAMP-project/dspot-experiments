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
package com.karumi.rosie.domain.usecase;


import com.karumi.rosie.domain.usecase.annotation.Success;
import com.karumi.rosie.domain.usecase.annotation.UseCase;
import com.karumi.rosie.domain.usecase.callback.OnSuccessCallback;
import com.karumi.rosie.domain.usecase.error.ErrorHandler;
import com.karumi.rosie.domain.usecase.error.OnErrorCallback;
import com.karumi.rosie.doubles.FakeCallbackScheduler;
import com.karumi.rosie.doubles.NetworkError;
import com.karumi.rosie.testutils.FakeTaskScheduler;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class UseCaseHandlerTest {
    private static final int ANY_RETURN_VALUE = 2;

    private static final int ANY_INT_PARAM = 1;

    public static final String ANY_EXECUTION_NAME = "anyExecution";

    public static final String NO_EXIST_METHOD_NAME = "noExistMethod";

    public static final String ANY_PARAM1 = "param1";

    public static final int ANY_PARAM2 = 2;

    @Test
    public void testExecuteAnyObject() throws Exception {
        TaskScheduler taskScheduler = Mockito.mock(TaskScheduler.class);
        UseCaseHandlerTest.EmptyUseCase anyUseCase = new UseCaseHandlerTest.EmptyUseCase();
        UseCaseParams emptyParams = givenEmptyUseCaseParms();
        ErrorHandler errorHandler = new ErrorHandler(new FakeCallbackScheduler());
        UseCaseHandler useCaseHandler = new UseCaseHandler(taskScheduler, errorHandler);
        useCaseHandler.execute(anyUseCase, emptyParams);
        Mockito.verify(taskScheduler, Mockito.only()).execute(ArgumentMatchers.any(UseCaseWrapper.class));
    }

    @Test
    public void testExecuteFailNotAnyUseCase() throws Exception {
        TaskScheduler taskScheduler = Mockito.mock(TaskScheduler.class);
        UseCaseHandlerTest.NoUseCase noUseCase = new UseCaseHandlerTest.NoUseCase();
        UseCaseParams emptyUseCaseParms = givenEmptyUseCaseParms();
        ErrorHandler errorHandler = new ErrorHandler(new FakeCallbackScheduler());
        UseCaseHandler useCaseHandler = new UseCaseHandler(taskScheduler, errorHandler);
        boolean exception = false;
        try {
            useCaseHandler.execute(noUseCase, emptyUseCaseParms);
        } catch (IllegalArgumentException e) {
            exception = true;
        }
        Assert.assertTrue(exception);
        Mockito.verify(taskScheduler, Mockito.never()).execute(ArgumentMatchers.any(UseCaseWrapper.class));
    }

    @Test
    public void testExecuteWithMethodName() throws Exception {
        TaskScheduler taskScheduler = Mockito.mock(TaskScheduler.class);
        UseCaseHandlerTest.AnyUseCase anyUseCase = new UseCaseHandlerTest.AnyUseCase();
        ErrorHandler errorHandler = new ErrorHandler(new FakeCallbackScheduler());
        UseCaseHandler useCaseHandler = new UseCaseHandler(taskScheduler, errorHandler);
        UseCaseCall useCaseCall = new UseCaseCall(anyUseCase, useCaseHandler);
        useCaseCall.useCaseName(UseCaseHandlerTest.ANY_EXECUTION_NAME).execute();
        Mockito.verify(taskScheduler, Mockito.only()).execute(ArgumentMatchers.any(UseCaseWrapper.class));
    }

    @Test
    public void testExecuteFailsWithWrongMethodName() throws Exception {
        TaskScheduler taskScheduler = Mockito.mock(TaskScheduler.class);
        UseCaseHandlerTest.AnyUseCase anyUseCase = new UseCaseHandlerTest.AnyUseCase();
        ErrorHandler errorHandler = new ErrorHandler(new FakeCallbackScheduler());
        UseCaseHandler useCaseHandler = new UseCaseHandler(taskScheduler, errorHandler);
        UseCaseCall useCaseCall = new UseCaseCall(anyUseCase, useCaseHandler);
        boolean error = false;
        try {
            useCaseCall.useCaseName(UseCaseHandlerTest.NO_EXIST_METHOD_NAME).execute();
        } catch (IllegalArgumentException e) {
            error = true;
        }
        Assert.assertTrue(error);
        Mockito.verify(taskScheduler, Mockito.never()).execute(ArgumentMatchers.any(UseCaseWrapper.class));
    }

    @Test
    public void testExecuteWithArgs() throws Exception {
        TaskScheduler taskScheduler = Mockito.mock(TaskScheduler.class);
        UseCaseHandlerTest.AnyUseCase anyUseCase = new UseCaseHandlerTest.AnyUseCase();
        ErrorHandler errorHandler = new ErrorHandler(new FakeCallbackScheduler());
        UseCaseHandler useCaseHandler = new UseCaseHandler(taskScheduler, errorHandler);
        String anyArg1 = UseCaseHandlerTest.ANY_PARAM1;
        int anyArg2 = UseCaseHandlerTest.ANY_PARAM2;
        UseCaseCall useCaseCall = new UseCaseCall(anyUseCase, useCaseHandler);
        useCaseCall.args(anyArg1, anyArg2).execute();
        Mockito.verify(taskScheduler, Mockito.only()).execute(ArgumentMatchers.any(UseCaseWrapper.class));
    }

    @Test
    public void testExecuteAmbigous() throws Exception {
        TaskScheduler taskScheduler = Mockito.mock(TaskScheduler.class);
        UseCaseHandlerTest.AmbiguousUseCase ambigousUseCase = new UseCaseHandlerTest.AmbiguousUseCase();
        ErrorHandler errorHandler = new ErrorHandler(new FakeCallbackScheduler());
        UseCaseHandler useCaseHandler = new UseCaseHandler(taskScheduler, errorHandler);
        UseCaseCall useCaseCall = new UseCaseCall(ambigousUseCase, useCaseHandler);
        boolean exception = false;
        try {
            useCaseCall.args(UseCaseHandlerTest.ANY_PARAM1, UseCaseHandlerTest.ANY_PARAM2).execute();
        } catch (IllegalArgumentException e) {
            exception = true;
        }
        Assert.assertTrue(exception);
        Mockito.verify(taskScheduler, Mockito.never()).execute(ArgumentMatchers.any(UseCaseWrapper.class));
    }

    @Test
    public void testExecuteNoAmbigous() throws Exception {
        TaskScheduler taskScheduler = Mockito.mock(TaskScheduler.class);
        UseCaseHandlerTest.AmbiguousUseCase ambigousUseCase = new UseCaseHandlerTest.AmbiguousUseCase();
        ErrorHandler errorHandler = new ErrorHandler(new FakeCallbackScheduler());
        UseCaseHandler useCaseHandler = new UseCaseHandler(taskScheduler, errorHandler);
        UseCaseCall useCaseCall = new UseCaseCall(ambigousUseCase, useCaseHandler);
        useCaseCall.args(UseCaseHandlerTest.ANY_PARAM1, UseCaseHandlerTest.ANY_PARAM2).useCaseName("method1").execute();
        Mockito.verify(taskScheduler, Mockito.only()).execute(ArgumentMatchers.any(UseCaseWrapper.class));
    }

    @Test
    public void onSuccessCallbackShouldBeCalledWithSuccessArgs() {
        FakeTaskScheduler taskScheduler = new FakeTaskScheduler();
        UseCaseHandlerTest.EmptyResponseUseCase anyUseCase = new UseCaseHandlerTest.EmptyResponseUseCase();
        UseCaseHandlerTest.EmptyOnSuccess onSuccessCallback = new UseCaseHandlerTest.EmptyOnSuccess();
        ErrorHandler errorHandler = new ErrorHandler(new FakeCallbackScheduler());
        UseCaseHandler useCaseHandler = new UseCaseHandler(taskScheduler, errorHandler);
        UseCaseCall useCaseCall = new UseCaseCall(anyUseCase, useCaseHandler);
        useCaseCall.onSuccess(onSuccessCallback).execute();
        Assert.assertTrue(onSuccessCallback.isSuccess());
    }

    @Test
    public void completeCallbackShouldBeCalledWithSuccessArgs() {
        FakeTaskScheduler taskScheduler = new FakeTaskScheduler();
        UseCaseHandlerTest.AnyUseCase anyUseCase = new UseCaseHandlerTest.AnyUseCase();
        UseCaseHandlerTest.AnyOnSuccess onSuccessCallback = new UseCaseHandlerTest.AnyOnSuccess();
        ErrorHandler errorHandler = new ErrorHandler(new FakeCallbackScheduler());
        UseCaseHandler useCaseHandler = new UseCaseHandler(taskScheduler, errorHandler);
        UseCaseCall useCaseCall = new UseCaseCall(anyUseCase, useCaseHandler);
        useCaseCall.useCaseName("anyExecution").onSuccess(onSuccessCallback).execute();
        Assert.assertEquals(UseCaseHandlerTest.ANY_RETURN_VALUE, onSuccessCallback.getValue());
    }

    @Test
    public void onSuccessCallbackShouldBeCalledWithSuccessArgsAndDowncastingResponse() {
        FakeTaskScheduler taskScheduler = new FakeTaskScheduler();
        UseCaseHandlerTest.AnyUseCase anyUseCase = new UseCaseHandlerTest.AnyUseCase();
        UseCaseHandlerTest.AnyOnSuccessWithDowncast onSuccessCallback = new UseCaseHandlerTest.AnyOnSuccessWithDowncast();
        ErrorHandler errorHandler = new ErrorHandler(new FakeCallbackScheduler());
        UseCaseHandler useCaseHandler = new UseCaseHandler(taskScheduler, errorHandler);
        UseCaseCall useCaseCall = new UseCaseCall(anyUseCase, useCaseHandler);
        useCaseCall.useCaseName("downcastResponse").onSuccess(onSuccessCallback).execute();
        Assert.assertNotNull(onSuccessCallback.getValue());
    }

    @Test
    public void completeCallbackShouldNotBeExecutedWhenNotMatchArgs() {
        FakeTaskScheduler taskScheduler = new FakeTaskScheduler();
        UseCaseHandlerTest.AnyUseCase anyUseCase = new UseCaseHandlerTest.AnyUseCase();
        UseCaseHandlerTest.EmptyOnSuccess onSuccessCallback = new UseCaseHandlerTest.EmptyOnSuccess();
        ErrorHandler errorHandler = new ErrorHandler(new FakeCallbackScheduler());
        UseCaseHandler useCaseHandler = new UseCaseHandler(taskScheduler, errorHandler);
        UseCaseCall useCaseCall = new UseCaseCall(anyUseCase, useCaseHandler);
        useCaseCall.useCaseName("anyExecution").onSuccess(onSuccessCallback).execute();
        Assert.assertFalse(onSuccessCallback.isSuccess());
    }

    @Test
    public void shouldCallErrorOnErrorWhenUseCaseInvokeAnError() {
        FakeTaskScheduler taskScheduler = new FakeTaskScheduler();
        UseCaseHandlerTest.ErrorUseCase errorUseCase = new UseCaseHandlerTest.ErrorUseCase();
        ErrorHandler errorHandler = new ErrorHandler(new FakeCallbackScheduler());
        UseCaseHandler useCaseHandler = new UseCaseHandler(taskScheduler, errorHandler);
        OnErrorCallback errorCallback = Mockito.spy(onErrorCallback);
        UseCaseCall useCaseCall = new UseCaseCall(errorUseCase, useCaseHandler);
        useCaseCall.useCaseName("customError").onError(errorCallback).execute();
        Mockito.verify(errorCallback).onError(ArgumentMatchers.any(Error.class));
    }

    @Test
    public void shouldCallOnErrorCallbackWhenUseCaseThrowAnUnhandledException() {
        FakeTaskScheduler taskScheduler = new FakeTaskScheduler();
        UseCaseHandlerTest.ErrorUseCase errorUseCase = new UseCaseHandlerTest.ErrorUseCase();
        ErrorHandler errorHandler = new ErrorHandler(new FakeCallbackScheduler());
        UseCaseHandler useCaseHandler = new UseCaseHandler(taskScheduler, errorHandler);
        OnErrorCallback errorCallback = Mockito.spy(onErrorCallback);
        UseCaseCall useCaseCall = new UseCaseCall(errorUseCase, useCaseHandler);
        useCaseCall.useCaseName("launchException").onError(errorCallback).execute();
        Mockito.verify(errorCallback).onError(ArgumentMatchers.any(Error.class));
    }

    @Test
    public void shouldCallErrorHandlerWhenUseCaseInvokeAnError() {
        FakeTaskScheduler taskScheduler = new FakeTaskScheduler();
        UseCaseHandlerTest.ErrorUseCase errorUseCase = new UseCaseHandlerTest.ErrorUseCase();
        ErrorHandler errorHandler = Mockito.mock(ErrorHandler.class);
        UseCaseHandler useCaseHandler = new UseCaseHandler(taskScheduler, errorHandler);
        UseCaseCall useCaseCall = new UseCaseCall(errorUseCase, useCaseHandler);
        useCaseCall.useCaseName("customError").execute();
        notifyError(ArgumentMatchers.any(Error.class), ArgumentMatchers.eq(((OnErrorCallback) (null))));
    }

    @Test
    public void shouldNotifyErrorHandlerWhenUseCaseOnErrorCallbackDoesNotExist() {
        FakeTaskScheduler taskScheduler = new FakeTaskScheduler();
        UseCaseHandlerTest.ErrorUseCase errorUseCase = new UseCaseHandlerTest.ErrorUseCase();
        ErrorHandler errorHandler = Mockito.mock(ErrorHandler.class);
        UseCaseHandler useCaseHandler = new UseCaseHandler(taskScheduler, errorHandler);
        UseCaseCall useCaseCall = new UseCaseCall(errorUseCase, useCaseHandler);
        useCaseCall.useCaseName("customError").onError(specificErrorCallback).execute();
        notifyError(ArgumentMatchers.any(Error.class), ArgumentMatchers.eq(specificErrorCallback));
    }

    @Test
    public void shouldCallErrorHandlerErrorWhenUseCaseThrowsAnException() {
        FakeTaskScheduler taskScheduler = new FakeTaskScheduler();
        UseCaseHandlerTest.ErrorUseCase errorUseCase = new UseCaseHandlerTest.ErrorUseCase();
        ErrorHandler errorHandler = Mockito.mock(ErrorHandler.class);
        UseCaseHandler useCaseHandler = new UseCaseHandler(taskScheduler, errorHandler);
        UseCaseCall useCaseCall = new UseCaseCall(errorUseCase, useCaseHandler);
        useCaseCall.useCaseName("launchException").execute();
        Mockito.verify(errorHandler).notifyException(ArgumentMatchers.any(Exception.class), ArgumentMatchers.eq(((OnErrorCallback) (null))));
    }

    @Test
    public void shouldThrowExceptionIfUseCaseNotifiesSuccessButThereIsNoOnSuccessCallback() {
        FakeTaskScheduler taskScheduler = new FakeTaskScheduler();
        UseCaseHandlerTest.SuccessUseCase successUseCase = new UseCaseHandlerTest.SuccessUseCase();
        ErrorHandler errorHandler = Mockito.mock(ErrorHandler.class);
        UseCaseHandler useCaseHandler = new UseCaseHandler(taskScheduler, errorHandler);
        UseCaseCall useCaseCall = new UseCaseCall(successUseCase, useCaseHandler);
        useCaseCall.execute();
        Mockito.verify(errorHandler).notifyException(ArgumentMatchers.any(IllegalStateException.class), ArgumentMatchers.eq(((OnErrorCallback) (null))));
    }

    @Test
    public void shouldThrowExceptionIfTheOnSuccessCallbackHasNoMethodsWithSuccessAnnotations() {
        FakeTaskScheduler taskScheduler = new FakeTaskScheduler();
        UseCaseHandlerTest.SuccessUseCase successUseCase = new UseCaseHandlerTest.SuccessUseCase();
        ErrorHandler errorHandler = Mockito.mock(ErrorHandler.class);
        UseCaseHandler useCaseHandler = new UseCaseHandler(taskScheduler, errorHandler);
        UseCaseCall useCaseCall = new UseCaseCall(successUseCase, useCaseHandler);
        useCaseCall.onSuccess(new OnSuccessCallback() {}).execute();
        Mockito.verify(errorHandler).notifyException(ArgumentMatchers.any(IllegalStateException.class), ArgumentMatchers.eq(((OnErrorCallback) (null))));
    }

    @Test
    public void shouldSupportNullUseCaseParams() {
        FakeTaskScheduler taskScheduler = new FakeTaskScheduler();
        UseCaseHandlerTest.AnyUseCase useCase = new UseCaseHandlerTest.AnyUseCase();
        ErrorHandler errorHandler = Mockito.mock(ErrorHandler.class);
        UseCaseHandler useCaseHandler = new UseCaseHandler(taskScheduler, errorHandler);
        UseCaseHandlerTest.AnyOnSuccess onSuccessCallback = new UseCaseHandlerTest.AnyOnSuccess();
        UseCaseCall useCaseCall = new UseCaseCall(useCase, useCaseHandler);
        useCaseCall.args(null, UseCaseHandlerTest.ANY_INT_PARAM).onSuccess(onSuccessCallback).execute();
        Assert.assertEquals(UseCaseHandlerTest.ANY_INT_PARAM, onSuccessCallback.getValue());
    }

    private OnErrorCallback onErrorCallback = new OnErrorCallback<Error>() {
        @Override
        public boolean onError(Error error) {
            return false;
        }
    };

    private OnErrorCallback specificErrorCallback = new OnErrorCallback<NetworkError>() {
        @Override
        public boolean onError(NetworkError error) {
            return false;
        }
    };

    private class AnyOnSuccess implements OnSuccessCallback {
        private int value;

        @Success
        public void onSuccess(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    private class AnyOnSuccessWithDowncast implements OnSuccessCallback {
        private List<String> value;

        @Success
        public void onSucess(List<String> value) {
            this.value = value;
        }

        public List<String> getValue() {
            return value;
        }
    }

    private class EmptyOnSuccess implements OnSuccessCallback {
        private boolean success = false;

        @Success
        public void onSuccess() {
            success = true;
        }

        public boolean isSuccess() {
            return success;
        }
    }

    private class AnyUseCase extends RosieUseCase {
        AnyUseCase() {
            setCallbackScheduler(new FakeCallbackScheduler());
        }

        @UseCase(name = "anyExecution")
        public void anyExecution() {
            notifySuccess(UseCaseHandlerTest.ANY_RETURN_VALUE);
        }

        @UseCase(name = "downcastResponse")
        public void anyExecutionDowncast() {
            notifySuccess(new ArrayList<String>());
        }

        @UseCase
        public void methodWithArgs(String arg1, int arg2) {
            notifySuccess(arg2);
        }
    }

    private class EmptyUseCase extends RosieUseCase {
        EmptyUseCase() {
        }

        @UseCase
        public void anyExecution() {
        }
    }

    private class AmbiguousUseCase extends RosieUseCase {
        @UseCase(name = "method1")
        public void method1(String arg1, int arg2) {
        }

        @UseCase
        public void method2(String arg1, int arg2) {
        }
    }

    private class NoUseCase extends RosieUseCase {}

    private class EmptyResponseUseCase extends RosieUseCase {
        public EmptyResponseUseCase() {
            setCallbackScheduler(new FakeCallbackScheduler());
        }

        @UseCase
        public void method2() {
            notifySuccess();
        }
    }

    private class ErrorUseCase extends RosieUseCase {
        public ErrorUseCase() {
            setCallbackScheduler(new FakeCallbackScheduler());
        }

        @UseCase(name = "customError")
        public void errorMethod() throws Exception {
            notifyError(new Error("error network", new Exception()));
        }

        @UseCase(name = "launchException")
        public void launchExceptionMethod() throws Exception {
            throw new Exception("exception");
        }
    }

    private class SuccessUseCase extends RosieUseCase {
        public SuccessUseCase() {
            setCallbackScheduler(new FakeCallbackScheduler());
        }

        @UseCase
        public void execute() {
            notifySuccess();
        }
    }
}

