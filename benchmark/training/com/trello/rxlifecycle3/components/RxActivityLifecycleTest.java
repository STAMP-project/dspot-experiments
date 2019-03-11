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
package com.trello.rxlifecycle3.components;


import com.trello.rxlifecycle3.components.support.RxFragmentActivity;
import io.reactivex.Observable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class RxActivityLifecycleTest {
    private Observable<Object> observable;

    @Test
    public void testRxActivity() {
        testLifecycle(Robolectric.buildActivity(RxActivityLifecycleTest.TestRxActivity.class));
        testBindUntilEvent(Robolectric.buildActivity(RxActivityLifecycleTest.TestRxActivity.class));
        testBindToLifecycle(Robolectric.buildActivity(RxActivityLifecycleTest.TestRxActivity.class));
    }

    @Test
    public void testRxFragmentActivity() {
        testLifecycle(Robolectric.buildActivity(RxActivityLifecycleTest.TestRxFragmentActivity.class));
        testBindUntilEvent(Robolectric.buildActivity(RxActivityLifecycleTest.TestRxFragmentActivity.class));
        testBindToLifecycle(Robolectric.buildActivity(RxActivityLifecycleTest.TestRxFragmentActivity.class));
    }

    @Test
    public void testRxAppCompatActivity() {
        // TODO: Doesn't work due to https://github.com/robolectric/robolectric/issues/1796
        // 
        // testBindUntilEvent(Robolectric.buildActivity(RxAppCompatActivity.class));
        // testBindToLifecycle(Robolectric.buildActivity(RxAppCompatActivity.class));
    }

    // These classes are just for testing since components are abstract
    public static class TestRxActivity extends RxActivity {}

    public static class TestRxFragmentActivity extends RxFragmentActivity {}
}

