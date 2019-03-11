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
package com.google.devtools.build.lib.util;


import FileType.NO_EXTENSION;
import FileTypeSet.ANY_FILE;
import FileTypeSet.NO_FILE;
import com.google.common.collect.ImmutableList;
import com.google.devtools.build.lib.util.FileType.HasFileType;
import com.google.devtools.build.lib.vfs.Path;
import com.google.devtools.build.lib.vfs.PathFragment;
import com.google.devtools.build.lib.vfs.inmemoryfs.InMemoryFileSystem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Test for {@link FileType} and {@link FileTypeSet}.
 */
@RunWith(JUnit4.class)
public class FileTypeTest {
    private static final FileType CFG = FileType.of(".cfg");

    private static final FileType HTML = FileType.of(".html");

    private static final FileType TEXT = FileType.of(".txt");

    private static final FileType CPP_SOURCE = FileType.of(".cc", ".cpp", ".cxx", ".C");

    private static final FileType JAVA_SOURCE = FileType.of(".java");

    private static final FileType PYTHON_SOURCE = FileType.of(".py");

    private static final class HasFileTypeImpl implements HasFileType {
        private final String path;

        private HasFileTypeImpl(String path) {
            this.path = path;
        }

        @Override
        public String filePathForFileTypeMatcher() {
            return path;
        }

        @Override
        public String toString() {
            return path;
        }
    }

    @Test
    public void simpleDotMatch() {
        assertThat(FileTypeTest.TEXT.matches("readme.txt")).isTrue();
    }

    @Test
    public void doubleDotMatches() {
        assertThat(FileTypeTest.TEXT.matches("read.me.txt")).isTrue();
    }

    @Test
    public void noExtensionMatches() {
        assertThat(NO_EXTENSION.matches("hello")).isTrue();
        assertThat(NO_EXTENSION.matches("/path/to/hello")).isTrue();
    }

    @Test
    public void picksLastExtension() {
        assertThat(FileTypeTest.TEXT.matches("server.cfg.txt")).isTrue();
    }

    @Test
    public void onlyExtensionStillMatches() {
        assertThat(FileTypeTest.TEXT.matches(".txt")).isTrue();
    }

    @Test
    public void handlesPathObjects() {
        Path readme = new InMemoryFileSystem().getPath("/readme.txt");
        assertThat(FileTypeTest.TEXT.matches(readme)).isTrue();
    }

    @Test
    public void handlesPathFragmentObjects() {
        PathFragment readme = PathFragment.create("some/where/readme.txt");
        assertThat(FileTypeTest.TEXT.matches(readme)).isTrue();
    }

    @Test
    public void fileTypeSetContains() {
        FileTypeSet allowedTypes = FileTypeSet.of(FileTypeTest.TEXT, FileTypeTest.HTML);
        assertThat(allowedTypes.matches("readme.txt")).isTrue();
        assertThat(allowedTypes.matches("style.css")).isFalse();
    }

    @Test
    public void justJava() {
        assertThat(filterAll(FileTypeTest.JAVA_SOURCE)).isEqualTo("Foo.java");
    }

    @Test
    public void javaAndCpp() {
        assertThat(filterAll(FileTypeTest.JAVA_SOURCE, FileTypeTest.CPP_SOURCE)).isEqualTo("Foo.java bar.cc");
    }

    @Test
    public void allThree() {
        assertThat(filterAll(FileTypeTest.JAVA_SOURCE, FileTypeTest.CPP_SOURCE, FileTypeTest.PYTHON_SOURCE)).isEqualTo("Foo.java bar.cc baz.py");
    }

    @Test
    public void checkingSingleWithTypePredicate() throws Exception {
        HasFileType item = filename("config.txt");
        assertThat(FileType.contains(item, FileTypeTest.TEXT)).isTrue();
        assertThat(FileType.contains(item, FileTypeTest.CFG)).isFalse();
    }

    @Test
    public void checkingListWithTypePredicate() throws Exception {
        ImmutableList<HasFileType> unfiltered = ImmutableList.of(filename("config.txt"), filename("index.html"), filename("README.txt"));
        assertThat(FileType.contains(unfiltered, FileTypeTest.TEXT)).isTrue();
        assertThat(FileType.contains(unfiltered, FileTypeTest.CFG)).isFalse();
    }

    @Test
    public void filteringWithTypePredicate() throws Exception {
        ImmutableList<HasFileType> unfiltered = ImmutableList.of(filename("config.txt"), filename("index.html"), filename("README.txt"), filename("archive.zip"));
        assertThat(FileType.filter(unfiltered, FileTypeTest.TEXT)).containsExactly(unfiltered.get(0), unfiltered.get(2)).inOrder();
    }

    @Test
    public void filteringWithMatcherPredicate() throws Exception {
        ImmutableList<HasFileType> unfiltered = ImmutableList.of(filename("config.txt"), filename("index.html"), filename("README.txt"), filename("archive.zip"));
        assertThat(FileType.filter(unfiltered, FileTypeTest.TEXT::matches)).containsExactly(unfiltered.get(0), unfiltered.get(2)).inOrder();
    }

    @Test
    public void filteringWithAlwaysFalse() throws Exception {
        ImmutableList<HasFileType> unfiltered = ImmutableList.of(filename("config.txt"), filename("index.html"), filename("binary"), filename("archive.zip"));
        assertThat(FileType.filter(unfiltered, NO_FILE)).isEmpty();
    }

    @Test
    public void filteringWithAlwaysTrue() throws Exception {
        ImmutableList<HasFileType> unfiltered = ImmutableList.of(filename("config.txt"), filename("index.html"), filename("binary"), filename("archive.zip"));
        assertThat(FileType.filter(unfiltered, ANY_FILE)).containsExactly(unfiltered.get(0), unfiltered.get(1), unfiltered.get(2), unfiltered.get(3)).inOrder();
    }

    @Test
    public void exclusionWithTypePredicate() throws Exception {
        ImmutableList<HasFileType> unfiltered = ImmutableList.of(filename("config.txt"), filename("index.html"), filename("README.txt"), filename("server.cfg"));
        assertThat(FileType.except(unfiltered, FileTypeTest.TEXT)).containsExactly(unfiltered.get(1), unfiltered.get(3)).inOrder();
    }

    @Test
    public void listFiltering() throws Exception {
        ImmutableList<HasFileType> unfiltered = ImmutableList.of(filename("config.txt"), filename("index.html"), filename("README.txt"), filename("server.cfg"));
        FileTypeSet filter = FileTypeSet.of(FileTypeTest.HTML, FileTypeTest.CFG);
        assertThat(FileType.filterList(unfiltered, filter)).containsExactly(unfiltered.get(1), unfiltered.get(3)).inOrder();
    }
}

