/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.agera.content;


import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import com.google.android.agera.ActivationHandler;
import com.google.android.agera.content.test.matchers.HasPrivateConstructor;
import com.google.android.agera.content.test.matchers.UpdatableUpdated;
import com.google.android.agera.content.test.mocks.MockUpdatable;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@Config(manifest = NONE)
@RunWith(RobolectricTestRunner.class)
public final class ContentObservablesTest {
    private static final String TEST_KEY = "test key";

    private static final String NOT_TEST_KEY = "not test key";

    private static final String TEST_ACTION = "TEST_ACTION";

    private static final String PRIMARY_ACTION = "PRIMARY_ACTION";

    private MockUpdatable updatable;

    private MockUpdatable secondUpdatable;

    private ArgumentCaptor<OnSharedPreferenceChangeListener> sharedPreferenceListenerCaptor;

    @Mock
    private ActivationHandler mockActivationHandler;

    @Mock
    private SharedPreferences sharedPreferences;

    @Test
    public void shouldUpdateSharedPreferencesWhenKeyChanges() {
        updatable.addToObservable(ContentObservables.sharedPreferencesObservable(sharedPreferences, ContentObservablesTest.TEST_KEY));
        sharedPreferenceListenerCaptor.getValue().onSharedPreferenceChanged(sharedPreferences, ContentObservablesTest.TEST_KEY);
        MatcherAssert.assertThat(updatable, UpdatableUpdated.wasUpdated());
    }

    @Test
    public void shouldNotUpdateSharedPreferencesWhenOtherKeyChanges() {
        updatable.addToObservable(ContentObservables.sharedPreferencesObservable(sharedPreferences, ContentObservablesTest.TEST_KEY));
        sharedPreferenceListenerCaptor.getValue().onSharedPreferenceChanged(sharedPreferences, ContentObservablesTest.NOT_TEST_KEY);
        MatcherAssert.assertThat(updatable, UpdatableUpdated.wasNotUpdated());
    }

    @Test
    public void shouldBeAbleToObserveBroadcasts() {
        updatable.addToObservable(ContentObservables.broadcastObservable(ContentObservablesTest.getApplication(), ContentObservablesTest.TEST_ACTION));
    }

    @Test
    public void shouldUpdateForBroadcast() {
        updatable.addToObservable(ContentObservables.broadcastObservable(ContentObservablesTest.getApplication(), ContentObservablesTest.TEST_ACTION));
        sendBroadcast(new Intent(ContentObservablesTest.TEST_ACTION));
        MatcherAssert.assertThat(updatable, UpdatableUpdated.wasUpdated());
    }

    @Test
    public void shouldUpdateForSecondaryBroadcast() {
        updatable.addToObservable(ContentObservables.broadcastObservable(ContentObservablesTest.getApplication(), ContentObservablesTest.PRIMARY_ACTION, ContentObservablesTest.TEST_ACTION));
        sendBroadcast(new Intent(ContentObservablesTest.TEST_ACTION));
        MatcherAssert.assertThat(updatable, UpdatableUpdated.wasUpdated());
    }

    @Test
    public void shouldNotGetUpdateFromBroadcastForEmptyFilter() {
        updatable.addToObservable(ContentObservables.broadcastObservable(ContentObservablesTest.getApplication()));
        sendBroadcast(new Intent(ContentObservablesTest.TEST_ACTION));
        MatcherAssert.assertThat(updatable, UpdatableUpdated.wasNotUpdated());
    }

    @Test
    public void shouldHavePrivateConstructor() {
        MatcherAssert.assertThat(ContentObservables.class, HasPrivateConstructor.hasPrivateConstructor());
    }
}

