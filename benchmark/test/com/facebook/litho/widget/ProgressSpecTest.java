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
package com.facebook.litho.widget;


import ProgressSpec.DEFAULT_SIZE;
import View.MeasureSpec;
import View.MeasureSpec.UNSPECIFIED;
import com.facebook.litho.ComponentContext;
import com.facebook.litho.LithoView;
import com.facebook.litho.testing.testrunner.ComponentsTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Tests {@link ProgressSpec}
 */
@RunWith(ComponentsTestRunner.class)
public class ProgressSpecTest {
    private ComponentContext mContext;

    @Test
    public void testDefault() {
        LithoView view = getMountedView();
        assertThat(view.getMeasuredWidth()).isGreaterThan(0);
        assertThat(view.getMeasuredHeight()).isGreaterThan(0);
    }

    @Test
    public void testUnsetSize() {
        LithoView view = getMountedView();
        view.measure(MeasureSpec.makeMeasureSpec(0, UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, UNSPECIFIED));
        assertThat(view.getMeasuredWidth()).isEqualTo(DEFAULT_SIZE);
        assertThat(view.getMeasuredHeight()).isEqualTo(DEFAULT_SIZE);
    }
}

