/**
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


import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class BeanScopesTest {
    private BeanInjectedActivity_ activity;

    private CustomFragment_ fragment1;

    private DifferentFragment_ fragment2;

    @Test
    public void singletonDependencyIsSameReference() {
        SomeSingleton initialDependency = activity.singletonDependency;
        BeanInjectedActivity_ newActivity = Robolectric.buildActivity(BeanInjectedActivity_.class).create().get();
        assertThat(newActivity.singletonDependency).isSameAs(initialDependency);
    }

    @Test
    public void methodInjectedSingletonDependencyIsSameReference() {
        SomeSingleton initialDependency = activity.methodInjectedSingleton;
        BeanInjectedActivity_ newActivity = Robolectric.buildActivity(BeanInjectedActivity_.class).create().get();
        assertThat(newActivity.methodInjectedSingleton).isSameAs(initialDependency);
    }

    @Test
    public void methodAnnotatedParamsSingletonDependencyIsSameReference() {
        SomeSingleton initialDependency = activity.annotatedParamSingleton;
        BeanInjectedActivity_ newActivity = Robolectric.buildActivity(BeanInjectedActivity_.class).create().get();
        assertThat(newActivity.annotatedParamSingleton).isSameAs(initialDependency);
    }

    @Test
    public void allSingletonDependenciesInSameActivityAreSameReference() {
        assertThat(activity.singletonDependency).isSameAs(activity.annotatedParamSingleton);
        assertThat(activity.singletonDependency).isSameAs(activity.methodInjectedSingleton);
        assertThat(activity.singletonDependency).isSameAs(activity.multiDependencySingleton);
    }

    @Test
    public void allActivityScopedDependenciesInSameActivityAreSameReference() {
        assertThat(activity.activityScopedDependency).isSameAs(activity.annotatedParamActivityScoped);
        assertThat(activity.activityScopedDependency).isSameAs(activity.methodInjectedActivityScoped);
        assertThat(activity.activityScopedDependency).isSameAs(activity.multiDependencyActivityScopedBean);
    }

    @Test
    public void allFragmentScopedDependenciesInSameActivityAreNotSameReference() {
        assertThat(activity.fragmentScopedDependency).isNotSameAs(activity.annotatedParamFragmentScoped);
        assertThat(activity.fragmentScopedDependency).isNotSameAs(activity.methodInjectedFragmentScoped);
        assertThat(activity.fragmentScopedDependency).isNotSameAs(activity.multiDependencyFragmentScopedBean);
    }

    @Test
    public void allActivityScopedDependenciesInFragmentsAreSameReferenceAsInActivity() {
        assertThat(activity.activityScopedDependency).isSameAs(fragment1.activityScopedBean);
        assertThat(activity.activityScopedDependency).isSameAs(fragment2.activityScopedBean);
    }

    @Test
    public void allFragmentScopedDependenciesInFragmentAreSameReference() {
        assertThat(fragment1.fragmentScopedBean1).isSameAs(fragment1.fragmentScopedBean2);
    }

    @Test
    public void fragmentScopedDependenciesAreNotSameReferenceIfNotOnSameFragment() {
        assertThat(activity.fragmentScopedDependency).isNotSameAs(fragment1.fragmentScopedBean1);
        assertThat(activity.fragmentScopedDependency).isNotSameAs(fragment1.fragmentScopedBean2);
        assertThat(activity.fragmentScopedDependency).isNotSameAs(fragment2.fragmentScopedBean);
        assertThat(fragment1.fragmentScopedBean1).isNotSameAs(fragment2.fragmentScopedBean);
    }
}

