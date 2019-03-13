/**
 * Copyright (C) 2015 Fernando Cejas Open Source Project
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
package com.fernandocejas.android10.sample.domain.interactor;


import com.fernandocejas.android10.sample.domain.executor.PostExecutionThread;
import com.fernandocejas.android10.sample.domain.executor.ThreadExecutor;
import com.fernandocejas.android10.sample.domain.interactor.GetUserDetails.Params;
import com.fernandocejas.android10.sample.domain.repository.UserRepository;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class GetUserDetailsTest {
    private static final int USER_ID = 123;

    private GetUserDetails getUserDetails;

    @Mock
    private UserRepository mockUserRepository;

    @Mock
    private ThreadExecutor mockThreadExecutor;

    @Mock
    private PostExecutionThread mockPostExecutionThread;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testGetUserDetailsUseCaseObservableHappyCase() {
        getUserDetails.buildUseCaseObservable(Params.forUser(GetUserDetailsTest.USER_ID));
        Mockito.verify(mockUserRepository).user(GetUserDetailsTest.USER_ID);
        Mockito.verifyNoMoreInteractions(mockUserRepository);
        Mockito.verifyZeroInteractions(mockPostExecutionThread);
        Mockito.verifyZeroInteractions(mockThreadExecutor);
    }

    @Test
    public void testShouldFailWhenNoOrEmptyParameters() {
        expectedException.expect(NullPointerException.class);
        getUserDetails.buildUseCaseObservable(null);
    }
}

