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


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class UseCaseCallTest {
    @Test
    public void shouldExecuteUseCaseUsingTheUseCaseHandler() {
        RosieUseCase anyUseCase = Mockito.mock(RosieUseCase.class);
        UseCaseHandler useCaseHandler = Mockito.mock(UseCaseHandler.class);
        UseCaseCall useCaseCall = new UseCaseCall(anyUseCase, useCaseHandler);
        useCaseCall.execute();
        Mockito.verify(useCaseHandler).execute(ArgumentMatchers.eq(anyUseCase), ArgumentMatchers.any(UseCaseParams.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionWhenOnSucessCallbackIsNull() {
        RosieUseCase anyUseCase = Mockito.mock(RosieUseCase.class);
        UseCaseHandler useCaseHandler = Mockito.mock(UseCaseHandler.class);
        UseCaseCall useCaseCall = new UseCaseCall(anyUseCase, useCaseHandler);
        useCaseCall.onSuccess(null).execute();
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionWhenOnErrorCallbackIsNull() {
        RosieUseCase anyUseCase = Mockito.mock(RosieUseCase.class);
        UseCaseHandler useCaseHandler = Mockito.mock(UseCaseHandler.class);
        UseCaseCall useCaseCall = new UseCaseCall(anyUseCase, useCaseHandler);
        useCaseCall.onError(null).execute();
    }
}

