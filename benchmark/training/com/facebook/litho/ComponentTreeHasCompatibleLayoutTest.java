/**
 * Copyright 2014-present Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.facebook.litho;


import com.facebook.litho.testing.testrunner.ComponentsTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.shadows.ShadowLooper;


@RunWith(ComponentsTestRunner.class)
public class ComponentTreeHasCompatibleLayoutTest {
    private int mWidthSpec;

    private int mWidthSpec2;

    private int mHeightSpec;

    private int mHeightSpec2;

    private Component mComponent;

    private ShadowLooper mLayoutThreadShadowLooper;

    private ComponentContext mContext;

    private ComponentTree mComponentTree;

    @Test
    public void testNoLayoutComputed() {
        assertThat(mComponentTree.hasCompatibleLayout(mWidthSpec, mHeightSpec)).isFalse();
    }

    @Test
    public void testMainThreadLayoutSet() {
        mComponentTree.setRootAndSizeSpec(mComponent, mWidthSpec, mHeightSpec);
        assertThat(mComponentTree.hasCompatibleLayout(mWidthSpec, mHeightSpec)).isTrue();
        assertThat(mComponentTree.hasCompatibleLayout(mWidthSpec2, mHeightSpec2)).isFalse();
    }

    @Test
    public void testBackgroundLayoutSet() {
        mComponentTree.setSizeSpecAsync(mWidthSpec, mHeightSpec);
        assertThat(mComponentTree.hasCompatibleLayout(mWidthSpec, mHeightSpec)).isFalse();
        // Now the background thread run the queued task.
        mLayoutThreadShadowLooper.runToEndOfTasks();
        assertThat(mComponentTree.hasCompatibleLayout(mWidthSpec, mHeightSpec)).isTrue();
    }
}

