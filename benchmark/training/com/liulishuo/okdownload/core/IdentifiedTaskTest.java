/**
 * Copyright (c) 2018 LingoChamp Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.liulishuo.okdownload.core;


import com.liulishuo.okdownload.DownloadTask;
import java.io.File;
import org.junit.Test;
import org.mockito.Mockito;


public class IdentifiedTaskTest {
    private IdentifiedTask task;

    private IdentifiedTask another;

    private String url = "https://jacksgong.com";

    private File providedFile = new File("/provided-path/filename");

    private File parentFile = new File("/provided-path");

    private String filename = "filename";

    @Test
    public void compareIgnoreId_url() {
        Mockito.when(another.getUrl()).thenReturn("another-url");
        assertThat(task.compareIgnoreId(another)).isFalse();
        Mockito.when(another.getUrl()).thenReturn(url);
        assertThat(task.compareIgnoreId(another)).isTrue();
    }

    @Test
    public void compareIgnoreId_providedPathFile() {
        Mockito.when(another.getProvidedPathFile()).thenReturn(new File("/another-provided-path"));
        Mockito.when(another.getParentFile()).thenReturn(new File("/another-provided-path"));
        assertThat(task.compareIgnoreId(another)).isFalse();
        Mockito.when(another.getProvidedPathFile()).thenReturn(providedFile);
        assertThat(task.compareIgnoreId(another)).isTrue();
    }

    @Test
    public void compareIgnoreId_parentPath() {
        Mockito.when(another.getProvidedPathFile()).thenReturn(new File("/another-parent-path"));
        Mockito.when(another.getParentFile()).thenReturn(new File("/another-parent-path"));
        assertThat(task.compareIgnoreId(another)).isFalse();
        Mockito.when(another.getParentFile()).thenReturn(parentFile);
        assertThat(task.compareIgnoreId(another)).isTrue();
    }

    @Test
    public void compareIgnoreId_filename() {
        Mockito.when(another.getProvidedPathFile()).thenReturn(new File("/another-parent-path"));
        Mockito.when(another.getFilename()).thenReturn(null);
        assertThat(task.compareIgnoreId(another)).isFalse();
        Mockito.when(another.getFilename()).thenReturn("another-filename");
        assertThat(task.compareIgnoreId(another)).isFalse();
        Mockito.when(another.getFilename()).thenReturn(filename);
        assertThat(task.compareIgnoreId(another)).isTrue();
    }

    @Test
    public void compareIgnoreId_falseEmpty() {
        final IdentifiedTask task = DownloadTask.mockTaskForCompare(1);
        final IdentifiedTask anotherTask = DownloadTask.mockTaskForCompare(2);
        assertThat(task.compareIgnoreId(task)).isFalse();
        assertThat(task.compareIgnoreId(anotherTask)).isFalse();
    }
}

