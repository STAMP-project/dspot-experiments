/**
 * Copyright 2015 The Bazel Authors. All rights reserved.
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
package com.google.devtools.build.android.ziputils;


import com.google.common.base.Predicate;
import java.io.File;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for {@link SplitZipFilters}.
 */
@RunWith(JUnit4.class)
public class SplitZipFiltersTest {
    @Rule
    public final TemporaryFolder tmp = new TemporaryFolder();

    @Test
    public void testEntriesIn() throws Exception {
        File filterZip = createZip("pkg1/test1.class", "pkg2/test2.class");
        Predicate<String> filter = SplitZipFilters.entriesIn(filterZip.getAbsolutePath());
        assertThat(filter.apply("pkg1/test1.class")).isTrue();
        assertThat(filter.apply("pkg2/test1.class")).isFalse();
        assertThat(filter.apply("pkg2/test2.class")).isTrue();
        assertThat(filter.apply("pkg1/test1$inner.class")).isFalse();
    }
}

