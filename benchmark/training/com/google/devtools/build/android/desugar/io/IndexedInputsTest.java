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
package com.google.devtools.build.android.desugar.io;


import com.google.common.collect.ImmutableList;
import java.io.File;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Test that exercises the behavior of the IndexedInputs class.
 */
@RunWith(JUnit4.class)
public final class IndexedInputsTest {
    private static File lib1;

    private static String lib1Name;

    private static File lib2;

    private static String lib2Name;

    private static InputFileProvider lib1InputFileProvider;

    private static InputFileProvider lib2InputFileProvider;

    @Test
    public void testClassFoundWithParentLibrary() throws Exception {
        IndexedInputs indexedLib2 = new IndexedInputs(ImmutableList.of(IndexedInputsTest.lib2InputFileProvider));
        IndexedInputs indexedLib1 = new IndexedInputs(ImmutableList.of(IndexedInputsTest.lib1InputFileProvider));
        IndexedInputs indexedLib2AndLib1 = indexedLib1.withParent(indexedLib2);
        assertThat(indexedLib2AndLib1.getInputFileProvider("a/b/C.class").toString()).isEqualTo(IndexedInputsTest.lib2Name);
        assertThat(indexedLib2AndLib1.getInputFileProvider("a/b/D.class").toString()).isEqualTo(IndexedInputsTest.lib1Name);
        assertThat(indexedLib2AndLib1.getInputFileProvider("a/b/E.class").toString()).isEqualTo(IndexedInputsTest.lib2Name);
        indexedLib2 = new IndexedInputs(ImmutableList.of(IndexedInputsTest.lib2InputFileProvider));
        indexedLib1 = new IndexedInputs(ImmutableList.of(IndexedInputsTest.lib1InputFileProvider));
        IndexedInputs indexedLib1AndLib2 = indexedLib2.withParent(indexedLib1);
        assertThat(indexedLib1AndLib2.getInputFileProvider("a/b/C.class").toString()).isEqualTo(IndexedInputsTest.lib1Name);
        assertThat(indexedLib1AndLib2.getInputFileProvider("a/b/D.class").toString()).isEqualTo(IndexedInputsTest.lib1Name);
        assertThat(indexedLib1AndLib2.getInputFileProvider("a/b/E.class").toString()).isEqualTo(IndexedInputsTest.lib2Name);
    }

    @Test
    public void testClassFoundWithoutParentLibrary() throws Exception {
        IndexedInputs ijLib1Lib2 = new IndexedInputs(ImmutableList.of(IndexedInputsTest.lib1InputFileProvider, IndexedInputsTest.lib2InputFileProvider));
        assertThat(ijLib1Lib2.getInputFileProvider("a/b/C.class").toString()).isEqualTo(IndexedInputsTest.lib1Name);
        assertThat(ijLib1Lib2.getInputFileProvider("a/b/D.class").toString()).isEqualTo(IndexedInputsTest.lib1Name);
        assertThat(ijLib1Lib2.getInputFileProvider("a/b/E.class").toString()).isEqualTo(IndexedInputsTest.lib2Name);
        IndexedInputs ijLib2Lib1 = new IndexedInputs(ImmutableList.of(IndexedInputsTest.lib2InputFileProvider, IndexedInputsTest.lib1InputFileProvider));
        assertThat(ijLib2Lib1.getInputFileProvider("a/b/C.class").toString()).isEqualTo(IndexedInputsTest.lib2Name);
        assertThat(ijLib2Lib1.getInputFileProvider("a/b/D.class").toString()).isEqualTo(IndexedInputsTest.lib1Name);
        assertThat(ijLib2Lib1.getInputFileProvider("a/b/E.class").toString()).isEqualTo(IndexedInputsTest.lib2Name);
    }

    @Test
    public void testClassNotFound() throws Exception {
        IndexedInputs ijLib1Lib2 = new IndexedInputs(ImmutableList.of(IndexedInputsTest.lib1InputFileProvider, IndexedInputsTest.lib2InputFileProvider));
        assertThat(ijLib1Lib2.getInputFileProvider("a/b/F.class")).isNull();
    }
}

