/**
 * Copyright 2017 The Bazel Authors. All rights reserved.
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
package com.google.devtools.build.android.dexer;


import com.android.dex.Dex;
import java.io.IOException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link DexLimitTracker}.
 */
@RunWith(JUnit4.class)
public class DexLimitTrackerTest {
    private Dex dex;

    @Test
    public void testUnderLimit() {
        DexLimitTracker tracker = new DexLimitTracker(Math.max(dex.methodIds().size(), dex.fieldIds().size()));
        assertThat(tracker.track(dex)).isFalse();
    }

    @Test
    public void testOverLimit() throws IOException {
        DexLimitTracker tracker = new DexLimitTracker(((Math.max(dex.methodIds().size(), dex.fieldIds().size())) - 1));
        assertThat(tracker.track(dex)).isTrue();
        assertThat(tracker.track(dex)).isTrue();
        assertThat(tracker.track(DexFiles.toDex(DexLimitTrackerTest.convertClass(DexLimitTracker.class)))).isTrue();
    }

    @Test
    public void testRepeatedReferencesDeduped() throws IOException {
        DexLimitTracker tracker = new DexLimitTracker(Math.max(dex.methodIds().size(), dex.fieldIds().size()));
        assertThat(tracker.track(dex)).isFalse();
        assertThat(tracker.track(dex)).isFalse();
        assertThat(tracker.track(dex)).isFalse();
        assertThat(tracker.track(dex)).isFalse();
        assertThat(tracker.track(DexFiles.toDex(DexLimitTrackerTest.convertClass(DexLimitTracker.class)))).isTrue();
        assertThat(tracker.track(dex)).isTrue();
    }

    @Test
    public void testGoOverLimit() throws IOException {
        DexLimitTracker tracker = new DexLimitTracker(Math.max(dex.methodIds().size(), dex.fieldIds().size()));
        assertThat(tracker.track(dex)).isFalse();
        assertThat(tracker.track(DexFiles.toDex(DexLimitTrackerTest.convertClass(DexLimitTracker.class)))).isTrue();
    }

    @Test
    public void testClear() throws IOException {
        DexLimitTracker tracker = new DexLimitTracker(Math.max(dex.methodIds().size(), dex.fieldIds().size()));
        assertThat(tracker.track(dex)).isFalse();
        assertThat(tracker.track(DexFiles.toDex(DexLimitTrackerTest.convertClass(DexLimitTracker.class)))).isTrue();
        tracker.clear();
        assertThat(tracker.track(dex)).isFalse();
    }
}

