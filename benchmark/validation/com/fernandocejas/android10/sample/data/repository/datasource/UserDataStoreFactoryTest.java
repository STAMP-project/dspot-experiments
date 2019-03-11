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
package com.fernandocejas.android10.sample.data.repository.datasource;


import com.fernandocejas.android10.sample.data.ApplicationTestCase;
import com.fernandocejas.android10.sample.data.cache.UserCache;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;


public class UserDataStoreFactoryTest extends ApplicationTestCase {
    private static final int FAKE_USER_ID = 11;

    private UserDataStoreFactory userDataStoreFactory;

    @Mock
    private UserCache mockUserCache;

    @Test
    public void testCreateDiskDataStore() {
        BDDMockito.given(mockUserCache.isCached(UserDataStoreFactoryTest.FAKE_USER_ID)).willReturn(true);
        BDDMockito.given(mockUserCache.isExpired()).willReturn(false);
        UserDataStore userDataStore = userDataStoreFactory.create(UserDataStoreFactoryTest.FAKE_USER_ID);
        MatcherAssert.assertThat(userDataStore, CoreMatchers.is(CoreMatchers.notNullValue()));
        MatcherAssert.assertThat(userDataStore, CoreMatchers.is(CoreMatchers.instanceOf(DiskUserDataStore.class)));
        Mockito.verify(mockUserCache).isCached(UserDataStoreFactoryTest.FAKE_USER_ID);
        Mockito.verify(mockUserCache).isExpired();
    }

    @Test
    public void testCreateCloudDataStore() {
        BDDMockito.given(mockUserCache.isExpired()).willReturn(true);
        BDDMockito.given(mockUserCache.isCached(UserDataStoreFactoryTest.FAKE_USER_ID)).willReturn(false);
        UserDataStore userDataStore = userDataStoreFactory.create(UserDataStoreFactoryTest.FAKE_USER_ID);
        MatcherAssert.assertThat(userDataStore, CoreMatchers.is(CoreMatchers.notNullValue()));
        MatcherAssert.assertThat(userDataStore, CoreMatchers.is(CoreMatchers.instanceOf(CloudUserDataStore.class)));
        Mockito.verify(mockUserCache).isExpired();
    }
}

