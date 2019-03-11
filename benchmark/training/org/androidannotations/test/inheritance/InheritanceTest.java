/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed To in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.androidannotations.test.inheritance;


import android.app.Activity;
import android.content.Context;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedNotifier;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class InheritanceTest {
    @Test
    public void afterInjectMotherCallsFirst() {
        Child child = Child_.getInstance_(Mockito.mock(Context.class));
        assertThat(child.motherInitWasCalled).isTrue();
    }

    @Test
    public void afterViewsMotherCallsFirst() {
        OnViewChangedNotifier notifier = new OnViewChangedNotifier();
        OnViewChangedNotifier.replaceNotifier(notifier);
        Child_ child = Child_.getInstance_(Mockito.mock(Activity.class));
        notifier.notifyViewChanged(Mockito.mock(HasViews.class));
        assertThat(child.motherInitViewsWasCalled).isTrue();
    }
}

