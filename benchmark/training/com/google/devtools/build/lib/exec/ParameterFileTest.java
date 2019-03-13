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
package com.google.devtools.build.lib.exec;


import com.google.common.collect.ImmutableList;
import com.google.devtools.build.lib.actions.ParameterFile;
import com.google.devtools.build.lib.testutil.FoundationTestCase;
import com.google.devtools.build.lib.testutil.Suite;
import com.google.devtools.build.lib.testutil.TestSpec;
import com.google.devtools.build.lib.vfs.PathFragment;
import java.nio.charset.StandardCharsets;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link ParameterFile}.
 */
@TestSpec(size = Suite.SMALL_TESTS)
@RunWith(JUnit4.class)
public class ParameterFileTest extends FoundationTestCase {
    @Test
    public void testDerive() {
        assertThat(ParameterFile.derivePath(PathFragment.create("a/b"))).isEqualTo(PathFragment.create("a/b-2.params"));
        assertThat(ParameterFile.derivePath(PathFragment.create("b"))).isEqualTo(PathFragment.create("b-2.params"));
    }

    @Test
    public void testWriteAscii() throws Exception {
        assertThat(ParameterFileTest.writeContent(StandardCharsets.ISO_8859_1, ImmutableList.of("--foo", "--bar"))).containsExactly("--foo", "--bar");
        assertThat(ParameterFileTest.writeContent(StandardCharsets.UTF_8, ImmutableList.of("--foo", "--bar"))).containsExactly("--foo", "--bar");
    }

    @Test
    public void testWriteLatin1() throws Exception {
        assertThat(ParameterFileTest.writeContent(StandardCharsets.ISO_8859_1, ImmutableList.of("--f??"))).containsExactly("--f??");
        assertThat(ParameterFileTest.writeContent(StandardCharsets.UTF_8, ImmutableList.of("--f??"))).containsExactly("--f??");
    }

    @Test
    public void testWriteUtf8() throws Exception {
        assertThat(ParameterFileTest.writeContent(StandardCharsets.ISO_8859_1, ImmutableList.of("--lambda=?"))).containsExactly("--lambda=?");
        assertThat(ParameterFileTest.writeContent(StandardCharsets.UTF_8, ImmutableList.of("--lambda=?"))).containsExactly("--lambda=?");
    }
}

