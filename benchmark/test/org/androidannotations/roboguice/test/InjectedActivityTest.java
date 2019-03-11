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
package org.androidannotations.roboguice.test;


import R.id.injected_text_view;
import android.content.Context;
import android.widget.TextView;
import com.google.inject.Inject;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import roboguice.RoboGuice;


@RunWith(RobolectricTestRunner.class)
public class InjectedActivityTest {
    @Inject
    Context context;

    ActivityWithRoboGuice_ injectedActivity;

    @Inject
    Counter fieldCounter;

    @Inject
    FakeDateProvider fakeDateProvider;

    @Test
    public void shouldAssignStringToTextView() throws Exception {
        TextView injectedTextView = ((TextView) (injectedActivity.findViewById(injected_text_view)));
        Assert.assertThat(injectedTextView.getText().toString(), CoreMatchers.equalTo("Roboguice Activity tested with Robolectric - December 8, 2010"));
    }

    @Test
    public void shouldInjectSingletons() throws Exception {
        Counter instance = RoboGuice.getInjector(injectedActivity).getInstance(Counter.class);
        Assert.assertEquals(0, instance.count);
        (instance.count)++;
        Counter instanceAgain = RoboGuice.getInjector(injectedActivity).getInstance(Counter.class);
        Assert.assertEquals(1, instanceAgain.count);
        Assert.assertSame(fieldCounter, instance);
    }

    @Test
    public void shouldBeAbleToInjectAContext() throws Exception {
        Assert.assertNotNull(context);
    }
}

