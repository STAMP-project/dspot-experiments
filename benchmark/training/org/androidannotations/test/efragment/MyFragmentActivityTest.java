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
package org.androidannotations.test.efragment;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class MyFragmentActivityTest {
    private MyFragmentActivity_ activity;

    @Test
    public void canInjectNativeFragmentWithDefaultId() {
        assertThat(activity.myFragment).isNotNull();
    }

    @Test
    public void canInjectNativeFragmentWithId() {
        assertThat(activity.myFragment2).isNotNull();
    }

    @Test
    public void canInjectNativeFragmentWithWithDefaultTag() {
        assertThat(activity.myFragmentTag).isNotNull();
    }

    @Test
    public void canInjectNativeFragmentWithWithTag() {
        assertThat(activity.myFragmentTag2).isNotNull();
    }

    @Test
    public void methodInjectedFragmentById() {
        assertThat(activity.methodInjectedFragmentById).isNotNull();
    }

    @Test
    public void multiInjectedFragmentById() {
        assertThat(activity.multiInjectedFragmentById).isNotNull();
    }

    @Test
    public void methodInjectedFragmentByTag() {
        assertThat(activity.methodInjectedFragmentByTag).isNotNull();
    }

    @Test
    public void multiInjectedFragmentByTag() {
        assertThat(activity.multiInjectedFragmentByTag).isNotNull();
    }
}

