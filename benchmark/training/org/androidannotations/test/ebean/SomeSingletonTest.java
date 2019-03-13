/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
 * Copyright (C) 2016-2019 the AndroidAnnotations project
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
package org.androidannotations.test.ebean;


import android.content.Context;
import android.view.View;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedNotifier;
import org.androidannotations.test.EmptyActivityWithoutLayout_;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class SomeSingletonTest {
    @Test
    public void getInstanceReturnsSameInstance() {
        EmptyActivityWithoutLayout_ context = setupActivity(EmptyActivityWithoutLayout_.class);
        SomeSingleton_ firstInstance = SomeSingleton_.getInstance_(context);
        SomeSingleton_ secondInstance = SomeSingleton_.getInstance_(context);
        assertThat(firstInstance).isSameAs(secondInstance);
    }

    @Test
    public void viewsAreNotInjected() throws Exception {
        Context context = Mockito.mock(Context.class);
        OnViewChangedNotifier notifier = new OnViewChangedNotifier();
        OnViewChangedNotifier.replaceNotifier(notifier);
        SomeSingleton singleton = SomeSingleton_.getInstance_(context);
        notifier.notifyViewChanged(new HasViews() {
            @Override
            public <T extends View> T internalFindViewById(int id) {
                return ((T) (Mockito.mock(View.class)));
            }
        });
        assertThat(singleton.myTextView).isNull();
        assertThat(singleton.beanWithView.myTextView).isNull();
    }
}

