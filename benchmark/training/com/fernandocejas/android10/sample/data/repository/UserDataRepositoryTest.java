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
package com.fernandocejas.android10.sample.data.repository;


import com.fernandocejas.android10.sample.data.entity.UserEntity;
import com.fernandocejas.android10.sample.data.entity.mapper.UserEntityDataMapper;
import com.fernandocejas.android10.sample.data.repository.datasource.UserDataStore;
import com.fernandocejas.android10.sample.data.repository.datasource.UserDataStoreFactory;
import com.fernandocejas.android10.sample.domain.User;
import io.reactivex.Observable;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class UserDataRepositoryTest {
    private static final int FAKE_USER_ID = 123;

    private UserDataRepository userDataRepository;

    @Mock
    private UserDataStoreFactory mockUserDataStoreFactory;

    @Mock
    private UserEntityDataMapper mockUserEntityDataMapper;

    @Mock
    private UserDataStore mockUserDataStore;

    @Mock
    private UserEntity mockUserEntity;

    @Mock
    private User mockUser;

    @Test
    public void testGetUsersHappyCase() {
        List<UserEntity> usersList = new ArrayList<>();
        usersList.add(new UserEntity());
        BDDMockito.given(mockUserDataStore.userEntityList()).willReturn(Observable.just(usersList));
        userDataRepository.users();
        Mockito.verify(mockUserDataStoreFactory).createCloudDataStore();
        Mockito.verify(mockUserDataStore).userEntityList();
    }

    @Test
    public void testGetUserHappyCase() {
        UserEntity userEntity = new UserEntity();
        BDDMockito.given(mockUserDataStore.userEntityDetails(UserDataRepositoryTest.FAKE_USER_ID)).willReturn(Observable.just(userEntity));
        userDataRepository.user(UserDataRepositoryTest.FAKE_USER_ID);
        Mockito.verify(mockUserDataStoreFactory).create(UserDataRepositoryTest.FAKE_USER_ID);
        Mockito.verify(mockUserDataStore).userEntityDetails(UserDataRepositoryTest.FAKE_USER_ID);
    }
}

