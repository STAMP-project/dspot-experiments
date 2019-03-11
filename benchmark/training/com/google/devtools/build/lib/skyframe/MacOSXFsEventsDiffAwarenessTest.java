/**
 * Copyright 2016 The Bazel Authors. All rights reserved.
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
package com.google.devtools.build.lib.skyframe;


import com.google.common.collect.ImmutableMap;
import com.google.devtools.build.lib.skyframe.DiffAwareness.View;
import com.google.devtools.build.lib.skyframe.LocalDiffAwareness.Options;
import com.google.devtools.common.options.OptionsBase;
import com.google.devtools.common.options.OptionsProvider;
import java.nio.file.Path;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link MacOSXFsEventsDiffAwareness}
 */
@RunWith(JUnit4.class)
public class MacOSXFsEventsDiffAwarenessTest {
    private MacOSXFsEventsDiffAwareness underTest;

    private Path watchedPath;

    private OptionsProvider watchFsEnabledProvider;

    @Test
    public void testSimple() throws Exception {
        View view1 = underTest.getCurrentView(watchFsEnabledProvider);
        scratchFile("a/b/c");
        scratchFile("b/c/d");
        Thread.sleep(200);// Wait until the events propagate

        View view2 = underTest.getCurrentView(watchFsEnabledProvider);
        assertDiff(view1, view2, "a", "a/b", "a/b/c", "b", "b/c", "b/c/d");
        MacOSXFsEventsDiffAwarenessTest.rmdirs(watchedPath.resolve("a"));
        MacOSXFsEventsDiffAwarenessTest.rmdirs(watchedPath.resolve("b"));
        Thread.sleep(200);// Wait until the events propagate

        View view3 = underTest.getCurrentView(watchFsEnabledProvider);
        assertDiff(view2, view3, "a", "a/b", "a/b/c", "b", "b/c", "b/c/d");
    }

    /**
     * Only returns a fixed options class for {@link LocalDiffAwareness.Options}.
     */
    private static final class LocalDiffAwarenessOptionsProvider implements OptionsProvider {
        private final Options localDiffOptions;

        private LocalDiffAwarenessOptionsProvider(Options localDiffOptions) {
            this.localDiffOptions = localDiffOptions;
        }

        @Override
        public <O extends OptionsBase> O getOptions(Class<O> optionsClass) {
            if (optionsClass.equals(Options.class)) {
                return optionsClass.cast(localDiffOptions);
            }
            return null;
        }

        @Override
        public Map<String, Object> getStarlarkOptions() {
            return ImmutableMap.of();
        }
    }
}

