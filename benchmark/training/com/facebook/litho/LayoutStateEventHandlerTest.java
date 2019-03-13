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


import LayoutState.CalculateLayoutSource.TEST;
import com.facebook.litho.testing.testrunner.ComponentsTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;


@RunWith(ComponentsTestRunner.class)
public class LayoutStateEventHandlerTest {
    private int mUnspecifiedSizeSpec = 0;// SizeSpec.makeSizeSpec(0, SizeSpec.UNSPECIFIED);


    private Component mRootComponent;

    private Component mNestedComponent;

    @Test
    public void testNestedEventHandlerInput() {
        LayoutState.calculate(new ComponentContext(RuntimeEnvironment.application), mRootComponent, (-1), mUnspecifiedSizeSpec, mUnspecifiedSizeSpec, TEST);
    }
}

