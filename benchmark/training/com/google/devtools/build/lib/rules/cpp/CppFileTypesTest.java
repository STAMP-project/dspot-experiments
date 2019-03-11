/**
 * Copyright 2014 The Bazel Authors. All rights reserved.
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
package com.google.devtools.build.lib.rules.cpp;


import CppFileTypes.OBJECT_FILE;
import CppFileTypes.PIC_OBJECT_FILE;
import CppFileTypes.SHARED_LIBRARY;
import CppFileTypes.VERSIONED_SHARED_LIBRARY;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Test for {@link CppFileTypes}.
 */
@RunWith(JUnit4.class)
public class CppFileTypesTest {
    @Test
    public void testTwoDotExtensions() {
        assertThat(OBJECT_FILE.matches("test.o")).isTrue();
        assertThat(PIC_OBJECT_FILE.matches("test.pic.o")).isTrue();
        assertThat(OBJECT_FILE.matches("test.pic.o")).isFalse();
    }

    @Test
    public void testVersionedSharedLibraries() {
        assertThat(SHARED_LIBRARY.matches("somelibrary.so")).isTrue();
        assertThat(VERSIONED_SHARED_LIBRARY.matches("somelibrary.so.2")).isTrue();
        assertThat(VERSIONED_SHARED_LIBRARY.matches("somelibrary.so.20")).isTrue();
        assertThat(VERSIONED_SHARED_LIBRARY.matches("somelibrary.so.20.2")).isTrue();
        assertThat(VERSIONED_SHARED_LIBRARY.matches("a/somelibrary.so.2")).isTrue();
        assertThat(VERSIONED_SHARED_LIBRARY.matches("somelibrary.so.e")).isFalse();
        assertThat(VERSIONED_SHARED_LIBRARY.matches("somelibrary.so.2e")).isFalse();
        assertThat(VERSIONED_SHARED_LIBRARY.matches("somelibrary.so.e2")).isFalse();
        assertThat(VERSIONED_SHARED_LIBRARY.matches("somelibrary.so.20.e2")).isFalse();
        assertThat(VERSIONED_SHARED_LIBRARY.matches("somelibrary.a.2")).isFalse();
    }
}

