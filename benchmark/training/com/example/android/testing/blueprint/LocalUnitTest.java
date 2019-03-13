/**
 * Copyright 2015, The Android Open Source Project
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
package com.example.android.testing.blueprint;


import android.app.Activity;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import java.io.File;
import junit.framework.TestCase;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * A test class with unit tests that mock classes from the Android framework.
 */
public class LocalUnitTest {
    @Test
    public void mockFinalMethod() {
        Activity activity = Mockito.mock(Activity.class);
        Application app = Mockito.mock(Application.class);
        Mockito.when(activity.getApplication()).thenReturn(app);
        Assert.assertThat(app, CoreMatchers.is(CoreMatchers.equalTo(activity.getApplication())));
        Mockito.verify(activity).getApplication();
        Mockito.verifyNoMoreInteractions(activity);
    }

    @Test
    public void mockFinalClass() {
        BluetoothAdapter adapter = Mockito.mock(BluetoothAdapter.class);
        Mockito.when(adapter.isEnabled()).thenReturn(true);
        TestCase.assertTrue(adapter.isEnabled());
        Mockito.verify(adapter).isEnabled();
        Mockito.verifyNoMoreInteractions(adapter);
    }

    @Test
    public void loadTestDataFromResources() {
        File helloBleprintJson = new File(getClass().getResource("/helloBlueprint.json").getPath());
        Assert.assertThat(helloBleprintJson, CoreMatchers.notNullValue());
    }
}

