/**
 * Copyright 2013 Jake Wharton
 * Copyright 2014 Prateek Srivastava (@f2prateek)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dart;


import android.app.Activity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class DartTest {
    @Test
    public void zeroInjectionsInjectDoesNotThrowException() {
        class Example {}
        Example example = new Example();
        Dart.bind(example, null);
        Dart.bindNavigationModel(example, null, null);
        assertThat(Dart.EXTRA_BINDERS).contains(entry(Example.class, Dart.NO_OP));
        assertThat(Dart.NAVIGATION_MODEL_BINDERS).contains(entry(Example.class, Dart.NO_OP));
    }

    @Test
    public void bindingKnownPackagesIsNoOp() {
        Dart.bind(new Activity());
        Dart.bindNavigationModel(new Object(), new Activity());
        assertThat(Dart.EXTRA_BINDERS).isEmpty();
        assertThat(Dart.NAVIGATION_MODEL_BINDERS).isEmpty();
    }
}

