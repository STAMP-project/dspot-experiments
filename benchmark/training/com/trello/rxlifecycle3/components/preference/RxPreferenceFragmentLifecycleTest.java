/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.trello.rxlifecycle3.components.preference;


import android.os.Bundle;
import io.reactivex.Observable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class RxPreferenceFragmentLifecycleTest {
    private Observable<Object> observable;

    @Test
    public void testRxPreferenceFragmentCompat() {
        // Requires android.support.v7.preference.R.preferenceTheme
        // attribute being set.
        // 
        // testLifecycle(new TestRxPreferenceFragmentCompat());
        // testBindUntilEvent(new TestRxPreferenceFragmentCompat());
        // testBindToLifecycle(new TestRxPreferenceFragmentCompat());
    }

    // These classes are just for testing since components are abstract
    public static class TestRxPreferenceFragmentCompat extends RxPreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle bundle, String s) {
        }
    }
}

