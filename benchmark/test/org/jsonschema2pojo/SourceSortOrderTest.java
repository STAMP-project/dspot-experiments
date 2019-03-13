/**
 * Copyright ? 2010-2017 Nokia
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jsonschema2pojo;


import SourceSortOrder.FILES_FIRST;
import SourceSortOrder.SUBDIRS_FIRST;
import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class SourceSortOrderTest {
    @Test
    public void testTwoFilesAreCompared_FILES_FIRST() throws IOException {
        testTwoFilesAreCompared(FILES_FIRST.getComparator());
    }

    @Test
    public void twoDirectoriesAreCompared_FILES_FIRST() throws IOException {
        testTwoDirectoriesAreCompared(FILES_FIRST.getComparator());
    }

    @Test
    public void testTwoFilesAreCompared_SUBDIRS_FIRST() throws IOException {
        testTwoFilesAreCompared(SUBDIRS_FIRST.getComparator());
    }

    @Test
    public void twoDirectoriesAreCompared_SUBDIRS_FIRST() throws IOException {
        testTwoDirectoriesAreCompared(SUBDIRS_FIRST.getComparator());
    }

    @Test
    public void filesBeforeDirectories_FILES_FIRST() {
        final Comparator<File> fileComparator = FILES_FIRST.getComparator();
        final File mockFile = mockFile();
        final File mockDir = mockDirectory();
        MatcherAssert.assertThat(fileComparator.compare(mockFile, mockDir), lessThan(0));
        MatcherAssert.assertThat(fileComparator.compare(mockDir, mockFile), greaterThan(0));
        Mockito.verify(mockFile, Mockito.never()).compareTo(ArgumentMatchers.any(File.class));
        Mockito.verify(mockDir, Mockito.never()).compareTo(ArgumentMatchers.any(File.class));
    }

    @Test
    public void filesBeforeDirectories_SUBDIRS_FIRST() {
        final Comparator<File> fileComparator = SUBDIRS_FIRST.getComparator();
        final File mockFile = mockFile();
        final File mockDir = mockDirectory();
        MatcherAssert.assertThat(fileComparator.compare(mockFile, mockDir), greaterThan(0));
        MatcherAssert.assertThat(fileComparator.compare(mockDir, mockFile), lessThan(0));
        Mockito.verify(mockFile, Mockito.never()).compareTo(ArgumentMatchers.any(File.class));
        Mockito.verify(mockDir, Mockito.never()).compareTo(ArgumentMatchers.any(File.class));
    }
}

