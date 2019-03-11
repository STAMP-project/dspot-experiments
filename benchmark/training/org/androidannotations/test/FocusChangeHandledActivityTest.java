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
package org.androidannotations.test;


import R.id.button1;
import R.id.buttonWithButtonArgument;
import R.id.extendedConventionButton;
import R.id.snake_case_button;
import android.view.View;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class FocusChangeHandledActivityTest {
    private FocusChangeHandledActivity_ activity;

    @Test
    public void testEventHandled() {
        assertThat(activity.snakeCaseButtonEventHandled).isFalse();
        activity.findViewById(snake_case_button).getOnFocusChangeListener().onFocusChange(null, false);
        assertThat(activity.snakeCaseButtonEventHandled).isTrue();
    }

    @Test
    public void testViewPassed() {
        assertThat(activity.view).isNull();
        View view = activity.findViewById(extendedConventionButton);
        view.getOnFocusChangeListener().onFocusChange(view, false);
        assertThat(activity.view).isEqualTo(view);
    }

    @Test
    public void testButtonPassed() {
        assertThat(activity.view).isNull();
        View view = activity.findViewById(buttonWithButtonArgument);
        view.getOnFocusChangeListener().onFocusChange(view, false);
        assertThat(activity.view).isSameAs(view);
    }

    @Test
    public void testHasFocusPassed() {
        assertThat(activity.hasFocus).isFalse();
        View view = activity.findViewById(button1);
        view.getOnFocusChangeListener().onFocusChange(view, true);
        assertThat(activity.hasFocus).isTrue();
    }
}

