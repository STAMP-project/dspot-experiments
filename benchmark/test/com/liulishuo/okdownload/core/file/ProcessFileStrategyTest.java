/**
 * Copyright (c) 2017 LingoChamp Inc.
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
package com.liulishuo.okdownload.core.file;


import DownloadOutputStream.Factory;
import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.OkDownload;
import com.liulishuo.okdownload.TestUtils;
import java.io.File;
import java.io.IOException;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public class ProcessFileStrategyTest {
    private ProcessFileStrategy strategy;

    @Mock
    private DownloadTask task;

    @Test
    public void discardProcess() throws IOException {
        final File existFile = new File("./exist-path");
        existFile.createNewFile();
        Mockito.when(task.getFile()).thenReturn(existFile);
        strategy.discardProcess(task);
        assertThat(existFile.exists()).isFalse();
    }

    @Test(expected = IOException.class)
    public void discardProcess_deleteFailed() throws IOException {
        final File file = Mockito.mock(File.class);
        Mockito.when(task.getFile()).thenReturn(file);
        Mockito.when(file.exists()).thenReturn(true);
        Mockito.when(file.delete()).thenReturn(false);
        strategy.discardProcess(task);
    }

    @Test
    public void isPreAllocateLength() throws IOException {
        TestUtils.mockOkDownload();
        // no pre-allocate set on task.
        Mockito.when(task.getSetPreAllocateLength()).thenReturn(null);
        final DownloadOutputStream.Factory factory = OkDownload.with().outputStreamFactory();
        Mockito.when(factory.supportSeek()).thenReturn(false);
        assertThat(strategy.isPreAllocateLength(task)).isFalse();
        Mockito.when(factory.supportSeek()).thenReturn(true);
        assertThat(strategy.isPreAllocateLength(task)).isTrue();
        // pre-allocate set on task.
        Mockito.when(task.getSetPreAllocateLength()).thenReturn(false);
        assertThat(strategy.isPreAllocateLength(task)).isFalse();
        Mockito.when(task.getSetPreAllocateLength()).thenReturn(true);
        assertThat(strategy.isPreAllocateLength(task)).isTrue();
        // pre-allocate set on task is true but can't support seek.
        Mockito.when(factory.supportSeek()).thenReturn(false);
        assertThat(strategy.isPreAllocateLength(task)).isFalse();
    }
}

