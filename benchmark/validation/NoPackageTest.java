/**
 * Copyright (C) 2017 The Android Open Source Project
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


import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;


@RunWith(JUnit4.class)
public class NoPackageTest {
    private LifecycleOwner mLifecycleOwner;

    private Lifecycle mLifecycle;

    private LifecycleRegistry mRegistry;

    @Test
    public void testNoPackage() {
        NoPackageObserver observer = Mockito.mock(NoPackageObserver.class);
        mRegistry.addObserver(observer);
        mRegistry.handleLifecycleEvent(Event.ON_CREATE);
        Mockito.verify(observer).onCreate();
    }
}

