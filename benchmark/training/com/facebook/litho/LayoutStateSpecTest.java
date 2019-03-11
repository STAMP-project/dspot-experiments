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


@RunWith(ComponentsTestRunner.class)
public class LayoutStateSpecTest {
    private static final int COMPONENT_ID = 37;

    private int mWidthSpec;

    private int mHeightSpec;

    private LayoutState mLayoutState;

    private Component mComponent;

    private ComponentContext mContext;

    @Test
    public void testCompatibleInputAndSpec() {
        assertThat(mLayoutState.isCompatibleComponentAndSpec(LayoutStateSpecTest.COMPONENT_ID, mWidthSpec, mHeightSpec)).isTrue();
    }

    @Test
    public void testIncompatibleInput() {
        assertThat(mLayoutState.isCompatibleComponentAndSpec(((LayoutStateSpecTest.COMPONENT_ID) + 1000), mWidthSpec, mHeightSpec)).isFalse();
    }

    @Test
    public void testIncompatibleWidthSpec() {
        assertThat(mLayoutState.isCompatibleComponentAndSpec(LayoutStateSpecTest.COMPONENT_ID, ((mWidthSpec) + 1000), mHeightSpec)).isFalse();
    }

    @Test
    public void testIncompatibleHeightSpec() {
        assertThat(mLayoutState.isCompatibleComponentAndSpec(LayoutStateSpecTest.COMPONENT_ID, mWidthSpec, ((mHeightSpec) + 1000))).isFalse();
    }
}

