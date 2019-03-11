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


import Window.FEATURE_NO_TITLE;
import android.view.WindowManager.LayoutParams;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class WindowFeatureTest {
    private WindowFeatureActivity_ activity;

    @Test
    public void testActivityHasWindowFeatures() {
        assertThat(activity.getWindow()).hasFeature(FEATURE_NO_TITLE).hasFeature(FEATURE_NO_TITLE);
    }

    @Test
    public void testActivityHasWindowFlags() {
        assertThat((((activity.getWindow().getAttributes().flags) & (LayoutParams.FLAG_FULLSCREEN)) != 0)).isTrue();
    }
}

