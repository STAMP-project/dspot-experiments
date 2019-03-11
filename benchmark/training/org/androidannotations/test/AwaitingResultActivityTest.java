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
package org.androidannotations.test;


import AwaitingResultActivity.FIFTH_REQUEST;
import AwaitingResultActivity.FIRST_REQUEST;
import AwaitingResultActivity.FORTH_REQUEST;
import AwaitingResultActivity.SECOND_REQUEST;
import AwaitingResultActivity.THIRD_REQUEST;
import android.content.Intent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class AwaitingResultActivityTest {
    private AwaitingResultActivity_ activity;

    @Test
    public void onlyFirstRequestAnnotatedMethodAreCalled() {
        activity.onActivityResult(FIRST_REQUEST, 0, null);
        assertThat(activity.onResultCalled).isTrue();
        assertThat(activity.onResultWithDataCalled).isFalse();
        assertThat(activity.onActivityResultWithResultCodeAndDataCalled).isFalse();
        assertThat(activity.onActivityResultWithDataAndResultCodeCalled).isFalse();
        assertThat(activity.onResultWithIntResultCodeCalled).isFalse();
        assertThat(activity.onResultWithIntegerResultCodeCalled).isFalse();
        assertThat(activity.onResultWithResultExtraCodeCalled).isFalse();
    }

    @Test
    public void onlySecondRequestAnnotatedMethodAreCalled() {
        activity.onActivityResult(SECOND_REQUEST, 0, null);
        assertThat(activity.onResultCalled).isFalse();
        assertThat(activity.onResultWithDataCalled).isTrue();
        assertThat(activity.onActivityResultWithResultCodeAndDataCalled).isTrue();
        assertThat(activity.onActivityResultWithDataAndResultCodeCalled).isTrue();
        assertThat(activity.onResultWithIntResultCodeCalled).isFalse();
        assertThat(activity.onResultWithIntegerResultCodeCalled).isFalse();
        assertThat(activity.onResultWithResultExtraCodeCalled).isFalse();
    }

    @Test
    public void onlyThirdRequestAnnotatedMethodAreCalled() {
        activity.onActivityResult(THIRD_REQUEST, 0, null);
        assertThat(activity.onResultCalled).isFalse();
        assertThat(activity.onResultWithDataCalled).isFalse();
        assertThat(activity.onActivityResultWithResultCodeAndDataCalled).isFalse();
        assertThat(activity.onActivityResultWithDataAndResultCodeCalled).isFalse();
        assertThat(activity.onResultWithIntResultCodeCalled).isTrue();
        assertThat(activity.onResultWithIntegerResultCodeCalled).isTrue();
        assertThat(activity.onResultWithResultExtraCodeCalled).isFalse();
    }

    @Test
    public void onlyForthRequestAnnotatedMethodAreCalled() {
        activity.onActivityResult(FORTH_REQUEST, 0, null);
        assertThat(activity.onResultCalled).isFalse();
        assertThat(activity.onResultWithDataCalled).isFalse();
        assertThat(activity.onActivityResultWithResultCodeAndDataCalled).isFalse();
        assertThat(activity.onActivityResultWithDataAndResultCodeCalled).isFalse();
        assertThat(activity.onResultWithIntResultCodeCalled).isFalse();
        assertThat(activity.onResultWithIntegerResultCodeCalled).isFalse();
        assertThat(activity.onResultWithResultExtraCodeCalled).isTrue();
    }

    @Test
    public void onResultWithIntentExtrasPassed() {
        Intent intent = new Intent();
        Intent extraIntent = new Intent("someAction");
        intent.putExtra("extraIntent", extraIntent);
        activity.onActivityResult(FIFTH_REQUEST, 0, intent);
        assertThat(activity.originalIntent).isEqualTo(intent);
        assertThat(activity.extraIntent).isEqualTo(extraIntent);
    }
}

